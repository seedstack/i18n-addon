/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.io;

import com.google.common.collect.Lists;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.shared.BadRequestException;
import org.seedstack.io.Parser;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@RunWith(JMockit.class)
public class IOResourceTest {

    @Tested
    private IOResource underTest;
    @Injectable
    private FluentAssembler fluentAssembler;
    @Injectable
    private KeyRepository keyRepository;
    @Mocked
    private FormDataMultiPart multiPart;
    @Mocked
    private BodyPart bodyPart;
    @Mocked
    private ContentDisposition contentDisposition;
    @Mocked
    private Parser<I18nCSVRepresentation> parser;
    @Mocked
    private InputStream inputStream;

    @Test(expected = BadRequestException.class)
    public void testImportKeysWithoutFile() throws Exception {
        underTest.importTranslations(null);
    }

    @Test(expected = BadRequestException.class)
    public void testImportKeysWithWrongFileExtension() throws Exception {
        new Expectations() {
            {
                multiPart.getBodyParts();
                result = Lists.newArrayList(bodyPart);

                bodyPart.getContentDisposition();
                result = contentDisposition;

                contentDisposition.getFileName();
                result = "i18n.xlsx";
            }
        };
        underTest.importTranslations(multiPart);
    }

    @Test
    public void testImportKeys() throws Exception {
        Deencapsulation.setField(underTest, "parser", parser);
        new Expectations() {
            {
                multiPart.getBodyParts();
                result = Lists.newArrayList(bodyPart);

                bodyPart.getContentDisposition();
                result = contentDisposition;

                contentDisposition.getFileName();
                result = "i18n.csv";

                parser.parse(withAny(inputStream), I18nCSVRepresentation.class);
                result = Lists.newArrayList(getI18nCSVRepresentation());
            }
        };
        Response response = underTest.importTranslations(multiPart);
        Assertions.assertThat(response.getStatus()).isEqualTo(200);
        Assertions.assertThat(response.getEntity()).isEqualTo(String.format(IOResource.LOADED_KEYS_MESSAGE, 1));
    }

    private I18nCSVRepresentation getI18nCSVRepresentation() {
        I18nCSVRepresentation representation = new I18nCSVRepresentation();
        representation.setKey("key");
        Map<String, String> translations = new HashMap<String, String>();
        translations.put("fr", "frTranslation");
        representation.setValue(translations);
        return representation;
    }
}
