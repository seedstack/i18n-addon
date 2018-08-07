/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.rest.internal.key;

import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.I18nPermissions;
import org.seedstack.i18n.rest.internal.shared.NotFoundException;
import org.seedstack.i18n.rest.internal.shared.WebAssertions;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.security.RequiresPermissions;
import org.seedstack.seed.transaction.Transactional;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@Path("/seed-i18n/keys/{key}")
public class KeyResource {

    private static final String THE_KEY_SHOULD_NOT_BE_NULL = "The key should not be null";
    private static final String THE_KEY_SHOULD_CONTAINS_A_LOCALE = "The key should contains a locale.";
    public static final String KEY_NOT_FOUND = "The key %s didn't exists";

    private final KeyFinder keyFinder;
    private final KeyRepository keyRepository;

    @PathParam("key")
    private String keyName;

    @Context
    private UriInfo uriInfo;

    @Inject
    public KeyResource(KeyFinder keyFinder, KeyRepository keyRepository) {
        this.keyFinder = keyFinder;
        this.keyRepository = keyRepository;
    }

    /**
     * Returns a key with the default translation.
     *
     * @return translated key
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions(I18nPermissions.KEY_READ)
    public KeyRepresentation getKey() {
        KeyRepresentation keyRepresentation = keyFinder.findKeyWithName(keyName);
        if (keyRepresentation == null) {
            throw new NotFoundException(String.format(KEY_NOT_FOUND, keyName));
        }
        return keyRepresentation;
    }

    /**
     * Updates a key with the translation in the default language.
     *
     * @param representation key representation
     * @return key representation
     * @throws URISyntaxException if the URI is not valid.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions(I18nPermissions.KEY_WRITE)
    public Response updateKey(KeyRepresentation representation) throws URISyntaxException {
        WebAssertions.assertNotNull(representation, THE_KEY_SHOULD_NOT_BE_NULL);
        WebAssertions.assertNotBlank(representation.getDefaultLocale(), THE_KEY_SHOULD_CONTAINS_A_LOCALE);

        Key key = loadKeyIfExistsOrFail();
        key.setComment(representation.getComment());
        key.setOutdated();
        // Updates the default translation
        key.addTranslation(representation.getDefaultLocale(),
                representation.getTranslation(),
                representation.isApprox());

        keyRepository.update(key);

        return Response.ok(new URI(uriInfo.getRequestUri() + "/" + keyName))
                .entity(keyFinder.findKeyWithName(keyName)).build();
    }

    /**
     * Deletes a key.
     *
     * @return http code 204 (no content) or 404 if the key is not found
     */
    @DELETE
    @RequiresPermissions(I18nPermissions.KEY_DELETE)
    public Response deleteKey() {
        Key key = loadKeyIfExistsOrFail();
        keyRepository.remove(key);
        return Response.noContent().build();
    }

    private Key loadKeyIfExistsOrFail() {
        return keyRepository.get(keyName)
                .orElseThrow(() -> new NotFoundException(String.format(KEY_NOT_FOUND, keyName)));
    }
}
