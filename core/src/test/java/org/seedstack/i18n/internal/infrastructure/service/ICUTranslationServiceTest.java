/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import com.google.common.collect.Lists;
import com.ibm.icu.util.ULocale;
import org.seedstack.i18n.LocalizationService;
import org.seedstack.i18n.internal.TranslationService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyFactoryDefault;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 26/06/2014
 */
public class ICUTranslationServiceTest {

    private LocalizationService localizationService;
    private TranslationService translationService;
    private KeyRepository keyRepository;
    private ICULocaleService localeService;

    @Before
    public void setUp() {
        localeService = mock(ICULocaleService.class);
        keyRepository = mock(KeyRepository.class);
        localizationService = new ICULocalizationService(localeService, keyRepository);
        translationService = new ICUTranslationService(keyRepository, localizationService);
    }

    @Test
    public void localization_should_use_locale_fallback() {
        KeyFactory keyFactory = new KeyFactoryDefault();
        String fr_be = "fr_BE";
        String fr = "fr";
        String keyName1 = "key1";
        String keyName2 = "key2";
        when(localeService.getClosestULocale(fr_be)).thenReturn(new ULocale(fr_be));
        when(localeService.getClosestULocale("fr")).thenReturn(new ULocale(fr));

        // key with fr translation
        Key key1 = keyFactory.createKey(keyName1);
        String expectedTranslationFr = "youpi";
        key1.addTranslation("fr", expectedTranslationFr, false, false);

        // key with fr-BE translation
        Key key2 = keyFactory.createKey(keyName2);
        String expectedTranslationFrBE = "youpi une fois";
        key2.addTranslation("fr_BE", expectedTranslationFrBE, false, false);

        when(keyRepository.loadAll()).thenReturn(Lists.newArrayList(key1, key2));
        Map<String, String> translationsForLocale = translationService.getTranslationsForLocale(fr_be);
        Assertions.assertThat(translationsForLocale.get(keyName1)).isEqualTo(expectedTranslationFr);
        Assertions.assertThat(translationsForLocale.get(keyName2)).isEqualTo(expectedTranslationFrBE);
    }
}
