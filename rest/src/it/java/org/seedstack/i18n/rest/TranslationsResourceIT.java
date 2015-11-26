/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
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

    private static final String EN = "en";
    private static final String FR = "fr";
    private static final String ID_FIELD = "name";
    private static final String LOGIN = "admin";
    private static final String PASSWORD = "password";
    private static final String APPLICATION_JSON = "application/json";
    private static final String PATH_PREFIX = "seed-i18n/";

    @ArquillianResource
    private URL baseURL;


    private String keyName;
    private JSONObject jsonKey;
    private JSONObject jsonTranslation;

    /**
     * Initializes the test.
     *
     * @throws JSONException if something gets wrong
     */
    @Before
    public void before() throws JSONException {
        keyName = UUID.randomUUID().toString();
        // KEY
        jsonKey = new JSONObject();
        jsonKey.put(ID_FIELD, keyName);
        jsonKey.put("defaultLocale", EN);
        jsonKey.put("comment", "comment");
        jsonKey.put("translation", "translation");
        jsonKey.put("missing", false);

        // TRANSLATION
        JSONObject jsonTranslationEN = new JSONObject();
        jsonTranslationEN.put("locale", EN);
        jsonTranslationEN.put("translation", "translation");
        jsonTranslationEN.put("outdated", false);
        jsonTranslationEN.put("approx", false);

        JSONObject jsonTranslationFR = new JSONObject();
        jsonTranslationFR.put("locale", FR);
        jsonTranslationFR.put("translation", "translation");
        jsonTranslationFR.put("outdated", false);
        jsonTranslationFR.put("approx", false);

        jsonTranslation = new JSONObject();
        jsonTranslation.put(ID_FIELD, keyName);
        jsonTranslation.put("source", jsonTranslationEN);
        jsonTranslation.put("target", jsonTranslationFR);
        jsonTranslation.put("missing", false);
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
        httpPost("keys", jsonKey.toString());

        try {
            // Add the fr translation
            httpPut("translations/fr/" + keyName, jsonTranslation.toString());

            // Get the translation
            Response response = httpGet("translations/fr/" + keyName);

            // missing value must have been update by the server
            jsonTranslation.put("missing", false);
            JSONAssert.assertEquals(jsonTranslation, new JSONObject(response.asString()), false);

        } finally {
            delete(baseURL.toString() + "seed-i18n/keys/" + keyName);
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
     * @throws JSONException if something gets wrong
     */
    @RunAsClient
    @Test
    public void outdated_scenario() throws JSONException {
        // Create the key
        httpPost("keys", jsonKey.toString());

        try {
            // Add two outdated translations
            sendTranslationUpdate("zztranslation", EN, true);
            sendTranslationUpdate("translation", FR, true);

            // Update the default translation
            jsonKey.put("translation", "updated translation");
            httpPut("keys/" + jsonKey.getString(ID_FIELD), jsonKey.toString(), 200);

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
            delete(baseURL.toString() + "seed-i18n/keys/" + jsonKey.getString(ID_FIELD));
        }
    }

    /**
     * Checks the outdated state of the key.
     *
     * @param outdated expected value
     * @return true if outdated state of the key corresponding to the outdated param
     * @throws JSONException
     */
    private Response assertOutdatedStatus(boolean outdated) throws JSONException {
        Response response = httpGet("keys/" + jsonKey.getString(ID_FIELD));
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
     * @throws JSONException
     */
    private void sendTranslationUpdate(String translation, String locale, boolean outdated) throws JSONException {
        JSONObject jsonTranslationValueObject = new JSONObject();
        jsonTranslationValueObject.put("translation", translation);
        jsonTranslationValueObject.put("outdated", outdated);
        jsonTranslationValueObject.put("locale", locale);
        jsonTranslationValueObject.put("approx", true);
        jsonTranslation.put("target", jsonTranslationValueObject);

        // Translate in english
        httpPut("translations/" + locale + "/" + jsonKey.getString(ID_FIELD), jsonTranslation.toString());
    }

    /**
     * Gets the resource at the path and expect a 200 status code.
     *
     * @param path the resource URI
     * @return the http response
     */
    private Response httpGet(String path) throws JSONException {
        return httpGet(path, 200);
    }

    /**
     * Gets the resource at the path and expect the given status code.
     *
     * @param path the resource URI
     * @return the http response
     */
    private Response httpGet(String path, int statusCode) throws JSONException {
        return httpRequest(statusCode).get(baseURL.toString() + PATH_PREFIX + path);
    }

    /**
     * Posts the body to the given path and expect a 200 status code.
     *
     * @param path the resource URI
     * @param body the resource representation
     * @return the http response
     */
    private Response httpPost(String path, String body) {
        return httpRequest(201).body(body).post(baseURL.toString() + PATH_PREFIX + path);
    }

    /**
     * Puts the body to the given path and expect a 200 status code.
     *
     * @param path the resource URI
     * @param body the resource representation
     * @return the http response
     */
    private Response httpPut(String path, String body) throws JSONException {
        return httpPut(path, body, 204);
    }

    /**
     * Puts the body to the given path and expect a 200 status code.
     *
     * @param path the resource URI
     * @param body the resource representation
     * @return the http response
     */
    private Response httpPut(String path, String body, int status) throws JSONException {
        return httpRequest(status).body(body).put(baseURL.toString() + PATH_PREFIX + path);
    }


    private RequestSpecification httpRequest(int statusCode) {
        return expect().statusCode(statusCode).given().auth().basic(LOGIN, PASSWORD).
                header("Accept", APPLICATION_JSON).header("Content-Type", APPLICATION_JSON);
    }
}
