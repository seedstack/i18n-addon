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

import java.net.URL;

import static com.jayway.restassured.RestAssured.expect;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class LocalesResourcesIT extends AbstractSeedWebIT {

    private static final String BASE_URL = "seed-i18n";
    private static final String CODE = "code";
    private static final String LANGUAGE = "language";
    private static final String ENGLISH_LANGUAGE = "englishLanguage";

    private JSONObject itLocale;
    private JSONObject enCALocale;

    @ArquillianResource
    private URL baseURL;

    @Before
    public void before() throws JSONException {
        itLocale = new JSONObject();
        itLocale.put(CODE, "it");
        itLocale.put(LANGUAGE, "italiano");
        itLocale.put(ENGLISH_LANGUAGE, "Italian");

        enCALocale = new JSONObject();
        enCALocale.put(CODE, "en-CA");
        enCALocale.put(LANGUAGE, "English (Canada)");
        enCALocale.put(ENGLISH_LANGUAGE, "English (Canada)");
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).setWebXML("WEB-INF/web.xml");
    }

    @RunAsClient
    @Test
    public void getSupportedLocales() throws JSONException {
        Response response = httpGet("/locales");
        JSONArray result = new JSONArray(response.asString());
        Assertions.assertThat(result.length()).isGreaterThan(100);

        response = httpGet("/locales/it");
        assertEquals(itLocale, new JSONObject(response.asString()));
        response = httpGet("/locales/en-CA");
        assertEquals(enCALocale, new JSONObject(response.asString()));
    }

    private void assertEquals(JSONObject source, JSONObject result) throws JSONException {
        Assertions.assertThat(source.getString(CODE)).isEqualTo(result.getString(CODE));
        Assertions.assertThat(source.getString(LANGUAGE)).isEqualTo(result.getString(LANGUAGE));
        Assertions.assertThat(source.getString(ENGLISH_LANGUAGE)).isEqualTo(result.getString(ENGLISH_LANGUAGE));
    }

    private Response httpGet(String path) {
        return expect().statusCode(200).given().auth().basic("admin", "password").
                header("Accept", "application/json").header("Content-Type", "application/json")
                .get(baseURL.toString() + BASE_URL + path);
    }
}
