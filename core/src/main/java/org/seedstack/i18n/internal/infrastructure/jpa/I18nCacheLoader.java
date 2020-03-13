/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.jpa;

import com.google.common.cache.CacheLoader;
import org.apache.commons.lang.StringUtils;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.service.TranslationService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class I18nCacheLoader extends CacheLoader<String, Map<String, String>> {
    @Inject
    private TranslationService messageService;
    @Inject
    private LocaleService localeService;

    @Override
    public Map<String, String> load(String key) {
        checkNotNull(key, "key must not be null");
        // If the default locale is not available, then the application is not configured
        if (StringUtils.isNotBlank(localeService.getDefaultLocale())) {
            return messageService.getTranslationsForLocale(key);
        } else {
            return new HashMap<>();
        }
    }
}
