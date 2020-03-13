/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.infrastructure.service;

import javax.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.LocalizationService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class LocalizationServiceFallbackIT {

    @Inject
    private LocalizationService localizationService;

    @Inject
    private LocaleService localeService;

    @Inject
    private KeyRepository keyRepository;

    @Inject
    private KeyFactory keyFactory;

    @Test
    public void localize_should_fallback_to_parent_locale() {
        localeService.addLocale("fr");
        localeService.addLocale("fr-BE");

        Key key = keyFactory.createKey("key");
        key.addTranslation("fr", "youpi");
        keyRepository.add(key);

        String actualTranslation = localizationService.localize("fr-BE", "key");

        Assertions.assertThat(actualTranslation).isEqualTo("youpi");
    }

    @Test
    public void localize_fallback_without_translation() {
        localeService.addLocale("fr");
        localeService.addLocale("fr-BE");

        String actualTranslation = localizationService.localize("fr-BE", "key");

        Assertions.assertThat(actualTranslation).isEqualTo("[key]");
    }

    @After
    public void clean() {
        for (Key key : keyRepository.loadAll()) {
            keyRepository.remove(key);
        }
        for (String locale : localeService.getAvailableLocales()) {
            localeService.deleteLocale(locale);
        }

    }
}
