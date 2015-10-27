/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.service;

import com.ibm.icu.util.ULocale;
import org.seedstack.i18n.api.LocalizationService;
import org.seedstack.i18n.internal.application.service.TranslationService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.internal.domain.model.key.Translation;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 28/11/13
 */
public class TranslationServiceImpl implements TranslationService {

    private KeyRepository keyRepository;

    private LocalizationService localizationService;

    /**
     * Constructor.
     *
     * @param keyRepository       key repository
     * @param localizationService localization service
     */
    @Inject
    public TranslationServiceImpl(KeyRepository keyRepository, LocalizationService localizationService) {
        this.keyRepository = keyRepository;
        this.localizationService = localizationService;
    }

    @Override
    public Map<String, String> getTranslationsForLocale(String locale) {
        ICUBasedLocalizationService icuBasedLocalizationService = (ICUBasedLocalizationService) localizationService;
        Map<String, String> translations = new HashMap<String, String>();
        List<ULocale> localeIterator = icuBasedLocalizationService.getParentLocale(new ULocale(locale));

        for (Key key : keyRepository.loadAll()) {
            Translation translation = icuBasedLocalizationService.getTranslationWithFallBack(key, localeIterator);
            if (translation != null) {
                translations.put(key.getEntityId(), translation.getValue());
            }
        }

        return translations;
    }
}
