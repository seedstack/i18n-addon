/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import com.google.common.collect.Lists;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;

import java.util.Map;

import static org.junit.Assert.fail;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@RunWith(JMockit.class)
public class TranslationServiceImplTest {

    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String FR = "fr";
    private static final String FR_BE = "fr-BE";
    public static final String FR_TRANSLATION = "quatre-vingt-dix";
    public static final String FR_BE_TRANSLATION = "Nonante";

    @Tested
    private TranslationServiceImpl underTest;
    @Mocked
    private ICULocalizationService localizationService;
    @Mocked
    private LocaleService localeService;
    @Mocked
    private KeyRepository keyRepository;

    @Before
    public void before() {
        underTest = new TranslationServiceImpl(keyRepository, localizationService, localeService);
    }

    @Test
    public void localizationWithoutKeyDoNotReturnNull() {
        Map<String, String> translationsForLocale = underTest.getTranslationsForLocale(FR_BE);

        Assertions.assertThat(translationsForLocale).isNotNull();
        Assertions.assertThat(translationsForLocale).hasSize(0);
    }

    @Test
    public void testLocalization() {
        new Expectations() {
            {
                Key key1 = new Key(KEY1);
                Key key2 = new Key(KEY2);
                key1.addTranslation(FR_BE, FR_BE_TRANSLATION);
                key2.addTranslation(FR_BE, FR_BE_TRANSLATION);

                keyRepository.loadAll();
                result = Lists.newArrayList(key1, key2);

                localizationService.localize(FR_BE, KEY1);
                result = FR_TRANSLATION;
                localizationService.localize(FR_BE, KEY2);
                result = FR_TRANSLATION;
            }
        };

        Map<String, String> translationsForLocale = underTest.getTranslationsForLocale(FR_BE);

        Assertions.assertThat(translationsForLocale).hasSize(2);
        Assertions.assertThat(translationsForLocale).containsEntry(KEY1, FR_TRANSLATION);
        Assertions.assertThat(translationsForLocale).containsEntry(KEY2, FR_TRANSLATION);
    }

    @Test
    public void testLocalizationWithMissingTranslations() {
        new Expectations() {
            {
                Key key1 = new Key(KEY1);
                Key key2 = new Key(KEY2);
                key1.addTranslation(FR_BE, FR_BE_TRANSLATION);

                keyRepository.loadAll();
                result = Lists.newArrayList(key1, key2);

                localizationService.localize(FR_BE, KEY1);
                result = FR_BE_TRANSLATION;
            }
        };

        Map<String, String> translationsForLocale = underTest.getTranslationsForLocale(FR_BE);

        Assertions.assertThat(translationsForLocale).hasSize(1);
        Assertions.assertThat(translationsForLocale).containsEntry(KEY1, FR_BE_TRANSLATION);
    }

    @Test
    public void testLocalizationWithAllowMissingTranslations() {
        new Expectations() {
            {
                Key key1 = new Key(KEY1);
                Key key2 = new Key(KEY2);
                key1.addTranslation(FR_BE, FR_BE_TRANSLATION);

                keyRepository.loadAll();
                result = Lists.newArrayList(key1, key2);

                localizationService.localize(FR_BE, KEY1);
                result = FR_BE_TRANSLATION;

                localizationService.localize(FR_BE, KEY2);
                result = FR_BE_TRANSLATION;
            }
        };
        Deencapsulation.setField(underTest, "allowMissingTranslation", true);

        Map<String, String> translationsForLocale = underTest.getTranslationsForLocale(FR_BE);

        Assertions.assertThat(translationsForLocale).hasSize(2);
        Assertions.assertThat(translationsForLocale).containsEntry(KEY1, FR_BE_TRANSLATION);
        Assertions.assertThat(translationsForLocale).containsEntry(KEY2, FR_BE_TRANSLATION);
    }

    @Test
    public void testTranslateNullPreconditions() {
        try {
            underTest.translate(null, "foo", "bar");
            fail();
        } catch (IllegalArgumentException e) { /**/ }
        try {
            underTest.translate("foo", null, "bar");
            fail();
        } catch (IllegalArgumentException e) { /**/ }

        underTest.translate("foo", "bar", null); // accepts null for translation
    }

    @Test
    public void testTranslateEmptyPreconditions() {
        try {
            underTest.translate("", "foo", "bar");
            fail();
        } catch (IllegalArgumentException e) { /**/ }
        try {
            underTest.translate("foo", "", "bar");
            fail();
        } catch (IllegalArgumentException e) { /**/ }

        underTest.translate("foo", "bar", ""); // accepts empty for translation
    }

    @Test
    public void testTranslate() {
        final Key key = new Key(KEY1);
        new Expectations() {
            {
                keyRepository.load(KEY1);
                result = key;
            }
        };
        underTest.translate(KEY1, FR, FR_TRANSLATION);

        Assertions.assertThat(key.getTranslation(FR).getValue()).isEqualTo(FR_TRANSLATION);
    }

    @Test
    public void testUpdateDefaultLocaleTranslation() {
        final Key key = new Key(KEY1);
        key.addTranslation(FR_BE, FR_BE_TRANSLATION);

        new Expectations() {
            {
                keyRepository.load(KEY1);
                result = key;

                localeService.getDefaultLocale();
                result = FR;
            }
        };
        underTest.translate(KEY1, FR, FR_TRANSLATION);

        Assertions.assertThat(key.getTranslation(FR).getValue()).isEqualTo(FR_TRANSLATION);
        Assertions.assertThat(key.isOutdated()).isTrue();
        Assertions.assertThat(key.getTranslation(FR).isOutdated()).isFalse();
        Assertions.assertThat(key.getTranslation(FR_BE).isOutdated()).isTrue();
    }

    @Test
    public void testUpdateOutdatedTranslations() {
        final Key key = new Key(KEY1);
        key.addTranslation(FR, "outdated");
        key.addTranslation(FR_BE, "outdated");
        key.setOutdated();

        new Expectations() {
            {
                keyRepository.load(KEY1);
                result = key;
            }
        };
        underTest.translate(KEY1, FR, FR_TRANSLATION);
        underTest.translate(KEY1, FR_BE, FR_BE_TRANSLATION);

        Assertions.assertThat(key.isOutdated()).isFalse();
        Assertions.assertThat(key.getTranslation(FR).isOutdated()).isFalse();
        Assertions.assertThat(key.getTranslation(FR_BE).isOutdated()).isFalse();
    }

    @Test
    public void testUpdateWithoutChangeDoNothing() {
        final Key key = new Key(KEY1);
        key.addTranslation(FR, FR_TRANSLATION);
        key.addTranslation(FR_BE, FR_BE_TRANSLATION);

        new Expectations() {
            {
                keyRepository.load(KEY1);
                result = key;
            }
        };
        underTest.translate(KEY1, FR, FR_TRANSLATION);

        Assertions.assertThat(key.isOutdated()).isFalse();
    }
}
