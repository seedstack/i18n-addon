/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.seedstack.coffig.Config;

@Config("i18n")
public class I18nConfig {
    private boolean translationFallback = false;
    private boolean allowMissingTranslations = true;
    private List<String> additionalLocales = new ArrayList<>();
    private CacheConfig cache = new CacheConfig();

    /**
     * The translationFallback flag is false by default. When true it enables a fallback to the default
     * language when no translation is found after walking the locale hierarchy.
     *
     * @return true if fallback is allowed, false otherwise.
     */
    public boolean isTranslationFallback() {
        return translationFallback;
    }

    public I18nConfig setTranslationFallback(boolean translationFallback) {
        this.translationFallback = translationFallback;
        return this;
    }

    /**
     * The flag allowMissingTranslation is true by default.
     * <ul>
     * <li>When true the missing translation won't appear in the key/translation map.</li>
     * <li>When false they will appear with the translation [key.name]</li>
     * </ul>
     *
     * @return true if missing translations are processed as empty string, false otherwise.
     */
    public boolean isAllowMissingTranslations() {
        return allowMissingTranslations;
    }

    public I18nConfig setAllowMissingTranslations(boolean allowMissingTranslations) {
        this.allowMissingTranslations = allowMissingTranslations;
        return this;
    }

    public List<String> getAdditionalLocales() {
        return Collections.unmodifiableList(additionalLocales);
    }

    public I18nConfig addAdditionalLocales(String... additionalLocales) {
        this.additionalLocales.addAll(Arrays.asList(additionalLocales));
        return this;
    }

    public CacheConfig cacheConfig() {
        return cache;
    }

    @Config("cache")
    public static class CacheConfig {
        private static final int DEFAULT_CACHE_MAX_SIZE = 8192;
        private static final int DEFAULT_CACHE_CONCURRENCY = 32;
        private int maxSize = DEFAULT_CACHE_MAX_SIZE;
        private int initialSize;
        private int concurrencyLevel;

        public CacheConfig() {
            this.initialSize = this.maxSize / 4;
            this.concurrencyLevel = DEFAULT_CACHE_CONCURRENCY;
        }

        public int getInitialSize() {
            return this.initialSize;
        }

        public int getMaxSize() {
            return this.maxSize;
        }

        public int getConcurrencyLevel() {
            return this.concurrencyLevel;
        }
    }
}
