/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit Test on LocaleService.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 15/07/2014
 */
public class LocaleServiceImplTest {

    private LocaleServiceImpl localeService;

    @Before
    public void setUp() {
        localeService = mock(LocaleServiceImpl.class);
    }

    @Test
    public void getClosestLocale_return_actual_locale() {
        when(localeService.isAvailable("fr_BE")).thenReturn(true);
        when(localeService.getDefaultLocale()).thenReturn("en");
        when(localeService.getClosestLocale("fr_BE")).thenCallRealMethod();
        when(localeService.getClosestULocale("fr_BE")).thenCallRealMethod();
        String zzz = localeService.getClosestLocale("fr_BE");
        Assertions.assertThat(zzz).isEqualTo("fr-BE");
    }

    @Test
    public void getClosestLocale_return_closest_locale() {
        when(localeService.getAvailableLocales()).thenReturn(Sets.newHashSet("en", "fr"));
        when(localeService.getDefaultLocale()).thenReturn("en");
        when(localeService.getClosestLocale("fr_BE")).thenCallRealMethod();
        when(localeService.getClosestULocale("fr_BE")).thenCallRealMethod();
        String zzz = localeService.getClosestLocale("fr_BE");
        Assertions.assertThat(zzz).isEqualTo("fr");
    }

    @Test
    public void getClosestLocale_return_default_when_no_closest_locale() {
        when(localeService.getDefaultLocale()).thenReturn("fr");
        when(localeService.getClosestLocale("zzz")).thenCallRealMethod();
        when(localeService.getClosestULocale("zzz")).thenCallRealMethod();
        String zzz = localeService.getClosestLocale("zzz");
        Assertions.assertThat(zzz).isEqualTo("fr");
    }

    @Test
    public void getClosestLocale_is_null_when_no_default_locale() {
        String zzz = localeService.getClosestLocale("zzz");
        Assertions.assertThat(zzz).isNull();
    }
}
