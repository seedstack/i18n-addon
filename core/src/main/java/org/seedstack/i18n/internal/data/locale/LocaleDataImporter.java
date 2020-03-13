/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.data.locale;

import java.util.stream.Stream;
import javax.inject.Inject;
import org.seedstack.business.assembler.dsl.FluentAssembler;
import org.seedstack.business.data.BaseDataImporter;
import org.seedstack.business.modelmapper.ModelMapper;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleFactory;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
public class LocaleDataImporter extends BaseDataImporter<LocaleDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocaleDataImporter.class);
    private final LocaleRepository localeRepository;
    private final LocaleFactory localeFactory;
    private final FluentAssembler fluentAssembler;

    @Inject
    public LocaleDataImporter(LocaleRepository localeRepository, LocaleFactory localeFactory,
            FluentAssembler fluentAssembler) {
        this.localeRepository = localeRepository;
        this.localeFactory = localeFactory;
        this.fluentAssembler = fluentAssembler;
    }

    @Override
    public boolean isInitialized() {
        return !localeRepository.isEmpty();
    }

    @Override
    public void clear() {
        LOGGER.info("Clearing all i18n locales");
        localeRepository.clear();
    }

    @Override
    public void importData(Stream<LocaleDTO> data) {
        data.map(dto -> {
            Locale newLocale = localeFactory.createFromCode(dto.getCode());
            fluentAssembler.merge(dto).with(ModelMapper.class).into(newLocale);
            return newLocale;
        }).forEach(localeRepository::add);
        LOGGER.info("Import of i18n locales completed");
    }
}
