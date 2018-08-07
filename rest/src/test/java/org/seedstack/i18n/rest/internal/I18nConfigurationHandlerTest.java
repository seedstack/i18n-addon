/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.coffig.Coffig;
import org.seedstack.i18n.I18nConfig;
import org.seedstack.i18n.LocaleService;
import org.seedstack.seed.Application;

import java.util.HashMap;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class I18nConfigurationHandlerTest {
    private LocaleService localeService = mock(LocaleService.class);
    private Coffig coffig = mock(Coffig.class);
    private Application application = mock(Application.class);
    private I18nConfigurationHandler underTest;

    @Before
    public void setup() {
        when(application.getConfiguration()).thenReturn(coffig);
    }

    private void configure(String defaultLocale, String[] availableLocales, String[] additionalLocales) {
        when(localeService.getDefaultLocale()).thenReturn(defaultLocale);
        when(localeService.getAvailableLocales()).thenReturn(ImmutableSet.copyOf(availableLocales));
        when(coffig.get(I18nConfig.class)).thenReturn(new I18nConfig().addAdditionalLocales(additionalLocales));
        underTest = new I18nConfigurationHandler(application, localeService);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_additional_languages() {
        configure("fr-FR", new String[]{"en-GB", "fr-FR", "es-ES", "es-AR", "pt-PT", "pt-BR", "sk-SK", "ru-RU", "zh-CN", "fr-CN", "en-CN"}, new String[]{"fr-CN", "en-CN"});

        HashMap<String, Object> sourceConfiguration = new HashMap<>();
        underTest.overrideConfiguration(I18nConfigurationHandler.W20_CORE_FRAGMENT, I18nConfigurationHandler.CULTURE_MODULE, sourceConfiguration);

        // all languages are available
        assertThat((Set<String>) sourceConfiguration.get(I18nConfigurationHandler.AVAILABLE_CULTURES)).containsOnly("en-GB", "fr-FR", "es-ES", "es-AR", "pt-PT", "pt-BR", "sk-SK", "ru-RU", "zh-CN", "fr-CN", "en-CN");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_language_matching() {
        configure("fr-CA", new String[]{"en-GB", "fr-CA", "es-AR", "fr-XX"}, new String[]{});

        HashMap<String, Object> sourceConfiguration = new HashMap<>();
        underTest.overrideConfiguration(I18nConfigurationHandler.W20_CORE_FRAGMENT, I18nConfigurationHandler.CULTURE_MODULE, sourceConfiguration);

        // fr-XX is assimilated to fr in W20
        assertThat((Set<String>) sourceConfiguration.get(I18nConfigurationHandler.AVAILABLE_CULTURES)).containsOnly("en-GB", "fr-CA", "es-AR", "fr");
    }

    @Test
    public void most_specific_match_is_chosen() {
        configure("mn-Mong-CN-xx", new String[]{}, new String[]{});

        HashMap<String, Object> sourceConfiguration = new HashMap<>();
        underTest.overrideConfiguration(I18nConfigurationHandler.W20_CORE_FRAGMENT, I18nConfigurationHandler.CULTURE_MODULE, sourceConfiguration);

        assertThat((String) sourceConfiguration.get(I18nConfigurationHandler.DEFAULT_CULTURE)).isEqualTo("mn-Mong-CN");
    }
}
