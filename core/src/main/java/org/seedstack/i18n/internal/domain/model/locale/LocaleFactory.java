/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.locale;


import org.seedstack.business.domain.GenericFactory;

/**
 * Locale Factory interface.
 */
public interface LocaleFactory extends GenericFactory<Locale> {

    /**
     * Creates a locale from a locale code, e.g. fr-BE for French (Belgium).
     *
     * @param localeCode locale code
     * @return the locale
     */
    Locale createFromCode(String localeCode);

    /**
     * Creates a locale based on the language code, e.g. "fr" for French.
     *
     * @param language language identifier
     * @return the locale
     */
    Locale createFromLanguage(String language);

    /**
     * Creates a locale based on the language and the region, e.g. "fr" and "BE" for French (Belgium).
     *
     * @param language language identifier
     * @param region   region identifier
     * @return the locale
     */
    Locale createFromLanguageAndRegion(String language, String region);

    Locale createFromLocale(java.util.Locale locale);
}
