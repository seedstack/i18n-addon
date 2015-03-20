/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.handlers;

import com.ibm.icu.util.LocaleMatcher;
import com.ibm.icu.util.LocalePriorityList;
import com.ibm.icu.util.ULocale;
import org.seedstack.i18n.api.LocaleService;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.seed.persistence.jpa.api.JpaUnit;
import org.seedstack.seed.transaction.api.Transactional;
import org.seedstack.w20.spi.FragmentConfigurationHandler;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 17/04/2014
 */
public class I18nConfigurationHandler implements FragmentConfigurationHandler {

    private static final String[] W20_AVAILABLE_LOCALES = new String[]{
            "f-ZA", "af", "am-ET", "am", "ar", "ar-AE", "ar-BH", "ar-DZ", "ar-EG", "ar-IQ", "ar-JO", "ar-KW", "ar-LB", "ar-LY", "ar-MA",
            "ar-OM", "ar-QA", "ar-SA", "ar-SY", "ar-TN", "ar-YE", "arn-CL", "arn", "as-IN", "as", "az-Cyrl-AZ", "az-Cyrl",
            "az-Latn-AZ", "az-Latn", "az", "ba-RU", "ba", "be-BY", "be", "bg-BG", "bg", "bn-BD", "bn-IN", "bn", "bo-CN", "bo",
            "br-FR", "br", "bs-Cyrl-BA", "bs-Cyrl", "bs-Latn-BA", "bs-Latn", "bs", "ca-ES", "ca", "co-FR", "co", "cs-CZ", "cs",
            "cy-GB", "cy", "da-DK", "da", "de-AT", "de-CH", "de-DE", "de-LI", "de-LU", "de", "dsb-DE", "dsb", "dv-MV", "dv", "el-GR",
            "el", "en-029", "en-AU", "en-BZ", "en-CA", "en-GB", "en-IE", "en-IN", "en-JM", "en-MY", "en-NZ", "en-PH", "en-SG",
            "en-TT", "en-US", "en-ZA", "en-ZW", "es-AR", "es-BO", "es-CL", "es-CO", "es-CR", "es-DO", "es-EC", "es-ES", "es-GT",
            "es-HN", "es-MX", "es-NI", "es-PA", "es-PE", "es-PR", "es-PY", "es-SV", "es-US", "es-UY", "es-VE", "es", "et-EE", "et",
            "eu-ES", "eu", "fa-IR", "fa", "fi-FI", "fi", "fil-PH", "fil", "fo-FO", "fo", "fr-BE", "fr-CA", "fr-CH", "fr-FR", "fr-LU",
            "fr-MC", "fr", "fy-NL", "fy", "ga-IE", "ga", "gd-GB", "gd", "gl-ES", "gl", "gsw-FR", "gsw", "gu-IN", "gu", "ha-Latn-NG",
            "ha-Latn", "ha", "he-IL", "he", "hi-IN", "hi", "hr-BA", "hr-HR", "hr", "hsb-DE", "hsb", "hu-HU", "hu", "hy-AM", "hy",
            "id-ID", "id", "ig-NG", "ig", "ii-CN", "ii", "is-IS", "is", "it-CH", "it-IT", "it", "iu-Cans-CA", "iu-Cans", "iu-Latn-CA",
            "iu-Latn", "iu", "ja-JP", "ja", "ka-GE", "ka", "kk-KZ", "kk", "kl-GL", "kl", "km-KH", "km", "kn-IN", "kn", "ko-KR", "ko",
            "kok-IN", "kok", "ky-KG", "ky", "lb-LU", "lb", "lo-LA", "lo", "lt-LT", "lt", "lv-LV", "lv", "mi-NZ", "mi", "mk-MK", "mk",
            "ml-IN", "ml", "mn-Cyrl", "mn-MN", "mn-Mong-CN", "mn-Mong", "mn", "moh-CA", "moh", "mr-IN", "mr", "ms-BN", "ms-MY", "ms",
            "mt-MT", "mt", "nb-NO", "nb", "ne-NP", "ne", "nl-BE", "nl-NL", "nl", "nn-NO", "nn", "no", "nso-ZA", "nso", "oc-FR", "oc",
            "or-IN", "or", "pa-IN", "pa", "pl-PL", "pl", "prs-AF", "prs", "ps-AF", "ps", "pt-BR", "pt-PT", "pt", "qut-GT", "qut",
            "quz-BO", "quz-EC", "quz-PE", "quz", "rm-CH", "rm", "ro-RO", "ro", "ru-RU", "ru", "rw-RW", "rw", "sa-IN", "sa", "sah-RU",
            "sah", "se-FI", "se-NO", "se-SE", "se", "si-LK", "si", "sk-SK", "sk", "sl-SI", "sl", "sma-NO", "sma-SE", "sma", "smj-NO",
            "smj-SE", "smj", "smn-FI", "smn", "sms-FI", "sms", "sq-AL", "sq", "sr-Cyrl-BA", "sr-Cyrl-CS", "sr-Cyrl-ME",
            "sr-Cyrl-RS", "sr-Cyrl", "sr-Latn-BA", "sr-Latn-CS", "sr-Latn-ME", "sr-Latn-RS", "sr-Latn", "sr", "sv-FI", "sv-SE",
            "sv", "sw-KE", "sw", "syr-SY", "syr", "ta-IN", "ta", "te-IN", "te", "tg-Cyrl-TJ", "tg-Cyrl", "tg", "th-TH", "th", "tk-TM",
            "tk", "tn-ZA", "tn", "tr-TR", "tr", "tt-RU", "tt", "tzm-Latn-DZ", "tzm-Latn", "tzm", "ug-CN", "ug", "uk-UA", "uk", "ur-PK",
            "ur", "uz-Cyrl-UZ", "uz-Cyrl", "uz-Latn-UZ", "uz-Latn", "uz", "vi-VN", "vi", "wo-SN", "wo", "xh-ZA", "xh", "yo-NG", "yo",
            "zh-CHS", "zh-CHT", "zh-CN", "zh-Hans", "zh-Hant", "zh-HK", "zh-MO", "zh-SG", "zh-TW", "zh", "zu-ZA", "zu"
    };
    private static final String W20_CORE = "w20-core";
    private static final String CULTURE = "culture";
    private static final String AVAILABLE = "available";
    private static final String DEFAULT = "default";
    private static final String EN = "en";

