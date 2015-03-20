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

import com.ibm.icu.util.ULocale;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.i18n.rest.locale.LocaleAssembler;
import org.seedstack.i18n.rest.locale.LocaleFinder;
import org.seedstack.i18n.rest.locale.LocaleRepresentation;
import jodd.util.collection.SortedArrayList;
import org.seedstack.seed.persistence.jpa.api.JpaUnit;
import org.seedstack.seed.transaction.api.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
* @author pierre.thirouin@ext.mpsa.com
* Date: 26/11/13
*/
@JpaUnit("seed-i18n-domain")
@Transactional
public class LocaleJpaFinder implements LocaleFinder {

    @Inject
    private LocaleRepository localeRepository;

    @Inject
    private LocaleAssembler assembler;

    @Override
    public LocaleRepresentation findDefaultLocale() {
        Locale locale = localeRepository.getDefaultLocale();
        if (locale != null) {
            return assembler.assembleDtoFromAggregate(locale);
        } else {
            return null;
        }
    }

    @Override
    public List<LocaleRepresentation> findAvailableLocales() {
        List<Locale> locales = localeRepository.loadAll();
        List<LocaleRepresentation> localeRepresentations = new ArrayList<LocaleRepresentation>();
        for (Locale locale : locales) {
            localeRepresentations.add(assembler.assembleDtoFromAggregate(locale));
        }
        return localeRepresentations;
    }

    @Override
    public LocaleRepresentation findAvailableLocale(String localeCode) {
        Locale locale = localeRepository.load(localeCode);
        if (locale != null) {
			return assembler.assembleDtoFromAggregate(locale);
		}
        return null;
    }

    @Override
    public List<LocaleRepresentation> findLocalesFullVersion() {
        List<LocaleRepresentation> localeRepresentations = new SortedArrayList<LocaleRepresentation>();
        ULocale[] locales = ULocale.getAvailableLocales();
        for (ULocale locale : locales) {
            localeRepresentations.add(assembleLocaleRepresentationFromLocale(locale));
        }
        return localeRepresentations;
    }

    @Override
    public List<LocaleRepresentation> findLocales() {
        List<LocaleRepresentation> localeRepresentations = new SortedArrayList<LocaleRepresentation>();
        java.util.Locale[] locales = java.util.Locale.getAvailableLocales();
        for (java.util.Locale locale : locales) {
            localeRepresentations.add(assembleLocaleRepresentationFromLocale(locale));
        }
        return localeRepresentations;
    }

    @Override
    public LocaleRepresentation findLocale(String localeCode) {
        ULocale[] locales = ULocale.getAvailableLocales();
        for (ULocale locale : locales) {
            if (locale.toString().equals(localeCode)) {
                return assembleLocaleRepresentationFromLocale(locale);
            }
        }
        return null;
    }

    /**
     * Assemble a locale from an ULocale (from ICU library).
     *
     * @param locale ULocale
     * @return locale
     */
    LocaleRepresentation assembleLocaleRepresentationFromLocale(ULocale locale) {
        LocaleRepresentation representation = new LocaleRepresentation();
        representation.setCode(locale.getBaseName());
        representation.setLanguage(locale.getDisplayNameWithDialect(locale));
        representation.setEnglishLanguage(locale.getDisplayNameWithDialect(ULocale.ENGLISH));
        return representation;
    }

    /**
     * Assemble a locale from an java.util.Locale.
     *
     * @param locale locale
     * @return locale representation
     */
    LocaleRepresentation assembleLocaleRepresentationFromLocale(java.util.Locale locale) {
        LocaleRepresentation representation = new LocaleRepresentation();
        representation.setCode(locale.toString());
        representation.setLanguage(locale.getDisplayName(locale));
        representation.setEnglishLanguage(locale.getDisplayName(java.util.Locale.ENGLISH));
        return representation;
    }
}
