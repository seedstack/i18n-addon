/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.apache.commons.configuration.Configuration;
import org.seedstack.seed.core.api.Application;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 28/05/2014
 */
public class I18nCacheProvider implements Provider<LoadingCache<String, Map<String, String>>> {

    private static final int DEFAULT_CACHE_SIZE = 8192;
    private static final int DEFAULT_CACHE_CONCURRENCY = 32;

    @Inject
    private Application application;

    @Inject
    private CacheLoader<String, Map <String, String>> i18nCacheLoader;

    I18nCacheProvider() {
    }

    @Singleton
    @Override
    public LoadingCache<String, Map<String, String>> get() {
        Configuration configuration = application.getConfiguration();
        int cacheSize = configuration.getInt("org.seedstack.i18n.cache.max-size", DEFAULT_CACHE_SIZE);
        return CacheBuilder.newBuilder().maximumSize(cacheSize)
                .concurrencyLevel(configuration.getInt("org.seedstack.i18n.cache.concurrency", DEFAULT_CACHE_CONCURRENCY))
                .initialCapacity(configuration.getInt("org.seedstack.i18n.cache.initial-size", cacheSize / 4))
                .build(i18nCacheLoader);
    }
}
