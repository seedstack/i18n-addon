/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.csv;

import org.seedstack.business.assembler.LegacyBaseAssembler;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.seedstack.business.assembler.BaseAssembler;
import org.seedstack.i18n.rest.internal.io.CSVRepresentation;

import java.util.HashMap;
import java.util.Map;

/**
 * Assemble data for export.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
class CSVAssembler extends LegacyBaseAssembler<Key, CSVRepresentation> {

    @Override
    protected void doAssembleDtoFromAggregate(CSVRepresentation targetDto, Key sourceEntity) {
        targetDto.setKey(sourceEntity.getId());

        Map<String, String> translations = new HashMap<>();
        for (Map.Entry<String, Translation> entry : sourceEntity.getTranslations().entrySet()) {
            translations.put(entry.getKey(), entry.getValue().getValue());
        }
        targetDto.setValue(translations);
    }

    @Override
    protected void doMergeAggregateWithDto(Key targetKey, CSVRepresentation sourceDto) {
        if (sourceDto.getValue() != null) {
            for (Map.Entry<String, String> entry : sourceDto.getValue().entrySet()) {
                String locale = entry.getKey();
                String translation = entry.getValue();

                if (shouldUpdateTranslation(targetKey, locale, translation)) {
                    targetKey.addTranslation(locale, translation);
                }
            }
        }
    }

    private boolean shouldUpdateTranslation(Key targetKey, String locale, String translation) {
        return isNotBlank(translation) && translationHasChanged(targetKey, locale, translation);
    }

    private boolean isNotBlank(String translation) {
        return translation != null && !translation.trim().equals("");
    }

    private boolean translationHasChanged(Key targetKey, String localeCode, String translationValue) {
        Translation translation = targetKey.getTranslation(localeCode);
        return translation == null || !translation.getValue().equals(translationValue);
    }
}
