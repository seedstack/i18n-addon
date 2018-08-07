/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.locale;

import com.ibm.icu.util.ULocale;
import org.seedstack.business.domain.BaseFactory;

/**
 * Locale factory implementation.
 */
class LocaleFactoryDefault extends BaseFactory<Locale> implements LocaleFactory {

    @Override
    public Locale createFromLanguage(String language) {
        return createFromULocale(new ULocale(language));
    }

    @Override
    public Locale createFromLanguageAndRegion(String language, String region) {
        return createFromULocale(new ULocale(language, region));
    }

    @Override
    public Locale createFromCode(String localeCode) {
        return this.createFromULocale(ULocale.forLanguageTag(localeCode));
    }

    @Override
    public Locale createFromULocale(ULocale locale) {
        String normalizedLocaleCode = locale.toLanguageTag();
        String nativeLanguageName = locale.getDisplayName(locale);
        String englishLanguageName = locale.getDisplayName(ULocale.ENGLISH);
        return new Locale(normalizedLocaleCode, nativeLanguageName, englishLanguageName);
    }
}
