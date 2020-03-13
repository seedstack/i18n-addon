/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
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
 * Web exception which sends an HTTP error code 404 (not found) with a message.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 07/02/14
 */
public class NotFoundException  extends WebApplicationException {

    private static final long serialVersionUID = -3016921984877824548L;

    /**
     * NotFoundException constructor.
     *
     * @param message functional message indicating the request error
     */
    public NotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
