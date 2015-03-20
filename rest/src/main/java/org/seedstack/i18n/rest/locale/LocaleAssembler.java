/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.locale;

import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.business.api.interfaces.assembler.BaseAssembler;

/**
 * @author pierre.thirouin@ext.mpsa.com
 * Date: 26/11/13
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
        targetEntity.setEntityId(sourceDto.getCode());
        targetEntity.setLanguage(sourceDto.getLanguage());
        targetEntity.setEnglishLanguage(sourceDto.getEnglishLanguage());
    }
}
