/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.locale;

import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.business.assembler.BaseAssembler;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class LocaleAssembler extends BaseAssembler<Locale, LocaleRepresentation> {

    @Override
    protected void doAssembleDtoFromAggregate(LocaleRepresentation targetDto, Locale sourceEntity) {
        targetDto.setCode(sourceEntity.getEntityId());
        targetDto.setLanguage(sourceEntity.getLanguage());
        targetDto.setEnglishLanguage(sourceEntity.getEnglishLanguage());
    }

    @Override
    protected void doMergeAggregateWithDto(Locale targetEntity, LocaleRepresentation sourceDto) {
        // Do nothing
    }
}
