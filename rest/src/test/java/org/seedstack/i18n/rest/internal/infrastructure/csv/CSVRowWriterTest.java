/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.csv;

import java.io.ByteArrayOutputStream;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.io.supercsv.Column;
import org.seedstack.io.supercsv.SuperCsvTemplate;
import org.supercsv.cellprocessor.Optional;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class CSVRowWriterTest {

    private CSVRowWriter underTest;
    private SuperCsvTemplate template = new SuperCsvTemplate("name");

    @Before
    public void setUp() {
        addColumn("key");
        addColumn("fr");
        addColumn("en");
        underTest = new CSVRowWriter(template);
    }

    @Test(expected = IllegalStateException.class)
    public void testConstructorMissingKeyColumn() {
        new CSVRowWriter(new SuperCsvTemplate("name"));
    }

    @Test
    public void testAddColumnValue() throws Exception {
        underTest.addColumnValue("fr", "fromage");
        underTest.addColumnValue("en", "pancake");

        underTest.addColumnValue("it", "pizza");
    }

    @Test
    public void testGetColumnNames() throws Exception {
        Assertions.assertThat(underTest.getColumnNames()).hasSize(3);
    }

    @Test
    public void testWrite() throws Exception {
        underTest.addColumnValue("key", "speciality");
        underTest.addColumnValue("fr", "fromage");
        underTest.addColumnValue("en", "pancake");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        underTest.write(outputStream);
        Assertions.assertThat(outputStream.toString()).isEqualTo("speciality;fromage;pancake\n");
    }

    @Test
    public void testWriteWithMissingTranslation() throws Exception {
        underTest.addColumnValue("key", "speciality");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        underTest.write(outputStream);
        Assertions.assertThat(outputStream.toString()).isEqualTo("speciality;;\n");
    }

    @Test
    public void testWriteWithHeader() throws Exception {
        underTest.addColumnValue("key", "speciality");
        underTest.addColumnValue("fr", "fromage");
        underTest.addColumnValue("en", "pancake");

        underTest.shouldPrintHeader(true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        underTest.write(outputStream);
        Assertions.assertThat(outputStream.toString()).isEqualTo("key;fr;en\nspeciality;fromage;pancake\n");
    }

    private void addColumn(String name) {
        template.addColumn(new Column(name, name, new Optional(), new Optional()));
    }
}
