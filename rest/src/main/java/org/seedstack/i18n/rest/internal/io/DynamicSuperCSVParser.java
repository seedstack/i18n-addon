/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.io;

import org.seedstack.io.RendererErrorCode;
import org.seedstack.io.spi.AbstractTemplateParser;
import org.seedstack.io.supercsv.SuperCsvTemplate;
import org.seedstack.seed.SeedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;

import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom parser for SuperCSV which allow to parse i18n files (containing dynamic columns).
 *
 * @param <T> object to parse
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 11/04/2014
 */
@Named("i18nSuperCSV")
public class DynamicSuperCSVParser<T> extends AbstractTemplateParser<SuperCsvTemplate, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicSuperCSVParser.class);
    private static final String PARAM = "param";

    @SuppressWarnings("unchecked")
    @Override
    public List<T> parse(InputStream inputStream, Class<T> clazz) {

        // Preconditions
        SeedException.createNew(RendererErrorCode.INCORRECT_PARAM).put(PARAM, clazz).throwsIf(!DataRepresentation.class.equals(clazz));
        SeedException.createNew(RendererErrorCode.INCORRECT_PARAM).put(PARAM, inputStream).throwsIfNull(inputStream);

        ICsvMapReader mapReader = null;
        List<T> dataRepresentations = new ArrayList<T>();
        try {
            mapReader = new CsvMapReader(new InputStreamReader(inputStream, template.getCharsetName()), template.getPreferences());

            // Read file header and use it to determine the number of column
            String[] headers = mapReader.getHeader(true);
            if (headers != null && headers.length > 0) {
                final CellProcessor[] processors = new CellProcessor[headers.length];
                for (int i = 0; i < processors.length; i++) {
                    processors[i] = new Optional();
                }

                // Read the input stream
                Map<String, Object> beanMap;
                while ((beanMap = mapReader.read(headers, processors)) != null) {
                    // Get a bean map and transform it in an data representation
                    DataRepresentation dataRepresentation = buildDataRepresentation(beanMap);
                    dataRepresentations.add((T) dataRepresentation);
                }
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            closeQuietly(mapReader);
        }
        return dataRepresentations;
    }

    private DataRepresentation buildDataRepresentation(Map<String, Object> beanMap) {
        DataRepresentation dataRepresentation = new DataRepresentation();
        dataRepresentation.setKey((String) beanMap.get("key"));
        beanMap.remove("key");
        Map<String, String> translations = new HashMap<String, String>(beanMap.size());
        for (Map.Entry<String, Object> translation : beanMap.entrySet()) {
            translations.put(translation.getKey(), (String) translation.getValue());
        }
        dataRepresentation.setValue(translations);
        return dataRepresentation;
    }

    private void closeQuietly(ICsvMapReader mapReader) {
        if (mapReader != null) {
            try {
                mapReader.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
