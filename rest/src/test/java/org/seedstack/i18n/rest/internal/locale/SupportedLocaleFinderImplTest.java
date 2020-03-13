/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.locale;

import com.ibm.icu.util.ULocale;
import java.util.ArrayList;
import java.util.List;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.seedstack.business.assembler.Assembler;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleFactory;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class SupportedLocaleFinderImplTest {
    public static final String EN = "en";
    public static final String FR = "fr";

    @Tested
    private SupportedLocaleFinderImpl underTest;
    @Injectable
    private LocaleFactory localeFactory;
    @Injectable
    private Assembler<Locale, LocaleRepresentation> localeAssembler;
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
        new MockUp<ULocale>() {

            @Mock
            ULocale[] getAvailableLocales() {
                List<ULocale> locales = new ArrayList<>();
                for (String locale : localeCodes) {
                    locales.add(new ULocale(locale));
                }
                return locales.toArray(new ULocale[]{});
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
        mockAssembler(ULocale.ENGLISH, enLocale, enLocaleRepresentation);
    }

    public void mockAssemblerForFR() {
        mockAssembler(ULocale.FRENCH, frLocale, frLocaleRepresentation);
    }

    public void mockAssembler(final ULocale uLocale, final Locale locale,
            final LocaleRepresentation localeRepresentation) {
        new Expectations() {
            {
                localeFactory.createFromULocale(uLocale);
                result = locale;

                localeAssembler.createDtoFromAggregate(locale);
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

    public void mockAssemblerForAdditionalLocale(final String localeCode, final Locale locale,
            final LocaleRepresentation localeRepresentation) {
        new Expectations() {
            {
                localeFactory.createFromCode(localeCode);
                result = locale;

                localeAssembler.createDtoFromAggregate(locale);
                result = localeRepresentation;
            }
        };
    }
}
