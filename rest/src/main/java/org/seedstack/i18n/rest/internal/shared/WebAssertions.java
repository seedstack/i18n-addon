/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.shared;

import org.apache.commons.lang.StringUtils;

/**
 * Class with various web check utilities.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
public final class WebAssertions {

    private WebAssertions() {

    }

    /**
     * Throws a BadRequestException if predicate is false.
     *
     * @param check predicate to check
     * @param message error message
     */
    public static void assertIf(boolean check, String message) {
        if (!check) {
            throw new BadRequestException(message);
        }
    }

    /**
     * Throws a BadRequestException if actual is null.
     *
     * @param actual object
     * @param message error message
     */
    public static void assertNotNull(Object actual, String message) {
        if (actual == null) {
            throw new BadRequestException(message);
        }
    }

    /**
     * Throws a BadRequestException if actual is null.
     *
     * @param actual object
     * @param message error message
     */
    public static void assertNotBlank(String actual, String message) {
        if (StringUtils.isBlank(actual)) {
            throw new BadRequestException(message);
        }
    }
}
