/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.jpa;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.seedstack.i18n.I18nConfig;
import org.seedstack.seed.Configuration;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class I18nCacheProvider implements Provider<LoadingCache<String, Map<String, String>>> {
    @Configuration
    private I18nConfig.CacheConfig cacheConfig = new I18nConfig.CacheConfig();
    @Inject
    private CacheLoader<String, Map<String, String>> i18nCacheLoader;

    @Singleton
    @Override
    public LoadingCache<String, Map<String, String>> get() {
        return CacheBuilder.newBuilder()
                .maximumSize(cacheConfig.getMaxSize())
                .concurrencyLevel(cacheConfig.getConcurrencyLevel())
                .initialCapacity(cacheConfig.getInitialSize())
                .build(i18nCacheLoader);
    }
}
