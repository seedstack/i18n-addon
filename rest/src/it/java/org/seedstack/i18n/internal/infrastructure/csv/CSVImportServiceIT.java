/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.internal.infrastructure.csv;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.io.ImportService;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class CSVImportServiceIT {

    @Inject
    private ImportService csvImportService;
    @Inject
    private KeyRepository keyRepository;

    private String keyName;

    @Before
    public void setUp() throws Exception {
        keyName = UUID.randomUUID().toString();
        System.out.printf(keyName);
    }

    @Test
    public void testServiceIsInjectable() throws Exception {
        assertThat(csvImportService).isNotNull();
    }

    @Test
    public void testImportEmptyStream() throws Exception {
        InputStream emptyInputStream = new ByteArrayInputStream("".getBytes());
        int importedKeys = csvImportService.importKeysWithTranslations(emptyInputStream);
        assertThat(importedKeys).isEqualTo(0);
    }

    private ByteArrayInputStream givenCsvWithKeys() {
        String content = "key;en;fr\n" + keyName + ";bar;\n";
        return new ByteArrayInputStream(content.getBytes());
    }

    @Test
    public void testImportKeys() throws Exception {
        InputStream emptyInputStream = givenCsvWithKeys();
        int importedKeys = csvImportService.importKeysWithTranslations(emptyInputStream);
        assertThat(importedKeys).isEqualTo(1);
    }

    @Test
    public void testImportKeysHasBeenPersisted() throws Exception {
        InputStream emptyInputStream = givenCsvWithKeys();

        csvImportService.importKeysWithTranslations(emptyInputStream);

        Key foo = keyRepository.get(keyName).orElse(null);
        assertThat(foo).isNotNull();
        assertThat(foo.getId()).isEqualTo(keyName);
        assertThat(foo.getTranslation("en").getValue()).isEqualTo("bar");
    }

    @Test
    public void testDoNotImportEmptyTranslation() throws Exception {
        InputStream emptyInputStream = givenCsvWithKeys();

        csvImportService.importKeysWithTranslations(emptyInputStream);

        Key foo = keyRepository.get(keyName).orElse(null);
        assertThat(foo).isNotNull();
        assertThat(foo.isTranslated("fr")).isFalse();
    }

    @Test
    public void testReImportKeysWorks() throws Exception {
        csvImportService.importKeysWithTranslations(givenCsvWithKeys());
        csvImportService.importKeysWithTranslations(givenCsvWithKeys());
    }
}
