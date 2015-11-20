/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
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

    @Test
    public void testLocaleConstructor() {
        Locale locale = new Locale("es-AR", "español (Argentina)", "Spanish (Argentina)");
        Assertions.assertThat(locale.getEntityId()).isEqualTo("es-AR");
        Assertions.assertThat(locale.getLanguage()).isEqualTo("español (Argentina)");
        Assertions.assertThat(locale.getEnglishLanguage()).isEqualTo("Spanish (Argentina)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLocaleId() {
        new Locale("es_AR", "español (Argentina)", "Spanish (Argentina)");
    }

    @Test
    public void testDefaultLocale() {
        Locale locale = new Locale("es-AR", "español (Argentina)", "Spanish (Argentina)");
        Assertions.assertThat(locale.isDefaultLocale()).isFalse();
        locale.setDefaultLocale(true);
        Assertions.assertThat(locale.isDefaultLocale()).isTrue();
    }
}
