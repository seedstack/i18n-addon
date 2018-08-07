/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.csv;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.io.supercsv.SuperCsvTemplate;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@RunWith(JMockit.class)
public class I18nCSVRendererTest {

    private I18nCSVRenderer underTest = new I18nCSVRenderer();
    private SuperCsvTemplate superCsvTemplate;

    @Mocked
    private Key key;

    @Before
    public void setUp() throws Exception {
        superCsvTemplate = new SuperCsvTemplate("name");
        Deencapsulation.setField(underTest, "template", superCsvTemplate);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRenderWithoutParameterNotImplemented() throws Exception {
        underTest.render(new ByteArrayOutputStream(), key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRenderWithWrongModelClass() throws Exception {
        underTest.render(new ByteArrayOutputStream(), new Object(), "application/csv", printHeader(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRenderWithoutPrintHeaderParameter() throws Exception {
        underTest.render(new ByteArrayOutputStream(), key, "application/csv", new HashMap<>());
    }

    @Test
    public void testRenderDoNotPrintHeader(@Mocked final CSVRowWriter csvRowWriter) throws Exception {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new Expectations() {
            {
                new CSVRowWriter(superCsvTemplate);
                result = csvRowWriter;

                csvRowWriter.shouldPrintHeader(false);

                csvRowWriter.getColumnNames();
                result = new String[]{"key", "fr"};
                key.getId();
                result = "keyName";
                csvRowWriter.addColumnValue("key", "keyName");

                key.isTranslated("fr");
                result = true;
                key.getTranslation("fr").getValue();
                result = "frTranslation";
                csvRowWriter.addColumnValue("fr", "frTranslation");

                csvRowWriter.write(outputStream);
            }
        };
        underTest.render(outputStream, key, "application/csv", printHeader(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testRenderWithZeroColumn() {
        underTest.render(new ByteArrayOutputStream(), key, "application/csv", printHeader(true));
    }

    public Map<String, Object> printHeader(boolean shouldPrintHeader) {
        Map<String, Object> printHeader = new HashMap<>();
        printHeader.put("printHeader", shouldPrintHeader);
        return printHeader;
    }
}