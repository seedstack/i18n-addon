/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.messages;

import com.google.common.cache.LoadingCache;
import org.seedstack.i18n.rest.exception.SeedWebCheckUtils;
import org.seedstack.seed.persistence.jpa.api.JpaUnit;
import org.seedstack.seed.transaction.api.Transactional;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * This REST resource provide method to access all the application message.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 17/04/2014
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@Path("/seed-i18n/messages")
public class MessageResource {

    @Inject
    private LoadingCache<String, Map<String, String>> loadingCache;

    /**
     * Returns a map of key, translation for the given locale.
     *
     * @param locale locale identifier
     * @return status code 200 with all application messages or "{}" if no message
     */
    @GET
    @Path("/{locale}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTranslations(@PathParam("locale") String locale) {
        SeedWebCheckUtils.checkIfNotBlank(locale, "The locale should not be blank");
        Map<String, String> messages = loadingCache.getUnchecked(locale);
        if (messages != null && !messages.isEmpty()) {
			return Response.ok(messages).build();
		}
        return Response.ok("{}").build();
    }

}
