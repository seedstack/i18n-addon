/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.io;

import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.io.spi.AbstractTemplateRenderer;
import org.seedstack.io.supercsv.SuperCsvTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;

import javax.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 11/04/2014
 */
@Named("i18nSuperCSV")
public class DynamicSuperCSVRenderer extends AbstractTemplateRenderer<SuperCsvTemplate> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicSuperCSVRenderer.class);
    private static final String PRINT_HEADER = "printHeader";
    private static final String KEY = "key";

    @Override
    public void render(OutputStream outputStream, Object model) {
        render(outputStream, model, null, null);
    }

    @Override
    public void render(OutputStream outputStream, Object model, String mimeType, Map<String, Object> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("Missing 'parameters' attribute");
        }
        if (parameters.get(PRINT_HEADER) == null) {
            throw new IllegalArgumentException("Missing 'printHeader' boolean in parameters");
        }

        ICsvMapWriter mapWriter = null;
        try {
            mapWriter = new CsvMapWriter(new OutputStreamWriter(outputStream, template.getCharsetName()),
                    template.getPreferences());
            final List<CellProcessor> confList = template.getWritingCellProcessors();
            final CellProcessor[] processors = confList.toArray(new CellProcessor[confList.size()]);
            final String[] fields = template.getFields().toArray(new String[template.getFields().size()]);

            // Write the header
            if ((Boolean)parameters.get(PRINT_HEADER)) {
                mapWriter.writeHeader(fields);
            }

            // Write key with its requested translations
            if (model instanceof Key) {
                Key key = (Key) model;
                Map<String, String> map = new HashMap<String, String>(fields.length);
                for (String field : fields) {
                    if (KEY.equals(field)) {
                        map.put(KEY, ((Key) model).getEntityId());
                    } else {
                        if (!key.getTranslations().isEmpty() && key.getTranslation(field) != null) {
                            map.put(field, key.getTranslation(field).getValue());
                        }
                    }
                }
                mapWriter.write(map, fields, processors);
            } else {
                // This renderer is custom for Key
                throw new IllegalArgumentException(model.getClass().getSimpleName() + " was found. But only Key class is supported by this renderer");
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if( mapWriter != null ) {
                try {
                    mapWriter.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }
}
