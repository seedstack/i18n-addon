/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.io;

import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.seedstack.business.api.interfaces.assembler.BaseAssembler;

import java.util.HashMap;
import java.util.Map;

/**
 * Assemble data for export.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 16/04/2014
 */
public class DataAssembler extends BaseAssembler<Key, DataRepresentation> {

    @Override
    protected void doAssembleDtoFromAggregate(DataRepresentation targetDto, Key sourceEntity) {
        targetDto.setKey(sourceEntity.getEntityId());
        Map<String, String> translations = new HashMap<String, String>();
        for (Map.Entry<String, Translation> entry : sourceEntity.getTranslations().entrySet()) {
            translations.put(entry.getKey(), entry.getValue().getValue());
        }
        targetDto.setValue(translations);
    }

    @Override
    protected void doMergeAggregateWithDto(Key targetEntity, DataRepresentation sourceDto) {
        for (Map.Entry<String, String> entry : sourceDto.getValue().entrySet()) {
            Translation translation = targetEntity.getTranslation(entry.getKey());
            // Create a translation if it doesn't exist or if the translation has changed
            // So, if a translation has changed "outdated" and "approx" are passed to false
            if (translation == null || !translation.getValue().equals(entry.getValue())) {
                targetEntity.addTranslation(entry.getKey(), entry.getValue(), false, false);
            }
        }
    }
}
