/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.key;


import org.seedstack.business.domain.GenericRepository;

import java.util.List;

/**
 * Stores application keys and translations.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
public interface KeyRepository extends GenericRepository<Key, String> {

    /**
     * Gets all keys.
     *
     * @return all keys
     */
    List<Key> loadAll();

    /**
     * Get the number of key.
     *
     * @return key count
     */
    Long count();

    /**
     * Saves all the given keys. It uses staging to reduce IO calls. Should be used for massive update.
     *
     * @param keys keys to persist
     */
    @Deprecated
    void persistAll(List<Key> keys);

    /**
     * Fast delete of all keys.
     */
    void deleteAll();

    /**
     * Deletes all the given keys. It reduces IO calls. Should be used for massive delete.
     *
     * @param keys keys to delete
     */
    void delete(List<Key> keys);

}
