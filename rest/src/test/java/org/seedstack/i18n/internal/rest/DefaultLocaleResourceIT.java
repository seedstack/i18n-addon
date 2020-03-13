/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.rest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.i18n.shared.AbstractI18nRestIT;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 10/12/13
 */
public class DefaultLocaleResourceIT extends AbstractI18nRestIT {

    private static final String CODE = "code";
    private static final String LANGUAGE = "language";
    private static final String ENGLISH_LANGUAGE_FIELD = "englishLanguage";

    private JSONObject italianJsonObject;

    @Before
    public void before() throws JSONException {
        italianJsonObject = new JSONObject();
        italianJsonObject.put(CODE, "it");
        italianJsonObject.put(LANGUAGE, "italiano");
        italianJsonObject.put(ENGLISH_LANGUAGE_FIELD, "Italian");
    }

    @Test
    public void putThenGetDefaultLocale() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(italianJsonObject);

        httpPut("available-locales", jsonArray.toString(), 200);

        httpPut("default-locale", italianJsonObject.toString(), 200);

        String response = httpGet("default-locale", 200).asString();
        JSONAssert.assertEquals(new JSONObject(response), italianJsonObject, true);
    }
}
