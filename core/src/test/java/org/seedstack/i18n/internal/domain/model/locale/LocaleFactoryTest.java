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
        Locale frLocale = localeFactory.createFromLanguage("fr");
        assertLocale(frLocale, "fr", "français", "French");
    }

    @Test
    public void testCreateLocaleWithLanguageAndRegion() {
        Locale locale = localeFactory.createFromLanguageAndRegion("fr", "BE");
        assertLocale(locale, "fr-BE", "français (Belgique)", "French (Belgium)");

        Locale esARLocale = localeFactory.createFromLanguageAndRegion("es", "ar");
        assertLocale(esARLocale, "es-AR", "español (Argentina)", "Spanish (Argentina)");
    }

    @Test
    public void testLocaleCreationDontDependOnPlatform() {
        java.util.Locale.setDefault(new java.util.Locale("fr"));
        Locale esARLocale = localeFactory.createFromLanguageAndRegion("es", "ar");
        assertLocale(esARLocale, "es-AR", "español (Argentina)", "Spanish (Argentina)");

        java.util.Locale.setDefault(new java.util.Locale("en"));
        esARLocale = localeFactory.createFromLanguageAndRegion("es", "ar");
        assertLocale(esARLocale, "es-AR", "español (Argentina)", "Spanish (Argentina)");
    }

    @Test
    public void testCreateLocaleWithLanguageAndCountry() {
        Locale frLocale = localeFactory.createFromCode("fr");
        assertLocale(frLocale, "fr", "français", "French");

        Locale locale = localeFactory.createFromCode("fr-BE");
        assertLocale(locale, "fr-BE", "français (Belgique)", "French (Belgium)");
    }

    private void assertLocale(Locale locale, String code, String language, String englishLanguage) {
        Assertions.assertThat(locale.getEntityId()).isEqualTo(code);
        Assertions.assertThat(locale.getLanguage()).isEqualTo(language);
        Assertions.assertThat(locale.getEnglishLanguage()).isEqualTo(englishLanguage);
    }
}
