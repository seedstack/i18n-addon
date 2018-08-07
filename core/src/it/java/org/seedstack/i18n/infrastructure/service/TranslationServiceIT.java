/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.infrastructure.service;

import java.util.Map;
import javax.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.internal.domain.service.TranslationService;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class TranslationServiceIT {

    public static final String EN = "en";
    public static final String EN_US = "en-US";

    @Inject
    private KeyRepository keyRepository;
    @Inject
    private KeyFactory keyFactory;
    @Inject
    private TranslationService translationService;
    @Inject
    private LocaleService localeService;

    @Before
    public void setUp() throws Exception {
        localeService.addLocale(EN_US);
    }

    @Test
    public void testGetEmptyWhenNoTranslations() {
        Map<String, String> translations = translationService.getTranslationsForLocale(EN_US);

        Assertions.assertThat(translations).isNotNull();
        Assertions.assertThat(translations).isEmpty();
    }

    @Test
    public void testGetAllTheTranslationForALocale() {
        givenTheTranslatedKey("name1", EN_US, "my translation");
        givenTheTranslatedKey("name2", EN_US, "my second translation");

        Map<String, String> translations = translationService.getTranslationsForLocale(EN_US);

        Assertions.assertThat(translations.size()).isEqualTo(2);
    }

    @Test
    public void testGetTranslationsWithFallback() {
        givenTheTranslatedKey("name3", EN, "my translation");
        givenTheTranslatedKey("name4", EN_US, "my second translation");

        Map<String, String> translations = translationService.getTranslationsForLocale(EN_US);

        Assertions.assertThat(translations.size()).isEqualTo(2);
    }

    private void givenTheTranslatedKey(String keyName, String locale, String translation) {
        Key key = keyFactory.createKey(keyName);
        key.addTranslation(locale, translation);
        keyRepository.addOrUpdate(key);
    }

    @After
    public void clean() {
        for (String locale : localeService.getAvailableLocales()) {
            localeService.deleteLocale(locale);
        }
        keyRepository.clear();
    }
}