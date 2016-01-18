/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.locale;

import mockit.*;
import mockit.integration.junit4.JMockit;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@RunWith(JMockit.class)
public class SupportedLocaleFinderImplTest {

    public static final String EN = "en";
    public static final String FR = "fr";
    private final java.util.Locale enJLocale = new java.util.Locale(EN);
    private final java.util.Locale frJLocale = new java.util.Locale(FR);

    @Tested
    private SupportedLocaleFinderImpl underTest;
    @Injectable
    private LocaleFactory localeFactory;
    @Injectable
    private LocaleAssembler localeAssembler;
    @Mocked
    private Locale enLocale;
    @Mocked
    private Locale frLocale;
    private LocaleRepresentation frLocaleRepresentation;
    private LocaleRepresentation enLocaleRepresentation;

    @Before
    public void setUp() throws Exception {
        enLocaleRepresentation = new LocaleRepresentation();
        enLocaleRepresentation.setCode(EN);
        enLocaleRepresentation.setLanguage("English");
        enLocaleRepresentation.setEnglishLanguage("English");

        frLocaleRepresentation = new LocaleRepresentation();
        frLocaleRepresentation.setCode(FR);
        frLocaleRepresentation.setLanguage("français");
        frLocaleRepresentation.setEnglishLanguage("French");
    }

    @Test
    public void testFindSupportedLocalesDontReturnNull() {
        mockAvailableLocales();
        List<LocaleRepresentation> supportedLocales = underTest.findSupportedLocales();
        Assertions.assertThat(supportedLocales).isNotNull();
        Assertions.assertThat(supportedLocales).isEmpty();
    }

    private void mockAvailableLocales(final String... localeCodes) {
        new MockUp<java.util.Locale>() {

            @Mock
            java.util.Locale[] getAvailableLocales() {
                List<java.util.Locale> locales = new ArrayList<java.util.Locale>();
                for (String locale : localeCodes) {
                    locales.add(new java.util.Locale(locale));
                }
                return locales.toArray(new java.util.Locale[]{});
            }
        };
    }

    @Test
    public void testFindSupportedLocales() {
        mockAvailableLocales(EN, FR);
        mockAssemblerForEN();
        mockAssemblerForFR();

        List<LocaleRepresentation> supportedLocales = underTest.findSupportedLocales();
        Assertions.assertThat(supportedLocales).hasSize(2);
        Assertions.assertThat(supportedLocales.get(0).getCode()).isEqualTo(EN);
        Assertions.assertThat(supportedLocales.get(1).getCode()).isEqualTo(FR);
    }

    public void mockAssemblerForEN() {
        mockAssembler(enJLocale, enLocale, enLocaleRepresentation);
    }

    public void mockAssemblerForFR() {
        mockAssembler(frJLocale, frLocale, frLocaleRepresentation);
    }

    public void mockAssembler(final java.util.Locale jLocale, final Locale locale, final LocaleRepresentation localeRepresentation) {
        new Expectations() {
            {
                localeFactory.createFromLocale(jLocale);
                result = locale;

                localeAssembler.assembleDtoFromAggregate(locale);
                result = localeRepresentation;
            }
        };
    }

    @Test
    public void testFindSupportedLocale() {
        mockAvailableLocales(EN, FR);
        mockAssemblerForEN();
        mockAssemblerForFR();
        LocaleRepresentation locale = underTest.findSupportedLocale(FR);
        Assertions.assertThat(locale.getCode()).isEqualTo(FR);
        Assertions.assertThat(locale.getEnglishLanguage()).isEqualTo("French");
    }

    @Test
    public void testFindSupportedLocaleReturnNull() {
        mockAvailableLocales();
        Assertions.assertThat(underTest.findSupportedLocale(FR)).isNull();
    }

    @Test
    public void testAdditionalLocalesCodes() {
        mockAvailableLocales(EN);
        mockAssemblerForEN();
        mockAssemblerForAdditionalLocale("fr", frLocale, frLocaleRepresentation);

        Deencapsulation.setField(underTest, "additionalLocaleCodes", new String[]{"fr"});

        LocaleRepresentation supportedLocale = underTest.findSupportedLocale("fr");
        Assertions.assertThat(supportedLocale).isNotNull();
        Assertions.assertThat(supportedLocale.getCode()).isEqualTo("fr");

        Assertions.assertThat(underTest.findSupportedLocales()).hasSize(2);
    }

    public void mockAssemblerForAdditionalLocale(final String localeCode, final Locale locale, final LocaleRepresentation localeRepresentation) {
        new Expectations() {
            {
                localeFactory.createFromCode(localeCode);
                result = locale;

                localeAssembler.assembleDtoFromAggregate(locale);
                result = localeRepresentation;
            }
        };
    }
}
