/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import org.seedstack.i18n.api.LocaleService;
import org.seedstack.i18n.api.LocalizationService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.javatuples.Triplet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.persistence.jpa.api.JpaUnit;
import org.seedstack.seed.transaction.api.Transactional;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertEquals;

@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class ICUBasedLocalizationServiceUnitTest {

    private String french;

    private String US;

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
        defaultLocale = "fr-FR";
        french = "fr-FR";
        String dummy = "pouet";
        US = "en-US";

        localeService.addLocale(french);
        localeService.addLocale(US);
        localeService.addLocale(dummy);
        localeService.changeDefaultLocaleTo(french);
    }

    @After
    public void clean() {
        for (String locale : localeService.getAvailableLocales()) {
            localeService.deleteLocale(locale);
        }
        for (Key key : repository.loadAll()) {
            repository.delete(key);
        }
    }

    @Test
    public void getClosestLocale_returns_closest_locale() {
        String result1 = localeService.getClosestLocale("en-CA");
        assertEquals("en-US", result1);

        String result2 = localeService.getClosestLocale("zh-CN");
        assertEquals(defaultLocale, result2);
    }

    @Test
    public void formatDate_formats_a_date() {
        Calendar cal = new GregorianCalendar(2013, 4, 28, 17, 52, 5);

        String result1 = localizationService.formatDate("fr-FR", cal.getTime(), "dMyHmsE");
        assertEquals("mar. 28/5/2013 17:52:05", result1);
        String result2 = localizationService.formatDate("en-US", cal.getTime(), "dMyHmsE");
        assertEquals("Tue, 5/28/2013, 17:52:05", result2);
        String result3 = localizationService.formatDate("fr-FR", new Date(cal.getTimeInMillis() + TimeZone.getDefault().getOffset(cal.getTimeInMillis())), "dMyHmsE", "America/Argentina/Buenos_Aires");
        assertEquals("mar. 28/5/2013 14:52:05", result3);
    }

    @Test
    public void parseDate_parses_a_date() throws ParseException {
        Calendar cal = new GregorianCalendar(2013, 4, 28, 17, 52, 5);

        Date result1 = localizationService.parseDate("fr-FR", "mar. 28/5/2013 17:52:05", "dMyHmsE");
        assertEquals(cal.getTime(), result1);
        Date result2 = localizationService.parseDate("en-US", "Tue, 5/28/2013, 17:52:05", "dMyHmsE");
        assertEquals(cal.getTime(), result2);
        Date result3 = localizationService.parseDate("fr-FR", "mar. 28/5/2013 14:52:05", "dMyHmsE", "America/Argentina/Buenos_Aires");
        assertEquals(new Date(cal.getTimeInMillis() + TimeZone.getDefault().getOffset(cal.getTimeInMillis())), result3);
    }

    @Test
    public void formatCurrency_formats_currency() {
        String result1 = localizationService.formatCurrencyAmount("fr-FR", 12.34);
        assertEquals("12,34 €", result1);
        String result2 = localizationService.formatCurrencyAmount("en-CA", 12.34);
        assertEquals("CA$12.34", result2);

        String result3 = localizationService.formatCurrencyAmount("fr-FR", 12.34, "GBP");
        assertEquals("12,34 £UK", result3);
        String result4 = localizationService.formatCurrencyAmount("en-US", 12.34, "EUR");
        assertEquals("€12.34", result4);
    }

    @Test
    public void parseCurrency_parses_currency() throws ParseException {
        Number result1 = localizationService.parseCurrencyAmount("fr-FR", "12,34 €");
        assertEquals(12.34, result1.doubleValue(), 0);
        Number result2 = localizationService.parseCurrencyAmount("en-CA", "CA$12.34");
        assertEquals(12.34, result2.doubleValue(), 0);

        Number result3 = localizationService.parseCurrencyAmount("fr-FR", "12,34 £UK", "GBP");
        assertEquals(12.34, result3.doubleValue(), 0);
        Number result4 = localizationService.parseCurrencyAmount("en-US", "€12.34", "EUR");
        assertEquals(12.34, result4.doubleValue(), 0);
    }

    @Test
    public void formatNumber_formats_a_number() {
        String result1 = localizationService.formatNumber("fr-FR", 12.34);
        assertEquals("12,34", result1);
        String result2 = localizationService.formatNumber("en-US", 12.34);
        assertEquals("12.34", result2);
    }

    @Test
    public void parseNumber_parses_a_number() throws ParseException {
        Number result1 = localizationService.parseNumber("fr-FR", "12,34");
        assertEquals(12.34, result1.doubleValue(), 0);
        Number result2 = localizationService.parseNumber("en-US", "12.34");
        assertEquals(12.34, result2.doubleValue(), 0);
    }

    @Test
    public void localize_localizes_simple_message() {
        String key = "key";
        String messageValue = "message";
        Map<String, Triplet<String, Boolean, Boolean>> translations = new HashMap<String, Triplet<String, Boolean, Boolean>>();
        Triplet<String, Boolean, Boolean> usTranslation = new Triplet<String, Boolean, Boolean>(messageValue, true, true);
        translations.put("fr_FR", usTranslation);
        Key key1 = factory.createKey(key, "comment", translations);
        repository.persist(key1);
        assertEquals(messageValue, localizationService.localize("fr-FR", "key"));
    }

    @Test
    public void localize_localizes_no_message() {
        String key = "key";
        assertEquals("[" + key + "]", localizationService.localize("fr-FR", "key"));

        Map<String, Triplet<String, Boolean, Boolean>> translations = new HashMap<String, Triplet<String, Boolean, Boolean>>();
        Key key1 = factory.createKey(key, "comment", translations);
        repository.persist(key1);
        assertEquals("[" + key + "]", localizationService.localize("fr-FR", "key"));
    }

    @Test
    public void localize_localizes_complex_message() {
        String key = "key";
        Object[] arguments = {
                7,
                new Date(new GregorianCalendar(2013, 4, 28, 17, 52, 5).getTimeInMillis()),
                "a disturbance in the Force",
                17.36
        };
        String messageValueUS = "At {1,time} on {1,date}, there was {2} on planet {0,number,integer}. The incident cost {3, number, currency}";

        String messageValueFR = "Le {1,date} à {1,time}, il y eut \"{2}\" sur la planète {0,number,integer}. Cet incident a coûté {3, number, currency}";

        Map<String, Triplet<String, Boolean, Boolean>> translations = new HashMap<String, Triplet<String, Boolean, Boolean>>();
        Triplet<String, Boolean, Boolean> usTranslation = new Triplet<String, Boolean, Boolean>(messageValueUS, true, true);
        Triplet<String, Boolean, Boolean> frTranslation = new Triplet<String, Boolean, Boolean>(messageValueFR, true, true);
        translations.put("en_US", usTranslation);
        translations.put("fr_FR", frTranslation);
        Key key1 = factory.createKey(key, "comment", translations);
        repository.persist(key1);

        assertEquals("At 5:52:05 PM on May 28, 2013, there was a disturbance in the Force on planet 7. The incident cost $17.36", localizationService.localize("en-US", "key", arguments));

        assertEquals("Le 28 mai 2013 à 17:52:05, il y eut \"a disturbance in the Force\" sur la planète 7. Cet incident a coûté 17,36 €", localizationService.localize("fr-FR", "key", arguments));
    }

    @Test
    public void localize_with_plural() {
        String key = "key";
        Object[] arguments1 = {
                0,
                "MyDisk"
        };
        String messageValueUS = "{0, plural, " +
                "=0{There are no files on disk \"{1}\".}" +
                "=1{There is one file on disk \"{1}\".}" +
                "other{There are # files on disk \"{1}\".}}";

        Map<String, Triplet<String, Boolean, Boolean>> translations = new HashMap<String, Triplet<String, Boolean, Boolean>>();
        Triplet<String, Boolean, Boolean> usTranslation = new Triplet<String, Boolean, Boolean>(messageValueUS, true, true);
        translations.put("en_US", usTranslation);
        Key key1 = factory.createKey(key, "comment", translations);
        repository.persist(key1);

        assertEquals("There are no files on disk \"MyDisk\".", localizationService.localize("en-US", "key", arguments1));

        Object[] arguments2 = {
                1,
                "Toto"
        };
        assertEquals("There is one file on disk \"Toto\".", localizationService.localize("en-US", "key", arguments2));

        Object[] arguments3 = {
                20,
                "Pouet"
        };
        assertEquals("There are 20 files on disk \"Pouet\".", localizationService.localize("en-US", "key", arguments3));
    }
}
