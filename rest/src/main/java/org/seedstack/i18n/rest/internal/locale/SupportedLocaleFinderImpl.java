/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.locale;

import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleFactory;
import org.seedstack.seed.Configuration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Finds the locales available on the platform.
 *
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
class SupportedLocaleFinderImpl implements SupportedLocaleFinder {

    private final LocaleFactory localeFactory;
    private final LocaleAssembler localeAssembler;

    @Configuration(value = "org.seedstack.i18n.additional-locales", mandatory = false)
    private String[] additionalLocaleCodes;

    @Inject
    public SupportedLocaleFinderImpl(LocaleFactory localeFactory, LocaleAssembler localeAssembler) {
        this.localeFactory = localeFactory;
        this.localeAssembler = localeAssembler;
    }

    @Override
    public List<LocaleRepresentation> findSupportedLocales() {
        List<LocaleRepresentation> localeRepresentations = new ArrayList<>();
        for (java.util.Locale jLocale : java.util.Locale.getAvailableLocales()) {
            if (!jLocale.toString().equals("")) {
                localeRepresentations.add(convertToLocaleRepresentation(jLocale));
            }
        }
        localeRepresentations.addAll(findAdditionalLocale());
        Collections.sort(localeRepresentations);
        return localeRepresentations;
    }

    private LocaleRepresentation convertToLocaleRepresentation(java.util.Locale jLocale) {
        Locale locale = localeFactory.createFromLocale(jLocale);
        return localeAssembler.assembleDtoFromAggregate(locale);
    }

    private List<LocaleRepresentation> findAdditionalLocale() {
        List<LocaleRepresentation> localeRepresentations = new ArrayList<>();
        if (additionalLocaleCodes != null) {
            for (String additionalLocaleCode : additionalLocaleCodes) {
                Locale locale = localeFactory.createFromCode(additionalLocaleCode);
                localeRepresentations.add(localeAssembler.assembleDtoFromAggregate(locale));
            }
        }
        return localeRepresentations;
    }

    @Override
    public LocaleRepresentation findSupportedLocale(String localeCode) {
        for (LocaleRepresentation localeRepresentation : findSupportedLocales()) {
            if (localeRepresentation.getCode().equals(localeCode)) {
                return localeRepresentation;
            }
        }
        return null;
    }
}
