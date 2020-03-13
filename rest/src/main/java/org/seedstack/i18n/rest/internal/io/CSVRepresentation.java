/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.io;


import org.seedstack.business.assembler.MatchingEntityId;
import org.seedstack.business.assembler.MatchingFactoryParameter;

import java.util.Map;

/**
 * Data representation for export.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
public class CSVRepresentation {

    private String key;

    private Map<String, String> value;

    /**
     * Default constructor.
     */
    public CSVRepresentation() {
    }

    /**
     * Gets the locale
     *
     * @return locale code
     */
    @MatchingFactoryParameter(index = 0)
    @MatchingEntityId
    public String getKey() {
        return key;
    }

    /**
     * Sets the locale.
     *
     * @param key locale code
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the association of key and translation
     *
     * @return key/translation map
     */
    public Map<String, String> getValue() {
        return value;
    }

    /**
     * Sets the association of key and translation
     *
     * @param value key/translation map
     */
    public void setValue(Map<String, String> value) {
        this.value = value;
    }
}