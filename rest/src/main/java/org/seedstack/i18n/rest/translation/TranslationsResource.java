/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.translation;

import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.exception.SeedWebCheckUtils;
import org.seedstack.i18n.utils.BooleanUtils;
import org.seedstack.business.api.interfaces.finder.Range;
import org.seedstack.business.api.interfaces.finder.Result;
import org.seedstack.business.api.interfaces.view.PaginatedView;
import org.seedstack.seed.persistence.jpa.api.JpaUnit;
import org.seedstack.seed.security.api.annotations.RequiresPermissions;
import org.seedstack.seed.transaction.api.Propagation;
import org.seedstack.seed.transaction.api.Transactional;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This REST resource provide access to translations.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Path("/seed-i18n/translations")
public class TranslationsResource {

    private static final String LOCALE = "locale";
    private static final String PAGE_INDEX = "pageIndex";
    private static final String PAGE_SIZE = "pageSize";
    private static final String IS_APPROX = "isApprox";
    private static final String IS_MISSING = "isMissing";
    private static final String IS_OUTDATED = "isOutdated";
    private static final String SEARCH_NAME = "searchName";
    private static final String THE_LOCALE_SHOULD_NOT_BE_BLANK = "The locale should not be blank";
    private static final String THE_TRANSLATION_SHOULD_NOT_BE_NULL = "The translation should not be null";
    private static final String THE_TRANSLATION_TARGET_SHOULD_NOT_BE_NULL = "The translation target should not be null";
    private static final String THE_KEY_SHOULD_NOT_BE_BLANK = "The key should not be blank";

    @Inject
    private TranslationFinder translationFinder;

    @Inject
    private KeyRepository keyRepository;

    /**
     * Returns filtered translation with pagination.
     *
     * @param locale     locale identifier
     * @param pageIndex  page index
     * @param pageSize   page size
     * @param isMissing  filter indicator on missing translation
     * @param isApprox   filter indicator on approximate translation
     * @param isOutdated filter indicator on outdated translation
     * @param searchName filter on key name
     * @return status code 200 with filtered translations or 204 if no translation
     */
    @GET
    @Path("/{locale}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions("seed:i18n:translation:read")
    public Response getTranslations(@PathParam(LOCALE) String locale, @QueryParam(PAGE_INDEX) Long pageIndex, @QueryParam(PAGE_SIZE) Integer pageSize,
                                    @QueryParam(IS_MISSING) Boolean isMissing, @QueryParam(IS_APPROX) Boolean isApprox,
                                    @QueryParam(IS_OUTDATED) Boolean isOutdated, @QueryParam(SEARCH_NAME) String searchName) {
        SeedWebCheckUtils.checkIfNotBlank(locale, THE_LOCALE_SHOULD_NOT_BE_BLANK);

        // Prepare criteria
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(IS_MISSING, BooleanUtils.falseToNull(isMissing));
        criteria.put(IS_APPROX, BooleanUtils.falseToNull(isApprox));
        criteria.put(IS_OUTDATED, BooleanUtils.falseToNull(isOutdated));
        criteria.put(SEARCH_NAME, searchName);
        criteria.put(LOCALE, locale);
        Response response = Response.noContent().build();

        if (pageIndex != null && pageSize != null) {
            // If pagination
            Result<TranslationRepresentation> result = translationFinder.findAllTranslations(Range.rangeFromPageInfo(pageIndex, pageSize), criteria);
            PaginatedView<TranslationRepresentation> paginatedView = new PaginatedView<TranslationRepresentation>(result, pageSize, pageIndex);
            response = Response.ok(paginatedView).build();
        } else {
            // Otherwise
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
     * @param locale locale identifier
     * @param key    key name
     * @return status code 200 with a translation or 404 if not found
     */
    @GET
    @Path("/{locale}/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions("seed:i18n:translation:read")
    public Response getTranslation(@PathParam(LOCALE) String locale, @PathParam("key") String key) {
        SeedWebCheckUtils.checkIfNotBlank(locale, THE_LOCALE_SHOULD_NOT_BE_BLANK);
        SeedWebCheckUtils.checkIfNotBlank(key, THE_KEY_SHOULD_NOT_BE_BLANK);
        TranslationRepresentation translation = translationFinder.findTranslation(locale, key);
        if (translation != null) {
            return Response.ok(translation).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Update translation for the given locale.
     *
     * @param representation translation representation
     * @param locale         locale identifier
     * @param keyName        key name
     * @return status code 204 (no content) or 404 (not found) if key or target translation is null.
     */
    @PUT
    @Path("/{locale}/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresPermissions("seed:i18n:translation:write")
    public Response translate(TranslationRepresentation representation, @PathParam(LOCALE) String locale,
                              @PathParam("key") String keyName) {
        SeedWebCheckUtils.checkIfNotBlank(locale, THE_LOCALE_SHOULD_NOT_BE_BLANK);
        SeedWebCheckUtils.checkIfNotNull(representation, THE_TRANSLATION_SHOULD_NOT_BE_NULL);
        SeedWebCheckUtils.checkIfNotNull(representation.getTarget(), THE_TRANSLATION_TARGET_SHOULD_NOT_BE_NULL);

        Key key = keyRepository.load(keyName);

        if (key != null) {
            key.addTranslation(locale, representation.getTarget().getTranslation(),
                    representation.getTarget().isApprox(), false);
            keyRepository.save(key);
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.noContent().build();
    }
}
