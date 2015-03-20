/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.data;

import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.assertj.core.api.Assertions;
import org.javatuples.Triplet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.core.api.DataManager;
import org.seedstack.seed.core.api.Logging;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.persistence.jpa.api.JpaUnit;
import org.seedstack.seed.transaction.api.Transactional;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 14/03/14
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class ExportDataIT {

    @Inject
    private DataManager dataManager;

    @Inject
    private KeyFactory keyFactory;

    @Inject
    private KeyRepository keyRepository;

    @Logging
    private Logger logger;

    @Before
    public void setUp() {
        Map<String, Triplet<String, Boolean, Boolean>> translations = new HashMap<String, Triplet<String, Boolean, Boolean>>();
        translations.put("fr", new Triplet<String, Boolean, Boolean>("key1fr", false, false));
        translations.put("en", new Triplet<String, Boolean, Boolean>("key1en", false, false));

        Key key1 = keyFactory.createKey("key1", "my comment", translations);
        keyRepository.persist(key1);
    }

    @Test
    public void export_aggregate_key() {
        // Export
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        dataManager.exportData(outputStream, "seed-i18n");
        logger.info(new String(outputStream.toByteArray()));

        // Remove data
        keyRepository.delete(keyRepository.load("key1"));
        Key deletedKey = keyRepository.load("key1");
        Assertions.assertThat(deletedKey).isNull();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        dataManager.importData(inputStream, null, null, true);

        // Check imported data
        Key loadedKey = keyRepository.load("key1");
        Assertions.assertThat(loadedKey).isNotNull();
    }

}
