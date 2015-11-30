/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.locale;

import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.i18n.rest.internal.shared.WebChecks;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.security.RequiresPermissions;
import org.seedstack.seed.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This REST resource provide method to access the application default locale.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 26/11/13
 */

@JpaUnit("seed-i18n-domain")
@Transactional
@Path("/seed-i18n/default-locale")
public class DefaultLanguageResource {

    @Inject
    private LocaleFinder localeFinder;

    @Inject
    private LocaleRepository localeRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLanguageResource.class);

    /**
     * @return the default locale.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions("seed:i18n:locale:read")
    public Response getDefaultLocales() {
        LocaleRepresentation defaultLocale = localeFinder.findDefaultLocale();
        if (defaultLocale != null) {
            return Response.ok(defaultLocale).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Changes the default locale
     *
     * @param representation locale representation
     * @param uriInfo        uriInfo
     * @return result
     * @throws java.net.URISyntaxException if URI is not valid
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions("seed:i18n:locale:write")
    public Response changeDefaultLocale(LocaleRepresentation representation, @Context UriInfo uriInfo) throws URISyntaxException {
        WebChecks.checkIfNotNull(representation, "The locale should not be null");
        WebChecks.checkIfNotBlank(representation.getCode(), "The locale code should not be blank");
        try {
            localeRepository.changeDefaultLocaleTo(representation.getCode());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(new URI(uriInfo.getRequestUri() + "/"))
                .entity(localeFinder.findDefaultLocale()).build();
    }
}
