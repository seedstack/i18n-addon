/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.translation;

import org.seedstack.business.view.Page;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.I18nPermissions;
import org.seedstack.i18n.rest.internal.key.KeySearchCriteria;
import org.seedstack.i18n.rest.internal.shared.NotFoundException;
import org.seedstack.i18n.rest.internal.shared.WebAssertions;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.security.RequiresPermissions;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.seedstack.i18n.rest.internal.key.KeySearchCriteria.*;

/**
 * This REST resource provide access to translations.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@Path("/seed-i18n/translations/{locale}")
public class TranslationsResource {

    private static final String PAGE_INDEX = "pageIndex";
    private static final String PAGE_SIZE = "pageSize";

    @Inject
    private TranslationFinder translationFinder;

    @Inject
    private KeyRepository keyRepository;

    @PathParam(LOCALE)
    private String locale;

    /**
     * Returns filtered translation with pagination.
     *
     * @param pageIndex  page index
     * @param pageSize   page size
     * @param isMissing  filter indicator on missing translation
     * @param isApprox   filter indicator on approximate translation
     * @param isOutdated filter indicator on outdated translation
     * @param searchName filter on key name
     * @return status code 200 with filtered translations or 204 if no translation
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions(I18nPermissions.TRANSLATION_READ)
    public Response getTranslations(@QueryParam(PAGE_INDEX) Long pageIndex, @QueryParam(PAGE_SIZE) Integer pageSize,
                                    @QueryParam(IS_MISSING) Boolean isMissing, @QueryParam(IS_APPROX) Boolean isApprox,
                                    @QueryParam(IS_OUTDATED) Boolean isOutdated, @QueryParam(SEARCH_NAME) String searchName) {

        Response response = Response.noContent().build();

        if (pageIndex != null && pageSize != null) {
            KeySearchCriteria criteria = new KeySearchCriteria(isMissing, isApprox, isOutdated, searchName, locale);
            response = Response.ok(translationFinder.findAllTranslations(new Page(pageIndex, pageSize), criteria)).build();
        } else {
            List<TranslationRepresentation> translations = translationFinder.findTranslations(locale);
            if (!translations.isEmpty()) {
                response = Response.ok(translations).build();
            }
        }
        return response;
    }

    /**
     * Returns a translation for specified key and locale.
     *
     * @param key    key name
     * @return status code 200 with a translation or 404 if not found
     */
    @GET
    @Path("/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions(I18nPermissions.TRANSLATION_READ)
    public TranslationRepresentation getTranslation(@PathParam("key") String key) {
        TranslationRepresentation translation = translationFinder.findTranslation(locale, key);
        if (translation == null) {
            throw new NotFoundException("No translation for key: " + key + " and locale: " + locale);
        }
        return translation;
    }

    /**
     * Update translation for the given locale.
     *
     * @param keyName        key name
     * @param representation translation representation
     * @return status code 204 (no content) or 404 (not found) if key or target translation is null.
     */
    @PUT
    @Path("/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions(I18nPermissions.TRANSLATION_WRITE)
    public Response updateTranslation(@PathParam("key") String keyName, TranslationRepresentation representation) {
        WebAssertions.assertNotNull(representation, "The translation should not be null");
        TranslationValueRepresentation translationToUpdate = representation.getTarget();
        WebAssertions.assertNotNull(translationToUpdate, "The translation target should not be null");

        Key key = keyRepository.load(keyName);
        if (key == null) {
            throw new NotFoundException("The key is not found");
        }

        key.addTranslation(locale, translationToUpdate.getTranslation(), translationToUpdate.isApprox());
        keyRepository.save(key);

        return Response.noContent().build();
    }
}
