/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.infrastructure.service;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.LocalizationService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class ICUBasedLocalizationServiceIT {

    private static final String FR_FR = "fr-FR";
    private static final String EN_US = "en-US";
    public static final String EN_CA = "en-CA";
    public static final String ZH_CN = "zh-CN";
    private static final String FANCY_LOCALE = "fo-BA";
    public static final String KEY = "key";

    private String defaultLocale;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private KeyFactory factory;

    @Inject
    private KeyRepository repository;

    @Inject
    private LocaleService localeService;

    @Before
    public void before() {
        for (String locale : localeService.getAvailableLocales()) {
            localeService.deleteLocale(locale);
        }
        defaultLocale = FR_FR;

        localeService.addLocale(FR_FR);
        localeService.addLocale(EN_US);
        localeService.addLocale(FANCY_LOCALE);
        localeService.changeDefaultLocaleTo(FR_FR);
    }

    @After
    public void clean() {
        for (String locale : localeService.getAvailableLocales()) {
            localeService.deleteLocale(locale);
        }
        for (Key key : repository.loadAll()) {
            repository.remove(key);
        }
    }

    @Test
    public void getClosestLocale_returns_closest_locale() {
        String result1 = localeService.getClosestLocale(EN_CA);
        assertEquals(EN_US, result1);

        String result2 = localeService.getClosestLocale(ZH_CN);
        assertEquals(defaultLocale, result2);
    }

    @Test
    public void formatDate_formats_a_date() {
        Calendar cal = new GregorianCalendar(2013, 4, 28, 17, 52, 5);

        String result1 = localizationService.formatDate(FR_FR, cal.getTime(), "dMyHmsE");
        assertEquals("mar. 28/5/2013 17:52:05", result1);
        String result2 = localizationService.formatDate(EN_US, cal.getTime(), "dMyHmsE");
        assertEquals("Tue, 5/28/2013, 17:52:05", result2);
        String result3 = localizationService.formatDate(FR_FR, new Date(cal.getTimeInMillis() + TimeZone.getDefault().getOffset(cal.getTimeInMillis())), "dMyHmsE", "America/Argentina/Buenos_Aires");
        assertEquals("mar. 28/5/2013 14:52:05", result3);
    }

    @Test
    public void parseDate_parses_a_date() throws ParseException {
        Calendar cal = new GregorianCalendar(2013, 4, 28, 17, 52, 5);

        Date result1 = localizationService.parseDate(FR_FR, "mar. 28/5/2013 17:52:05", "dMyHmsE");
        assertEquals(cal.getTime(), result1);
        Date result2 = localizationService.parseDate(EN_US, "Tue, 5/28/2013, 17:52:05", "dMyHmsE");
        assertEquals(cal.getTime(), result2);
        Date result3 = localizationService.parseDate(FR_FR, "mar. 28/5/2013 14:52:05", "dMyHmsE", "America/Argentina/Buenos_Aires");
        assertEquals(new Date(cal.getTimeInMillis() + TimeZone.getDefault().getOffset(cal.getTimeInMillis())), result3);
    }

    @Test
    public void formatCurrency_formats_currency() {
        String result1 = localizationService.formatCurrencyAmount(FR_FR, 12.34);
        assertEquals("12,34 €", result1);
        String result2 = localizationService.formatCurrencyAmount(EN_CA, 12.34);
        assertEquals("CA$12.34", result2);

        String result3 = localizationService.formatCurrencyAmount(FR_FR, 12.34, "GBP");
        assertEquals("12,34 £UK", result3);
        String result4 = localizationService.formatCurrencyAmount(EN_US, 12.34, "EUR");
        assertEquals("€12.34", result4);
    }

    @Test
    public void parseCurrency_parses_currency() throws ParseException {
        Number result1 = localizationService.parseCurrencyAmount(FR_FR, "12,34 €");
        assertEquals(12.34, result1.doubleValue(), 0);
        Number result2 = localizationService.parseCurrencyAmount(EN_CA, "CA$12.34");
        assertEquals(12.34, result2.doubleValue(), 0);

        Number result3 = localizationService.parseCurrencyAmount(FR_FR, "12,34 £UK", "GBP");
        assertEquals(12.34, result3.doubleValue(), 0);
        Number result4 = localizationService.parseCurrencyAmount(EN_US, "€12.34", "EUR");
        assertEquals(12.34, result4.doubleValue(), 0);
    }

    @Test
    public void formatNumber_formats_a_number() {
        String result1 = localizationService.formatNumber(FR_FR, 12.34);
        assertEquals("12,34", result1);
        String result2 = localizationService.formatNumber(EN_US, 12.34);
        assertEquals("12.34", result2);
    }

    @Test
    public void parseNumber_parses_a_number() throws ParseException {
        Number result1 = localizationService.parseNumber(FR_FR, "12,34");
        assertEquals(12.34, result1.doubleValue(), 0);
        Number result2 = localizationService.parseNumber(EN_US, "12.34");
        assertEquals(12.34, result2.doubleValue(), 0);
    }

    @Test
    public void localize_localizes_simple_message() {
        String messageValue = "message";

        Key key = factory.createKey(KEY);
        key.addTranslation(FR_FR, messageValue);
        repository.add(key);

        assertEquals(messageValue, localizationService.localize(FR_FR, KEY));
    }

    @Test
    public void localize_localizes_no_message() {
        String key = KEY;
        assertEquals("[" + key + "]", localizationService.localize(FR_FR, KEY));

        Key key1 = factory.createKey(key);
        repository.add(key1);
        assertEquals("[" + key + "]", localizationService.localize(FR_FR, KEY));
    }

    @Test
    public void localize_localizes_complex_message() {
        Object[] arguments = {
                7,
                new Date(new GregorianCalendar(2013, 4, 28, 17, 52, 5).getTimeInMillis()),
                "a disturbance in the Force",
                17.36
        };
        String messageValueUS = "At {1,time} on {1,date}, there was {2} on planet {0,number,integer}. The incident cost {3, number, currency}";

        String messageValueFR = "Le {1,date} à {1,time}, il y eut \"{2}\" sur la planète {0,number,integer}. Cet incident a coûté {3, number, currency}";

        Key key = factory.createKey(KEY);
        key.addTranslation(EN_US, messageValueUS);
        key.addTranslation(FR_FR, messageValueFR);
        repository.add(key);

        assertEquals("At 5:52:05 PM on May 28, 2013, there was a disturbance in the Force on planet 7. The incident cost $17.36", localizationService.localize(EN_US, KEY, arguments));

        assertEquals("Le 28 mai 2013 à 17:52:05, il y eut \"a disturbance in the Force\" sur la planète 7. Cet incident a coûté 17,36 €", localizationService.localize(FR_FR, KEY, arguments));
    }

    @Test
    public void localize_with_plural() {
        String key = KEY;
        Object[] arguments1 = {
                0,
                "MyDisk"
        };
        String messageValueUS = "{0, plural, " +
                "=0{There are no files on disk \"{1}\".}" +
                "=1{There is one file on disk \"{1}\".}" +
                "other{There are # files on disk \"{1}\".}}";

        Key key1 = factory.createKey(key);
        key1.addTranslation(EN_US, messageValueUS);
        repository.add(key1);

        assertEquals("There are no files on disk \"MyDisk\".", localizationService.localize(EN_US, KEY, arguments1));

        Object[] arguments2 = {
                1,
                "Toto"
        };
        assertEquals("There is one file on disk \"Toto\".", localizationService.localize(EN_US, KEY, arguments2));

        Object[] arguments3 = {
                20,
                "Pouet"
        };
        assertEquals("There are 20 files on disk \"Pouet\".", localizationService.localize(EN_US, KEY, arguments3));
    }
}
