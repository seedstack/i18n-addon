/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.jpa;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import org.seedstack.seed.Install;

import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@Install
public class I18nCacheModule extends PrivateModule {

    private static final TypeLiteral<LoadingCache<String, Map<String, String>>> LOADING_CACHE_TYPE_LITERAL = new TypeLiteral<LoadingCache<String, Map<String, String>>>() {
    };
    private static final TypeLiteral<CacheLoader<String, Map<String, String>>> CACHE_LOADER_TYPE_LITERAL = new TypeLiteral<CacheLoader<String, Map<String, String>>>() {
    };

    @Override
    protected void configure() {
        bind(CACHE_LOADER_TYPE_LITERAL).to(I18nCacheLoader.class).in(Singleton.class);
        bind(LOADING_CACHE_TYPE_LITERAL).toProvider(I18nCacheProvider.class).in(Singleton.class);
        expose(LOADING_CACHE_TYPE_LITERAL);
    }
}
