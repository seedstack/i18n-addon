/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleFactory;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;


/**
 * Unit Test on LocaleService.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 15/07/2014
 */
public class ICULocaleServiceTest {
    private static final String EN = "en";
    private static final String FR = "fr";
    private static final String FR_BE = "fr-BE";

    @Tested
    private ICULocaleService localeService;
    @Injectable
    private LocaleRepository localeRepository;
    @Injectable
    private LocaleFactory localeFactory;
    @Mocked
    private Locale defaultLocale;
    @Mocked
    private Locale locale;

    @Test
    public void testLocaleIsAvailableAcceptsNull() {
        boolean frIsAvailable = localeService.isAvailable(null);
        Assertions.assertThat(frIsAvailable).isFalse();
    }

    @Test
    public void testLocaleIsNotAvailable() {
        new Expectations() {
            {
                localeRepository.contains(FR);
                result = false;
            }
        };
        Assertions.assertThat(localeService.isAvailable(FR)).isFalse();
    }

    @Test
    public void testLocaleIsAvailable() {
        new Expectations() {
            {
                localeRepository.contains(FR);
                result = true;
            }
        };
        Assertions.assertThat(localeService.isAvailable(FR)).isTrue();
    }

    @Test
    public void getClosestLocaleReturnsActualLocale() {
        new Expectations() {
            {
                localeRepository.contains(FR_BE);
                result = true;
            }
        };

        String closestLocale = localeService.getClosestLocale(FR_BE);
        Assertions.assertThat(closestLocale).isEqualTo(FR_BE);
    }

    @Test
    public void getClosestLocale_return_closest_locale() {
        new Expectations() {
            {
                localeRepository.getDefaultLocale();
                result = defaultLocale;

                defaultLocale.getId();
                result = EN;

                localeRepository.loadAll();
                result = Lists.newArrayList(locale);

                locale.getId();
                result = FR;

                localeRepository.contains(FR_BE);
                result = false;
            }
        };

        String closestLocale = localeService.getClosestLocale(FR_BE);
        Assertions.assertThat(closestLocale).isEqualTo(FR);
    }

    @Test
    public void getClosestLocale_return_default_when_no_closest_locale() {
        new Expectations() {
            {
                localeRepository.getDefaultLocale();
                result = defaultLocale;

                defaultLocale.getId();
                result = FR;

                localeRepository.contains("zzz");
                result = false;
            }
        };
        String zzz = localeService.getClosestLocale("zzz");
        Assertions.assertThat(zzz).isEqualTo(FR);
    }

    @Test
    public void getClosestLocale_is_null_when_no_default_locale() {
        new Expectations() {
            {
                localeRepository.contains("zzz");
                result = false;
            }
        };
        String zzz = localeService.getClosestLocale("zzz");
        Assertions.assertThat(zzz).isNull();
    }
}
