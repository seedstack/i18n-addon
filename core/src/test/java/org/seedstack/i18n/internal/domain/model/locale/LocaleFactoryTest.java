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
public class LocaleFactoryTest {

    private LocaleFactory localeFactory = new LocaleFactoryDefault();

    @Test
    public void testCreateLocaleWithLanguage() {
        Locale frLocale = localeFactory.create("fr");
        Assertions.assertThat(frLocale.getEntityId()).isEqualTo("fr");
        Assertions.assertThat(frLocale.getLanguage()).isEqualTo("français");
        Assertions.assertThat(frLocale.getEnglishLanguage()).isEqualTo("French");

        Locale enLocale = localeFactory.create("en");
        Assertions.assertThat(enLocale.getEntityId()).isEqualTo("en");
        Assertions.assertThat(enLocale.getLanguage()).isEqualTo("English");
        Assertions.assertThat(enLocale.getEnglishLanguage()).isEqualTo("English");
    }

    @Test
    public void testCreateLocaleWithLanguageAndRegion() {
        Locale locale = localeFactory.create("fr", "BE");
        Assertions.assertThat(locale.getEntityId()).isEqualTo("fr-BE");
        Assertions.assertThat(locale.getLanguage()).isEqualTo("français (Belgique)");
        Assertions.assertThat(locale.getEnglishLanguage()).isEqualTo("French (Belgium)");
    }

    @Test
    public void testCreateLocaleWithLanguageAndCountry() {
        Locale locale = localeFactory.createFromCode("fr-BE");
        Assertions.assertThat(locale.getEntityId()).isEqualTo("fr-BE");
        Assertions.assertThat(locale.getLanguage()).isEqualTo("français (Belgique)");
        Assertions.assertThat(locale.getEnglishLanguage()).isEqualTo("French (Belgium)");
    }
}
