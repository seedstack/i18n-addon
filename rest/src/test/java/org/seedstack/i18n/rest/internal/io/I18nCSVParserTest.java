/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.io;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.seedstack.io.supercsv.SuperCsvTemplate;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class I18nCSVParserTest {

    private static final String FILE_CONTENT = "key;en;fr\nkey1;t9nEn;t9nFr\nkey2;t9nEn;t9nFr\n";
    private final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(FILE_CONTENT.getBytes());
    private I18nCSVParser underTest = new I18nCSVParser();

    @Test(expected = NullPointerException.class)
    public void testNullInputStream() throws Exception {
        underTest.parse(null, I18nCSVRepresentation.class);
    }

    @Test(expected = NullPointerException.class)
    public void testNullClass() throws Exception {
        underTest.parse(byteArrayInputStream, null);
    }

    @Test
    public void parseSuccessfully() {
        underTest.setTemplate(new SuperCsvTemplate("name"));

        List<I18nCSVRepresentation> i18nCSVRepresentations = underTest.parse(byteArrayInputStream, I18nCSVRepresentation.class);

        Assertions.assertThat(i18nCSVRepresentations.size()).isEqualTo(2);
        I18nCSVRepresentation i18nCSVRepresentation = i18nCSVRepresentations.iterator().next();
        Assertions.assertThat(i18nCSVRepresentation.getKey()).isEqualTo("key1");
        Assertions.assertThat(i18nCSVRepresentation.getValue().size()).isEqualTo(2);
        Assertions.assertThat(i18nCSVRepresentation.getValue().get("en")).isEqualTo("t9nEn");
        Assertions.assertThat(i18nCSVRepresentation.getValue().get("fr")).isEqualTo("t9nFr");
    }

    @Test
    public void parseSuccessfullyWithMissingTranslations() {
        final String FILE_CONTENT = "key;en;fr\nkey1;;t9nFr\nkey2;;t9nFr\n";
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(FILE_CONTENT.getBytes());

        underTest.setTemplate(new SuperCsvTemplate("name"));

        List<I18nCSVRepresentation> i18nCSVRepresentations = underTest.parse(byteArrayInputStream, I18nCSVRepresentation.class);

        Assertions.assertThat(i18nCSVRepresentations.size()).isEqualTo(2);
        I18nCSVRepresentation i18nCSVRepresentation = i18nCSVRepresentations.get(0);
        Assertions.assertThat(i18nCSVRepresentation.getKey()).isEqualTo("key1");
        Assertions.assertThat(i18nCSVRepresentation.getValue().size()).isEqualTo(2);
        Assertions.assertThat(i18nCSVRepresentation.getValue().get("en")).isNull();
        Assertions.assertThat(i18nCSVRepresentation.getValue().get("fr")).isEqualTo("t9nFr");
    }
}
