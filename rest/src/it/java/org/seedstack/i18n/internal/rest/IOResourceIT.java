/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.rest;

import org.json.JSONException;
import org.junit.Test;
import org.seedstack.i18n.shared.AbstractI18nRestIT;

public class IOResourceIT extends AbstractI18nRestIT {

    @Test
    public void postCSV() throws JSONException {
        httpPostCSV("keys/file", "key1;a;b;c", 200);
    }
}
