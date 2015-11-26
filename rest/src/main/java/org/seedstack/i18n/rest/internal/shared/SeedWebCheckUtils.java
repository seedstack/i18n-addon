/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
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
 *         Date: 26/05/2014
 */
public final class SeedWebCheckUtils {

    private SeedWebCheckUtils() {

    }

    /**
     * Throws a BadRequestException if predicate is false.
     * @param check predicate to check
     * @param message error message
     */
    public static void checkIf(boolean check, String message) {
        if (!check) {
            throw new BadRequestException(message);
        }
    }

    /**
     * Throws a BadRequestException if actual is not null.
     * @param actual object
     * @param message error message
     */
    public static void checkIfNull(Object actual, String message) {
        if (actual != null) {
            throw new BadRequestException(message);
        }
    }

    /**
     * Throws a BadRequestException if actual is null.
     * @param actual object
     * @param message error message
     */
    public static void checkIfNotNull(Object actual, String message) {
        if (actual == null) {
            throw new BadRequestException(message);
        }
    }

    /**
     * Throws a BadRequestException if actual is null.
     * @param actual object
     * @param message error message
     */
    public static void checkIfNotBlank(String actual, String message) {
        if (StringUtils.isBlank(actual)) {
            throw new BadRequestException(message);
        }
    }

    /**
     * Throws a NotFoundException if actual is null.
     * @param actual object
     * @param message error message
     */
    public static void checkNotFound(Object actual, String message) {
        if (actual == null) {
            throw new NotFoundException(message);
        }
    }

    /**
     * Throws a NotFoundException if actual is null.
     * @param actual object
     * @param message error message
     */
    public static void checkNotFound(String actual, String message) {
        if (StringUtils.isNotBlank(actual)) {
            throw new NotFoundException(message);
        }
    }
}
