/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
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
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

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
        return entityManager.createQuery("SELECT k FROM org.seedstack.i18n.internal.domain.model.key.Key k")
                .getResultList();
    }

    @Override
    public Long count() {
        return (Long) entityManager.createQuery("SELECT COUNT(k) FROM org.seedstack.i18n.internal.domain.model.key.Key k")
                .getSingleResult();
    }

    @Override
    public void deleteAll() {
        entityManager.createQuery("DELETE FROM org.seedstack.i18n.internal.domain.model.key.Key").executeUpdate();
        invalidCache();
    }

    @Override
    public void delete(List<Key> keys) {
        for (Key key : keys) {
            delete(key);
        }
        invalidCache();
    }

    @Override
    public void doDelete(Key key) {
        super.doDelete(key);
        invalidCache();
    }

    @Override
    public void doPersist(Key entity) {
        super.doPersist(entity);
        invalidCache();
    }

    @Override
    public Key doSave(Key entity) {
        invalidCache();
        return super.doSave(entity);
    }

    private void invalidCache() {
        loadingCache.invalidateAll();
    }
}
