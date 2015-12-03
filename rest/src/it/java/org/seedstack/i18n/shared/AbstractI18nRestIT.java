/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.shared;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.json.JSONException;
import org.seedstack.seed.it.AbstractSeedWebIT;

import java.net.URL;

import static com.jayway.restassured.RestAssured.expect;
import static org.junit.Assert.fail;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public abstract class AbstractI18nRestIT extends AbstractSeedWebIT {

    private static final String LOGIN = "admin";
    private static final String PASSWORD = "password";
    private static final String APPLICATION_JSON = "application/json";
    private static final String PATH_PREFIX = "seed-i18n/";

    @ArquillianResource
    private URL baseURL;


    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).setWebXML("WEB-INF/web.xml");
    }

    protected void assertResponseHasStatusCode(Response response, int statusCode) {
        if (response.statusCode() != statusCode) {
            fail("Status code " + response.statusCode() + " [" + response.prettyPrint() + "]");
        }
    }

    /**
     * Gets the resource at the path and expect a 200 status code.
     *
     * @param path the resource URI
     * @return the http response
     */
    protected Response httpGet(String path) throws JSONException {
        return httpGet(path, 200);
    }

    /**
     * Gets the resource at the path and expect the given status code.
     *
     * @param path the resource URI
     * @return the http response
     */
    protected Response httpGet(String path, int statusCode) throws JSONException {
        return httpRequest(statusCode).get(baseURL.toString() + PATH_PREFIX + path);
    }

    /**
     * Posts the body to the given path and expect a 200 status code.
     *
     * @param path the resource URI
     * @param body the resource representation
     * @return the http response
     */
    protected Response httpPost(String path, String body, int status) {
        return httpRequest(status).body(body).post(baseURL.toString() + PATH_PREFIX + path);
    }

    /**
     * Puts the body to the given path and expect a 200 status code.
     *
     * @param path the resource URI
     * @param body the resource representation
     * @return the http response
     */
    protected Response httpPut(String path, String body) throws JSONException {
        return httpPut(path, body, 204);
    }

    /**
     * Puts the body to the given path and expect a 200 status code.
     *
     * @param path the resource URI
     * @param body the resource representation
     * @return the http response
     */
    protected Response httpPut(String path, String body, int status) throws JSONException {
        return httpRequest(status).body(body).put(baseURL.toString() + PATH_PREFIX + path);
    }

    /**
     * Deletes the resource at the given path.
     *
     * @param path the resource URI
     * @return the http response
     */
    protected Response httpDelete(String path, int status) throws JSONException {
        return httpRequest(status).delete(baseURL.toString() + PATH_PREFIX + path);
    }


    protected RequestSpecification httpRequest(int statusCode) {
        return expect().statusCode(statusCode).given().auth().basic(LOGIN, PASSWORD).
                header("Accept", APPLICATION_JSON).header("Content-Type", APPLICATION_JSON);
    }
}
