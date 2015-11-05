/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import com.ibm.icu.util.ULocale;
import org.seedstack.i18n.LocalizationService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyFactoryDefault;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test on ICUBasedLocalizationService.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 25/06/2014
 */
public class ICULocalizationServiceTest {

    private LocalizationService localizationService;
    private KeyRepository keyRepository;
    private ICULocaleService localeService;

    @Before
    public void setUp() {
        localeService = mock(ICULocaleService.class);
        keyRepository = mock(KeyRepository.class);
        localizationService = new ICULocalizationService(localeService, keyRepository);
    }

    @Test
    public void localization_should_use_locale_fallback() {
        KeyFactory keyFactory = new KeyFactoryDefault();
        String fr_be = "fr_BE";
        String fr = "fr";
        String key = "key";
        when(localeService.getClosestULocale(fr_be)).thenReturn(new ULocale(fr_be));
        when(localeService.getClosestULocale("fr")).thenReturn(new ULocale(fr));
        Key key1 = keyFactory.createKey(key);
        String expectedTranslation = "youpi";
        key1.addTranslation("fr", expectedTranslation, false, false);
        when(keyRepository.load(key)).thenReturn(key1);
        String localize = localizationService.localize(fr_be, key);
        Assertions.assertThat(localize).isEqualTo(expectedTranslation);
    }

    @Test
    public void localization_key_not_found() {
        String fr_be = "fr_BE";
        String fr = "fr";
        String key = "key";
        when(localeService.getClosestULocale(fr_be)).thenReturn(new ULocale(fr_be));
        when(localeService.getClosestULocale("fr")).thenReturn(new ULocale(fr));
        when(keyRepository.load(key)).thenReturn(null);
        String localize = localizationService.localize(fr_be, key);
        Assertions.assertThat(localize).isEqualTo("[key]");
    }

    @Test
    public void localization_translation_not_found() {
        KeyFactory keyFactory = new KeyFactoryDefault();
        String fr_be = "fr_BE";
        String fr = "fr";
        String key = "key";
        when(localeService.getClosestULocale(fr_be)).thenReturn(new ULocale(fr_be));
        when(localeService.getClosestULocale("fr")).thenReturn(new ULocale(fr));
        Key key1 = keyFactory.createKey(key);
        when(keyRepository.load(key)).thenReturn(key1);
        String localize = localizationService.localize(fr_be, key);
        Assertions.assertThat(localize).isEqualTo("[key]");
    }

    @Test
    public void localization_with_arguments() {
        KeyFactory keyFactory = new KeyFactoryDefault();
        String fr_be = "fr_BE";
        String fr = "fr";
        String key = "key";
        when(localeService.getClosestULocale(fr_be)).thenReturn(new ULocale(fr_be));
        when(localeService.getClosestULocale("fr")).thenReturn(new ULocale(fr));
        Key key1 = keyFactory.createKey(key);
        key1.addTranslation("fr", "hello {0}", false, false);
        when(keyRepository.load(key)).thenReturn(key1);
        String localize = localizationService.localize(fr_be, key, "world!");
        Assertions.assertThat(localize).isEqualTo("hello world!");
    }
}
