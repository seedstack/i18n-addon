/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.infrastructure.jpa;

import com.google.inject.Inject;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleFactory;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

/**
 * Tests locale repository.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class LocaleJpaRepositoryIT {

    private static final String localeId = "fr";

    private Locale expectedLocale;

    @Inject
    private LocaleFactory localeFactory;
    @Inject
    private LocaleRepository localeRepository;

    @Before
    public void setUp() {
        expectedLocale = localeFactory.createFromCode(localeId);
    }

    @After
    public void cleanUp() {
        List<Locale> locales = localeRepository.loadAll();
        for (Locale locale : locales) {
            localeRepository.remove(locale);
        }
    }

    @Test
    public void persist_then_load() {
        localeRepository.add(expectedLocale);
        Locale locale = localeRepository.get(expectedLocale.getId()).orElseThrow(IllegalArgumentException::new);
        Assertions.assertThat(locale).isEqualTo(expectedLocale);
    }

    @Test
    public void count() {
        localeRepository.add(expectedLocale);
        Long count = localeRepository.size();
        Assertions.assertThat(count).isEqualTo(1);
    }

    @Test
    public void save_then_load() {
        localeRepository.addOrUpdate(expectedLocale);
        Locale locale = localeRepository.get(expectedLocale.getId()).orElseThrow(IllegalArgumentException::new);
        Assertions.assertThat(locale.getEnglishLanguage()).isEqualTo("French");
    }

    @Test
    public void persist_load_then_delete() {
        localeRepository.add(expectedLocale);
        Locale locale = localeRepository.get(localeId).orElseThrow(IllegalArgumentException::new);
        Assertions.assertThat(locale).isNotNull();
        localeRepository.remove(locale);
        Assertions.assertThat(localeRepository.get(localeId)).isEmpty();
    }
}
