/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.key;

import com.google.common.base.Preconditions;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.apache.commons.lang.StringUtils;
import org.seedstack.business.api.interfaces.assembler.BaseAssembler;

/**
 * Assembles a Key representation with key metadata and default translation.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 15/05/2014
 */
public class KeyAssembler extends BaseAssembler<Key, KeyRepresentation> {

    @Override
    protected void doAssembleDtoFromAggregate(KeyRepresentation targetDto, Key sourceEntity) {
        Preconditions.checkArgument(sourceEntity.getTranslations().size() <= 1, "The key should only contains the default translation, but translations had more than one element.");

        targetDto.setName(sourceEntity.getEntityId());
        targetDto.setOutdated(sourceEntity.isOutdated());
        targetDto.setComment(sourceEntity.getComment());

        if (!sourceEntity.getTranslations().isEmpty()) {
            Translation translation = sourceEntity.getTranslations().values().iterator().next();

            targetDto.setDefaultLocale(translation.getEntityId().getLocale());
            targetDto.setApprox(translation.isApproximate());
            targetDto.setTranslation(translation.getValue());
            targetDto.setMissing(StringUtils.isBlank(translation.getValue()));
        } else {
            targetDto.setMissing(true);
        }
    }

    @Override
    protected void doMergeAggregateWithDto(Key targetEntity, KeyRepresentation sourceDto) {
        targetEntity.setOutdated(sourceDto.isOutdated());
        targetEntity.setComment(sourceDto.getComment());
        targetEntity.addTranslation(sourceDto.getDefaultLocale(), sourceDto.getTranslation(), sourceDto.isApprox(), sourceDto.isOutdated());
    }
}
