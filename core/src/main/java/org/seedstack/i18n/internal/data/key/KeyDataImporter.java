/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.data.key;

import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.seed.DataImporter;
import org.seedstack.seed.DataSet;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 13/03/14
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@DataSet(group="seed-i18n", name="key")
public class KeyDataImporter implements DataImporter<KeyDTO> {

    @Inject
    private KeyRepository keyRepository;

    private Set<KeyDTO> staging = new HashSet<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyDataImporter.class);

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public boolean isInitialized() {
        boolean initialized = keyRepository.count() != 0;
        if(initialized) {
            LOGGER.info("i18n keys already initialized");
        } else {
            LOGGER.info("i18n keys not initialized");
        }

        return initialized;
    }

    @Override
    public void importData(KeyDTO data) {
        staging.add(data);
    }

    @Override
    public void commit(boolean clear) {
        if (clear) {
            LOGGER.info("Clear i18n key repository");
            for (Key key : keyRepository.loadAll()) {
                keyRepository.delete(key);
            }
        }
        LOGGER.info("staging size: " + staging.size());
        for (KeyDTO keyDTO : staging) {
            try {
                Key key = fluentAssembler.merge(keyDTO).into(Key.class).fromFactory();
                keyRepository.persist(key);
            } catch (RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
                throw e;
            }
        }
        LOGGER.info("Import of i18n key completed");
        staging.clear();
    }

    @Override
    public void rollback() {
        staging.clear();
        LOGGER.warn("Rollback i18n key import");
    }
}
