/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.data.DataManager;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

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
        keyRepository.add(key1);
    }

    @Test
    public void export_aggregate_key() {
        // Export
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        dataManager.exportData(outputStream, "seed-i18n");

        // Remove data
        keyRepository.remove(KEY_NAME);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        dataManager.importData(inputStream);

        // Check imported data
        Assertions.assertThat(keyRepository.get(KEY_NAME)).isNotEmpty();
    }

}
