/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest;

import com.jayway.restassured.response.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.json.JSONException;
import org.junit.Test;
import org.seedstack.seed.it.AbstractSeedWebIT;

import java.net.URL;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.fail;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class MessageResourcesIT extends AbstractSeedWebIT {

    private static final String BASE_URL = "seed-i18n";

    @ArquillianResource
    private URL baseURL;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).setWebXML("WEB-INF/web.xml");
    }

    @RunAsClient
    @Test
    public void getTranslations() throws JSONException {
        Response response = httpGet("/messages/en");
        assertResponseHasStatusCode(response, 200);
    }

    private Response httpGet(String path) {
        return given().auth().basic("admin", "password").header("Accept", "application/json")
                .header("Content-Type", "application/json").get(baseURL.toString() + BASE_URL + path);
    }

    private void assertResponseHasStatusCode(Response response, int statusCode) {
        if (response.statusCode() != statusCode) {
            fail("Status code " + response.statusCode() + " [" + response.prettyPrint() + "]");
        }
    }
}
