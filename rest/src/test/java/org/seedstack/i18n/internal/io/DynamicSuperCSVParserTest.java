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

import org.seedstack.i18n.rest.io.DataRepresentation;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.seedstack.io.supercsv.SuperCsvTemplate;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 16/04/2014
 */
public class DynamicSuperCSVParserTest {

    private final String file = "key;en;fr\nkey1;t9nEn;t9nFr\nkey2;t9nEn;t9nFr\n";
    private final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file.getBytes());

    @Test
    public void parse_successfully() {
        DynamicSuperCSVParser<DataRepresentation> underTest = new DynamicSuperCSVParser<DataRepresentation>();
        underTest.setTemplate(new SuperCsvTemplate("name"));

        List<DataRepresentation> dataRepresentations = underTest.parse(byteArrayInputStream, DataRepresentation.class);

        Assertions.assertThat(dataRepresentations.size()).isEqualTo(2);
        DataRepresentation dataRepresentation = dataRepresentations.iterator().next();
        Assertions.assertThat(dataRepresentation.getKey()).isEqualTo("key1");
        Assertions.assertThat(dataRepresentation.getValue().size()).isEqualTo(2);
        Assertions.assertThat(dataRepresentation.getValue().get("en")).isEqualTo("t9nEn");
        Assertions.assertThat(dataRepresentation.getValue().get("fr")).isEqualTo("t9nFr");
    }
}
