/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.i18n.rest.internal.key.KeyRepresentation;
import org.seedstack.i18n.rest.internal.translation.TranslationRepresentation;
import org.seedstack.i18n.rest.internal.translation.TranslationValueRepresentation;
import org.seedstack.i18n.shared.AbstractI18nRestIT;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class TranslationsResourceIT extends AbstractI18nRestIT {

    private static final String EN = "en";
    private static final String FR = "fr";

    private String keyName;
    private JSONObject jsonKey;
    private JSONObject jsonTranslation;

    @Before
    public void before() throws JSONException, JsonProcessingException {
        keyName = UUID.randomUUID().toString();

        KeyRepresentation keyRepresentation = new KeyRepresentation(keyName, EN, "translation", "comment");
        jsonKey = new JSONObject(new ObjectMapper().writeValueAsString(keyRepresentation));

        TranslationRepresentation keyTranslation = new TranslationRepresentation(keyName, "comment");
        keyTranslation.setSource(new TranslationValueRepresentation(EN, "translation"));
        keyTranslation.setTarget(new TranslationValueRepresentation(FR, "traduction"));
        jsonTranslation = new JSONObject(new ObjectMapper().writeValueAsString(keyTranslation));
    }

    @Test
    public void get_create_update_translations() throws JSONException {
        httpPost("keys", jsonKey.toString(), 201);

        try {
            httpPut("translations/fr/" + keyName, jsonTranslation.toString());

            Response response = httpGet("translations/fr/" + keyName);

            // missing value must have been update by the server
            jsonTranslation.put("missing", false);
            JSONAssert.assertEquals(jsonTranslation, new JSONObject(response.asString()), false);

        } finally {
            httpDelete("keys/" + keyName, 204);
        }
    }

    /**
     * Checks the outdated scenario.
     * 1) A key is added
     * 2) Add fr translation
     * 3) Update default translation => key become outdated
     * 4) Update en translation => key is still outdated
     * 5) Update fr translation => key is no longer outdated
     */
    @Test
    public void outdated_scenario() throws JSONException {
        httpPost("keys", jsonKey.toString(), 201);

        try {
            // Add two outdated translations
            sendTranslationUpdate("zztranslation", EN, true);
            sendTranslationUpdate("translation", FR, true);

            // Update the default translation
            jsonKey.put("translation", "updated translation");
            httpPut("keys/" + keyName, jsonKey.toString(), 200);

            // The key should remain outdated
            assertOutdatedStatus(true);

            // Update English translations
            sendTranslationUpdate("updated translation", EN, false);

            // The key should remain outdated
            assertOutdatedStatus(true);

            // Update French translations
            sendTranslationUpdate("updated translation", FR, false);

            // Now, all the translation have been updated
            assertOutdatedStatus(false);

        } finally {
            // clean the repo
            httpDelete("keys/" + keyName, 204);
        }
    }

    /**
     * Checks the outdated state of the key.
     *
     * @param outdated expected value
     * @return true if outdated state of the key corresponding to the outdated param
     */
    private Response assertOutdatedStatus(boolean outdated) throws JSONException {
        Response response = httpGet("keys/" + keyName);
        JSONObject actual = new JSONObject(response.asString());
        Assertions.assertThat(actual.getBoolean("outdated")).isEqualTo(outdated);
        return response;
    }

    /**
     * Sends an HTTP put request to update the translation for the given locale.
     *
     * @param translation translation
     * @param locale      locale
     * @param outdated    outdated
     */
    private void sendTranslationUpdate(String translation, String locale, boolean outdated) throws JSONException {
        JSONObject jsonTranslationValueObject = new JSONObject();
        jsonTranslationValueObject.put("translation", translation);
        jsonTranslationValueObject.put("outdated", outdated);
        jsonTranslationValueObject.put("locale", locale);
        jsonTranslationValueObject.put("approx", true);
        jsonTranslation.put("target", jsonTranslationValueObject);

        // Translate in english
        httpPut("translations/" + locale + "/" + keyName, jsonTranslation.toString());
    }
}
