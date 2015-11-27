/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;


import com.ibm.icu.util.LocaleMatcher;
import com.ibm.icu.util.LocalePriorityList;
import com.ibm.icu.util.ULocale;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleFactory;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * Locale service implementation.
 */
@JpaUnit("seed-i18n-domain")
@Transactional
public class ICULocaleService implements LocaleService {

    private static final String LOCALE_MUST_NOT_BE_NULL = "locale must not be null";

    private LocaleRepository localeRepository;

    private LocaleFactory localeFactory;

    /**
     * Constructor.
     *
     * @param localeRepository locale repository
     * @param localeFactory    locale factory
     */
    @Inject
    public ICULocaleService(LocaleRepository localeRepository, LocaleFactory localeFactory) {
        this.localeRepository = localeRepository;
        this.localeFactory = localeFactory;
    }

    @Override
    public boolean isAvailable(String localeCode) {
        return localeCode != null && localeRepository.load(localeCode) != null;
    }

    @Override
    public Set<String> getAvailableLocales() {
        Set<String> result = new HashSet<String>();
        for (Locale locale : localeRepository.loadAll()) {
            result.add(locale.getEntityId());
        }
        return result;
    }

    @Override
    public Set<String> getSupportedLocales() {
        Set<String> supportedLocales = new HashSet<String>();
        java.util.Locale[] locales = java.util.Locale.getAvailableLocales();
        for (java.util.Locale locale : locales) {
            supportedLocales.add(localeFactory.createFromLocale(locale).getEntityId());
        }
        return supportedLocales;
    }

    @Override
    public String getDefaultLocale() {
        Locale defaultLocale = localeRepository.getDefaultLocale();
        if (defaultLocale != null) {
            return defaultLocale.getEntityId();
        } else {
            return null;
        }
    }

    @Override
    public void changeDefaultLocaleTo(String locale) {
        localeRepository.changeDefaultLocaleTo(locale);
    }

    @Override
    public void addLocale(String locale) {
        checkIsNotEmpty(locale);
        if (localeRepository.load(locale) == null) {
            Locale newLocale = localeFactory.createFromCode(locale);
            localeRepository.persist(newLocale);
        }
    }

    private void checkIsNotEmpty(String locale) {
        if (locale == null || locale.equals("")) {
            throw new IllegalArgumentException(LOCALE_MUST_NOT_BE_NULL);
        }
    }

    @Override
    public void deleteLocale(String locale) {
        checkIsNotEmpty(locale);
        Locale localeToDelete = localeRepository.load(locale);
        if (localeToDelete != null) {
            localeRepository.delete(localeToDelete);
        }
    }

    @Override
    public String getClosestLocale(String locale) {
        ULocale closestULocale = getClosestULocale(locale);
        if (closestULocale != null) {
            return closestULocale.toLanguageTag();
        } else {
            return null;
        }
    }

    private ULocale getClosestULocale(String locale) {
        String defaultLocale = getDefaultLocale();

        if (isAvailable(locale)) {
            return new ULocale(locale);
        }
        LocalePriorityList.Builder builder = null;
        if (defaultLocale != null) {
            builder = LocalePriorityList.add(defaultLocale);
        }
        for (String availableLocale : getAvailableLocales()) {
            if (defaultLocale == null || !defaultLocale.equals(availableLocale)) {
                if (builder == null) {
                    builder = LocalePriorityList.add(availableLocale);
                }
                builder.add(availableLocale);
            }
        }
        if (builder != null) {
            LocaleMatcher localeMatcher = new LocaleMatcher(builder.build());
            return localeMatcher.getBestMatch(locale);
        } else {
            return null;
        }
    }
}
