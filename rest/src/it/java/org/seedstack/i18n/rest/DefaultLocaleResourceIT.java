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
 * Date: 10/12/13
 */
public class DefaultLocaleResourceIT extends AbstractSeedWebIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLocaleResourceIT.class);

    private static final String ID_FIELD = "code";

    private static final String RESOURCE_URL = "seed-i18n/default-locale";

    private static final String ENGLISH_LANGUAGE_FIELD = "englishLanguage";

    private JSONObject jsonObject;

    @Before
    public void before() throws JSONException {
        jsonObject = new JSONObject();
        jsonObject.put("code", "it");
        jsonObject.put("language", "english");
        jsonObject.put(ENGLISH_LANGUAGE_FIELD, "english");
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).setWebXML("WEB-INF/web.xml");
    }

    @RunAsClient
    @Test
    public void putThenGetDefaultLocale(@ArquillianResource URL baseURL) throws JSONException {
        Response response;

        JSONArray array = new JSONArray();
        array.put(jsonObject);
        // PUT "it" locale => 200 OK
        expect().statusCode(200).given().auth().basic("admin", "password").
                header("Accept", "application/json").header("Content-Type", "application/json")
                .body(array.toString()).put(baseURL.toString() + "seed-i18n/available-locales");

        response = expect().statusCode(200).given().auth().basic("admin", "password").
                header("Accept", "application/json").header("Content-Type", "application/json")
                .body(jsonObject.toString()).put(baseURL.toString() + RESOURCE_URL);

        //JSONAssert.assertEquals(jsonObject, new JSONObject(response.asString()), false);

        LOGGER.info("UPDATE KEY {}, status code: {}", jsonObject.getString("code"), response.getStatusCode());

        // GET "it" locale => 200 OK
        response = expect().statusCode(200).given().auth().basic("admin", "password").
                header("Accept", "application/json").header("Content-Type", "application/json")
                .get(baseURL.toString() + RESOURCE_URL);
        JSONObject returnedObject = new JSONObject(response.asString());
        Assertions.assertThat(jsonObject.getString(ID_FIELD)).isEqualTo(returnedObject.getString(ID_FIELD));

        LOGGER.info("GET LOCALES 'it', status code: {}", response.getStatusCode());
    }
}
