/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.shared;


import org.seedstack.seed.security.AuthorizationException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Map the AuthorizationException to status code 403 (forbidden).
 */
@Provider
public class AuthorizationExceptionMapper implements ExceptionMapper<AuthorizationException> {

    @Override
    public Response toResponse(AuthorizationException exception) {
        return Response.status(Response.Status.FORBIDDEN).entity("Method forbidden").type(MediaType.TEXT_PLAIN_TYPE).build();
    }
}