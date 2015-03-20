/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
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
 * @author PDC Date: 02/12/13
 */
public class StatisticResourcesIT extends AbstractSeedWebIT {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(StatisticResourcesIT.class);

	private static final String ID_FIELD = "locale";

	private static final String BASE_URL = "rest/seed-i18n/statistic";

	private static final String ENGLISH_LANGUAGE_FIELD = "englishLanguage";

	private static final String TRANSLATED = "translated";

	private static final String TOTRANSLATE = "totranslate";

	private static final String KEY_TOTAL = "keytotal";

	private JSONObject jsonObjectEn;

	private JSONObject jsonObjectFr;

	@Before
	public void before() throws JSONException {
		jsonObjectEn = new JSONObject();
		jsonObjectEn.put(ID_FIELD, "en");
		jsonObjectEn.put(TRANSLATED, 50);
		jsonObjectEn.put(TOTRANSLATE, 0);
		jsonObjectEn.put(KEY_TOTAL, 50);
		jsonObjectEn.put(ENGLISH_LANGUAGE_FIELD, "english");

		jsonObjectFr = new JSONObject();
		jsonObjectFr.put(ID_FIELD, "fr");
		jsonObjectFr.put(TRANSLATED, 50);
		jsonObjectFr.put(TOTRANSLATE, 0);
		jsonObjectFr.put(KEY_TOTAL, 50);
		jsonObjectFr.put(ENGLISH_LANGUAGE_FIELD, "french");
	}

	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class).setWebXML("WEB-INF/web.xml");
	}

	@RunAsClient
	@Test
	public void get_statistic(@ArquillianResource URL baseURL)
			throws JSONException {
		// No.1 params get all locales selectLang=""
		// Get all locales => 200 OK
		Response response = expect().statusCode(200).given().auth()
				.basic("admin", "password")
				.header("Accept", "application/json")
				.header("Content-Type", "application/json")
				.get(baseURL.toString() + BASE_URL);

		JSONArray result = new JSONArray(response.asString());
		Assertions.assertThat(result.length()).isEqualTo(2);
		for (int i = 0; i < result.length(); i++) {
			if (result.getJSONObject(i).get(ID_FIELD).equals("en")) {
				JSONObject jsonObject = result.getJSONObject(i);
				Assertions.assertThat(jsonObject.get(TRANSLATED)).isEqualTo(
						jsonObjectEn.get(TRANSLATED));
				Assertions.assertThat(jsonObject.get(TOTRANSLATE)).isEqualTo(
						jsonObjectEn.get(TOTRANSLATE));
				Assertions.assertThat(jsonObject.get(KEY_TOTAL)).isEqualTo(
						jsonObjectEn.get(KEY_TOTAL));
				Assertions.assertThat(jsonObject.get(ENGLISH_LANGUAGE_FIELD))
						.isEqualTo(jsonObjectEn.get(ENGLISH_LANGUAGE_FIELD));
			}

		}

		// No.2 params select a locale selectLang=fr
		response = expect().statusCode(200).given().auth()
				.basic("admin", "password")
				.header("Accept", "application/json")
				.header("Content-Type", "application/json")
				.get(baseURL.toString() + BASE_URL + "?selectLang=fr");

		result = new JSONArray(response.asString());
		Assertions.assertThat(result.length()).isEqualTo(1);
		JSONObject jsonObject = result.getJSONObject(0);
		Assertions.assertThat(jsonObject.get(TRANSLATED)).isEqualTo(
				jsonObjectFr.get(TRANSLATED));
		Assertions.assertThat(jsonObject.get(TOTRANSLATE)).isEqualTo(
				jsonObjectFr.get(TOTRANSLATE));
		Assertions.assertThat(jsonObject.get(KEY_TOTAL)).isEqualTo(
				jsonObjectFr.get(KEY_TOTAL));
		Assertions.assertThat(jsonObject.get(ENGLISH_LANGUAGE_FIELD))
				.isEqualTo(jsonObjectFr.get(ENGLISH_LANGUAGE_FIELD));

	}
}
