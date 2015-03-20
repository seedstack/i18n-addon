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
import org.assertj.core.api.Assertions;
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

/**
 * @author pierre.thirouin@ext.mpsa.com
 * Date: 05/12/13
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class LocalizationServiceIT {

    @Inject
    private LocalizationService localizationService;

    @Inject
    private LocaleService localeService;

    @Inject
    private KeyRepository repository;

    @Inject
    private KeyFactory factory;

    @Before
    public void before() {
        Set<String> availableLocales = localeService.getAvailableLocales();
        for (String availableLocale : availableLocales) {
            localeService.deleteLocale(availableLocale);
        }
        localeService.addLocale("en-US");
        localeService.addLocale("en-GB");
        localeService.addLocale("fr");
        localeService.addLocale("fr-FR");
        localeService.addLocale("zh-CN");
    }

    @Test
    public void localize_currency() {
        Number number = 10000.00;
        String expectedNumber;

        expectedNumber = localizationService.formatCurrencyAmount(Locale.UK.toString(), number);
        Assertions.assertThat(expectedNumber).isEqualTo("£10,000.00");

        expectedNumber = localizationService.formatCurrencyAmount(Locale.SIMPLIFIED_CHINESE.toString(), number);
        Assertions.assertThat(expectedNumber).isEqualTo("￥10,000.00");
    }

    @Test
    public void localize_date() throws ParseException {
        String skeleton = "dd/MM/yyyy HH:mm:ss";
        Date date = new Date();
        String formattedDate = localizationService.formatDate(Locale.US.toString(), date, skeleton);
        Date expectedDate = localizationService.parseDate(Locale.US.toString(), formattedDate, skeleton);
        Assertions.assertThat(expectedDate).isToday();
    }

    @Test
    public void localize_number() throws ParseException {
        Float number = new Float(10000.01);
        String formattedNumber = localizationService.formatNumber(Locale.US.toString(), number);
        Number expectedNumber = localizationService.parseNumber(Locale.US.toString(), formattedNumber);
        Assertions.assertThat(expectedNumber.floatValue()).isEqualTo(number);

        formattedNumber = localizationService.formatNumber(Locale.FRANCE.toString(), number);
        expectedNumber = localizationService.parseNumber(Locale.FRANCE.toString(), formattedNumber);
        Assertions.assertThat(expectedNumber.floatValue()).isEqualTo(number);
    }

    @Test
    public void localize_string() throws ParseException {
        String keyID1 = "name1";
        String fr_FRValue = "ma traduction";
        String frValue = "ma traduction, une fois";

        // Create a key with a translation
        Map<String, Triplet<String, Boolean, Boolean>> translations = new HashMap<String, Triplet<String, Boolean, Boolean>>();
        Triplet<String, Boolean, Boolean> fr_FRTranslation = new Triplet<String, Boolean, Boolean>(fr_FRValue, true, true);
        Triplet<String, Boolean, Boolean> frTranslation = new Triplet<String, Boolean, Boolean>(frValue, true, true);
        translations.put("fr", frTranslation);
        translations.put("fr_FR", fr_FRTranslation);
        Key key1 = factory.createKey(keyID1, "comment", translations);
        repository.persist(key1);

        String localizedKey = localizationService.localize("fr_FR", keyID1);
        Assertions.assertThat(localizedKey).isEqualTo(fr_FRValue);

        localizedKey = localizationService.localize("fr", keyID1);
        Assertions.assertThat(localizedKey).isEqualTo(frValue);
    }

    @Test
    public void translation_with_closest_locale_en_GB() {
        String locale_en = "en";
        String locale_en_GB = "en_GB";
        String translation_en = "my translation";
        String translation_en_GB = "my translation with a GB accent";
        localeService.addLocale(locale_en);
        localeService.addLocale(locale_en_GB);

        Map<String, Triplet<String, Boolean, Boolean>> translations = new HashMap<String, Triplet<String, Boolean, Boolean>>();

        translations.put(locale_en, Triplet.with(translation_en, false, false));
        translations.put(locale_en_GB, Triplet.with(translation_en_GB, false, false));
        // create the key
        Key key = factory.createKey("key", "comment", translations);
        repository.persist(key);

        String requestedTranslation = localizationService.localize(locale_en, key.getEntityId());
        Assertions.assertThat(requestedTranslation).isEqualTo(translation_en);

        requestedTranslation = localizationService.localize(locale_en_GB, key.getEntityId());
        Assertions.assertThat(requestedTranslation).isEqualTo(translation_en_GB);

        localeService.deleteLocale(locale_en);
        localeService.deleteLocale(locale_en_GB);
        repository.delete(key);
    }

    @After
    public void clean() {
        for (String locale : localeService.getAvailableLocales()) {
            localeService.deleteLocale(locale);
        }

    }
}
