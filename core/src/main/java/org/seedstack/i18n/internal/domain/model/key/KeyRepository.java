/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.internal.domain.model.key;

import java.util.List;
import org.seedstack.business.domain.Repository;

/**
 * Stores application keys and translations.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
public interface KeyRepository extends Repository<Key, String> {
    /**
     * Gets all keys.
     *
     * @return all keys
     */
    List<Key> loadAll();

    /**
     * Deletes all the given keys. It reduces IO calls. Should be used for massive delete.
     *
     * @param keys keys to delete
     */
    void delete(List<Key> keys);
}
