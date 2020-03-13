/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.csv;

import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import mockit.Expectations;
import mockit.Mocked;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.assembler.dsl.MergeFromRepositoryOrFactory;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.io.CSVRepresentation;
import org.seedstack.io.Parser;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class CSVImportServiceTest {

    private CSVImportService csvImportService;

    @Mocked
    private Parser<CSVRepresentation> parser;
    @Mocked
    private KeyRepository keyRepository;
    @Mocked
    private FluentAssembler fluentAssembler;
    @Mocked
    private MergeFromRepositoryOrFactory mergeAggregate;
    private InputStream inputStream;

    @Before
    public void setUp() throws Exception {
        csvImportService = new CSVImportService(fluentAssembler, keyRepository, parser);
        inputStream = new ByteArrayInputStream("".getBytes());
    }

    @Test
    public void testImportEmptyInputStream() {
        givenEmptyStream();
        int importedKeys = csvImportService.importKeysWithTranslations(inputStream);
        Assertions.assertThat(importedKeys).isEqualTo(0);
    }

    private void givenEmptyStream() {
        new Expectations() {
            {
                parser.parse(inputStream, CSVRepresentation.class);
                result = Lists.newArrayList();
            }
        };
    }

    @Test
    public void testImportKeys() {
        givenParsedKeys(KeyBuilder.key("translation").with("fr", "tranduction").build());
        int importedKeys = csvImportService.importKeysWithTranslations(inputStream);
        Assertions.assertThat(importedKeys).isEqualTo(1);
    }

    private void givenParsedKeys(final CSVRepresentation... representations) {
        new Expectations() {
            {
                parser.parse(inputStream, CSVRepresentation.class);
                result = Lists.newArrayList(representations);

                mergeAggregate.orFromFactory();
                result = withAny(new Key("keyname"));
            }
        };
    }

    @Test
    public void testImportedKeyWasPersisted() {
        CSVRepresentation key1 = KeyBuilder.key("translation").with("fr", "tranduction").build();
        CSVRepresentation key2 = KeyBuilder.key("translation").with("fr", "tranduction").build();
        givenParsedKeys(key1, key2);

        thenKeyWasPersisted(2);

        csvImportService.importKeysWithTranslations(inputStream);
    }

    private void thenKeyWasPersisted(int numberOfKeys) {
        new Expectations() {
            {
                keyRepository.add(withAny(new Key("keyname")));
                times = 2;
            }
        };
    }

    static class KeyBuilder {

        private CSVRepresentation CSVRepresentation;

        public KeyBuilder(String key) {
            this.CSVRepresentation = new CSVRepresentation();
            this.CSVRepresentation.setKey(key);
        }

        public static KeyBuilder key(String key) {
            return new KeyBuilder(key);
        }

        public KeyBuilder with(String locale, String translation) {
            Map<String, String> translationsByLocale = this.CSVRepresentation.getValue();
            if (translationsByLocale == null) {
                translationsByLocale = new HashMap<>();
            }
            translationsByLocale.put(locale, translation);
            this.CSVRepresentation.setValue(translationsByLocale);
            return this;
        }

        public CSVRepresentation build() {
            return CSVRepresentation;
        }
    }
}
