/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.infrastructure.service;

import java.util.Set;
import javax.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.LocaleService;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class LocaleServiceIT {

    @Inject
    public LocaleService localeService;

    @Test
    public void manage_default_locale() {
        String locale = "zh";
        // Add a new locale
        localeService.addLocale(locale);

        boolean isAvailable = localeService.isAvailable(locale);
        Assertions.assertThat(isAvailable).isEqualTo(true);

        Set<String> availableLocales = localeService.getAvailableLocales();
        Assertions.assertThat(availableLocales.size()).isGreaterThanOrEqualTo(1);

        // Set the locale as default locale
        localeService.changeDefaultLocaleTo(locale);

        // Check if default locale has changed
        String defaultLocale = localeService.getDefaultLocale();
        Assertions.assertThat(defaultLocale).isEqualTo(locale);

        // Delete the locale
        localeService.deleteLocale(locale);

        // Check that the locale has been deleted
        isAvailable = localeService.isAvailable(locale);
        Assertions.assertThat(isAvailable).isEqualTo(false);

        // Check if default locale has changed to default
        defaultLocale = localeService.getDefaultLocale();
        Assertions.assertThat(defaultLocale).isNull();
    }

    @Test
    public void multiple_locales() {
        localeService.addLocale("en");
        localeService.addLocale("en-US");
        localeService.addLocale("en-GB");
        Set<String> availableLocales = localeService.getAvailableLocales();
        Assertions.assertThat(availableLocales.size()).isEqualTo(3);
    }

    @Test
    public void multiple_locale_for_same_language() {
        localeService.addLocale("en");
        localeService.addLocale("en-US");

        String closestLocale = localeService.getClosestLocale("en");
        Assertions.assertThat(closestLocale).isEqualTo("en");

        closestLocale = localeService.getClosestLocale("en-US");
        Assertions.assertThat(closestLocale).isEqualTo("en-US");
    }

    @Test
    public void closest_locale_for_unavailable_locale() {
        localeService.addLocale("en");
        String closestLocale = localeService.getClosestLocale("en-CA");
        Assertions.assertThat(closestLocale).isEqualTo("en");
    }

    @Test
    public void closest_locale_for_unavailable_locale2() {
        localeService.addLocale("fr");
        localeService.addLocale("fr-FR");
        String closestLocale = localeService.getClosestLocale("fr-CA");
        Assertions.assertThat(closestLocale).isEqualTo("fr");
    }

    @Before
    public void clean_locales() {
        Set<String> availableLocales = localeService.getAvailableLocales();
        for (String availableLocale : availableLocales) {
            localeService.deleteLocale(availableLocale);
        }
    }

    @After
    public void clean() {
        for (String locale : localeService.getAvailableLocales()) {
            localeService.deleteLocale(locale);
        }

    }
}