    private final LocaleMatcher localeMatcher;

    @Inject
    private LocaleService localeService;

    @Inject
    private LocaleRepository localeRepository;

    /**
     * Constructor.
     */
    public I18nConfigurationHandler() {
        LocalePriorityList.Builder builder = null;
        for (String w20AvailableLocale : W20_AVAILABLE_LOCALES) {
            if (builder == null) {
                builder = LocalePriorityList.add(w20AvailableLocale);
            }
            builder.add(w20AvailableLocale);
        }
        localeMatcher = new LocaleMatcher(builder.build());
    }

    @Override
    public Boolean overrideFragmentStatus(String fragmentName) {
        return null;
    }

    @Override
    public Boolean overrideModuleStatus(String fragmentName, String moduleName) {
        if (W20_CORE.equals(fragmentName) && CULTURE.equals(moduleName)) {
            return true;
        }
        return null;
    }

    @Override
    public void overrideVariables(String fragmentName, Map<String, String> variables) {
        // Do nothing
    }

    @JpaUnit("seed-i18n-domain")
    @Transactional
    @Override
    public void overrideConfiguration(String fragmentName, String moduleName, Map<String, Object> sourceConfiguration) {
        if (W20_CORE.equals(fragmentName) && CULTURE.equals(moduleName)) {
            // "available" : ["en-US", "fr-FR"]
            Set<String> availableLocales = localeService.getAvailableLocales();
            Set<String> closestAvailableLocales = new HashSet<String>(availableLocales.size());
            for (String availableLocale : availableLocales) {
                closestAvailableLocales.add(getClosestW20Locale(availableLocale));
            }
            sourceConfiguration.put(AVAILABLE, closestAvailableLocales);
            // "default" : "ietf-code-of-default-language"
            Locale defaultLocale = localeRepository.getDefaultLocale();
            if (defaultLocale != null) {
                sourceConfiguration.put(DEFAULT, getClosestW20Locale(defaultLocale.getEntityId()));
            } else {
                sourceConfiguration.put(DEFAULT, EN);
            }
        }

    }

    private String getClosestW20Locale(String locale) {
        ULocale bestMatch = localeMatcher.getBestMatch(locale);
        return bestMatch.toLanguageTag();
    }
}
