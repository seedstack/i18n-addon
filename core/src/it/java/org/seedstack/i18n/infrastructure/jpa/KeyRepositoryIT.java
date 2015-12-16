/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.infrastructure.jpa;

import com.google.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.seedstack.i18n.internal.infrastructure.jpa.Units;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@RunWith(SeedITRunner.class)
public class KeyRepositoryIT {

    private String keyId;
    private Random random;
    @Inject
    private KeyFactory factory;
    @Inject
    private KeyRepository underTest;

    @Before
    public void setUp() {
        keyId = UUID.randomUUID().toString();
        random = new Random();
    }

    @Test
    public void testObjectMapping() {
        persistKey();
        assertSameLoadedKey();
    }

    @JpaUnit(Units.I18N)
    @Transactional
    public void persistKey() {
        Key key = createKey(keyId);
        underTest.persist(key);
    }

    @JpaUnit(Units.I18N)
    @Transactional
    @Test(expected = EntityExistsException.class)
    public void testCannotPersistDuplicate() {
        Key key = createKey(keyId);
        underTest.persist(key);
        Key key2 = createKey(keyId);
        underTest.persist(key2);
    }

    private Key createKey(String keyName) {
        Key key = factory.createKey(keyName);
        key.setComment("comment");
        key.addTranslation("fr", "traduction", true);
        key.addTranslation("en", "translation");
        return key;
    }

    @JpaUnit(Units.I18N)
    @Transactional
    public void assertSameLoadedKey() {
        Key key2 = underTest.load(keyId);
        assertThat(key2.getComment()).isEqualTo("comment");
        assertThat(key2.isOutdated()).isFalse();

        Translation frTranslation = key2.getTranslation("fr");
        assertThat(frTranslation).isNotNull();
        assertThat(frTranslation.getValue()).isEqualTo("traduction");
        assertThat(frTranslation.isApproximate()).isTrue();
        assertThat(frTranslation.isOutdated()).isFalse();

        assertThat(key2.getTranslation("en").getValue()).isEqualTo("translation");
    }

    @Test
    public void testSaveOutdatedState() {
        persistOutdatedKey();
        assertLoadedKeyIsOutdated();
    }

    @JpaUnit(Units.I18N)
    @Transactional
    public void persistOutdatedKey() {
        Key key = createKey(keyId);
        key.setOutdated();
        underTest.persist(key);
    }

    @JpaUnit(Units.I18N)
    @Transactional
    public void assertLoadedKeyIsOutdated() {
        Key key2 = underTest.load(keyId);
        assertThat(key2.isOutdated()).isTrue();
        assertThat(key2.getTranslation("fr").isOutdated()).isTrue();
        assertThat(key2.getTranslation("en").isOutdated()).isTrue();
    }

    @JpaUnit(Units.I18N)
    @Transactional
    @Test
    public void testLoadAll() {
        int expectedSize = random.nextInt(5) + 2;
        for (int i = 0; i < expectedSize; i++) {
            underTest.persist(createKey(UUID.randomUUID().toString()));
        }
        assertThat(underTest.loadAll()).hasSize(expectedSize);
    }

    @JpaUnit(Units.I18N)
    @Transactional
    @Test
    public void testCount() throws Exception {
        int expectedSize = random.nextInt(5) + 2;
        for (int i = 0; i < expectedSize; i++) {
            underTest.persist(createKey(UUID.randomUUID().toString()));
        }
        assertThat(underTest.count()).isEqualTo(expectedSize);
    }

    @JpaUnit(Units.I18N)
    @Transactional
    @Test
    public void testDeleteAll() {
        for (int i = 0; i < 5; i++) {
            underTest.persist(createKey(UUID.randomUUID().toString()));
        }
        underTest.deleteAll();

        assertThat(underTest.count()).isZero();
    }

    @JpaUnit(Units.I18N)
    @Transactional
    @Test
    public void testDelete() {
        List<Key> keysToDelete = new ArrayList<Key>();
        for (int i = 0; i < 5; i++) {
            Key key = createKey(UUID.randomUUID().toString());
            keysToDelete.add(key);
            underTest.persist(key);
        }
        underTest.delete(keysToDelete);

        assertThat(underTest.count()).isZero();
    }

    @JpaUnit(Units.I18N)
    @Transactional
    @After
    public void tearDown() throws Exception {
        underTest.deleteAll();
    }
}
