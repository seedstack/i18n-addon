/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.csv;

import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.business.assembler.dsl.MergeAggregateWithRepositoryThenFactoryProvider;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.io.I18nCSVRepresentation;
import org.seedstack.io.Parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@RunWith(JMockit.class)
public class CSVImportServiceTest {

    private CSVImportService csvImportService;

    @Mocked
    private Parser<I18nCSVRepresentation> parser;
    @Mocked
    private KeyRepository keyRepository;
    @Mocked
    private FluentAssembler fluentAssembler;
    @Mocked
    private MergeAggregateWithRepositoryThenFactoryProvider mergeAggregate;
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
                parser.parse(inputStream, I18nCSVRepresentation.class);
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

    private void givenParsedKeys(final I18nCSVRepresentation... representations) {
        new Expectations() {
            {
                parser.parse(inputStream, I18nCSVRepresentation.class);
                result = Lists.newArrayList(representations);

                mergeAggregate.orFromFactory();
                result = withAny(new Key("keyname"));
            }
        };
    }

    @Test
    public void testImportedKeyWasPersisted() {
        I18nCSVRepresentation key1 = KeyBuilder.key("translation").with("fr", "tranduction").build();
        I18nCSVRepresentation key2 = KeyBuilder.key("translation").with("fr", "tranduction").build();
        givenParsedKeys(key1, key2);

        thenKeyWasPersisted(2);

        csvImportService.importKeysWithTranslations(inputStream);
    }

    private void thenKeyWasPersisted(int numberOfKeys) {
        new Expectations() {
            {
                keyRepository.persist(withAny(new Key("keyname")));
                times = 2;
            }
        };
    }

    static class KeyBuilder {

        private I18nCSVRepresentation i18nCSVRepresentation;

        public KeyBuilder(String key) {
            this.i18nCSVRepresentation = new I18nCSVRepresentation();
            this.i18nCSVRepresentation.setKey(key);
        }

        public static KeyBuilder key(String key) {
            return new KeyBuilder(key);
        }

        public KeyBuilder with(String locale, String translation) {
            Map<String, String> translationsByLocale = this.i18nCSVRepresentation.getValue();
            if (translationsByLocale == null) {
                translationsByLocale = new HashMap<String, String>();
            }
            translationsByLocale.put(locale, translation);
            this.i18nCSVRepresentation.setValue(translationsByLocale);
            return this;
        }

        public I18nCSVRepresentation build() {
            return i18nCSVRepresentation;
        }
    }
}
