/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.LocalizationService;
import org.seedstack.i18n.internal.TranslationService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.Configuration;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
public class TranslationServiceImpl implements TranslationService {

    public static final String IS_EMPTY_ERROR_MESSAGE = "The %s can't be null or empty";
    private final KeyRepository keyRepository;
    private final LocalizationService localizationService;
    private final LocaleService localeService;

    /**
     * The allowMissingTranslation field is true by default.
     * <ul>
     * <li>When true the missing translation won't appear in the key/translation map.</li>
     * <li>When false they will appear with the translation [key.name]</li>
     * </ul>
     */
    @Configuration(value = "org.seedstack.i18n.allow-missing-translation", defaultValue = "true")
    private boolean allowMissingTranslation;

    /**
     * Constructor.
     *
     * @param keyRepository       the key repository
     * @param localizationService the localization service
     * @param localeService       the locale service
     */
    @Inject
    public TranslationServiceImpl(KeyRepository keyRepository, LocalizationService localizationService, LocaleService localeService) {
        this.keyRepository = keyRepository;
        this.localizationService = localizationService;
        this.localeService = localeService;
    }

    @Override
    public Map<String, String> getTranslationsForLocale(String locale) {
        Map<String, String> translations = new HashMap<String, String>();
        for (Key key : keyRepository.loadAll()) {
            if (key.isTranslated(locale) || !allowMissingTranslation) {
                String translation = localizationService.localize(locale, key.getEntityId());
                translations.put(key.getEntityId(), translation);
            }
        }
        return translations;
    }

    @Override
    public void translate(String keyName, String locale, String value) {
        if (isEmpty(keyName)) {
            throw new IllegalArgumentException(String.format(IS_EMPTY_ERROR_MESSAGE, "key name"));
        }
        if (isEmpty(locale)) {
            throw new IllegalArgumentException(String.format(IS_EMPTY_ERROR_MESSAGE, "locale"));
        }

        Key key = keyRepository.load(keyName);
        if (isEmpty(value)) {
            key.removeTranslation(locale);
        } else {
            updateTranslation(locale, value, key);
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.equals("");
    }

    private void updateTranslation(String locale, String value, Key key) {
        if (translationHasChanged(key, locale, value)) {
            if (isDefaultLocale(locale)) {
                key.setOutdated();
            }
            key.addTranslation(locale, value);
        }
    }

    private boolean translationHasChanged(Key key, String locale, String newTranslation) {
        Translation translation = key.getTranslation(locale);
        return translation == null || !newTranslation.equals(translation.getValue());
    }

    private boolean isDefaultLocale(String locale) {
        String defaultLocale = localeService.getDefaultLocale();
        return defaultLocale != null && defaultLocale.equals(locale);
    }
}
