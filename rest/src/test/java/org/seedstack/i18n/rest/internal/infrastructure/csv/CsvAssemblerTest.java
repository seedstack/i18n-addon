/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.csv;

import org.junit.Before;
import org.junit.Test;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.rest.internal.infrastructure.csv.CSVImportServiceTest.KeyBuilder;
import org.seedstack.i18n.rest.internal.io.I18nCSVRepresentation;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class CsvAssemblerTest {

    private CsvAssembler underTest;
    private Key key;

    @Before
    public void setUp() throws Exception {
        underTest = new CsvAssembler();
        key = new Key("foo");
    }

    @Test
    public void testMergeWithEmptyRepresentation() {
        I18nCSVRepresentation emptyRepresentation = new I18nCSVRepresentation();

        underTest.mergeAggregateWithDto(key, emptyRepresentation);

        assertThat(key.getEntityId()).as("check entityId don't change").isEqualTo("foo");
    }

    @Test
    public void testMergeWithoutTranslations() {
        underTest.mergeAggregateWithDto(key, KeyBuilder.key("foo").build());
        assertThat(key.getEntityId()).as("check entityId don't change").isEqualTo("foo");
    }

    @Test
    public void testMergeWithTranslation() {
        I18nCSVRepresentation representation = KeyBuilder.key("foo")
                .with("en", "translation").with("fr", "traduction").build();

        underTest.mergeAggregateWithDto(key, representation);

        assertThat(key.isTranslated("en")).isTrue();
        assertThat(key.getTranslation("en").getValue()).isEqualTo("translation");
        assertThat(key.isTranslated("fr")).isTrue();
        assertThat(key.getTranslation("fr").getValue()).isEqualTo("traduction");
    }

    @Test
    public void testMergeIgnoreMissingTranslations() {
        I18nCSVRepresentation representation = KeyBuilder.key("foo")
                .with("en", "").with("fr", null).build();

        underTest.mergeAggregateWithDto(key, representation);

        assertThat(key.isTranslated("en")).isFalse();
        assertThat(key.isTranslated("fr")).isFalse();
    }

    @Test
    public void testMergeUpdateTranslation() {
        key.addTranslation("en", "translation");
        I18nCSVRepresentation representation = KeyBuilder.key("foo")
                .with("en", "newTranslation").build();

        underTest.mergeAggregateWithDto(key, representation);

        assertThat(key.getTranslation("en").getValue()).isEqualTo("newTranslation");
    }

    @Test
    public void testAssemble() throws Exception {
        I18nCSVRepresentation representation = underTest.assembleDtoFromAggregate(key);

        assertThat(representation.getKey()).isEqualTo("foo");
        assertThat(representation.getValue()).isNotNull();
        assertThat(representation.getValue()).isEmpty();
    }

    @Test
    public void testAssembleWithTranslations() throws Exception {
        key.addTranslation("en", "translation");
        key.addTranslation("fr", "traduction");

        I18nCSVRepresentation representation = underTest.assembleDtoFromAggregate(key);

        assertThat(representation.getValue()).containsEntry("en", "translation");
        assertThat(representation.getValue()).containsEntry("fr", "traduction");
    }
}
