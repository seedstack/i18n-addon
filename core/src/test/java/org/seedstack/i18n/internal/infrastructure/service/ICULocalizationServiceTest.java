/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mockit.Deencapsulation;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.i18n.I18nConfig;
import org.seedstack.i18n.LocalizationService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.internal.domain.service.TranslationService;

/**
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

    @Before
    public void before() {
        localeService = mock(ICULocaleService.class);
        mockLocaleService();
        keyRepository = mock(KeyRepository.class);
        TranslationService translationService = new TranslationServiceImpl(keyRepository, localeService);
        Deencapsulation.setField(translationService, "i18nConfig", new I18nConfig());
        localizationService = new ICULocalizationService(localeService, translationService);
    }

    @Test
    public void localization_should_use_locale_fallback() {
        addTranslation(FR, FR_TRANSLATION);

        String localizedMessage = localizationService.localize(FR_BE, KEY);

        Assertions.assertThat(localizedMessage).isEqualTo(FR_TRANSLATION);
    }

    private void addTranslation(String locale, String value) {
        Key key1 = new Key(KEY);
        key1.addTranslation(locale, value);
        when(keyRepository.get(KEY)).thenReturn(Optional.of(key1));
    }

    @Test
    public void localization_key_not_found() {
        when(keyRepository.get(KEY)).thenReturn(Optional.empty());

        String localize = localizationService.localize(FR_BE, KEY);

        Assertions.assertThat(localize).isEqualTo("[key]");
    }

    @Test
    public void localization_translation_not_found() {
        when(keyRepository.get(KEY)).thenReturn(Optional.of(new Key(KEY)));

        String localize = localizationService.localize(FR_BE, KEY);

        Assertions.assertThat(localize).isEqualTo("[key]");
    }

    @Test
    public void localization_with_arguments() {
        addTranslation(FR_BE, TRANSLATION_WITH_ARGUMENTS);

        String localize = localizationService.localize(FR_BE, KEY, "Bruxelles!");

        Assertions.assertThat(localize).isEqualTo("Bonjour Bruxelles!");
    }

    private void mockLocaleService() {
        when(localeService.getClosestLocale(FR_BE)).thenReturn(FR_BE);
        when(localeService.getClosestLocale(FR)).thenReturn(FR);
    }
}
