/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.locale;

import org.seedstack.business.assembler.LegacyBaseAssembler;
import org.seedstack.i18n.internal.domain.model.locale.Locale;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class LocaleAssembler extends LegacyBaseAssembler<Locale, LocaleRepresentation> {

    @Override
    protected void doAssembleDtoFromAggregate(LocaleRepresentation targetDto, Locale sourceEntity) {
        targetDto.setCode(sourceEntity.getId());
        targetDto.setLanguage(sourceEntity.getLanguage());
        targetDto.setEnglishLanguage(sourceEntity.getEnglishLanguage());
    }

    @Override
    protected void doMergeAggregateWithDto(Locale targetEntity, LocaleRepresentation sourceDto) {
        // Do nothing
    }
}
