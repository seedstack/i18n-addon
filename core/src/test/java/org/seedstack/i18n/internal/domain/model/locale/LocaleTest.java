/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.locale;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class LocaleTest {

    public static final String ES_AR = "es-AR";
    public static final String ESPAÑOL_ARGENTINA = "español (Argentina)";
    public static final String SPANISH_ARGENTINA = "Spanish (Argentina)";

    @Test
    public void testLocaleConstructor() {
        Locale locale = new Locale(ES_AR, ESPAÑOL_ARGENTINA, SPANISH_ARGENTINA);
        Assertions.assertThat(locale.getId()).isEqualTo(ES_AR);
        Assertions.assertThat(locale.getLanguage()).isEqualTo(ESPAÑOL_ARGENTINA);
        Assertions.assertThat(locale.getEnglishLanguage()).isEqualTo(SPANISH_ARGENTINA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLocaleId() {
        new Locale("es_AR", ESPAÑOL_ARGENTINA, SPANISH_ARGENTINA);
    }

    @Test
    public void testDefaultLocale() {
        Locale locale = new Locale(ES_AR, ESPAÑOL_ARGENTINA, SPANISH_ARGENTINA);
        Assertions.assertThat(locale.isDefaultLocale()).isFalse();
        locale.setDefaultLocale(true);
        Assertions.assertThat(locale.isDefaultLocale()).isTrue();
    }
}
