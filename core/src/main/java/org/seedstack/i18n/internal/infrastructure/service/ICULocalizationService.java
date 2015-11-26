/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
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
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.LocalizationService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.seedstack.i18n.internal.domain.model.locale.Locale;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Localization service implementation based on ICU.
 */
public class ICULocalizationService implements LocalizationService {

    private final LocaleService localeService;
    private final KeyRepository keyRepository;

    /**
     * Constructor.
     *
     * @param localeService locale service
     * @param keyRepository key repository
     */
    @Inject
    public ICULocalizationService(LocaleService localeService, KeyRepository keyRepository) {
        this.localeService = localeService;
        this.keyRepository = keyRepository;
    }

    @Override
    public String localize(String locale, String key) {
        return localize(locale, key, (Object[]) null);
    }

    private ULocale getClosestULocale(String locale) {
        return new ULocale(localeService.getClosestLocale(locale));
    }

    @Override
    public String localize(String locale, String key, Object... args) {
        ULocale closestLocale = getClosestULocale(locale);
        Key loadedKey = keyRepository.load(key);

        String localizedString;
        if (loadedKey == null) {
            localizedString = '[' + key + ']';
        } else {

            // TODO business logic to search in other locales
            List<ULocale> localeIterator = getParentLocale(closestLocale);
            Translation translation = getTranslationWithFallBack(loadedKey, localeIterator);

            if (translation == null) {
                localizedString = '[' + key + ']';
            } else if (ArrayUtils.isEmpty(args)) {
                localizedString = translation.getValue();
            } else {
                MessageFormat mf = new MessageFormat(translation.getValue(), closestLocale);
                localizedString = mf.format(args);
            }
        }
        return localizedString;
    }

    Translation getTranslationWithFallBack(Key loadedKey, List<ULocale> localeIterator) {
        Translation translation = null;
        for (ULocale uLocale : localeIterator) {
            translation = loadedKey.getTranslation(Locale.formatLocaleCode(uLocale.getBaseName())); // TODO fix bug
            if (translation != null) {
                break;
            }
        }
        return translation;
    }

    List<ULocale> getParentLocale(ULocale locale) {
        List<ULocale> locales = new ArrayList<ULocale>();
        ULocale current = locale;
        while ( current != null && StringUtils.isNotBlank(current.toString())) {
            locales.add(current);
            current = current.getFallback();
        }
        return locales;
    }

    @Override
    public String formatDate(String locale, Date date, String skeleton) {
        return formatDate(locale, date, skeleton, null);
    }

    @Override
    public String formatDate(String locale, Date date, String skeleton, String timezone) {
        ULocale closestLocale = getClosestULocale(locale);
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
        ULocale closestLocale = getClosestULocale(locale);
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
        ULocale closestLocale = getClosestULocale(locale);
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
        ULocale closestLocale = getClosestULocale(locale);
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
        ULocale closestLocale = getClosestULocale(locale);
        NumberFormat nf = NumberFormat.getInstance(closestLocale);
        return nf.format(number);
    }

    @Override
    public Number parseNumber(String locale, String value) throws ParseException {
        ULocale closestLocale = getClosestULocale(locale);
        NumberFormat nf = NumberFormat.getInstance(closestLocale);
        return nf.parse(value);
    }
}
