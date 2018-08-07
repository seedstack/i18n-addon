/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.data.DataManager;
import org.seedstack.i18n.internal.domain.model.locale.LocaleFactory;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.i18n.internal.infrastructure.jpa.Units;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Propagation;
import org.seedstack.seed.transaction.Transactional;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@RunWith(SeedITRunner.class)
public class LocaleImportExportIT {

    private static final String EXPECTED_DATA = "[{\"group\":\"seed-i18n\",\"name\":\"locales\",\"items\":[" +
            "{\"code\":\"en\",\"language\":\"English\",\"englishLanguage\":\"English\",\"defaultLocale\":false}," +
            "{\"code\":\"fr\",\"language\":\"français\",\"englishLanguage\":\"French\",\"defaultLocale\":false}" +
            "]}]";

    @Inject
    private LocaleFactory localeFactory;
    @Inject
    private LocaleRepository localeRepository;
    @Inject
    private DataManager dataManager;

    @Transactional
    @JpaUnit(Units.I18N)
    @Before
    public void setUp() throws Exception {
        localeRepository.clear();
    }

    @Transactional
    @JpaUnit(Units.I18N)
    @Test
    public void testExport() throws Exception {
        givenPersistedLocales();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        dataManager.exportData(outputStream, "seed-i18n", "locales");

        assertThat(new String(outputStream.toByteArray(), "UTF-8")).isEqualTo(EXPECTED_DATA);
    }

    @Transactional
    @JpaUnit(Units.I18N)
    @Test
    public void testImport() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(EXPECTED_DATA.getBytes());

        dataManager.importData(inputStream, "seed-i18n", "locales");

        assertThat(localeRepository.size()).isEqualTo(2);
        assertThat(localeRepository.get("fr")).isNotEmpty();
        assertThat(localeRepository.get("en")).isNotEmpty();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @JpaUnit(Units.I18N)
    public void givenPersistedLocales() {
        localeRepository.add(localeFactory.createFromCode("fr"));
        localeRepository.add(localeFactory.createFromCode("en"));
    }
}
