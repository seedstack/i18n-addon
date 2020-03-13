/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.rest;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.i18n.shared.AbstractI18nRestIT;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author PDC Date: 02/12/13
 */
public class StatisticResourcesIT extends AbstractI18nRestIT {

    private static final String ID_FIELD = "locale";
    private static final String ENGLISH_LANGUAGE_FIELD = "englishLanguage";
    private static final String TRANSLATED = "translated";
    private static final String TO_TRANSLATE = "totranslate";
    private static final String KEY_TOTAL = "keytotal";

    private JSONObject jsonObjectEn;
    private JSONObject jsonObjectFr;

    @Before
    public void before() throws JSONException {
        jsonObjectEn = new JSONObject();
        jsonObjectEn.put(ID_FIELD, "en");
        jsonObjectEn.put(TRANSLATED, 4);
        jsonObjectEn.put(TO_TRANSLATE, 0);
        jsonObjectEn.put(KEY_TOTAL, 4);
        jsonObjectEn.put(ENGLISH_LANGUAGE_FIELD, "English");

        jsonObjectFr = new JSONObject();
        jsonObjectFr.put(ID_FIELD, "fr");
        jsonObjectFr.put(TRANSLATED, 3);
        jsonObjectFr.put(TO_TRANSLATE, 1);
        jsonObjectFr.put(KEY_TOTAL, 4);
        jsonObjectFr.put(ENGLISH_LANGUAGE_FIELD, "French");
    }

    @Test
    public void get_statistic() throws JSONException {
        testStatisticsForAllLocales();
        testStatisticsForOneLocale();
    }

    private void testStatisticsForAllLocales() throws JSONException {
        Response response = httpGet("statistic", 200);
        JSONArray result = new JSONArray(response.asString());

        Assertions.assertThat(result.length()).isEqualTo(2);

        for (int i = 0; i < result.length(); i++) {
            if (result.getJSONObject(i).get(ID_FIELD).equals("en")) {
                JSONObject jsonObject = result.getJSONObject(i);
                JSONAssert.assertEquals(jsonObject, jsonObjectEn, true);
            }
        }
    }

    private void testStatisticsForOneLocale() throws JSONException {
        Response response = httpGet("statistic?selectLang=fr", 200);

        JSONArray result = new JSONArray(response.asString());
        Assertions.assertThat(result.length()).isEqualTo(1);
        JSONObject jsonObject = result.getJSONObject(0);
        JSONAssert.assertEquals(jsonObject, jsonObjectFr, true);
    }
}
