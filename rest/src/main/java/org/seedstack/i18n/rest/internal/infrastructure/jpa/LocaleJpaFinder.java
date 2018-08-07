/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.rest.internal.infrastructure.jpa;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.seedstack.business.assembler.Assembler;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.i18n.rest.internal.locale.LocaleFinder;
import org.seedstack.i18n.rest.internal.locale.LocaleRepresentation;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
class LocaleJpaFinder implements LocaleFinder {

    @Inject
    private LocaleRepository localeRepository;

    @Inject
    private Assembler<Locale, LocaleRepresentation> assembler;

    @Override
    public LocaleRepresentation findDefaultLocale() {
        Locale locale = localeRepository.getDefaultLocale();
        if (locale != null) {
            return assembler.createDtoFromAggregate(locale);
        } else {
            return null;
        }
    }

    @Override
    public List<LocaleRepresentation> findAvailableLocales() {
        List<Locale> locales = localeRepository.loadAll();
        List<LocaleRepresentation> localeRepresentations = new ArrayList<>();
        for (Locale locale : locales) {
            localeRepresentations.add(assembler.createDtoFromAggregate(locale));
        }
        return localeRepresentations;
    }

    @Override
    public LocaleRepresentation findAvailableLocale(String localeCode) {
        return localeRepository.get(localeCode).map(assembler::createDtoFromAggregate).orElse(null);
    }
}
