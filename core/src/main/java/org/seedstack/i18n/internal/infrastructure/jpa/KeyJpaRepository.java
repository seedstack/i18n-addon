/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.jpa;

import com.google.common.cache.LoadingCache;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.jpa.BaseJpaRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * JPA implementation of key repository.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
public class KeyJpaRepository extends BaseJpaRepository<Key, String> implements KeyRepository {

    @Inject
    private LoadingCache<String, Map<String, String>> loadingCache;

    @SuppressWarnings("unchecked")
    @Override
    public List<Key> loadAll() {
        return getEntityManager().createQuery("SELECT k FROM org.seedstack.i18n.internal.domain.model.key.Key k")
                .getResultList();
    }

    @Override
    public void delete(List<Key> keys) {
        for (Key key : keys) {
            remove(key);
        }
        invalidCache();
    }

    @Override
    public void clear() {
        super.clear();
        invalidCache();
    }

    @Override
    public void remove(String id) {
        super.remove(id);
        invalidCache();
    }

    @Override
    public void remove(Key aggregate) {
        super.remove(aggregate);
        invalidCache();
    }

    @Override
    public void add(Key aggregate) {
        super.add(aggregate);
        invalidCache();
    }

    @Override
    public Key update(Key aggregate) {
        invalidCache();
        return super.update(aggregate);
    }

    private void invalidCache() {
        loadingCache.invalidateAll();
    }
}
