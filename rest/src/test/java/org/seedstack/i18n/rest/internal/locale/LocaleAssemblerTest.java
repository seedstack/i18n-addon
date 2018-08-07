/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.locale;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.internal.domain.model.locale.Locale;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@RunWith(JMockit.class)
public class LocaleAssemblerTest {

    @Tested
    private LocaleAssembler underTest;
    @Mocked
    private Locale locale;

    @Test
    public void testAssemble() throws Exception {
        new Expectations() {
            {
                locale.getId();
                result = "fr";
                locale.getLanguage();
                result = "français";
                locale.getEnglishLanguage();
                result = "French";
            }
        };

        LocaleRepresentation localeRepresentation = underTest.createDtoFromAggregate(locale);
        Assertions.assertThat(localeRepresentation.getCode()).isEqualTo("fr");
        Assertions.assertThat(localeRepresentation.getLanguage()).isEqualTo("français");
        Assertions.assertThat(localeRepresentation.getEnglishLanguage()).isEqualTo("French");
    }
}
