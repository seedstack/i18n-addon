/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.csv;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.seedstack.i18n.rest.internal.io.CSVRepresentation;
import org.seedstack.io.supercsv.SuperCsvTemplate;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class I18nCSVParserTest {

    private static final String FILE_CONTENT = "key;en;fr\nkey1;t9nEn;t9nFr\nkey2;t9nEn;t9nFr\n";
    private final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(FILE_CONTENT.getBytes());
    private CSVParser underTest = new CSVParser();

    @Test(expected = NullPointerException.class)
    public void testNullInputStream() throws Exception {
        underTest.parse(null, CSVRepresentation.class);
    }

    @Test(expected = NullPointerException.class)
    public void testNullClass() throws Exception {
        underTest.parse(byteArrayInputStream, null);
    }

    @Test
    public void parseSuccessfully() {
        underTest.setTemplate(new SuperCsvTemplate("name"));

        List<CSVRepresentation> CSVRepresentations = underTest.parse(byteArrayInputStream, CSVRepresentation.class);

        Assertions.assertThat(CSVRepresentations.size()).isEqualTo(2);
        CSVRepresentation CSVRepresentation = CSVRepresentations.iterator().next();
        Assertions.assertThat(CSVRepresentation.getKey()).isEqualTo("key1");
        Assertions.assertThat(CSVRepresentation.getValue().size()).isEqualTo(2);
        Assertions.assertThat(CSVRepresentation.getValue().get("en")).isEqualTo("t9nEn");
        Assertions.assertThat(CSVRepresentation.getValue().get("fr")).isEqualTo("t9nFr");
    }

    @Test
    public void parseSuccessfullyWithMissingTranslations() {
        final String FILE_CONTENT = "key;en;fr\nkey1;;t9nFr\nkey2;;t9nFr\n";
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(FILE_CONTENT.getBytes());

        underTest.setTemplate(new SuperCsvTemplate("name"));

        List<CSVRepresentation> CSVRepresentations = underTest.parse(byteArrayInputStream, CSVRepresentation.class);

        Assertions.assertThat(CSVRepresentations.size()).isEqualTo(2);
        CSVRepresentation CSVRepresentation = CSVRepresentations.get(0);
        Assertions.assertThat(CSVRepresentation.getKey()).isEqualTo("key1");
        Assertions.assertThat(CSVRepresentation.getValue().size()).isEqualTo(2);
        Assertions.assertThat(CSVRepresentation.getValue().get("en")).isNull();
        Assertions.assertThat(CSVRepresentation.getValue().get("fr")).isEqualTo("t9nFr");
    }
}
