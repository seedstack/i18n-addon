/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.messages;

import org.apache.commons.lang.StringUtils;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.service.TranslationService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * This REST resource provide method to access all the application message.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@Path("/seed-i18n/messages")
public class MessageResource {

    @Inject
    private TranslationService messageService;

    @Inject
    private LocaleService localeService;

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
        Map<String, String> messages = getMessages(locale);
        if (!messages.isEmpty()) {
			return Response.ok(messages).build();
		}
        return Response.ok("{}").build();
    }

    public Map<String, String> getMessages(String locale) {
        // If the default locale is not available, then the application is not configured
        if (StringUtils.isNotBlank(localeService.getDefaultLocale())) {
            return messageService.getTranslationsForLocale(locale);
        } else {
            return new HashMap<>();
        }
    }

}
