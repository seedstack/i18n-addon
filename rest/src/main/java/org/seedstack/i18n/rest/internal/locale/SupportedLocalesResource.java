/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.locale;

import org.seedstack.i18n.rest.internal.I18nPermissions;
import org.seedstack.i18n.rest.internal.shared.WebAssertions;
import org.seedstack.seed.security.RequiresPermissions;

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
 */
@Path("/seed-i18n/locales")
public class SupportedLocalesResource {

    private final SupportedLocaleFinder supportedLocaleFinder;

    @Inject
    public SupportedLocalesResource(SupportedLocaleFinder supportedLocaleFinder) {
        this.supportedLocaleFinder = supportedLocaleFinder;
    }

    /**
     * Returns all the locales supported.
     *
     * @return status code 200 with the locales or 204 if no locale are supported
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions(I18nPermissions.LOCALE_READ)
    public Response getSupportedLocales() {
        List<LocaleRepresentation> locales = supportedLocaleFinder.findSupportedLocales();
        if (!locales.isEmpty()) {
			return Response.ok(locales).build();
		}
        return Response.noContent().build();
    }

    /**
     * Returns the requested locale.
     *
     * @param localeCode locale code
     * @return status code 200 with the locale or 404 if not found
     */
    @GET
    @Path("/{localeCode}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions(I18nPermissions.LOCALE_READ)
    public Response getSupportedLocale(@PathParam("localeCode") String localeCode) {
        WebAssertions.assertNotNull(localeCode, "The locale should not be null");
        LocaleRepresentation locale = supportedLocaleFinder.findSupportedLocale(localeCode);
        if (locale != null) {
			return Response.ok(locale).build();
		}
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
