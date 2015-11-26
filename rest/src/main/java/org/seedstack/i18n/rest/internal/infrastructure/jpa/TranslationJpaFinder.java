/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.jpa;

import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.seedstack.i18n.rest.internal.translation.TranslationAssembler;
import org.seedstack.i18n.rest.internal.translation.TranslationFinder;
import org.seedstack.i18n.rest.internal.translation.TranslationRepresentation;
import org.apache.commons.lang.StringUtils;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.jpa.BaseJpaRangeFinder;
import org.seedstack.seed.core.utils.SeedCheckUtils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This class provides methods to find translations.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 15/05/2014
 */
public class TranslationJpaFinder extends BaseJpaRangeFinder<TranslationRepresentation> implements TranslationFinder {

    private static final String IS_APPROX = "isApprox";
    private static final String IS_MISSING = "isMissing";
    private static final String IS_OUTDATED = "isOutdated";
    private static final String SEARCH_NAME = "searchName";
    private static final String LOCALE = "locale";
    private static final String ENTITY_ID = "entityId";
    private static final String TRANSLATIONS = "translations";
    private static final String OUTDATED = "outdated";
    private static final String APPROXIMATE = "approximate";
    private static final String VALUE = "value";

    @Inject
    private KeyRepository keyRepository;

    @Inject
    private LocaleService localeService;

    @Inject
    private TranslationAssembler translationAssembler;

    @Inject
    private EntityManager entityManager;

    @Override
    public List<TranslationRepresentation> findTranslations(String localeId) {
        List<Key> keys = keyRepository.loadAll();
        List<TranslationRepresentation> translationRepresentations = new ArrayList<TranslationRepresentation>(keys.size());
        String defaultLocale = localeService.getDefaultLocale();
        for (Key key : keys) {
            translationRepresentations.add(translationAssembler.assembleDtoFromAggregate(key.subKey(defaultLocale, localeId)));
        }

        return translationRepresentations;
    }


    @Override
    public TranslationRepresentation findTranslation(String localeId, String keyId) {
        Key key = keyRepository.load(keyId);
        String defaultLocale = localeService.getDefaultLocale();
        SeedCheckUtils.checkIfNotNull(defaultLocale);
        if (key != null) {
            return translationAssembler.assembleDtoFromAggregate(key.subKey(defaultLocale, localeId));
        }
        return null;
    }

    @Override
    public Result<TranslationRepresentation> findAllTranslations(Range range, Map<String, Object> criteria) {
        return find(range, criteria);
    }

    @Override
    protected List<TranslationRepresentation> computeResultList(Range range, Map<String, Object> criteria) {
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
        List<TranslationRepresentation> translationRepresentations = new ArrayList<TranslationRepresentation>(keys.size());
        String defaultLocale = localeService.getDefaultLocale();
        for (Key key : keys) {
            translationRepresentations.add(translationAssembler.assembleDtoFromAggregate(key.subKey(defaultLocale, (String) criteria.get(LOCALE))));
        }

        return translationRepresentations;
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
            String locale = (String) criteria.get(LOCALE);

            // is the key LIKE searchName
            if (StringUtils.isNotBlank(searchName)) {
                predicates.add(cb.like(k.<String>get(ENTITY_ID), "%" + searchName + "%"));
            }

            // if a default translation is available
            if (isApprox != null || isOutdated != null) {
                // join translation table
                Join<Key, Translation> tln = k.join(TRANSLATIONS, JoinType.LEFT);
                // WHERE locale = default locale
                predicates.add(cb.equal(tln.get(ENTITY_ID).get(LOCALE), locale));

                // is the key outdated
                if (isOutdated != null) {
                    predicates.add(cb.equal(tln.<Boolean>get(OUTDATED), isOutdated));
                }

                // is the translation approximate
                if (isApprox != null) {
                    predicates.add(cb.equal(tln.<Boolean>get(APPROXIMATE), isApprox));
                }
            }
            // is the translation missing
            if (isMissing != null) {
                // SubQuery to find all the key which get a translation for the default locale
                //noinspection unchecked
                Subquery<String> subquery = q.subquery(String.class);
                Root<Key> fromKey = subquery.from(Key.class);
                subquery.select(fromKey.<String>get(ENTITY_ID));
                Join join = fromKey.join(TRANSLATIONS, JoinType.LEFT);
                subquery.where(cb.and(cb.equal(join.get(ENTITY_ID).get(LOCALE), locale), cb.notEqual(join.get(VALUE), "")));
                // Find all keys not in the above subquery, ie. all the keys missing
                predicates.add(cb.not(cb.in(k.get(ENTITY_ID)).value(subquery)));
            }
        }
        return predicates.toArray(new Predicate[predicates.size()]);
    }
}
