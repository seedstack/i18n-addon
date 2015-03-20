/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.key;

import org.javatuples.Triplet;
import org.seedstack.business.api.domain.GenericFactory;

import java.util.Map;

/**
 * Interface of Key factory.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 20/11/13
 */
public interface KeyFactory extends GenericFactory<Key> {

    /**
     * Create a Key.
     *
     * @param name key name
     * @return Key
     */
    Key createKey(String name);

    /**
     * Create a Key
     *
     * @param name     key name
     * @param comment  key description
     * @param outdated is key outdated
     * @return key
     */
    Key createKey(String name, String comment, boolean outdated);

    /**
     * Create a Key
     *
     * @param name         key name
     * @param comment      key description
     * @param translations key translations
     * @return key
     */
    Key createKey(String name, String comment, Map<String, Triplet<String, Boolean, Boolean>> translations);
}
