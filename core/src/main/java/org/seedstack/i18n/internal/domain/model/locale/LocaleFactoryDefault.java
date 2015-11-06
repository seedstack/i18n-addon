/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
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
public class LocaleFactoryDefault extends BaseFactory<Locale> implements LocaleFactory {

    @Override
    public Locale create(String localeId) {
        ULocale uLocale = new ULocale(localeId);
        return create(uLocale.getBaseName(), uLocale.getDisplayNameWithDialect(uLocale), uLocale.getDisplayNameWithDialect(ULocale.ENGLISH));
    }

    @Override
    public Locale create(String localeId, String language, String englishLanguage) {
        Locale locale = new Locale(localeId);
        locale.setLanguage(language);
        locale.setEnglishLanguage(englishLanguage);
        return locale;
    }

    @Override
    public Locale create(String code, String language, String englishLanguage, boolean defaultLocale) {
        return new Locale(code, language, englishLanguage, defaultLocale);
    }
}
