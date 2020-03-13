/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.data.key;

import java.util.stream.Stream;
import javax.inject.Inject;
import org.seedstack.business.assembler.dsl.FluentAssembler;
import org.seedstack.business.data.BaseDataImporter;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.Logging;
import org.seedstack.seed.transaction.Transactional;
import org.slf4j.Logger;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 13/03/14
 */
@JpaUnit("seed-i18n-domain")
@Transactional
public class KeyDataImporter extends BaseDataImporter<KeyDTO> {
    @Inject
    private KeyRepository keyRepository;
    @Logging
    private Logger logger;

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public boolean isInitialized() {
        return !keyRepository.isEmpty();
    }

    @Override
    public void clear() {
        logger.info("Clearing all i18n keys");
        keyRepository.clear();
    }

    @Override
    public void importData(Stream<KeyDTO> data) {
        logger.info("Importing i18n keys");
        fluentAssembler.merge(data)
                .into(Key.class)
                .fromFactory()
                .asStream()
                .forEach(keyRepository::add);
    }
}
