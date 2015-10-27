/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.locale;


import org.seedstack.business.api.domain.GenericFactory;

/**
 * Locale Factory interface.
 */
public interface LocaleFactory extends GenericFactory<Locale> {

    /**
     * Create a locale.
     *
     * @param localeId locale identifier
     * @return locale
     */
    Locale create(String localeId);

    /**
     * Create a locale.
     *
     * @param localeId        locale identifier
     * @param language        language
     * @param englishLanguage language english name
     * @return locale
     */
    Locale create(String localeId, String language, String englishLanguage);

    /**
     * Create a locale.
     *
     * @param code            code
     * @param language        language
     * @param englishLanguage language english name
     * @param defaultLocale   default locale
     * @return locale
     */
    Locale create(String code, String language, String englishLanguage, boolean defaultLocale);
}
