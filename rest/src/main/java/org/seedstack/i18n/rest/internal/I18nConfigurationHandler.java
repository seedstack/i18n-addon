/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.rest.internal;

import com.google.common.base.Joiner;
import com.ibm.icu.util.LocaleMatcher;
import com.ibm.icu.util.LocalePriorityList;
import com.ibm.icu.util.ULocale;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.seedstack.i18n.I18nConfig;
import org.seedstack.i18n.LocaleService;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.Application;
import org.seedstack.seed.transaction.Transactional;
import org.seedstack.w20.spi.FragmentConfigurationHandler;

/**
 * This W20 add-on handler provides configuration values for the W20-core culture module, notably the default and
 * available
 * languages. If the backend uses locales unknown to W20, they are translated to the best W20-acceptable match. This
 * uses
 * an hard-coded list of W20-acceptable cultures. This list can be extended with the <code>org.seedstack.i18n
 * .additional-locales.codes</code>
 * configuration property.
 *
 * This handler is defined as a singleton as its initialization is a bit cpu-intensive.
 *
 * @author pierre.thirouin@ext.mpsa.com
 * @author adrien.lauer@mpsa.com
 */
@Singleton
public class I18nConfigurationHandler implements FragmentConfigurationHandler {
    private static final String[] W20_BUILTIN_LOCALES = new String[]{
            "af", "af-ZA", "am", "am-ET", "ar", "ar-AE", "ar-BH", "ar-DZ", "ar-EG", "ar-IQ", "ar-JO", "ar-KW", "ar-LB"
            , "ar-LY",
            "ar-MA", "ar-OM", "ar-QA", "ar-SA", "ar-SY", "ar-TN", "ar-YE", "arn", "arn-CL", "as", "as-IN", "az", "az" +
            "-Cyrl",
            "az-Cyrl-AZ", "az-Latn", "az-Latn-AZ", "ba", "ba-RU", "be", "be-BY", "bg", "bg-BG", "bn", "bn-BD", "bn-IN",
            "bo", "bo-CN", "br", "br-FR", "bs", "bs-Cyrl", "bs-Cyrl-BA", "bs-Latn", "bs-Latn-BA", "ca", "ca-ES", "co",
            "co-FR", "cs", "cs-CZ", "cy", "cy-GB", "da", "da-DK", "de", "de-AT", "de-CH", "de-DE", "de-LI", "de-LU",
            "dsb",
            "dsb-DE", "dv", "dv-MV", "el", "el-GR", "en", "en-029", "en-AU", "en-BZ", "en-CA", "en-GB", "en-IE", "en" +
            "-IN", "en-JM",
            "en-MY", "en-NZ", "en-PH", "en-SG", "en-TT", "en-US", "en-ZA", "en-ZW", "es", "es-AR", "es-BO", "es-CL",
            "es-CO",
            "es-CR", "es-DO", "es-EC", "es-ES", "es-GT", "es-HN", "es-MX", "es-NI", "es-PA", "es-PE", "es-PR", "es-PY"
            , "es-SV",
            "es-US", "es-UY", "es-VE", "et", "et-EE", "eu", "eu-ES", "fa", "fa-IR", "fi", "fi-FI", "fil", "fil-PH",
            "fo",
            "fo-FO", "fr", "fr-BE", "fr-CA", "fr-CH", "fr-FR", "fr-LU", "fr-MC", "fy", "fy-NL", "ga", "ga-IE", "gd",
            "gd-GB", "gl",
            "gl-ES", "gsw", "gsw-FR", "gu", "gu-IN", "ha", "ha-Latn", "ha-Latn-NG", "he", "he-IL", "hi", "hi-IN", "hr"
            , "hr-BA",
            "hr-HR", "hsb", "hsb-DE", "hu", "hu-HU", "hy", "hy-AM", "id", "id-ID", "ig", "ig-NG", "ii", "ii-CN", "is"
            , "is-IS",
            "it", "it-CH", "it-IT", "iu", "iu-Cans", "iu-Cans-CA", "iu-Latn", "iu-Latn-CA", "ja", "ja-JP", "ka", "ka" +
            "-GE",
            "kk", "kk-KZ", "kl", "kl-GL", "km", "km-KH", "kn", "kn-IN", "ko", "ko-KR", "kok", "kok-IN", "ky", "ky-KG"
            , "lb",
            "lb-LU", "lo", "lo-LA", "lt", "lt-LT", "lv", "lv-LV", "mi", "mi-NZ", "mk", "mk-MK", "ml", "ml-IN", "mn",
            "mn-Cyrl",
            "mn-MN", "mn-Mong", "mn-Mong-CN", "moh", "moh-CA", "mr", "mr-IN", "ms", "ms-BN", "ms-MY", "mt", "mt-MT",
            "nb",
            "nb-NO", "ne", "ne-NP", "nl", "nl-BE", "nl-NL", "nn", "nn-NO", "no", "nso", "nso-ZA", "oc", "oc-FR", "or"
            , "or-IN",
            "pa", "pa-IN", "pl", "pl-PL", "prs", "prs-AF", "ps", "ps-AF", "pt", "pt-BR", "pt-PT", "qut", "qut-GT",
            "quz",
            "quz-BO", "quz-EC", "quz-PE", "rm", "rm-CH", "ro", "ro-RO", "ru", "ru-RU", "rw", "rw-RW", "sa", "sa-IN",
            "sah",
            "sah-RU", "se", "se-FI", "se-NO", "se-SE", "si", "si-LK", "sk", "sk-SK", "sl", "sl-SI", "sma", "sma-NO",
            "sma-SE",
            "smj", "smj-NO", "smj-SE", "smn", "smn-FI", "sms", "sms-FI", "sq", "sq-AL", "sr", "sr-Cyrl", "sr-Cyrl-BA"
            , "sr-Cyrl-CS",
            "sr-Cyrl-ME", "sr-Cyrl-RS", "sr-Latn", "sr-Latn-BA", "sr-Latn-CS", "sr-Latn-ME", "sr-Latn-RS", "sv", "sv" +
            "-FI",
            "sv-SE", "sw", "sw-KE", "syr", "syr-SY", "ta", "ta-IN", "te", "te-IN", "tg", "tg-Cyrl", "tg-Cyrl-TJ", "th"
            , "th-TH",
            "tk", "tk-TM", "tn", "tn-ZA", "tr", "tr-TR", "tt", "tt-RU", "tzm", "tzm-Latn", "tzm-Latn-DZ", "ug", "ug-CN",
            "uk", "uk-UA", "ur", "ur-PK", "uz", "uz-Cyrl", "uz-Cyrl-UZ", "uz-Latn", "uz-Latn-UZ", "vi", "vi-VN", "wo"
            , "wo-SN",
            "xh", "xh-ZA", "yo", "yo-NG", "zh", "zh-CHS", "zh-CHT", "zh-CN", "zh-Hans", "zh-Hant", "zh-HK", "zh-MO",
            "zh-SG",
            "zh-TW", "zu", "zu-ZA"
    };

