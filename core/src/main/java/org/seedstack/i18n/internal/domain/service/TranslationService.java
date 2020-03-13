/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.service;

import org.seedstack.business.Service;

import java.util.Map;
import java.util.Optional;

/**
 * This service provide access to the translations stored by the application.
 *
 * @author adrien.lauer@mpsa.com
 * @author pierre.thirouin@ext.mpsa.com
 */
@Service
public interface TranslationService {

    /**
     * Get the translation of key in the given locale or its closest parent locale.
     *
     * @param locale the locale
     * @param key    the key to translate
     * @return the translation found or absent
     */
    Optional<String> getTranslationWithFallback(String locale, String key);

    /**
     * Returns all the keys and their translations for the specified locale.
     *
     * @param locale The locale identifier.
     * @return a map of all the keys and their translations or an empty map
     */
    Map<String, String> getTranslationsForLocale(String locale);

    /**
     * Translates a key for a given locale.
     * <p>
     * If the locale is the default locale, set the key and all its translations as outdated.
     * </p>
     *
     * @param key         the key to translate
     * @param locale      the locale
     * @param translation the translation
     */
    void translate(String key, String locale, String translation);
}
