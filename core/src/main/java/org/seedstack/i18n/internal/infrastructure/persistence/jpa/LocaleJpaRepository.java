/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.persistence.jpa;

import com.google.common.base.Preconditions;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.seedstack.business.jpa.BaseJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 13/05/2014
 */
public class LocaleJpaRepository extends BaseJpaRepository<Locale, String> implements LocaleRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocaleJpaRepository.class);

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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Locale> q = cb.createQuery(Locale.class);
        Root<Locale> l = q.from(Locale.class);
        q.where(cb.equal(l.get("defaultLocale"), true));
        try {
            return entityManager.createQuery(q.select(l)).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Sets the given locale as default locale. If a locale was already the default reinitialize this locale.
     *
     * @param locale new default locale
     * @throws java.lang.IllegalArgumentException if the given locale parameter is blank
     * @throws java.lang.NullPointerException     if the new default locale is not an available locale
     */
    @Override
    public void changeDefaultLocaleTo(String locale) {
        Preconditions.checkArgument(StringUtils.isNotBlank(locale));
        Locale newDefault = load(locale.replace("-", "_"));
        Validate.notNull(newDefault, "The locale " + locale + " is not available.");
        Locale oldDefault = getDefaultLocale();
        if (oldDefault != null) {
            oldDefault.setDefaultLocale(false);
            save(oldDefault);
        }

        newDefault.setDefaultLocale(true);
        save(newDefault);
    }

    @Override
    public void persistAll(List<Locale> locales) {
        for (Locale locale : locales) {
            persist(locale);
        }
    }

    @Override
    public void clear() {
        entityManager.createQuery("DELETE FROM Locale k");
    }

}
