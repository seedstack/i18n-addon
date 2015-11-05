/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.data.key;

import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.seedstack.business.assembler.BaseAssembler;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 20/03/14
 */
public class KeyDTOAssembler extends BaseAssembler<Key, KeyDTO> {

    @Override
    protected void doAssembleDtoFromAggregate(KeyDTO targetDto, Key sourceEntity) {
        targetDto.setName(sourceEntity.getEntityId());
        targetDto.setComment(sourceEntity.getComment());
        targetDto.setOutdated(sourceEntity.isOutdated());
        for (Translation tln : sourceEntity.getTranslations().values()) {
            targetDto.addTranslationDTO(tln.getEntityId().getLocale(), tln.getValue(), tln.isOutdated(), tln.isApproximate());
        }
    }

    @Override
    protected void doMergeAggregateWithDto(Key targetEntity, KeyDTO sourceDto) {
        targetEntity.setComment(sourceDto.getComment());
        targetEntity.setOutdated(sourceDto.isOutdated());
        for (TranslationDTO tln : sourceDto.getTranslations()) {
            targetEntity.addTranslation(tln.getLocale(), tln.getValue(), tln.isApproximate(), false);
        }
    }
}
