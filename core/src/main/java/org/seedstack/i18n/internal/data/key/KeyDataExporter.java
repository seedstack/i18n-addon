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
import org.seedstack.business.data.BaseDataExporter;
import org.seedstack.business.data.DataSet;
import org.seedstack.business.specification.Specification;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
public class KeyDataExporter extends BaseDataExporter<KeyDTO> {

    @Inject
    private KeyRepository keyRepository;

    @Inject
    private FluentAssembler fluentAssembler;

    @Override
    public Stream<KeyDTO> exportData() {
        return fluentAssembler.assemble(keyRepository.get(Specification.any())).toStreamOf(KeyDTO.class);
    }
}
