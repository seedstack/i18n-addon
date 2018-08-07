/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.jpa;

import org.apache.commons.lang.StringUtils;
import org.seedstack.business.finder.Range;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.seedstack.i18n.rest.internal.infrastructure.jpa.shared.CountQuery;
import org.seedstack.i18n.rest.internal.infrastructure.jpa.shared.SelectQuery;
import org.seedstack.i18n.rest.internal.key.KeySearchCriteria;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class KeysQuery implements SelectQuery, CountQuery {

    private static final String ENTITY_ID = "entityId";
    private static final String OUTDATED = "outdated";
    private static final String TRANSLATIONS = "translations";
    private static final String LOCALE = "locale";
    private static final String APPROXIMATE = "approximate";
    private static final String TRANSLATION_VALUE = "value";

    private final EntityManager entityManager;
    private CriteriaQuery<Key> selectQuery;
    private CriteriaQuery<Long> countQuery;
    private Root<Key> keyRoot;
    private CriteriaBuilder criteriaBuilder;
    private List<Predicate> predicates = new ArrayList<>();

    public KeysQuery(EntityManager entityManager) {
        this.entityManager = entityManager;
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public SelectQuery select() {
        selectQuery = criteriaBuilder.createQuery(Key.class);
        keyRoot = selectQuery.from(Key.class);
        selectQuery.select(keyRoot);
        return this;
    }

    public CountQuery count() {
        countQuery = criteriaBuilder.createQuery(Long.class);
        keyRoot = countQuery.from(Key.class);
        countQuery.select(criteriaBuilder.count(keyRoot));
        return this;
    }

    @Override
    public void withCriteria(KeySearchCriteria criteria) {
        if (criteria != null) {
            addSQLLikePredicateOnName(criteria, predicates);
            addOutdatedPredicate(criteria, predicates);
            addApproximatePredicate(criteria, criteria.getLocale());
            addTranslationMissingPredicate(criteria, predicates, criteria.getLocale());
        }
    }

    private void addSQLLikePredicateOnName(KeySearchCriteria criteria, List<Predicate> predicates) {
        if (StringUtils.isNotBlank(criteria.getName())) {
            predicates.add(criteriaBuilder.like(keyRoot.get(ENTITY_ID), "%" + criteria.getName() + "%"));
        }
    }

    private void addOutdatedPredicate(KeySearchCriteria criteria, List<Predicate> predicates) {
        if (criteria.getOutdated() != null) {
            predicates.add(criteriaBuilder.equal(keyRoot.<Boolean>get(OUTDATED), criteria.getOutdated()));
        }
    }

    private void addTranslationMissingPredicate(KeySearchCriteria criteria, List<Predicate> predicates, String locale) {
        if (locale != null && criteria.getMissing() != null) {
            // SubQuery to find all the key which get a translation for the default locale
            //noinspection unchecked
            Subquery<String> keysWithTranslation = getAllKeysWithTranslationFor(locale);
            // Find all keys not in the above subQuery, ie. all the keys missing
            predicates.add(criteriaBuilder.not(criteriaBuilder.in(keyRoot.get(ENTITY_ID)).value(keysWithTranslation)));
        }
    }

    private Subquery<String> getAllKeysWithTranslationFor(String locale) {
        Subquery<String> subQuery;
        if (selectQuery != null) {
            subQuery = selectQuery.subquery(String.class);
        } else {
            subQuery = countQuery.subquery(String.class);
        }
        Root<Key> fromKey = subQuery.from(Key.class);

        subQuery.select(fromKey.get(ENTITY_ID));

        Join join = fromKey.join(TRANSLATIONS, JoinType.LEFT);

        Predicate keyIdsAreEquals = criteriaBuilder.equal(join.get(ENTITY_ID).get(LOCALE), locale);
        Predicate translationIsPresent = criteriaBuilder.notEqual(join.get(TRANSLATION_VALUE), "");

        subQuery.where(criteriaBuilder.and(keyIdsAreEquals, translationIsPresent));
        return subQuery;
    }

    private void addApproximatePredicate(KeySearchCriteria criteria, String locale) {
        if (locale != null && criteria.getApprox() != null) {
            Join<Key, Translation> tln = keyRoot.join(TRANSLATIONS, JoinType.LEFT);

            Predicate isLocale = criteriaBuilder.equal(tln.get(ENTITY_ID).get(LOCALE), locale);
            predicates.add(isLocale);

            Predicate isTranslationApproximate = criteriaBuilder.equal(tln.get(APPROXIMATE), criteria.getApprox());
            predicates.add(isTranslationApproximate);
        }
    }

    @Override
    public List<Key> getResultList(Range range) {
        if (!predicates.isEmpty()) {
            selectQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        }

        // Get all the keys with their default translation
        TypedQuery<Key> query = entityManager.createQuery(selectQuery);
        if (range != null) {
            query.setFirstResult((int) range.getOffset());
            query.setMaxResults((int) range.getSize());
        }
        return query.getResultList();
    }

    @Override
    public long getResult() {
        if (!predicates.isEmpty()) {
            countQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        }
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
