/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.infrastructure.service;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import org.assertj.core.api.Assertions;
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

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class LocalizationServiceIT {

    public static final String EN = "en";
    public static final String FR = "fr";
    public static final String EN_GB = "en-GB";
    public static final String EN_US = "en-US";
    public static final String ZH_CN = "zh-CN";
    public static final String FR_FR = "fr-FR";
    public static final String COMMENT = "comment";

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
        localeService.addLocale(EN_US);
        localeService.addLocale(EN_GB);
        localeService.addLocale(FR);
        localeService.addLocale(FR_FR);
        localeService.addLocale(ZH_CN);
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
        Float number = 10000.01f;
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
        Key key1 = factory.createKey(keyID1);
        key1.setComment(COMMENT);
        key1.addTranslation(FR, frValue);
        key1.addTranslation(FR_FR, fr_FRValue);
        repository.add(key1);

        String localizedKey = localizationService.localize(FR_FR, keyID1);
        Assertions.assertThat(localizedKey).isEqualTo(fr_FRValue);

        localizedKey = localizationService.localize(FR, keyID1);
        Assertions.assertThat(localizedKey).isEqualTo(frValue);
    }

    @Test
    public void translation_with_closest_locale_en_GB() {
        String translation_en = "my translation";
        String translation_en_GB = "my translation with a GB accent";
        localeService.addLocale(EN);
        localeService.addLocale(EN_GB);

        // create the key
        Key key = factory.createKey("key");
        key.addTranslation(EN, translation_en);
        key.addTranslation(EN_GB, translation_en_GB);
        repository.add(key);

        String requestedTranslation = localizationService.localize(EN, key.getId());
        Assertions.assertThat(requestedTranslation).isEqualTo(translation_en);

        requestedTranslation = localizationService.localize(EN_GB, key.getId());
        Assertions.assertThat(requestedTranslation).isEqualTo(translation_en_GB);

        localeService.deleteLocale(EN);
        localeService.deleteLocale(EN_GB);
        repository.remove(key);
    }

    @After
    public void clean() {
        for (String locale : localeService.getAvailableLocales()) {
            localeService.deleteLocale(locale);
        }
    }
}
