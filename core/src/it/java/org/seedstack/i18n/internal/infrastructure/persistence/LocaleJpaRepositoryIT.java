/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.persistence;

import com.google.inject.Inject;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleFactory;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.persistence.jpa.api.JpaUnit;
import org.seedstack.seed.transaction.api.Transactional;

import java.util.List;

/**
 * Tests locale repository.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 20/11/13
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class LocaleJpaRepositoryIT {

    private static final String localeId = "fr";

    private Locale expectedLocale;

    @Inject
    private LocaleFactory factory;
    @Inject
    private LocaleRepository localeRepository;

    @Before
    public void setUp() {
        expectedLocale = factory.create(localeId, "français", "french", true);
    }

    @After
    public void cleanUp() {
        List<Locale> locales = localeRepository.loadAll();
        for (Locale locale : locales) {
            localeRepository.delete(locale);
        }
    }

    @Test
    public void persist_then_load() {
        localeRepository.persist(expectedLocale);
        Locale locale = localeRepository.load(expectedLocale.getEntityId());
        Assertions.assertThat(locale).isEqualTo(expectedLocale);
    }

    @Test
    public void count() {
        localeRepository.persist(expectedLocale);
        Long count = localeRepository.count();
        Assertions.assertThat(count).isEqualTo(1);
    }

    @Test
    public void save_then_load() {
        localeRepository.save(expectedLocale);
        Locale locale = localeRepository.load(expectedLocale.getEntityId());
        Assertions.assertThat(locale.getEnglishLanguage()).isEqualTo("french");
    }

    @Test
    public void persist_load_then_delete() {
        localeRepository.persist(expectedLocale);
        Locale locale = localeRepository.load(localeId);
        Assertions.assertThat(locale).isNotNull();
        localeRepository.delete(locale);
        locale = localeRepository.load(localeId);
        Assertions.assertThat(locale).isNull();
    }
}
