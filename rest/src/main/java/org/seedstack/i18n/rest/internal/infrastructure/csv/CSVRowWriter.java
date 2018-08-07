/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.csv;

import org.seedstack.io.supercsv.SuperCsvTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
class CSVRowWriter {
    private final Logger LOGGER = LoggerFactory.getLogger(CSVRowWriter.class);

    private boolean shouldPrintHeader;
    private final SuperCsvTemplate template;
    private final String[] columnNames;
    private final Map<String, String> columnValues = new HashMap<>();
    private final CellProcessor[] cellProcessors;

    public CSVRowWriter(SuperCsvTemplate template) {
        this.cellProcessors = getCellProcessors(template);
        this.columnNames = getColumnsFromTemplate(template);
        this.template = template;
    }

    private CellProcessor[] getCellProcessors(SuperCsvTemplate template) {
        final List<CellProcessor> confList = template.getWritingCellProcessors();
        return confList.toArray(new CellProcessor[confList.size()]);
    }

    private String[] getColumnsFromTemplate(SuperCsvTemplate template) {
        final String[] fields = template.getFields().toArray(new String[template.getFields().size()]);
        if (fields.length < 1) {
            throw new IllegalStateException("The CSV template should at least contains the column \"" + I18nCSVTemplateLoader.KEY + "\".");
        }
        return fields;
    }

    void write(OutputStream outputStream) {
        ICsvMapWriter mapWriter = null;
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, template.getCharsetName());
            mapWriter = new CsvMapWriter(outputStreamWriter, template.getPreferences());

            if (shouldPrintHeader) {
                mapWriter.writeHeader(columnNames);
            }

            mapWriter.write(columnValues, columnNames, cellProcessors);

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            closeQuietly(mapWriter);
        }
    }

    void shouldPrintHeader(boolean shouldPrintHeader) {
        this.shouldPrintHeader = shouldPrintHeader;
    }

    void addColumnValue(String columnName, String value) {
        columnValues.put(columnName, value);
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    private void closeQuietly(ICsvMapWriter mapWriter) {
        if (mapWriter != null) {
            try {
                mapWriter.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
