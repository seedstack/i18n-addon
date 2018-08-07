/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.rest;

import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.i18n.shared.AbstractI18nRestIT;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class LocalesResourcesIT extends AbstractI18nRestIT {

    private static final String CODE = "code";
    private static final String LANGUAGE = "language";
    private static final String ENGLISH_LANGUAGE = "englishLanguage";

    private JSONObject itLocale;
    private JSONObject enCALocale;

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

    @Test
    public void getSupportedLocales() throws JSONException {
        String response = httpGet("locales", 200).asString();
        Assertions.assertThat(new JSONArray(response).length()).isGreaterThan(100);

        response = httpGet("locales/it", 200).asString();
        JSONAssert.assertEquals(itLocale, new JSONObject(response), true);

        response = httpGet("locales/en-CA", 200).asString();
        JSONAssert.assertEquals(enCALocale, new JSONObject(response), true);
    }
}
