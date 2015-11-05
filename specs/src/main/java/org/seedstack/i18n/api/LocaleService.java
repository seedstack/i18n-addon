/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.api;

import org.seedstack.business.Service;

import java.util.Set;

/**
 * Repository for locales. A locale is just a String identifying a region. Some
 * examples include fr-FR, en-GB, en-US-pouet, zn, any string you want to use.
 * 
 * @author yves.dautremay@mpsa.com
 */
@Service
public interface LocaleService {

	/**
	 * Tells if the given locale is available in this repository
	 * 
	 * @param localeCode
	 *            the locale to test. returns false if null.
	 * @return true is the locale is in the repository, false otherwise.
	 */
	boolean isAvailable(String localeCode);

	/**
	 * Gives all the locales defined in the reposiroty
	 * 
	 * @return a set with all the locales. An empty set if no locales are
	 *         defined.
	 */
	Set<String> getAvailableLocales();

	/**
	 * Default locale of the application.
	 * 
	 * @return The locale defined as default in the repository
	 */
	String getDefaultLocale();

    /**
     * Get the closest locale supported by the application
     *
     * @param locale The locale to match.
     * @return The supported locale.
     */
    String getClosestLocale(String locale);

	/**
	 * Changes the current default locale to the given one.
	 * 
	 * @param locale
	 *            the locale to define as default. Throws
	 *            IllegalArgumentException if null.
	 */
	void changeDefaultLocaleTo(String locale);

	/**
	 * Adds a locale to the repository. Does nothing if locale already exists.
	 * 
	 * @param locale
	 *            the locale to add. Throws IllegalArgumentException if null.
	 */
	void addLocale(String locale);

	/**
	 * Deletes a locale from the repository. Does nothing if the given locale
	 * does not exist in the repository.
	 * 
	 * @param locale
	 *            the locale to delete. Throws IllegalArgumentException if null.
	 */
	void deleteLocale(String locale);
}
