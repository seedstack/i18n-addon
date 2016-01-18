/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.data.locale;


import org.seedstack.business.assembler.AssemblerTypes;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.seed.DataExporter;
import org.seedstack.seed.DataSet;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.util.Iterator;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 20/03/14
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@DataSet(group = "seed-i18n", name = "locale")
public class LocaleDataExporter implements DataExporter<LocaleDTO> {

    @Inject
    private LocaleRepository localeRepository;

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public Iterator<LocaleDTO> exportData() {
        return fluentAssembler.assemble(localeRepository.loadAll()).with(AssemblerTypes.MODEL_MAPPER).to(LocaleDTO.class).iterator();
    }
}
