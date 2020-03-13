/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.jpa;

import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.jpa.BaseJpaRepository;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
class LocaleJpaRepository extends BaseJpaRepository<Locale, String> implements LocaleRepository {

    @Override
    public List<Locale> loadAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Locale> q = cb.createQuery(Locale.class);
        return getEntityManager().createQuery(q.select(q.from(Locale.class))).getResultList();
    }

    @Override
    public Locale getDefaultLocale() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Locale> query = criteriaBuilder.createQuery(Locale.class);
        Root<Locale> localeRoot = query.from(Locale.class);
        query.where(criteriaBuilder.equal(localeRoot.get("defaultLocale"), true));
        try {
            return getEntityManager().createQuery(query.select(localeRoot)).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void changeDefaultLocaleTo(String locale) {
        if (locale == null || locale.equals("")) {
            throw new IllegalArgumentException("The default locale cannot be null or empty.");
        }
        Locale newDefault = get(locale).orElseThrow(() -> new IllegalArgumentException(
                "New default locale (" + locale + ") is not available."));
        Locale oldDefault = getDefaultLocale();

        // If the default locale is the same do nothing
        if (newDefault != oldDefault) {
            // otherwise reinitialize the previous one
            if (oldDefault != null) {
                oldDefault.setDefaultLocale(false);
                update(oldDefault);
            }

            newDefault.setDefaultLocale(true);
            update(newDefault);
        }
    }
}
