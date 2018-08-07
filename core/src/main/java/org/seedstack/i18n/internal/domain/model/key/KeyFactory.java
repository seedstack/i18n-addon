/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.internal.domain.model.key;

import org.seedstack.business.domain.Factory;

/**
 * Interface of Key factory.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 20/11/13
 */
public interface KeyFactory extends Factory<Key> {

    /**
     * Create a Key.
     *
     * @param name key name
     * @return Key
     */
    Key createKey(String name);

}
