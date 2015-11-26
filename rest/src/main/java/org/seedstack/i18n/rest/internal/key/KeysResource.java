/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.key;

import org.apache.commons.lang.StringUtils;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.shared.BooleanUtils;
import org.seedstack.i18n.rest.internal.shared.SeedWebCheckUtils;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.security.RequiresPermissions;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST resources exposing keys for administration.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 21/11/13
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@Path("/seed-i18n/keys")
public class KeysResource {

    private static final String PAGE_INDEX = "pageIndex";
    private static final String PAGE_SIZE = "pageSize";
    private static final String IS_APPROX = "isApprox";
    private static final String SEARCH_NAME = "searchName";
    private static final String IS_MISSING = "isMissing";
    private static final String IS_OUTDATED = "isOutdated";
    private static final String KEY = "key";
    private static final String KEY_NAME_SHOULD_NOT_BE_BLANK = "Key name should not be blank";
    private static final String THE_KEY_SHOULD_NOT_BE_NULL = "The key should not be null";
    private static final String THE_KEY_SHOULD_CONTAINS_A_NAME = "The key should contains a name.";
    private static final String THE_KEY_SHOULD_CONTAINS_A_LOCALE = "The key should contains a locale.";

    @Inject
    private KeyFinder keyFinder;

    @Inject
    private KeyRepository keyRepository;

    @Inject
    private KeyFactory factory;

