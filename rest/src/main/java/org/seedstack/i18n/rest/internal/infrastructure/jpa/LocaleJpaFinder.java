/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.jpa;

import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.i18n.rest.internal.locale.LocaleAssembler;
import org.seedstack.i18n.rest.internal.locale.LocaleFinder;
import org.seedstack.i18n.rest.internal.locale.LocaleRepresentation;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
class LocaleJpaFinder implements LocaleFinder {

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
}
