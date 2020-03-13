/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import com.ibm.icu.util.ULocale;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.seedstack.i18n.I18nConfig;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.seedstack.i18n.internal.domain.service.TranslationService;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.Configuration;
import org.seedstack.seed.transaction.Transactional;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
class TranslationServiceImpl implements TranslationService {
    private static final String IS_EMPTY_ERROR_MESSAGE = "The %s can't be null or empty";
    private final KeyRepository keyRepository;
    private final LocaleService localeService;
    @Configuration
    private I18nConfig i18nConfig = new I18nConfig();

    @Inject
    public TranslationServiceImpl(KeyRepository keyRepository, LocaleService localeService) {
        this.keyRepository = keyRepository;
        this.localeService = localeService;
    }

    @Override
    public Optional<String> getTranslationWithFallback(String locale, String keyName) {
        return keyRepository.get(keyName).flatMap(k -> getTranslationWithFallback(locale, k));
    }

    private Optional<String> getTranslationWithFallback(String locale, Key key) {
        for (String parentLocale : getParentLocalesFor(new ULocale(locale))) {
            if (key.isTranslated(parentLocale)) {
                return Optional.of(key.getTranslation(parentLocale).getValue());
            }
        }

        if (i18nConfig.isTranslationFallback()) {
            String defaultLocale = localeService.getDefaultLocale();
            if (key.isTranslated(defaultLocale)) {
                return Optional.of(key.getTranslation(defaultLocale).getValue());
            }
        }

        return Optional.empty();
    }

    private List<String> getParentLocalesFor(ULocale locale) {
        List<String> locales = new ArrayList<>();
        ULocale current = locale;
        while (current != null && StringUtils.isNotBlank(current.toString())) {
            locales.add(current.toLanguageTag());
            current = current.getFallback();
        }
        return locales;
    }

    @Override
    public Map<String, String> getTranslationsForLocale(String locale) {
        Map<String, String> translations = new HashMap<>();
        for (Key key : keyRepository.loadAll()) {
            Optional<String> translation = getTranslationWithFallback(locale, key);
            if (translation.isPresent() || !i18nConfig.isAllowMissingTranslations()) {
                translations.put(key.getId(), translation.orElse("[" + key.getId() + "]"));
            }
        }
        return translations;
    }

    @Override
    public void translate(String keyName, String locale, String translation) {
        if (isEmpty(keyName)) {
            throw new IllegalArgumentException(String.format(IS_EMPTY_ERROR_MESSAGE, "key name"));
        }
        if (isEmpty(locale)) {
            throw new IllegalArgumentException(String.format(IS_EMPTY_ERROR_MESSAGE, "locale"));
        }

        Key key = keyRepository.get(keyName).orElseThrow(() -> new IllegalArgumentException(
                "The locale " + locale + " is not available."));
        if (isEmpty(translation)) {
            key.removeTranslation(locale);
        } else {
            updateTranslation(locale, translation, key);
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
