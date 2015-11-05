/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.key;


import org.seedstack.business.finder.Finder;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;

import java.util.List;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 21/11/13
 */
@Finder
public interface KeyFinder {

    /**
     * Returns all keys stored in the application
     *
     * @return list of key representations.
     */
    List<KeyRepresentation> findAllKeys();

    /**
     * Returns a key for a specified locale.
     *
     * @param name key name
     * @return a key representation
     */
    KeyRepresentation findKey(String name);

    /**
     * Returns request ranged result of key representations.
     *
     * @param range    range to query
     * @param criteria criteria filters
     * @return paginated key representations
     */
    Result<KeyRepresentation> findAllKeys(Range range, Map<String, Object> criteria);

    /**
     * Finds all keys with criteria.
     *
     * @param criteria criteria
     * @return list of key representation
     */
    List<KeyRepresentation> findAllKeys(Map<String, Object> criteria);
}
