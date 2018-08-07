/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.rest;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.junit.Test;
import org.seedstack.i18n.shared.AbstractI18nRestIT;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class KeyResourceIT extends AbstractI18nRestIT {

    private static final String KEY_FOO = "{\"name\":\"test.foo\",\"comment\":\"i18n\",\"translation\":\"bar\"," +
            "\"defaultLocale\":\"en\",\"missing\":false,\"approx\":false,\"outdated\":false}";
    private static final String UPDATED_KEY = "{\"name\":\"test.to.update\",\"comment\":\"i18n\"," +
            "\"translation\":\"updated\",\"defaultLocale\":\"en\",\"missing\":false,\"approx\":false," +
            "\"outdated\":true}";
    private static final String WRONG_UPDATED_KEY = "{\"name\":\"test.to.update\",\"comment\":\"i18n\"," +
            "\"translation\":\"updated\",\"defaultLocale\":\"\",\"missing\":false,\"approx\":false,\"outdated\":false}";

    @Test
    public void getKey() throws JSONException {
        Response getFooResponse = httpGet("keys/test.foo", 200);
        Assertions.assertThat(getFooResponse.asString()).isEqualTo(KEY_FOO);

        httpGet("keys/test.bar", 404);
    }

    @Test
    public void updateKey() throws JSONException {
        Response getUpdatedResponse = httpPut("keys/test.to.update", UPDATED_KEY, 200);
        Assertions.assertThat(getUpdatedResponse.asString()).isEqualTo(UPDATED_KEY);

        httpPut("keys/test.to.update", "", 400);
        httpPut("keys/test.to.update", WRONG_UPDATED_KEY, 400);
    }
}
