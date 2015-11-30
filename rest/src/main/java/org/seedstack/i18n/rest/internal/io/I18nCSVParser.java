/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.io;

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

import static org.seedstack.i18n.rest.internal.io.I18nCSVTemplateLoader.KEY;

/**
 * Custom parser for SuperCSV which allow to parse i18n files (containing dynamic columns).
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@Named(I18nCSVParser.I18N_PARSER)
class I18nCSVParser extends AbstractTemplateParser<SuperCsvTemplate, I18nCSVRepresentation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18nCSVParser.class);
    public static final String I18N_PARSER = "i18nSuperCSV";

    @Override
    public List<I18nCSVRepresentation> parse(InputStream inputStream, Class<I18nCSVRepresentation> clazz) {
        if (inputStream == null || clazz == null) {
            throw new NullPointerException();
        }

        ICsvMapReader mapReader = null;
        InputStreamReader inputStreamReader = null;
        List<I18nCSVRepresentation> i18nCSVRepresentations = new ArrayList<I18nCSVRepresentation>();

        try {
            inputStreamReader = new InputStreamReader(inputStream, template.getCharsetName());
            mapReader = new CsvMapReader(inputStreamReader, template.getPreferences());

            String[] headers = mapReader.getHeader(true);
            if (fileIsNotEmpty(headers)) {
                i18nCSVRepresentations = parseLines(mapReader, headers);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            closeQuietly(inputStreamReader);
            closeQuietly(mapReader);
        }

        return i18nCSVRepresentations;
    }

    private boolean fileIsNotEmpty(String[] headers) {
        return headers != null && headers.length > 0;
    }

    private List<I18nCSVRepresentation> parseLines(ICsvMapReader mapReader, String[] headers) throws IOException {
        List<I18nCSVRepresentation> i18nCSVRepresentations = new ArrayList<I18nCSVRepresentation>();
        final CellProcessor[] processors = getCellProcessorPerColumn(headers);

        Map<String, Object> beanMap;
        while ((beanMap = mapReader.read(headers, processors)) != null) {
            I18nCSVRepresentation i18nCSVRepresentation = convertToRepresentation(beanMap);
            i18nCSVRepresentations.add(i18nCSVRepresentation);
        }
        return i18nCSVRepresentations;
    }

    private CellProcessor[] getCellProcessorPerColumn(String[] headers) {
        final CellProcessor[] processors = new CellProcessor[headers.length];
        for (int i = 0; i < processors.length; i++) {
            processors[i] = new Optional();
        }
        return processors;
    }

    private I18nCSVRepresentation convertToRepresentation(Map<String, Object> beanMap) {
        I18nCSVRepresentation representation = new I18nCSVRepresentation();
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
        Map<String, String> translations = new HashMap<String, String>(beanMap.size());
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
