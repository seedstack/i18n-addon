/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.internal.TranslationService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com
 * Date: 05/12/13
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class TranslationServiceIT {

    public static final String EN_US = "en-US";

    @Inject
    private KeyRepository repository;

    @Inject
    private KeyFactory keyFactory;

    @Inject
    private TranslationService translationService;

    @Inject
    private LocaleService localeService;

    @Test
    public void translation_scenario() {
        localeService.addLocale(EN_US);

        createKeyWithTranslation("name1", EN_US, "my translation");
        createKeyWithTranslation("name2", EN_US, "my second translation");

        // Get all translations for a locale
        Map<String, String> requestedTranslations = translationService.getTranslationsForLocale(EN_US);
        Assertions.assertThat(requestedTranslations.size()).isEqualTo(2);
    }

    private void createKeyWithTranslation(String keyName, String locale, String translation) {
        Key key = keyFactory.createKey(keyName);
        key.setComment("comment");
        key.addTranslation(locale, translation);

        repository.persist(key);
    }

    @After
    public void clean() {
        for (String locale : localeService.getAvailableLocales()) {
            localeService.deleteLocale(locale);
        }
    }
}
