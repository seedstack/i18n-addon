/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.io;

import org.seedstack.business.Service;

import java.io.InputStream;

/**
 * A service which allows to import internationalization data.
 *
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@Service
public interface ImportService {

    /**
     * Imports a list of keys with their translations.
     *
     * @param inputStream the data to import
     * @return the number of keys imported
     */
    int importKeysWithTranslations(InputStream inputStream);
}
