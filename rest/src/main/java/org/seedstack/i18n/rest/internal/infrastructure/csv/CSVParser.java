/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.csv;

import org.seedstack.i18n.rest.internal.io.CSVRepresentation;
import org.seedstack.io.spi.AbstractTemplateParser;
import org.seedstack.io.supercsv.SuperCsvTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;

import javax.inject.Named;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.seedstack.i18n.rest.internal.infrastructure.csv.I18nCSVTemplateLoader.KEY;

/**
 * Custom parser for SuperCSV which allow to parse i18n files (containing dynamic columns).
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@Named(CSVParser.I18N_PARSER)
class CSVParser extends AbstractTemplateParser<SuperCsvTemplate, CSVRepresentation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVParser.class);
    public static final String I18N_PARSER = "i18nSuperCSV";

    @Override
    public List<CSVRepresentation> parse(InputStream inputStream, Class<CSVRepresentation> clazz) {
        if (inputStream == null || clazz == null) {
            throw new NullPointerException();
        }

        ICsvMapReader mapReader = null;
        InputStreamReader inputStreamReader = null;
        List<CSVRepresentation> CSVRepresentations = new ArrayList<>();

        try {
            inputStreamReader = new InputStreamReader(inputStream, template.getCharsetName());
            mapReader = new CsvMapReader(inputStreamReader, template.getPreferences());

            String[] headers = mapReader.getHeader(true);
            if (fileIsNotEmpty(headers)) {
                CSVRepresentations = parseLines(mapReader, headers);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            closeQuietly(inputStreamReader);
            closeQuietly(mapReader);
        }

        return CSVRepresentations;
    }

    private boolean fileIsNotEmpty(String[] headers) {
        return headers != null && headers.length > 0;
    }

    private List<CSVRepresentation> parseLines(ICsvMapReader mapReader, String[] headers) throws IOException {
        List<CSVRepresentation> CSVRepresentations = new ArrayList<>();
        final CellProcessor[] processors = getCellProcessorPerColumn(headers);

        Map<String, Object> beanMap;
        while ((beanMap = mapReader.read(headers, processors)) != null) {
            CSVRepresentation CSVRepresentation = convertToRepresentation(beanMap);
            CSVRepresentations.add(CSVRepresentation);
        }
        return CSVRepresentations;
    }

    private CellProcessor[] getCellProcessorPerColumn(String[] headers) {
        final CellProcessor[] processors = new CellProcessor[headers.length];
        for (int i = 0; i < processors.length; i++) {
            processors[i] = new Optional();
        }
        return processors;
    }

    private CSVRepresentation convertToRepresentation(Map<String, Object> beanMap) {
        CSVRepresentation representation = new CSVRepresentation();
        representation.setKey(parseKey(beanMap));
        representation.setValue(parseTranslations(beanMap));
        return representation;
    }

    private String parseKey(Map<String, Object> beanMap) {
        String key = (String) beanMap.get(KEY);
        // The key should be removed from the map in order to be able
        // to iterate over the map for parsing the translations.
        beanMap.remove(KEY);
        return key;
    }

    private Map<String, String> parseTranslations(Map<String, Object> beanMap) {
        Map<String, String> translations = new HashMap<>(beanMap.size());
        for (Map.Entry<String, Object> translation : beanMap.entrySet()) {
            translations.put(translation.getKey(), (String) translation.getValue());
        }
        return translations;
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
