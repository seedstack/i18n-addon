/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Web exception which sends an HTTP error code 409 (conflict) with a message.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 07/02/14
 */
public class AlreadyExistException extends WebApplicationException {

    private static final long serialVersionUID = -4568375759076851959L;

    /**
     * AlreadyExistException constructor.
     *
     * @param message functional message indicating the request error
     */
    public AlreadyExistException(String message) {
        super(Response.status(Response.Status.CONFLICT).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