    /**
     * Returns a list of filtered keys without their translations.
     *
     * @param pageIndex  page index
     * @param pageSize   page size
     * @param isMissing  filter on missing default translation
     * @param isApprox   filter on approximate default translation
     * @param isOutdated filter on outdated key
     * @param searchName filter on key name
     * @return keys without translations
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions("seed:i18n:key:read")
    public Response getKeys(@QueryParam(PAGE_INDEX) Long pageIndex, @QueryParam(PAGE_SIZE) Integer pageSize,
                            @QueryParam(IS_MISSING) Boolean isMissing, @QueryParam(IS_APPROX) Boolean isApprox,
                            @QueryParam(IS_OUTDATED) Boolean isOutdated, @QueryParam(SEARCH_NAME) String searchName) {
        // Prepare criteria
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(IS_MISSING, BooleanUtils.falseToNull(isMissing));
        criteria.put(IS_APPROX, BooleanUtils.falseToNull(isApprox));
        criteria.put(IS_OUTDATED, BooleanUtils.falseToNull(isOutdated));
        criteria.put(SEARCH_NAME, searchName);
        Response response = Response.noContent().build();
        if (pageIndex != null && pageSize != null) {
            Result<KeyRepresentation> result = keyFinder.findAllKeys(Range.rangeFromPageInfo(pageIndex, pageSize), criteria);
            PaginatedView<KeyRepresentation> paginatedView = new PaginatedView<KeyRepresentation>(result, pageSize, pageIndex);
            return Response.ok(paginatedView).build();
        }

        List<KeyRepresentation> keys = keyFinder.findAllKeys();
        if (!keyFinder.findAllKeys().isEmpty()) {
            return Response.ok(keys).build();
        }
        return response;
    }


    /**
     * Inserts a key with the translation in the default language.
     *
     * @param representation key representation
     * @param uriInfo        uri info
     * @return result
     * @throws URISyntaxException if the URI is not valid
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions("seed:i18n:key:write")
    public Response createKey(KeyRepresentation representation, @Context UriInfo uriInfo) throws URISyntaxException {
        SeedWebCheckUtils.checkIfNotNull(representation, THE_KEY_SHOULD_NOT_BE_NULL);
        SeedWebCheckUtils.checkIfNotBlank(representation.getName(), THE_KEY_SHOULD_CONTAINS_A_NAME);
        SeedWebCheckUtils.checkIfNotBlank(representation.getDefaultLocale(), THE_KEY_SHOULD_CONTAINS_A_LOCALE);

        Key key = keyRepository.load(representation.getName());
        if (key == null) {
            key = factory.createKey(representation.getName());
            key.setComment(representation.getComment());
            key.addTranslation(representation.getDefaultLocale(), representation.getTranslation(), representation.isApprox());
            keyRepository.persist(key);
        } else {
            return Response.status(Response.Status.CONFLICT).build();
        }

        return Response.created(new URI(uriInfo.getRequestUri() + "/" + key.getEntityId()))
                .entity(keyFinder.findKey(key.getEntityId())).build();
    }

    /**
     * Returns a key with the default translation.
     *
     * @param name key identifier
     * @return translated key
     */
    @GET
    @Path("/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions("seed:i18n:key:read")
    public Response getKey(@PathParam(KEY) String name) {
        SeedWebCheckUtils.checkIfNotBlank(name, KEY_NAME_SHOULD_NOT_BE_BLANK);
        KeyRepresentation key = keyFinder.findKey(name);
        if (key != null) {
            return Response.ok(keyFinder.findKey(name)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Updates a key with the translation in the default language.
     *
     * @param name           key name
     * @param representation key representation
     * @param uriInfo        URI data
     * @return key representation
     * @throws URISyntaxException if the URI is not valid.
     */
    @PUT
    @Path("/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions("seed:i18n:key:write")
    public Response updateKey(@PathParam(KEY) String name, KeyRepresentation representation, @Context UriInfo uriInfo) throws URISyntaxException {
        SeedWebCheckUtils.checkIfNotBlank(name, KEY_NAME_SHOULD_NOT_BE_BLANK);
        SeedWebCheckUtils.checkIfNotNull(representation, THE_KEY_SHOULD_NOT_BE_NULL);
        SeedWebCheckUtils.checkIfNotBlank(representation.getDefaultLocale(), THE_KEY_SHOULD_CONTAINS_A_LOCALE);

        Key key = keyRepository.load(name);
        if (key == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        key.setComment(representation.getComment());
        key.setOutdated();
        // Updates the default translation
        key.addTranslation(representation.getDefaultLocale(), representation.getTranslation(), representation.isApprox());

        keyRepository.save(key);

        return Response.ok(new URI(uriInfo.getRequestUri() + "/" + name)).entity(keyFinder.findKey(name)).build();
    }

    /**
     * Deletes all the filtered keys.
     *
     * @param isMissing  filter on missing default translation
     * @param isApprox   filter on approximate default translation
     * @param isOutdated filter on outdated key
     * @param searchName filter on key name
     * @return http status code 200 (ok) with the number of deleted keys
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions("seed:i18n:key:delete")
    public Response deleteKeys(@QueryParam(IS_MISSING) Boolean isMissing, @QueryParam(IS_APPROX) Boolean isApprox,
                               @QueryParam(IS_OUTDATED) Boolean isOutdated, @QueryParam(SEARCH_NAME) String searchName) {
        // Prepare criteria
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(IS_MISSING, BooleanUtils.falseToNull(isMissing));
        criteria.put(IS_APPROX, BooleanUtils.falseToNull(isApprox));
        criteria.put(IS_OUTDATED, BooleanUtils.falseToNull(isOutdated));
        criteria.put(SEARCH_NAME, searchName);

        // When no filter, use the fast delete all
        if (isMissing == null && isApprox == null && isOutdated == null && StringUtils.isBlank(searchName)) {
            keyRepository.deleteAll();
        }
        // Otherwise filter and then delete
        List<KeyRepresentation> keys = keyFinder.findAllKeys(criteria);
        List<Key> keysToDelete = new ArrayList<Key>(keys.size());
        for (KeyRepresentation key : keys) {
            Key keyToDelete = keyRepository.load(key.getName());
            if (keyToDelete != null) {
                keysToDelete.add(keyToDelete);
            }
        }
        keyRepository.delete(keysToDelete);

        return Response.ok(String.format("%d deleted keys", keys.size())).type(MediaType.TEXT_PLAIN_TYPE).build();
    }

    /**
     * Deletes a key.
     *
     * @param name key name
     * @return http code 204 (no content)
     * @throws URISyntaxException if the URI is not valid
     */
    @DELETE
    @Path("/{key}")
    @RequiresPermissions("seed:i18n:key:delete")
    public Response deleteKey(@PathParam(KEY) String name) throws URISyntaxException {
        SeedWebCheckUtils.checkIfNotBlank(name, KEY_NAME_SHOULD_NOT_BE_BLANK);
        Key key = keyRepository.load(name);
        if (key != null) {
            keyRepository.delete(key);
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

}
