/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.i18n.LocalizationService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyFactoryDefault;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test on ICUBasedLocalizationService.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
public class ICULocalizationServiceTest {
    private static final String FR_BE = "fr-BE";
    private static final String FR = "fr";
    private static final String KEY = "key";
    public static final String FR_TRANSLATION = "quatre-vingt-dix";
    public static final String TRANSLATION_WITH_ARGUMENTS = "Bonjour {0}";

    private LocalizationService localizationService;
    private KeyRepository keyRepository;
    private ICULocaleService localeService;
    private KeyFactory keyFactory = new KeyFactoryDefault();

    @Before
    public void before() {
        localeService = mock(ICULocaleService.class);
        mockLocaleService();
        keyRepository = mock(KeyRepository.class);
        localizationService = new ICULocalizationService(localeService, keyRepository);
    }

    @Test
    public void localization_should_use_locale_fallback() {
        addTranslation(FR, FR_TRANSLATION);

        String localizedMessage = localizationService.localize(FR_BE, KEY);

        Assertions.assertThat(localizedMessage).isEqualTo(FR_TRANSLATION);
    }

    @Test
    public void localization_key_not_found() {
        when(keyRepository.load(KEY)).thenReturn(null);

        String localize = localizationService.localize(FR_BE, KEY);

        Assertions.assertThat(localize).isEqualTo("[key]");
    }

    @Test
    public void localization_translation_not_found() {
        when(keyRepository.load(KEY)).thenReturn(keyFactory.createKey(KEY));

        String localize = localizationService.localize(FR_BE, KEY);

        Assertions.assertThat(localize).isEqualTo("[key]");
    }

    @Test
    public void localization_with_arguments() {
        addTranslation(FR_BE, TRANSLATION_WITH_ARGUMENTS);

        String localize = localizationService.localize(FR_BE, KEY, "Bruxelles!");

        Assertions.assertThat(localize).isEqualTo("Bonjour Bruxelles!");
    }

    private void addTranslation(String locale, String value) {
        Key key1 = keyFactory.createKey(KEY);
        key1.addTranslation(locale, value);
        when(keyRepository.load(KEY)).thenReturn(key1);
    }

    private void mockLocaleService() {
        when(localeService.getClosestLocale(FR_BE)).thenReturn(FR_BE);
        when(localeService.getClosestLocale(FR)).thenReturn(FR);
    }
}
