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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.seed.it.AbstractSeedWebIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

import static com.jayway.restassured.RestAssured.expect;

/**
 * @author pierre.thirouin@ext.mpsa.com
 * Date: 02/12/13
 */
public class LocalesResourcesIT extends AbstractSeedWebIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalesResourcesIT.class);

    private static final String ID_FIELD = "code";

    private static final String BASE_URL = "seed-i18n/locales";

    private static final String ENGLISH_LANGUAGE_FIELD = "englishLanguage";

    private JSONObject jsonObject;

    @Before
    public void before() throws JSONException {
        jsonObject = new JSONObject();
        jsonObject.put("code", "it");
        jsonObject.put("language", "italiano");
        jsonObject.put(ENGLISH_LANGUAGE_FIELD, "Italian");
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).setWebXML("WEB-INF/web.xml");
    }

    @RunAsClient
    @Test
    public void get_locales(@ArquillianResource URL baseURL) throws JSONException {
        //
        // Get all locales => 200 OK
        //
        Response response = expect().statusCode(200).given().auth().basic("admin", "password").
                header("Accept", "application/json").header("Content-Type", "application/json")
                .get(baseURL.toString() + BASE_URL);

        JSONArray result = new JSONArray(response.asString());
        Assertions.assertThat(result.length()).isGreaterThan(100);

        LOGGER.info("GET LOCALES, status code: {}", response.getStatusCode());

        //
        // Get "it" locale => 200 OK
        //
        response = expect().statusCode(200).given().auth().basic("admin", "password").
                header("Accept", "application/json").header("Content-Type", "application/json")
                .get(baseURL.toString() + BASE_URL + "/it");
        JSONObject required = new JSONObject(response.asString());
        Assertions.assertThat(jsonObject.getString(ID_FIELD)).isEqualTo(required.getString(ID_FIELD));
        Assertions.assertThat(jsonObject.getString(ENGLISH_LANGUAGE_FIELD)).isEqualTo(required.getString(ENGLISH_LANGUAGE_FIELD));

        LOGGER.info("GET LOCALES 'it', status code: {}", response.getStatusCode());
    }

}
