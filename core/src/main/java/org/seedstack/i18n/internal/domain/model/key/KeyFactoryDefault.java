/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.internal.domain.model.key;

import org.seedstack.business.domain.BaseFactory;

/**
 * Key factory implementation.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
public class KeyFactoryDefault extends BaseFactory<Key> implements KeyFactory {

    @Override
    public Key createKey(String name) {
        return new Key(name);
    }
}
