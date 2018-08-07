/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.shared;

import static io.restassured.RestAssured.expect;
import static org.junit.Assert.fail;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.junit.runner.RunWith;
import org.seedstack.seed.Configuration;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.undertow.LaunchWithUndertow;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@RunWith(SeedITRunner.class)
@LaunchWithUndertow
public abstract class AbstractI18nRestIT {

    private static final String LOGIN = "admin";
    private static final String PASSWORD = "password";
    private static final String APPLICATION_JSON = "application/json";
    private static final String PATH_PREFIX = "seed-i18n/";

    @Configuration("web.runtime.baseUrl")
    private String baseURL;

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
    protected Response httpGet(String path, int statusCode) {
        return httpRequest(statusCode, null).get(baseURL + PATH_PREFIX + path);
    }

    /**
     * Posts the body to the given path and expect a 200 status code.
     *
     * @param path the resource URI
     * @param body the resource representation
     * @return the http response
     */
    protected Response httpPost(String path, String body, int status) {
        return httpRequest(status, null).body(body).post(baseURL + PATH_PREFIX + path);
    }

    /**
     * Posts the given CSV as multipart form data to the given path and expect a 200 status code.
     *
     * @param path    the resource URI
     * @param csvBody the resource representation
     * @return the http response
     */
    protected Response httpPostCSV(String path, String csvBody, int status) {
        MultiPartSpecBuilder multipart = new MultiPartSpecBuilder(csvBody.getBytes());
        multipart.mimeType("multipart/form-data");
        multipart.fileName("file.csv");
        return httpRequest(status, "multipart/form-data")
                .multiPart(multipart.build())
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQ=")
                .post(baseURL + PATH_PREFIX + path);
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
    protected Response httpPut(String path, String body, int status) {
        return httpRequest(status, null).body(body).put(baseURL + PATH_PREFIX + path);
    }

    /**
     * Deletes the resource at the given path.
     *
     * @param path the resource URI
     * @return the http response
     */
    protected Response httpDelete(String path, int status) {
        return httpRequest(status, null).delete(baseURL + PATH_PREFIX + path);
    }

    protected RequestSpecification httpRequest(int statusCode, String contentType) {
        return expect().statusCode(statusCode)
                .given()
                .auth()
                .basic(LOGIN, PASSWORD)
                .header("Accept", APPLICATION_JSON)
                .header("Content-Type", contentType == null ? APPLICATION_JSON : contentType);
    }
}
