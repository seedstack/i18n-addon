/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.shared;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 04/12/13
 */
public final class BooleanUtils {

    /**
     * Private constructor.
     */
    private BooleanUtils() {
    }

    /**
     * If bool is false change it to null, otherwise do nothing.
     *
     * @param bool boolean
     * @return true if bool is true, null otherwise
     */
    @SuppressFBWarnings(value = "NP_BOOLEAN_RETURN_NULL", justification = "That's the point (but dubious)")
    public static Boolean falseToNull(Boolean bool) {
        if (bool != null && bool) {
            return true;
        }
        return null;
    }
}
