/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.translation;

import com.google.common.base.Preconditions;
import org.seedstack.i18n.api.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.apache.commons.lang.StringUtils;
import org.seedstack.business.api.interfaces.assembler.BaseAssembler;

import javax.inject.Inject;
import java.util.Map;

/**
 * Assembles a TranslationRepresentation which contains key information, default translation and a target translation.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 16/05/2014
 */
public class TranslationAssembler extends BaseAssembler<Key, TranslationRepresentation> {

    @Inject
    private LocaleService localeService;

    @Override
    protected void doAssembleDtoFromAggregate(TranslationRepresentation targetDto, Key sourceEntity) {
        Preconditions.checkArgument(sourceEntity.getTranslations().size() <= 2,
                "The key should only contain the default and the target translations. But the actual key has more than two translations.");
        targetDto.setName(sourceEntity.getEntityId());
        targetDto.setComment(sourceEntity.getComment());

        String defaultLocale = localeService.getDefaultLocale();
        for (Map.Entry<String, Translation> translation : sourceEntity.getTranslations().entrySet()) {
            TranslationValueRepresentation tlnRep = new TranslationValueRepresentation();
            if (translation.getValue() != null) {
                tlnRep.setLocale(translation.getValue().getEntityId().getLocale());
                tlnRep.setOutdated(translation.getValue().isOutdated());
                tlnRep.setApprox(translation.getValue().isApproximate());
                tlnRep.setTranslation(translation.getValue().getValue());
            } else {
                tlnRep.setLocale(translation.getKey());
                tlnRep.setTranslation("");
            }
            if (translation.getKey().equals(defaultLocale)) {
                targetDto.setSource(tlnRep);
            } else {
                targetDto.setTarget(tlnRep);
            }
        }
        targetDto.setMissing(targetDto.getTarget() == null || StringUtils.isBlank(targetDto.getTarget().getTranslation()));
    }

    @Override
    protected void doMergeAggregateWithDto(Key targetEntity, TranslationRepresentation sourceDto) {
        // update only the comment and the target translation (not the default translation)
        targetEntity.setComment(sourceDto.getComment());
        TranslationValueRepresentation target = sourceDto.getTarget();
        targetEntity.addTranslation(target.getLocale(), target.getTranslation(), target.isApprox(), target.isOutdated());
    }
}
