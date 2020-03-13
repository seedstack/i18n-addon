/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n;

import org.seedstack.business.Service;

import java.util.Set;

/**
 * This service allows to manage the application's locales.
 *
 * A locale is just a String identifying a region. Some
 * examples include fr-FR, en-GB, en-US-pouet, zn, any string you want to use.
 *
 * @author yves.dautremay@mpsa.com
 */
@Service
public interface LocaleService {

    /**
     * Indicates whether the locale is available. Returns false if the given code is null.
     *
     * @param localeCode the locale code, e.g. "fr-BE"
     * @return true is the locale exists, false otherwise.
     */
    boolean isAvailable(String localeCode);

    /**
     * Returns all the available locales for the application.
     *
     * @return a set with all the locales.
     */
    Set<String> getAvailableLocales();

    /**
     * Returns all the locales supported by the platform.
     *
     * @return a set with the supported locales.
     */
    Set<String> getSupportedLocales();

    /**
     * Returns the application's default locale.
     *
     * @return The default locale or null if there is no default locale
     */
    String getDefaultLocale();

    /**
     * Returns the closest locale from the given locale which is supported by the application.
     *
     * @param locale The locale to match.
     * @return The supported locale or null if there is no close locale
     */
    String getClosestLocale(String locale);

    /**
     * Changes the current default locale.
     *
     * @param locale the locale to define as default.
     * @throws IllegalArgumentException if the locale is null.
     */
    void changeDefaultLocaleTo(String locale);

    /**
     * Adds a new available locale.
     * <p>
     * Does nothing if locale already exists.
     * </p>
     *
     * @param locale the locale to add.
     * @throws IllegalArgumentException if the locale argument is null.
     */
    void addLocale(String locale);

    /**
     * Deletes an available locale.
     * <p>
     * Does nothing if the given locale does not exist.
     * </p>
     *
     * @param locale the locale to delete.
     * @throws IllegalArgumentException if the locale argument is null.
     */
    void deleteLocale(String locale);
}
