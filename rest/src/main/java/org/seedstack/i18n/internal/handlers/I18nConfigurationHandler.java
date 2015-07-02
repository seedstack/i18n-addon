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
import org.seedstack.seed.core.api.Application;
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

    private static final String[] W20_BUILTIN_LOCALES = new String[]{
            "af", "af_ZA", "am", "am_ET", "ar", "ar_AE", "ar_BH", "ar_DZ", "ar_EG", "ar_IQ", "ar_JO", "ar_KW", "ar_LB", "ar_LY",
            "ar_MA", "ar_OM", "ar_QA", "ar_SA", "ar_SY", "ar_TN", "ar_YE", "arn", "arn_CL", "as", "as_IN", "az", "az_Cyrl",
            "az_Cyrl_AZ", "az_Latn", "az_Latn_AZ", "ba", "ba_RU", "be", "be_BY", "bg", "bg_BG", "bn", "bn_BD", "bn_IN",
            "bo", "bo_CN", "br", "br_FR", "bs", "bs_Cyrl", "bs_Cyrl_BA", "bs_Latn", "bs_Latn_BA", "ca", "ca_ES", "co",
            "co_FR", "cs", "cs_CZ", "cy", "cy_GB", "da", "da_DK", "de", "de_AT", "de_CH", "de_DE", "de_LI", "de_LU", "dsb",
            "dsb_DE", "dv", "dv_MV", "el", "el_GR", "en_029", "en_AU", "en_BZ", "en_CA", "en_GB", "en_IE", "en_IN", "en_JM",
            "en_MY", "en_NZ", "en_PH", "en_SG", "en_TT", "en_US", "en_ZA", "en_ZW", "es", "es_AR", "es_BO", "es_CL", "es_CO",
            "es_CR", "es_DO", "es_EC", "es_ES", "es_GT", "es_HN", "es_MX", "es_NI", "es_PA", "es_PE", "es_PR", "es_PY", "es_SV",
            "es_US", "es_UY", "es_VE", "et", "et_EE", "eu", "eu_ES", "fa", "fa_IR", "fi", "fi_FI", "fil", "fil_PH", "fo",
            "fo_FO", "fr", "fr_BE", "fr_CA", "fr_CH", "fr_FR", "fr_LU", "fr_MC", "fy", "fy_NL", "ga", "ga_IE", "gd", "gd_GB", "gl",
            "gl_ES", "gsw", "gsw_FR", "gu", "gu_IN", "ha", "ha_Latn", "ha_Latn_NG", "he", "he_IL", "hi", "hi_IN", "hr", "hr_BA",
            "hr_HR", "hsb", "hsb_DE", "hu", "hu_HU", "hy", "hy_AM", "id", "id_ID", "ig", "ig_NG", "ii", "ii_CN", "is", "is_IS",
            "it", "it_CH", "it_IT", "iu", "iu_Cans", "iu_Cans_CA", "iu_Latn", "iu_Latn_CA", "ja", "ja_JP", "ka", "ka_GE",
            "kk", "kk_KZ", "kl", "kl_GL", "km", "km_KH", "kn", "kn_IN", "ko", "ko_KR", "kok", "kok_IN", "ky", "ky_KG", "lb",
            "lb_LU", "lo", "lo_LA", "lt", "lt_LT", "lv", "lv_LV", "mi", "mi_NZ", "mk", "mk_MK", "ml", "ml_IN", "mn", "mn_Cyrl",
            "mn_MN", "mn_Mong", "mn_Mong_CN", "moh", "moh_CA", "mr", "mr_IN", "ms", "ms_BN", "ms_MY", "mt", "mt_MT", "nb",
            "nb_NO", "ne", "ne_NP", "nl", "nl_BE", "nl_NL", "nn", "nn_NO", "no", "nso", "nso_ZA", "oc", "oc_FR", "or", "or_IN",
            "pa", "pa_IN", "pl", "pl_PL", "prs", "prs_AF", "ps", "ps_AF", "pt", "pt_BR", "pt_PT", "qut", "qut_GT", "quz",
            "quz_BO", "quz_EC", "quz_PE", "rm", "rm_CH", "ro", "ro_RO", "ru", "ru_RU", "rw", "rw_RW", "sa", "sa_IN", "sah",
            "sah_RU", "se", "se_FI", "se_NO", "se_SE", "si", "si_LK", "sk", "sk_SK", "sl", "sl_SI", "sma", "sma_NO", "sma_SE",
            "smj", "smj_NO", "smj_SE", "smn", "smn_FI", "sms", "sms_FI", "sq", "sq_AL", "sr", "sr_Cyrl", "sr_Cyrl_BA", "sr_Cyrl_CS",
            "sr_Cyrl_ME", "sr_Cyrl_RS", "sr_Latn", "sr_Latn_BA", "sr_Latn_CS", "sr_Latn_ME", "sr_Latn_RS", "sv", "sv_FI",
            "sv_SE", "sw", "sw_KE", "syr", "syr_SY", "ta", "ta_IN", "te", "te_IN", "tg", "tg_Cyrl", "tg_Cyrl_TJ", "th", "th_TH",
            "tk", "tk_TM", "tn", "tn_ZA", "tr", "tr_TR", "tt", "tt_RU", "tzm", "tzm_Latn", "tzm_Latn_DZ", "ug", "ug_CN",
            "uk", "uk_UA", "ur", "ur_PK", "uz", "uz_Cyrl", "uz_Cyrl_UZ", "uz_Latn", "uz_Latn_UZ", "vi", "vi_VN", "wo", "wo_SN",
            "xh", "xh_ZA", "yo", "yo_NG", "zh", "zh_CHS", "zh_CHT", "zh_CN", "zh_Hans", "zh_Hant", "zh_HK", "zh_MO", "zh_SG",
            "zh_TW", "zu", "zu_ZA"
    };

    private static final String W20_CORE = "w20-core";
    private static final String CULTURE = "culture";
    private static final String AVAILABLE = "available";
    private static final String DEFAULT = "default";
    private static final String EN = "en";

    private static volatile LocaleMatcher localeMatcher;

    @Inject
    private LocaleService localeService;

    @Inject
    private LocaleRepository localeRepository;

    @Inject
    public I18nConfigurationHandler(Application application) {
        // double-checked locking (don't remove the volatile on localeMatcher field)
        if (localeMatcher == null) {
            synchronized (this) {
                if (localeMatcher == null) {
                    LocalePriorityList.Builder builder = LocalePriorityList.add(EN);

                    for (String w20AvailableLocale : W20_BUILTIN_LOCALES) {
                        builder.add(w20AvailableLocale);
                    }

                    String[] additionalLocaleCodes = application.getConfiguration().getStringArray("org.seedstack.i18n.additional-locales.codes");
                    if (additionalLocaleCodes != null) {
                        for (String additionalLocaleCode : additionalLocaleCodes) {
                            builder.add(additionalLocaleCode);
                        }
                    }

                    localeMatcher = new LocaleMatcher(builder.build());
                }
            }
        }
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
            if (!sourceConfiguration.containsKey(AVAILABLE)) {
                Set<String> availableLocales = localeService.getAvailableLocales();
                Set<String> closestAvailableLocales = new HashSet<String>(availableLocales.size());

                for (String availableLocale : availableLocales) {
                    closestAvailableLocales.add(getClosestW20Locale(availableLocale));
                }

                sourceConfiguration.put(AVAILABLE, closestAvailableLocales);
            }

            // "default" : "ietf-code-of-default-language"
            if (!sourceConfiguration.containsKey(DEFAULT)) {
                Locale defaultLocale = localeRepository.getDefaultLocale();
                if (defaultLocale != null) {
                    sourceConfiguration.put(DEFAULT, getClosestW20Locale(defaultLocale.getEntityId()));
                } else {
                    sourceConfiguration.put(DEFAULT, EN);
                }
            }
        }

    }

    private String getClosestW20Locale(String locale) {
        ULocale bestMatch = localeMatcher.getBestMatch(locale);
        return bestMatch.toLanguageTag();
    }
}
