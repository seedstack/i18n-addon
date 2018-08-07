/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.rest.internal.key;

import static org.seedstack.i18n.rest.internal.key.KeySearchCriteria.IS_APPROX;
import static org.seedstack.i18n.rest.internal.key.KeySearchCriteria.IS_MISSING;
import static org.seedstack.i18n.rest.internal.key.KeySearchCriteria.IS_OUTDATED;
import static org.seedstack.i18n.rest.internal.key.KeySearchCriteria.SEARCH_NAME;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang.StringUtils;
import org.seedstack.business.view.Page;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.I18nPermissions;
import org.seedstack.i18n.rest.internal.shared.AlreadyExistException;
import org.seedstack.i18n.rest.internal.shared.WebAssertions;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.security.RequiresPermissions;
import org.seedstack.seed.transaction.Transactional;

/**
 * REST resources exposing keys for administration.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@Path("/seed-i18n/keys")
public class KeysResource {

    private static final String PAGE_INDEX = "pageIndex";
    private static final String PAGE_SIZE = "pageSize";

    private static final String THE_KEY_SHOULD_NOT_BE_NULL = "The key should not be null";
    private static final String THE_KEY_SHOULD_CONTAINS_A_NAME = "The key should contains a name.";
    private static final String THE_KEY_SHOULD_CONTAINS_A_LOCALE = "The key should contains a locale.";

    private final KeyFinder keyFinder;
    private final KeyRepository keyRepository;
    private final KeyFactory factory;

    @Context
    private UriInfo uriInfo;

    @Inject
    public KeysResource(KeyFinder keyFinder, KeyRepository keyRepository, KeyFactory factory) {
        this.keyFinder = keyFinder;
        this.keyRepository = keyRepository;
        this.factory = factory;
    }

    /**
     * Returns a list of filtered keys without their translations.
     *
     * @param pageIndex  page index
     * @param pageSize   page size
     * @param isMissing  filter on missing default translation
     * @param isApprox   filter on approximate default translation
     * @param isOutdated filter on outdated key
     * @param searchName filter on key name
     * @return 200 - keys without translations
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions(I18nPermissions.KEY_READ)
    public Response getKeys(@QueryParam(PAGE_INDEX) @DefaultValue("0") long pageIndex,
            @QueryParam(PAGE_SIZE) @DefaultValue("10") int pageSize,
            @QueryParam(IS_MISSING) Boolean isMissing,
            @QueryParam(IS_APPROX) Boolean isApprox,
            @QueryParam(IS_OUTDATED) Boolean isOutdated,
            @QueryParam(SEARCH_NAME) String searchName) {
        WebAssertions.assertIf(pageSize > 0, "Page size should be greater than zero.");
        KeySearchCriteria keySearchCriteria = new KeySearchCriteria(isMissing, isApprox, isOutdated, searchName);
        Page page = new Page(pageIndex, pageSize);
        return Response.ok(keyFinder.findKeysWithTheirDefaultTranslation(page, keySearchCriteria)).build();
    }

    /**
     * Inserts a key with the translation in the default language.
     *
     * @param keyRepresentation key representation
     * @return 201 if the resource is created, 409 if the resource already existed
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions(I18nPermissions.KEY_WRITE)
    public Response createKey(KeyRepresentation keyRepresentation) throws URISyntaxException {
        WebAssertions.assertNotNull(keyRepresentation, THE_KEY_SHOULD_NOT_BE_NULL);
        WebAssertions.assertNotBlank(keyRepresentation.getName(), THE_KEY_SHOULD_CONTAINS_A_NAME);
        WebAssertions.assertNotBlank(keyRepresentation.getDefaultLocale(), THE_KEY_SHOULD_CONTAINS_A_LOCALE);

        assertKeyDoNotAlreadyExists(keyRepresentation);

        Key key = factory.createKey(keyRepresentation.getName());
        key.setComment(keyRepresentation.getComment());
        addDefaultTranslation(keyRepresentation, key);
        keyRepository.add(key);

        return Response.created(new URI(uriInfo.getRequestUri() + "/" + key.getId()))
                .entity(keyFinder.findKeyWithName(key.getId())).build();
    }

    private void addDefaultTranslation(KeyRepresentation keyRepresentation, Key key) {
        String defaultLocale = keyRepresentation.getDefaultLocale();
        String translation = keyRepresentation.getTranslation();
        boolean approx = keyRepresentation.isApprox();
        key.addTranslation(defaultLocale, translation, approx);
    }

    private void assertKeyDoNotAlreadyExists(KeyRepresentation representation) {
        keyRepository.get(representation.getName()).ifPresent(k -> {throw new AlreadyExistException();});
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
    @Produces("text/plain")
    @RequiresPermissions(I18nPermissions.KEY_DELETE)
    public Response deleteKeys(@QueryParam(IS_MISSING) Boolean isMissing, @QueryParam(IS_APPROX) Boolean isApprox,
            @QueryParam(IS_OUTDATED) Boolean isOutdated, @QueryParam(SEARCH_NAME) String searchName) {

        KeySearchCriteria keySearchCriteria = new KeySearchCriteria(isMissing, isApprox, isOutdated, searchName);
        long numberOfDeletedKeys;
        if (shouldDeleteWithoutFilter(keySearchCriteria)) {
            numberOfDeletedKeys = keyRepository.size();
            keyRepository.clear(); // If no filter are precised use the "deleteAll()" method which is more optimized
        } else {
            numberOfDeletedKeys = deleteFilteredKeys(keySearchCriteria);
        }
        return Response.ok(String.format("%d deleted keys", numberOfDeletedKeys)).build();
    }

    private long deleteFilteredKeys(KeySearchCriteria keySearchCriteria) {
        List<KeyRepresentation> keysToDelete = keyFinder.findKeysWithTheirDefaultTranslation(keySearchCriteria);
        for (KeyRepresentation key : keysToDelete) {
            keyRepository.remove(key.getName());
        }
        return keysToDelete.size();
    }

    private boolean shouldDeleteWithoutFilter(KeySearchCriteria criteria) {
        return criteria.getMissing() == null
                && criteria.getApprox() == null
                && criteria.getOutdated() == null
                && StringUtils.isBlank(criteria.getName());
    }

}
