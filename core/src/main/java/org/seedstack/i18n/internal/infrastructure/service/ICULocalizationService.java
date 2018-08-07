/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.Currency;
import com.ibm.icu.util.CurrencyAmount;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import org.apache.commons.lang.StringUtils;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.LocalizationService;
import org.seedstack.i18n.internal.domain.service.TranslationService;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

/**
 * Localization service implementation based on ICU.
 */
@JpaUnit("seed-i18n-domain")
@Transactional
class ICULocalizationService implements LocalizationService {

    private final LocaleService localeService;
    private final TranslationService translationService;

    @Inject
    ICULocalizationService(LocaleService localeService, TranslationService translationService) {
        this.localeService = localeService;
        this.translationService = translationService;
    }

    @Override
    public String localize(String locale, String key) {
        return localize(locale, key, (Object[]) null);
    }

    @Override
    public String localize(String locale, String keyName, Object... args) {
        Optional<String> translation = translationService.getTranslationWithFallback(locale, keyName);
        if (translation.isPresent()) {
            return formatTranslation(locale, translation.get(), args);
        } else {
            return '[' + keyName + ']';
        }
    }

    private String formatTranslation(String locale, String translation, Object[] args) {
        if (args == null || args.length == 0) {
            return translation;
        } else {
            return new MessageFormat(translation, findClosestULocale(locale)).format(args);
        }
    }

    private ULocale findClosestULocale(String locale) {
        return new ULocale(localeService.getClosestLocale(locale));
    }

    @Override
    public String formatDate(String locale, Date date, String skeleton) {
        return formatDate(locale, date, skeleton, null);
    }

    @Override
    public String formatDate(String locale, Date date, String skeleton, String timezone) {
        ULocale closestLocale = findClosestULocale(locale);
        DateFormat formatter = DateFormat.getPatternInstance(skeleton, closestLocale);
        if (StringUtils.isNotEmpty(timezone)) {
            formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        }
        return formatter.format(date);
    }

    @Override
    public Date parseDate(String locale, String value, String skeleton) throws ParseException {
        return parseDate(locale, value, skeleton, null);
    }

    @Override
    public Date parseDate(String locale, String value, String skeleton, String timezone) throws ParseException {
        ULocale closestLocale = findClosestULocale(locale);
        DateFormat formatter = DateFormat.getPatternInstance(skeleton, closestLocale);
        if (StringUtils.isNotEmpty(timezone)) {
            formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        }
        return formatter.parse(value);
    }

    @Override
    public String formatCurrencyAmount(String locale, Number amount) {
        return formatCurrencyAmount(locale, amount, null);
    }

    @Override
    public String formatCurrencyAmount(String locale, Number amount, String currencyCode) {
        ULocale closestLocale = findClosestULocale(locale);
        Currency currency;
        if (StringUtils.isEmpty(currencyCode)) {
            currency = Currency.getInstance(new ULocale(locale));
        } else {
            currency = Currency.getInstance(currencyCode);
        }
        CurrencyAmount ca = new CurrencyAmount(amount, currency);
        NumberFormat nf = NumberFormat.getCurrencyInstance(closestLocale);
        nf.setCurrency(currency);
        return nf.format(ca);
    }

    @Override
    public Number parseCurrencyAmount(String locale, String value) throws ParseException {
        return parseCurrencyAmount(locale, value, null);
    }

    @Override
    public Number parseCurrencyAmount(String locale, String value, String currencyCode) throws ParseException {
        ULocale closestLocale = findClosestULocale(locale);
        Currency currency;
        if (StringUtils.isEmpty(currencyCode)) {
            currency = Currency.getInstance(new ULocale(locale));
        } else {
            currency = Currency.getInstance(currencyCode);
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance(closestLocale);
        nf.setCurrency(currency);
        return nf.parse(value);
    }

    @Override
    public String formatNumber(String locale, Number number) {
        ULocale closestLocale = findClosestULocale(locale);
        NumberFormat nf = NumberFormat.getInstance(closestLocale);
        return nf.format(number);
    }

    @Override
    public Number parseNumber(String locale, String value) throws ParseException {
        ULocale closestLocale = findClosestULocale(locale);
        NumberFormat nf = NumberFormat.getInstance(closestLocale);
        return nf.parse(value);
    }
}
