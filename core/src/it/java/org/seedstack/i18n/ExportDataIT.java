/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.DataManager;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class ExportDataIT {

    public static final String KEY_NAME = "key1";
    public static final String FR = "fr";
    public static final String EN = "en";

    @Inject
    private DataManager dataManager;

    @Inject
    private KeyFactory keyFactory;

    @Inject
    private KeyRepository keyRepository;

    @Before
    public void setUp() {
        Key key1 = keyFactory.createKey(KEY_NAME);
        key1.addTranslation(FR, "key1fr");
        key1.addTranslation(EN, "key1en");
        keyRepository.persist(key1);
    }

    @Test
    public void export_aggregate_key() {
        // Export
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        dataManager.exportData(outputStream, "seed-i18n");

        // Remove data
        keyRepository.delete(keyRepository.load(KEY_NAME));

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        dataManager.importData(inputStream, null, null, true);

        // Check imported data
        Key loadedKey = keyRepository.load(KEY_NAME);
        Assertions.assertThat(loadedKey).isNotNull();
    }

}
