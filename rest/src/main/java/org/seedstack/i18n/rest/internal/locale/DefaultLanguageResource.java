/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.locale;

import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.rest.internal.I18nPermissions;
import org.seedstack.i18n.rest.internal.shared.WebAssertions;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.Logging;
import org.seedstack.seed.security.RequiresPermissions;
import org.seedstack.seed.transaction.Transactional;
import org.slf4j.Logger;

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
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@Path("/seed-i18n/default-locale")
public class DefaultLanguageResource {

    @Logging
    private Logger logger;

    @Context
    private UriInfo uriInfo;

    private final LocaleFinder localeFinder;
    private final LocaleService localeService;

    @Inject
    public DefaultLanguageResource(LocaleFinder localeFinder, LocaleService localeService) {
        this.localeFinder = localeFinder;
        this.localeService = localeService;
    }

    /**
     * @return the default locale.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions(I18nPermissions.LOCALE_READ)
    public Response getDefaultLocale() {
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
     * @return result
     * @throws java.net.URISyntaxException if URI is not valid
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions(I18nPermissions.LOCALE_WRITE)
    public Response changeDefaultLocale(LocaleRepresentation representation) throws URISyntaxException {
        WebAssertions.assertNotNull(representation, "The locale should not be null");
        WebAssertions.assertNotBlank(representation.getCode(), "The locale code should not be blank");
        try {
            localeService.changeDefaultLocaleTo(representation.getCode());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(new URI(uriInfo.getRequestUri().toString()))
                .entity(localeFinder.findDefaultLocale()).build();
    }
}
