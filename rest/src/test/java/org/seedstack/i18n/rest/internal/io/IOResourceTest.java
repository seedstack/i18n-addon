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
import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.shared.BadRequestException;

import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@RunWith(JMockit.class)
public class IOResourceTest {

    @Tested
    private IOResource underTest;
    @Injectable
    private KeyRepository keyRepository;
    @Injectable
    private ImportService importService;

    @Mocked
    private FormDataMultiPart multiPart;
    @Mocked
    private BodyPart bodyPart;
    @Mocked
    private ContentDisposition contentDisposition;
    @Mocked
    private InputStream inputStream;

    @Test(expected = BadRequestException.class)
    public void testImportKeysWithoutFile() throws Exception {
        underTest.importTranslations(null);
    }

    @Test(expected = BadRequestException.class)
    public void testImportKeysWithWrongFileExtension() throws Exception {
        givenUploaded2FilesWith5Keys("i18n.xlsx");
        underTest.importTranslations(multiPart);
    }

    private void givenUploaded2FilesWith5Keys(final String fileName) {
        new NonStrictExpectations() {
            {
                multiPart.getBodyParts();
                result = Lists.newArrayList(bodyPart, bodyPart);

                bodyPart.getContentDisposition();
                result = contentDisposition;

                contentDisposition.getFileName();
                result = fileName;

                importService.importKeysWithTranslations(withAny(inputStream));
                result = 5;
            }
        };
    }

    @Test
    public void testImportKeys() throws Exception {
        givenUploaded2FilesWith5Keys("i18n.csv");

        Response response = underTest.importTranslations(multiPart);

        Assertions.assertThat(response.getStatus()).isEqualTo(200);
        Assertions.assertThat(response.getEntity()).isEqualTo(String.format(IOResource.LOADED_KEYS_MESSAGE, 10));
    }
}
