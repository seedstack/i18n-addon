/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.data.key;

import org.seedstack.business.assembler.BaseAssembler;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.Translation;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class KeyDTOAssembler extends BaseAssembler<Key, KeyDTO> {
    @Override
    public void mergeAggregateIntoDto(Key sourceAggregate, KeyDTO targetDto) {
        targetDto.setName(sourceAggregate.getId());
        targetDto.setComment(sourceAggregate.getComment());
        targetDto.setOutdated(sourceAggregate.isOutdated());
        for (Translation tln : sourceAggregate.getTranslations().values()) {
            targetDto.addTranslationDTO(tln.getId().getLocale(), tln.getValue(), tln.isOutdated(), tln.isApproximate());
        }
    }

    @Override
    public void mergeDtoIntoAggregate(KeyDTO sourceDto, Key targetAggregate) {
        targetAggregate.setComment(sourceDto.getComment());
        for (TranslationDTO translation : sourceDto.getTranslations()) {
            targetAggregate.addTranslation(translation.getLocale(), translation.getValue(), translation.isApproximate());
        }
        if (sourceDto.isOutdated()) {
            targetAggregate.setOutdated();
        }
        for (TranslationDTO translation : sourceDto.getTranslations()) {
            if (!translation.isOutdated()) {
                targetAggregate.addTranslation(translation.getLocale(), translation.getValue(), translation.isApproximate());
            }
        }
    }
}
