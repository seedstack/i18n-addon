/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.jpa;

import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.jpa.BaseJpaRepository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class LocaleJpaRepository extends BaseJpaRepository<Locale, String> implements LocaleRepository {

    @Override
    public List<Locale> loadAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Locale> q = cb.createQuery(Locale.class);
        return entityManager.createQuery(q.select(q.from(Locale.class))).getResultList();
    }

    @Override
    public Long count() {
        return (Long) entityManager.createQuery("SELECT count(*) FROM Locale k").getSingleResult();
    }

    @Override
    public Locale getDefaultLocale() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Locale> query = criteriaBuilder.createQuery(Locale.class);
        Root<Locale> localeRoot = query.from(Locale.class);
        query.where(criteriaBuilder.equal(localeRoot.get("defaultLocale"), true));
        try {
            return entityManager.createQuery(query.select(localeRoot)).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void changeDefaultLocaleTo(String locale) {
        if (locale == null || locale.equals("")) {
            throw new IllegalArgumentException("The default locale cannot be null or empty.");
        }
        Locale newDefault = load(locale);
        if (newDefault == null) {
            throw new IllegalArgumentException("Enable to change the default locale. The locale " + locale + " is not available.");
        }
        Locale oldDefault = getDefaultLocale();

        // If the default locale is the same do nothing
        if (newDefault != oldDefault) {
            // otherwise reinitialize the previous one
            if (oldDefault != null) {
                oldDefault.setDefaultLocale(false);
                save(oldDefault);
            }

            newDefault.setDefaultLocale(true);
            save(newDefault);
        }
    }

    @Override
    public void clear() {
        entityManager.createQuery("DELETE FROM Locale k");
    }

}
