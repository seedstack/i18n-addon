/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.locale;

import org.seedstack.i18n.rest.exception.SeedWebCheckUtils;
import org.seedstack.seed.persistence.jpa.api.JpaUnit;
import org.seedstack.seed.security.api.annotations.RequiresPermissions;
import org.seedstack.seed.transaction.api.Transactional;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * This REST resource provide access to applications locales.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 26/11/13
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@Path("/seed-i18n/locales")
public class LocalesResource {

    @Inject
    private LocaleFinder localeFinder;

    /**
     * Returns all possible locale.
     *
     * @return status code 200 with list of locale representation
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions("seed:i18n:locale:read")
    public Response getLocales() {
        List<LocaleRepresentation> locales = localeFinder.findLocales();
        if (!locales.isEmpty()) {
			return Response.ok(locales).build();
		}
        return Response.noContent().build();
    }

    /**
     * Returns the requested locale.
     *
     * @param localeId locale code
     * @return status code 200 with the locale representation or 404 if not found
     */
    @GET
    @Path("/{localeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions("seed:i18n:locale:read")
    public Response getLocale(@PathParam("localeId") String localeId) {
        SeedWebCheckUtils.checkIfNotNull(localeId, "The locale should not be null");
        LocaleRepresentation locale = localeFinder.findLocale(localeId);
        if (locale != null) {
			return Response.ok(locale).build();
		}
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
