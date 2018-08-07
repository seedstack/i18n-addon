/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.data.locale;

import java.util.stream.Stream;
import javax.inject.Inject;
import org.seedstack.business.assembler.dsl.FluentAssembler;
import org.seedstack.business.data.BaseDataExporter;
import org.seedstack.business.modelmapper.ModelMapper;
import org.seedstack.business.specification.Specification;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 20/03/14
 */
@JpaUnit("seed-i18n-domain")
@Transactional
public class LocaleDataExporter extends BaseDataExporter<LocaleDTO> {
    @Inject
    private LocaleRepository localeRepository;

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public Stream<LocaleDTO> exportData() {
        return fluentAssembler.assemble(localeRepository.get(Specification.any()))
                .with(ModelMapper.class)
                .toStreamOf(LocaleDTO.class);
    }
}
