/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n;

import org.seedstack.business.Service;

import java.text.ParseException;
import java.util.Date;

/**
 * This service provides common localization features according to a specified
 * locale.
 *
 * @author adrien.lauer@mpsa.com
 * @author yves.dautremay@mpsa.com
 */
@Service
public interface LocalizationService {
    /**
     * Return a localized string based on the specified locale and key.
     * If the translation is not found in the given locale, the method
     * reach parent locales (eg. "fr-BE" then "fr").
     * If no translation is found "[" + key + "]" is returned.
     *
     * @param locale The locale identifier.
     * @param key    The i18n key to translate.
     * @return The translation result.
     */
    String localize(String locale, String key);

    /**
     * Return a localized string based on the specified locale and key. The
     * pattern is formatted according to the given arguments
     *
     * @param locale The locale identifier.
     * @param key    The i18n key to translate.
     * @param args   The arguments used for message formatting.
     * @return The translation result.
     * @see LocalizationService#localize(String, String)
     */
    String localize(String locale, String key, Object... args);

    /**
     * Format a date using the specified locale.
     *
     * @param locale   The locale identifier.
     * @param date     The date to format.
     * @param skeleton The pattern elements that will be included in the formatted
     *                 string.
     * @return The formatted date as a string.
     */
    String formatDate(String locale, Date date, String skeleton);

    /**
     * Format a date using the specified locale and the specified timezone.
     *
     * @param locale   The locale identifier.
     * @param date     The date to format.
     * @param skeleton The pattern elements that will be included in the formatted
     *                 string.
     * @param timezone The timezone identifier.
     * @return The formatted date as a string.
     */
    String formatDate(String locale, Date date, String skeleton, String timezone);

    /**
     * Parse a date using the specified locale.
     *
     * @param locale   The locale identifier.
     * @param value    The string to parse.
     * @param skeleton The pattern elements used to parse the string.
     * @return The parsed date object.
     * @throws ParseException if the given string cannot be parsed into a Date
     */
    Date parseDate(String locale, String value, String skeleton) throws ParseException;

    /**
     * Parse a date using the specified locale and the specified timezone.
     *
     * @param locale   The locale identifier.
     * @param value    The string to parse.
     * @param skeleton The pattern elements used to parse the string.
     * @param timezone The timezone identifier.
     * @return The parsed date object.
     * @throws ParseException if the given string cannot be parsed into a Date
     */
    Date parseDate(String locale, String value, String skeleton, String timezone) throws ParseException;

    /**
     * Format the specified amount in the locale currency.
     *
     * @param locale The locale identifier.
     * @param amount The amount to format.
     * @return The formatted string.
     */
    String formatCurrencyAmount(String locale, Number amount);

    /**
     * Format the specified amount in the specified locale and currency.
     *
     * @param locale   The locale identifier.
     * @param amount   The amount to format.
     * @param currency The currency identifier as a 3-letter ISO4217 code.
     * @return The formatted string.
     */
    String formatCurrencyAmount(String locale, Number amount, String currency);

    /**
     * Parse the specified amount in the locale default currency.
     *
     * @param locale The locale identifier.
     * @param value  The value to parse.
     * @return The formatted string.
     * @throws ParseException if the given value cannot be parsed into an amount
     */
    Number parseCurrencyAmount(String locale, String value) throws ParseException;

    /**
     * Parse the specified amount in the specified locale and currency.
     *
     * @param locale   The locale identifier.
     * @param value    The value to parse.
     * @param currency The currency identifier.
     * @return The formatted string.
     * @throws ParseException if the given value cannot be parsed into an amount
     */
    Number parseCurrencyAmount(String locale, String value, String currency) throws ParseException;

    /**
     * Format a number according to the specified locale.
     *
     * @param locale The locale identifier.
     * @param number The number to format.
     * @return The formatted string.
     */
    String formatNumber(String locale, Number number);

    /**
     * Parse a number according to the specified locale.
     *
     * @param locale The locale identifier.
     * @param value  The string to parse.
     * @return The parsed number.
     * @throws ParseException if the given value cannot be parsed into a number
     */
    Number parseNumber(String locale, String value) throws ParseException;
}
