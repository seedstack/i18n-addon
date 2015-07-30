/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.infrastructure.finders.jpa;

import org.seedstack.i18n.api.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.seedstack.i18n.rest.key.KeyAssembler;
import org.seedstack.i18n.rest.key.KeyFinder;
import org.seedstack.i18n.rest.key.KeyRepresentation;
import org.apache.commons.lang.StringUtils;
import org.seedstack.business.api.interfaces.finder.Range;
import org.seedstack.business.api.interfaces.finder.Result;
import org.seedstack.business.jpa.BaseJpaRangeFinder;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * KeyFinder implementation for JPA.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 13/05/2014
 */
public class KeyJpaFinder extends BaseJpaRangeFinder<KeyRepresentation> implements KeyFinder {

    private static final String IS_APPROX = "isApprox";
    private static final String IS_MISSING = "isMissing";
    private static final String IS_OUTDATED = "isOutdated";
    private static final String SEARCH_NAME = "searchName";
    private static final String ENTITY_ID = "entityId";
    private static final String OUTDATED = "outdated";
    private static final String TRANSLATIONS = "translations";
    private static final String LOCALE = "locale";
    private static final String APPROXIMATE = "approximate";
    private static final String VALUE = "value";

    @Inject
    private EntityManager entityManager;

    @Inject
    private LocaleService localeService;

    @Inject
    private KeyAssembler keyAssembler;

    @Inject
    private KeyRepository keyRepository;

    @Override
    protected List<KeyRepresentation> computeResultList(Range range, Map<String, Object> criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Key> q = cb.createQuery(Key.class);
        Root<Key> k = q.from(Key.class);
        Predicate[] predicates = getPredicates(criteria, q, cb, k);
        q.select(k);
        if (predicates.length > 0) {
            q.where(cb.and(predicates));
        }
        List<Key> keys;
        // Get all the keys with their default translation
        if (range != null) {
            keys = entityManager.createQuery(q).setFirstResult((int) range.getOffset()).setMaxResults((int)range.getSize()).getResultList();
        } else {
            keys = entityManager.createQuery(q).getResultList();
        }

        return assembleRepresentationsFromEntities(keys);
    }

    private List<KeyRepresentation> assembleRepresentationsFromEntities(List<Key> keys) {
        List<KeyRepresentation> keyRepresentations = new ArrayList<KeyRepresentation>(keys.size());
        String defaultLocale = localeService.getDefaultLocale();
        for (Key key : keys) {
            keyRepresentations.add(keyAssembler.assembleDtoFromAggregate(key.subKey(defaultLocale)));
        }
        return keyRepresentations;
    }

    /**
     * Extracts predicates from criteria.
     *
     * @param criteria criteria
     * @param cb       criteria builder
     * @param k        root key
     * @return list of predicate
     */
    private Predicate[] getPredicates(Map<String, Object> criteria, CriteriaQuery q, CriteriaBuilder cb, Root<Key> k) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        if (criteria != null) {
            // extract criteria from the map
            Boolean isApprox = (Boolean) criteria.get(IS_APPROX);
            Boolean isMissing = (Boolean) criteria.get(IS_MISSING);
            Boolean isOutdated = (Boolean) criteria.get(IS_OUTDATED);
            String searchName = (String) criteria.get(SEARCH_NAME);

            // is the key LIKE searchName
            if (StringUtils.isNotBlank(searchName)) {
                predicates.add(cb.like(k.<String>get(ENTITY_ID), "%" + searchName + "%"));
            }
            // is the key outdated
            if (isOutdated != null) {
                predicates.add(cb.equal(k.<Boolean>get(OUTDATED), isOutdated));
            }

            // if a default translation is available
            String defaultLocale = localeService.getDefaultLocale();
            if (defaultLocale != null && isApprox != null) {
                // join translation table
                Join<Key, Translation> tln = k.join(TRANSLATIONS, JoinType.LEFT);
                // WHERE locale = default locale
                predicates.add(cb.equal(tln.get(ENTITY_ID).get(LOCALE), defaultLocale));

                // is the translation approximate
                predicates.add(cb.equal(tln.get(APPROXIMATE), isApprox));
            }

            // is the translation missing
            if (isMissing != null) {
                // SubQuery to find all the key which get a translation for the default locale
                //noinspection unchecked
                Subquery<String> subquery = q.subquery(String.class);
                Root<Key> fromKey = subquery.from(Key.class);
                subquery.select(fromKey.<String>get(ENTITY_ID));
                Join join = fromKey.join(TRANSLATIONS, JoinType.LEFT);
                subquery.where(cb.and(cb.equal(join.get(ENTITY_ID).get(LOCALE), defaultLocale), cb.notEqual(join.get(VALUE), "")));
                // Find all keys not in the above subquery, ie. all the keys missing
                predicates.add(cb.not(cb.in(k.get(ENTITY_ID)).value(subquery)));
            }
        }
        return predicates.toArray(new Predicate[predicates.size()]);
    }

    @Override
    protected long computeFullRequestSize(Map<String, Object> criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Key> k = q.from(Key.class);
        q.select(cb.count(k));
        if (criteria != null) {
            q.where(cb.and(getPredicates(criteria, q, cb, k)));
        }
        return entityManager.createQuery(q).getSingleResult();
    }

    @Override
    public List<KeyRepresentation> findAllKeys() {
        List<Key> keys = keyRepository.loadAll();
        return assembleRepresentationsFromEntities(keys);
    }

    @Override
    public KeyRepresentation findKey(String name) {
        Key key = keyRepository.load(name);
        if (key != null) {
            return keyAssembler.assembleDtoFromAggregate(key.subKey(localeService.getDefaultLocale()));
        } else {
            return null;
        }
    }

    @Override
    public Result<KeyRepresentation> findAllKeys(Range range, Map<String, Object> criteria) {
        return find(range, criteria);
    }

    @Override
    public List<KeyRepresentation> findAllKeys(Map<String, Object> criteria) {
        return computeResultList(null, criteria);
    }
}
