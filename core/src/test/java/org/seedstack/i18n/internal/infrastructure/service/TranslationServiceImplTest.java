/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import static org.junit.Assert.fail;

import com.google.common.collect.Lists;
import java.util.Map;
import java.util.Optional;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.i18n.I18nConfig;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class TranslationServiceImplTest {
    private static final String FR_TRANSLATION = "quatre-vingt-dix";
    private static final String FR_BE_TRANSLATION = "Nonante";
    private static final String EN_TRANSLATION = "ninety";
    private static final String EN = "en";
    private static final String FR_BE = "fr-BE";
    private static final String FR = "fr";
    private static final String KEY = "key";
    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    @Tested
    private TranslationServiceImpl underTest;
    @Mocked
    private LocaleService localeService;
    @Mocked
    private KeyRepository keyRepository;

    @Before
    public void before() {
        underTest = new TranslationServiceImpl(keyRepository, localeService);
    }

    @Test
    public void testGetTranslationWithFallback() {
        addKeys(addTranslation(KEY, FR, FR_TRANSLATION));

        Optional<String> localizedMessage = underTest.getTranslationWithFallback(FR_BE, KEY);

        Assertions.assertThat(localizedMessage.isPresent()).isTrue();
        Assertions.assertThat(localizedMessage.get()).isEqualTo(FR_TRANSLATION);
    }

    private Key addTranslation(final String keyName, String locale, String value) {
        final Key key = new Key(keyName);
        key.addTranslation(locale, value);
        new Expectations() {
            {
                keyRepository.get(keyName);
                result = Optional.of(key);
                minTimes = 0;
            }
        };
        return key;
    }

    private void addKeys(final Key... keys) {
        new Expectations() {
            {
                keyRepository.loadAll();
                result = Lists.newArrayList(keys);
                minTimes = 0;
            }
        };
    }

    @Test
    public void testGetTranslationWithMissingKey() {
        Deencapsulation.setField(underTest, "i18nConfig", new I18nConfig());
        Optional<String> localizedMessage = underTest.getTranslationWithFallback(FR_BE, KEY);

        Assertions.assertThat(localizedMessage.isPresent()).isFalse();
    }

    @Test
    public void getTranslationsNeverReturnNull() {
        Map<String, String> translationsForLocale = underTest.getTranslationsForLocale(FR_BE);

        Assertions.assertThat(translationsForLocale).isNotNull();
        Assertions.assertThat(translationsForLocale).hasSize(0);
    }

    @Test
    public void testGetTranslations() {
        addKeys(
                addTranslation(KEY1, FR_BE, FR_BE_TRANSLATION),
                addTranslation(KEY2, FR_BE, FR_BE_TRANSLATION)
        );

        Map<String, String> translationsForLocale = underTest.getTranslationsForLocale(FR_BE);

        Assertions.assertThat(translationsForLocale).hasSize(2);
        Assertions.assertThat(translationsForLocale).containsEntry(KEY1, FR_BE_TRANSLATION);
        Assertions.assertThat(translationsForLocale).containsEntry(KEY2, FR_BE_TRANSLATION);
    }

    @Test
    public void testGetTranslationsWithFallBack() {
        addKeys(addTranslation(KEY1, FR, FR_TRANSLATION));

        Map<String, String> translationsForLocale = underTest.getTranslationsForLocale(FR_BE);

        Assertions.assertThat(translationsForLocale).containsEntry(KEY1, FR_TRANSLATION);
    }

    @Test
    public void testGetTranslationsWithFallBackToDefault() {
        addKeys(addTranslation(KEY1, EN, EN_TRANSLATION));
        Deencapsulation.setField(underTest, "i18nConfig", new I18nConfig().setTranslationFallback(true));

        new Expectations() {
            {
                localeService.getDefaultLocale();
                result = EN;
            }
        };

        Map<String, String> translationsForLocale = underTest.getTranslationsForLocale(FR_BE);

        Assertions.assertThat(translationsForLocale).containsEntry(KEY1, EN_TRANSLATION);
    }

    @Test
    public void testGetTranslationsAllowMissingTranslations() {
        addKeys(
                addTranslation(KEY1, FR_BE, FR_BE_TRANSLATION),
                new Key(KEY2)
        );
        Deencapsulation.setField(underTest, "i18nConfig", new I18nConfig().setAllowMissingTranslations(true));

        Map<String, String> translationsForLocale = underTest.getTranslationsForLocale(FR_BE);

        Assertions.assertThat(translationsForLocale).hasSize(1);
        Assertions.assertThat(translationsForLocale).containsEntry(KEY1, FR_BE_TRANSLATION);
    }

    @Test
    public void testGetTranslationsDoNotAllowMissingTranslations() {
        addKeys(
                addTranslation(KEY1, FR_BE, FR_BE_TRANSLATION),
                new Key(KEY2)
        );
        Deencapsulation.setField(underTest, "i18nConfig", new I18nConfig().setAllowMissingTranslations(false));

        Map<String, String> translationsForLocale = underTest.getTranslationsForLocale(FR_BE);

        Assertions.assertThat(translationsForLocale).hasSize(2);
        Assertions.assertThat(translationsForLocale).containsEntry(KEY1, FR_BE_TRANSLATION);
        Assertions.assertThat(translationsForLocale).containsEntry(KEY2, "[key2]");
    }

    @Test
    public void testTranslateNullPreconditions() {
        try {
            underTest.translate(null, "foo", "bar");
            fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e).hasMessage("The key name can't be null or empty");
        }

        try {
            underTest.translate("foo", null, "bar");
            fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e).hasMessage("The locale can't be null or empty");
        }
    }

    @Test
    public void testTranslateEmptyPreconditions() {
        try {
            underTest.translate("", "foo", "bar");
            fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e).hasMessage("The key name can't be null or empty");
        }

        try {
            underTest.translate("foo", "", "bar");
            fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e).hasMessage("The locale can't be null or empty");
        }
    }

    @Test
    public void testTranslate() {
        final Key key = new Key(KEY1);
        new Expectations() {
            {
                keyRepository.get(KEY1);
                result = Optional.of(key);
            }
        };
        underTest.translate(KEY1, FR, FR_TRANSLATION);

        Assertions.assertThat(key.getTranslation(FR).getValue()).isEqualTo(FR_TRANSLATION);
    }

    @Test
    public void testUpdateDefaultLocaleTranslation() {
        Key key = addTranslation(KEY1, FR_BE, FR_BE_TRANSLATION);

        new Expectations() {
            {
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
                keyRepository.get(KEY1);
                result = Optional.of(key);
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
                keyRepository.get(KEY1);
                result = Optional.of(key);
            }
        };
        underTest.translate(KEY1, FR, FR_TRANSLATION);

        Assertions.assertThat(key.isOutdated()).isFalse();
    }
}