    static final String W20_CORE_FRAGMENT = "w20-core";
    static final String CULTURE_MODULE = "culture";
    static final String AVAILABLE_CULTURES = "available";
    static final String DEFAULT_CULTURE = "default";
    private static final String EN_LANGUAGE_TAG = "en";

    private final LocaleMatcher localeMatcher;
    private final List<String> supportedLocales;
    private final LocaleService localeService;

    @Inject
    public I18nConfigurationHandler(Application application, LocaleService localeService) {
        I18nConfig i18nConfig = application.getConfiguration().get(I18nConfig.class);
        List<String> additionalLocales = i18nConfig.getAdditionalLocales();
        supportedLocales = sortLanguages(
                W20_BUILTIN_LOCALES,
                additionalLocales.toArray(new String[additionalLocales.size()])
        );
        localeMatcher = new LocaleMatcher(LocalePriorityList.add(Joiner.on(',').join(supportedLocales)).build());
        this.localeService = localeService;
    }

    @Override
    @SuppressFBWarnings(value = "NP_BOOLEAN_RETURN_NULL", justification = "Mandated by the W20 bridge API")
    public Boolean overrideFragmentStatus(String fragmentName) {
        return null;
    }

    @Override
    @SuppressFBWarnings(value = "NP_BOOLEAN_RETURN_NULL", justification = "Mandated by the W20 bridge API")
    public Boolean overrideModuleStatus(String fragmentName, String moduleName) {
        if (W20_CORE_FRAGMENT.equals(fragmentName) && CULTURE_MODULE.equals(moduleName)) {
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
        if (W20_CORE_FRAGMENT.equals(fragmentName) && CULTURE_MODULE.equals(moduleName)) {
            // "available" : ["en-US", "fr-FR"]
            if (!sourceConfiguration.containsKey(AVAILABLE_CULTURES)) {
                Set<String> closestAvailableLocales = new HashSet<>();

                for (String availableLocale : localeService.getAvailableLocales()) {
                    closestAvailableLocales.add(getClosestW20Locale(availableLocale));
                }

                sourceConfiguration.put(AVAILABLE_CULTURES, closestAvailableLocales);
            }

            // "default" : "ietf-code-of-default-language"
            if (!sourceConfiguration.containsKey(DEFAULT_CULTURE)) {
                String defaultLocale = localeService.getDefaultLocale();
                if (defaultLocale != null) {
                    sourceConfiguration.put(DEFAULT_CULTURE, getClosestW20Locale(defaultLocale));
                } else {
                    sourceConfiguration.put(DEFAULT_CULTURE, EN_LANGUAGE_TAG);
                }
            }
        }

    }

    private String getClosestW20Locale(String locale) {
        if (supportedLocales.contains(locale)) {
            return locale;
        } else {
            return localeMatcher.getBestMatch(ULocale.forLanguageTag(locale)).toLanguageTag();
        }
    }

    private List<String> sortLanguages(String[]... arrays) {
        List<String> result = new ArrayList<>();

        for (String[] array : arrays) {
            if (array != null) {
                result.addAll(Arrays.asList(array));
            }
        }

        // Allows to define more generic locales (like 'en' or 'fr') with less priority than more specific ones (like
        // 'en-US' or 'fr-FR')
        Collections.sort(result);
        Collections.reverse(result);

        return result;
    }
}
