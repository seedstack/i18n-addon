/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.shared;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Web exception which sends an HTTP error code 400 with a message.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 26/05/2014
 */
public class BadRequestException extends WebApplicationException {

    private static final long serialVersionUID = -4568375759076851959L;

    /**
     * BadRequestException constructor.
     * Send to the client an HTTP error code 400 with a message.
     *
     * @param message functional message indicating the request error
     */
    public BadRequestException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
