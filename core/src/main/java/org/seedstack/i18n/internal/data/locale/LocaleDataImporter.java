/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.data.locale;


import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.seed.DataImporter;
import org.seedstack.seed.DataSet;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 20/03/14
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@DataSet(group = "seed-i18n", name = "locale")
public class LocaleDataImporter implements DataImporter<LocaleDTO> {

    @Inject
    private LocaleRepository localeRepository;

    private List<LocaleDTO> staging = new ArrayList<LocaleDTO>();

    private static final Logger LOGGER = LoggerFactory.getLogger(LocaleDataImporter.class);

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public boolean isInitialized() {
        boolean initialized = localeRepository.count() != 0;
        if (initialized) {
            LOGGER.info("i18n keys already initialized");
        } else {
            LOGGER.info("i18n keys not initialized");
        }

        return initialized;
    }

    @Override
    public void importData(LocaleDTO data) {
        staging.add(data);
    }

    @Override
    public void commit(boolean clear) {
        if (clear) {
            LOGGER.info("Clear i18n locale repository");
            for (Locale locale : localeRepository.loadAll()) {
                localeRepository.delete(locale);
            }
        }
        for (LocaleDTO localeDTO : staging) {
            Locale locale = fluentAssembler.merge(localeDTO).into(Locale.class).fromFactory();
            localeRepository.save(locale);
        }
        LOGGER.info("Import of i18n locale completed");
        staging.clear();
    }

    @Override
    public void rollback() {
        staging.clear();
        LOGGER.warn("Rollback i18n locale import");
    }
}
