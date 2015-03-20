/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.messages;


import org.seedstack.business.api.interfaces.query.finder.Finder;

import java.util.Map;

/**
 * Message finder.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 14/05/2014
 */
@Finder
public interface MessageFinder {

    /**
     * Finds all the key/translation pair for a locale.
     *
     * @param locale requested locale
     * @return map of key, translation
     */
    Map<String, String> findByLocale(String locale);
}
