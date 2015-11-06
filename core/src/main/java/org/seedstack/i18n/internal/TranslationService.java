/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal;

import org.seedstack.business.Service;

import java.util.Map;

/**
 * This service provide access to the translations stored by the application.
 *
 * @author adrien.lauer@mpsa.com
 * @author pierre.thirouin@ext.mpsa.com
 */
@Service
public interface TranslationService {

    /**
     * Returns all the keys and their translations for the specified locale.
     * @param locale The locale identifier.
     * @return a map of all the keys and their translations or an empty map
     */
    Map<String, String> getTranslationsForLocale(String locale);
}
