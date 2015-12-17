/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.internal.domain.model.locale.LocaleFactory;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.i18n.internal.infrastructure.jpa.Units;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.DataManager;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Propagation;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@RunWith(SeedITRunner.class)
public class LocaleImportExportIT {

    private static final String EXPECTED_DATA = "[{\"group\":\"seed-i18n\",\"name\":\"locale\",\"items\":[" +
            "{\"code\":\"en\",\"language\":\"English\",\"englishLanguage\":\"English\",\"defaultLocale\":false}," +
            "{\"code\":\"fr\",\"language\":\"français\",\"englishLanguage\":\"French\",\"defaultLocale\":false}" +
            "]}]";

    @Inject
    private LocaleFactory localeFactory;
    @Inject
    private LocaleRepository localeRepository;
    @Inject
    private DataManager dataManager;

    @Test
    public void testExport() throws Exception {
        givenPersistedLocales();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        dataManager.exportData(outputStream, "seed-i18n", "locale");

        assertThat(new String(outputStream.toByteArray(), "UTF-8")).isEqualTo(EXPECTED_DATA);
    }

    @Transactional
    @JpaUnit(Units.I18N)
    @Test
    public void testImport() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(EXPECTED_DATA.getBytes());

        dataManager.importData(inputStream, "seed-i18n", "locale", true);

        assertThat(localeRepository.count()).isEqualTo(2);
        assertThat(localeRepository.load("fr")).isNotNull();
        assertThat(localeRepository.load("en")).isNotNull();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @JpaUnit(Units.I18N)
    public void givenPersistedLocales() {
        localeRepository.persist(localeFactory.createFromCode("fr"));
        localeRepository.persist(localeFactory.createFromCode("en"));
    }
}
