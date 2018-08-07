/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.rest.internal.locale;

import org.seedstack.business.assembler.BaseAssembler;
import org.seedstack.i18n.internal.domain.model.locale.Locale;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class LocaleAssembler extends BaseAssembler<Locale, LocaleRepresentation> {
    @Override
    public void mergeAggregateIntoDto(Locale sourceAggregate, LocaleRepresentation targetDto) {
        targetDto.setCode(sourceAggregate.getId());
        targetDto.setLanguage(sourceAggregate.getLanguage());
        targetDto.setEnglishLanguage(sourceAggregate.getEnglishLanguage());
    }

    @Override
    public void mergeDtoIntoAggregate(LocaleRepresentation sourceDto, Locale targetAggregate) {
// do nothing
    }
}
