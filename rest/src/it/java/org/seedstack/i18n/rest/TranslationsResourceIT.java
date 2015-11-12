/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest;

import com.jayway.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.seed.it.AbstractSeedWebIT;
import org.skyscreamer.jsonassert.JSONAssert;

import java.net.URL;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.expect;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 02/12/13
 */
public class TranslationsResourceIT extends AbstractSeedWebIT {

    private static final String ID_FIELD = "name";

    private static final String BASE_URL = "seed-i18n/translations";

    private String idValue;

    private JSONObject jsonKeyObject;

    private JSONObject jsonTranslationObject;

    /**
     * Initializes the test.
     *
     * @throws JSONException if something gets wrong
     */
    @Before
    public void before() throws JSONException {
        idValue = UUID.randomUUID().toString();
        // KEY
        jsonKeyObject = new JSONObject();
        jsonKeyObject.put(ID_FIELD, idValue);
        jsonKeyObject.put("defaultLocale", "en");
        jsonKeyObject.put("comment", "comment");
        jsonKeyObject.put("translation", "translation");
        jsonKeyObject.put("missing", false);

        // TRANSLATION
        JSONObject jsonTranslationEN = new JSONObject();
        jsonTranslationEN.put("locale", "en");
        jsonTranslationEN.put("translation", "translation");
        jsonTranslationEN.put("outdated", false);
        jsonTranslationEN.put("approx", false);

        JSONObject jsonTranslationFR = new JSONObject();
        jsonTranslationFR.put("locale", "fr");
        jsonTranslationFR.put("translation", "translation");
        jsonTranslationFR.put("outdated", false);
        jsonTranslationFR.put("approx", false);

        jsonTranslationObject = new JSONObject();
        jsonTranslationObject.put(ID_FIELD, idValue);
        jsonTranslationObject.put("source", jsonTranslationEN);
        jsonTranslationObject.put("target", jsonTranslationFR);
        jsonTranslationObject.put("missing", false);
    }

    /**
     * @return WebArchive
     */
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).setWebXML("WEB-INF/web.xml");
    }

    /**
     * Checks POST key, PUT translation and GET translation
     *
     * @param baseURL base URL
     * @throws JSONException is something gets wrong
     */
    @RunAsClient
    @Test
    public void get_create_update_translations(@ArquillianResource URL baseURL) throws JSONException {

        // Create a key
        expect().statusCode(201).given().auth().basic("admin", "password").
                header("Accept", "application/json").header("Content-Type", "application/json").
                body(jsonKeyObject.toString()).post(baseURL.toString() + "seed-i18n/keys");

        Response response;
        try {
            // Add the fr translation
            expect().statusCode(204).given().auth().basic("admin", "password").
                    header("Accept", "application/json").header("Content-Type", "application/json").
                    body(jsonTranslationObject.toString()).put(baseURL.toString() + "seed-i18n/translations/fr/" + jsonKeyObject.getString(ID_FIELD));

            // Get the translation
            response = expect().statusCode(200).given().auth().basic("admin", "password").
                    header("Accept", "application/json").header("Content-Type", "application/json")
                    .get(baseURL.toString() + "seed-i18n/translations/fr/" + jsonKeyObject.getString(ID_FIELD));

            // missing value must have been update by the server
            jsonTranslationObject.put("missing", false);
            JSONAssert.assertEquals(jsonTranslationObject, new JSONObject(response.asString()), false);

        } finally {
            delete(baseURL.toString() + "seed-i18n/keys/" + jsonKeyObject.getString(ID_FIELD));
        }
    }

    /**
     * Checks the outdated scenario.
     * 1) A key is added
     * 2) Add fr translation
     * 3) Update default translation => key become outdated
     * 4) Update en translation => key is still outdated
     * 5) Update fr translation => key is no longer outdated
     *
     * @param baseURL base URL
     * @throws JSONException if something gets wrong
     */
    @RunAsClient
    @Test
    public void outdated_scenario(@ArquillianResource URL baseURL) throws JSONException {
        // Create the key
        expect().statusCode(201).given().auth().basic("admin", "password").
                header("Accept", "application/json").header("Content-Type", "application/json").
                body(jsonKeyObject.toString()).post(baseURL.toString() + "seed-i18n/keys");

        try {
            // Add translation in English and French
            translate(baseURL, "zztranslation", "en", true);
            translate(baseURL, "translation", "fr", true);

            // Update the default translation
            updateDefaultTranslation(baseURL);

            // Check if the key is outdated
            getKeyState(baseURL, true);

            // Update EN translations
            translate(baseURL, "updated translation", "en", false);

            // Check if the key is still outdated
            getKeyState(baseURL, true);

            // Update FR translations
            translate(baseURL, "updated translation", "fr", false);

            // Check if the key is not outdated
            getKeyState(baseURL, false);

        } finally {
            // clean the repo
            delete(baseURL.toString() + "seed-i18n/keys/" + jsonKeyObject.getString(ID_FIELD));
        }
    }

    /**
     * Checks the outdated state of the key.
     *
     * @param baseURL  base URL
     * @param outdated expected value
     * @return true if outdated state of the key corresponding to the outdated param
     * @throws JSONException
     */
    private Response getKeyState(URL baseURL, boolean outdated) throws JSONException {
        Response response;
        response = expect().statusCode(200).given().auth().basic("admin", "password").
                header("Accept", "application/json").header("Content-Type", "application/json").
                get(baseURL.toString() + "seed-i18n/keys/" + jsonKeyObject.getString(ID_FIELD));

        JSONObject actual = new JSONObject(response.asString());
        Assertions.assertThat(actual.getBoolean("outdated")).isEqualTo(outdated);
        return response;
    }

    /**
     * Changes default translation value.
     *
     * @param baseURL base URL
     * @throws JSONException
     */
    private void updateDefaultTranslation(URL baseURL) throws JSONException {
        jsonKeyObject.put("translation", "updated translation");
        expect().statusCode(200).given().auth().basic("admin", "password").
                header("Accept", "application/json").header("Content-Type", "application/json").
                body(jsonKeyObject.toString()).put(baseURL.toString() + "seed-i18n/keys/" + jsonKeyObject.getString(ID_FIELD));
    }

    /**
     * Adds english and french translation to the key.
     *
     * @param baseURL     baseURL
     * @param translation translation
     * @param outdated    outdated
     * @throws JSONException
     */
    private void translate(URL baseURL, String translation, String locale, boolean outdated) throws JSONException {
        JSONObject jsonTranslationValueObject = new JSONObject();
        jsonTranslationValueObject.put("translation", translation);
        jsonTranslationValueObject.put("outdated", outdated);
        jsonTranslationValueObject.put("locale", locale);
        jsonTranslationValueObject.put("approx", true);
        jsonTranslationObject.put("target", jsonTranslationValueObject);

        // Translate in english
        expect().statusCode(204).given().auth().basic("admin", "password").
                header("Accept", "application/json").header("Content-Type", "application/json").
                body(jsonTranslationObject.toString()).put(baseURL.toString() + BASE_URL + "/" + locale + "/" + jsonKeyObject.getString(ID_FIELD));

    }
}
