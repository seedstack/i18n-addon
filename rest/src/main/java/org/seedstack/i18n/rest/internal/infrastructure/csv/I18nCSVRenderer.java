/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.csv;

import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.io.spi.AbstractTemplateRenderer;
import org.seedstack.io.supercsv.SuperCsvTemplate;

import javax.inject.Named;
import java.io.OutputStream;
import java.util.Map;

/**
 * This class renders i18n keys in a CSV format. The number of columns depends on the number of locales.
 * The render method accepts one {@link Key} and prints it on one line.
 * <p>
 * When the renderer is called the first time, the parameters map should have the "printHeader" key set to true.
 * </p>
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@Named(I18nCSVRenderer.I18N_RENDERER)
class I18nCSVRenderer extends AbstractTemplateRenderer<SuperCsvTemplate> {

    public static final String I18N_RENDERER = "i18nSuperCSV";
    public static final String PRINT_HEADER = "printHeader";

    @Override
    public void render(OutputStream outputStream, Object model) {
        throw new UnsupportedOperationException("Use the method the render() method with the map parameter. " +
                "The map must contains a \"printHeader\" key with a boolean value.");
    }

    @Override
    public void render(OutputStream outputStream, Object model, String mimeType, Map<String, Object> parameters) {
        if (!(model instanceof Key)) {
            throw new IllegalArgumentException(model.getClass().getSimpleName() +
                    " was found. But only Key class is supported by this renderer");
        }
        if (parameters == null || !parameters.containsKey(PRINT_HEADER)) {
            throw new IllegalArgumentException("The parameters map should contains a key " +
                    "\"printHeader\" with a boolean value");
        }

        CSVRowWriter csvRowWriter = new CSVRowWriter(template);
        csvRowWriter.shouldPrintHeader(isFirstLine(parameters));
        addKeyWithTranslationsToFile((Key) model, csvRowWriter);
        csvRowWriter.write(outputStream);
    }

    private Boolean isFirstLine(Map<String, Object> parameters) {
        return (Boolean) parameters.get(PRINT_HEADER);
    }

    private void addKeyWithTranslationsToFile(Key key, CSVRowWriter csvRowWriter) {
        for (String columnName : csvRowWriter.getColumnNames()) {
            if (isColumnKey(columnName)) {
                csvRowWriter.addColumnValue(I18nCSVTemplateLoader.KEY, key.getId());
            } else {
                if (key.isTranslated(columnName)) {
                    csvRowWriter.addColumnValue(columnName, key.getTranslation(columnName).getValue());
                }
            }
        }
    }

    private boolean isColumnKey(String field) {
        return I18nCSVTemplateLoader.KEY.equals(field);
    }
}
