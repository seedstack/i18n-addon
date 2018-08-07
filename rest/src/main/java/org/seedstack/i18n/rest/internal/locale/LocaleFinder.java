/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.locale;


import org.seedstack.business.finder.Finder;

import java.util.List;

/**
 * This finder provide methods to find locales needed by the application.
 *
 * @author pierre.thirouin@ext.mpsa.com
 * Date: 26/11/13
 */
@Finder
public interface LocaleFinder {

    /**
     * Returns the default locale of the application
     * @return a LocaleRepresentation
     */
    LocaleRepresentation findDefaultLocale();

    /**
     * Returns the list of all available locales in the application.
     * @return a list of all available locales
     */
    List<LocaleRepresentation> findAvailableLocales();

    /**
     * Returns the locale corresponding to the specified locale code.
     *
     * @param localeCode the specified locale code
     * @return a representation of the requested locale
     */
    LocaleRepresentation findAvailableLocale(String localeCode);

}
