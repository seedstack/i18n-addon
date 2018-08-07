/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.internal.domain.model.locale;

import com.ibm.icu.util.ULocale;
import org.seedstack.business.domain.Factory;

/**
 * Locale Factory interface.
 */
public interface LocaleFactory extends Factory<Locale> {

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

    /**
     * Creates a locale based on a ICU ULocale.
     *
     * @param locale the ICU ULocale object.
     * @return the locale
     */
    Locale createFromULocale(ULocale locale);
}
