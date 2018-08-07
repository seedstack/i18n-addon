/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.translation;

import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.Translation;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
class TranslationLocaleAssemblerImpl implements TranslationLocaleAssembler{

    private final LocaleService localeService;

    @Inject
    public TranslationLocaleAssemblerImpl(LocaleService localeService) {
        this.localeService = localeService;
    }

    @Override
    public TranslationRepresentation assemble(Key key, String locale) {
        TranslationRepresentation representation = new TranslationRepresentation();
        assembleKeyInfo(key, representation);
        assembleSourceTranslation(representation, key);
        assembleTargetTranslation(representation, key, locale);
        return representation;
    }

    private void assembleKeyInfo(Key key, TranslationRepresentation representation) {
        representation.setName(key.getId());
        representation.setComment(key.getComment());
    }

    private void assembleSourceTranslation(TranslationRepresentation representation, Key key) {
        String defaultLocale = localeService.getDefaultLocale();
        if (defaultLocale != null && key.isTranslated(defaultLocale)) {
            TranslationValueRepresentation source = assembleTranslation(key, defaultLocale);
            representation.setSource(source);
        }
    }

    private void assembleTargetTranslation(TranslationRepresentation representation, Key key, String locale) {
        if (key.isTranslated(locale)) {
            TranslationValueRepresentation source = assembleTranslation(key, locale);
            representation.setTarget(source);
        } else {
            representation.setMissing(true);
        }
    }

    private TranslationValueRepresentation assembleTranslation(Key key, String locale) {
        TranslationValueRepresentation source = new TranslationValueRepresentation();
        source.setLocale(locale);
        Translation translation = key.getTranslation(locale);
        source.setTranslation(translation.getValue());
        source.setApprox(translation.isApproximate());
        source.setOutdated(translation.isOutdated());
        return source;
    }

    @Override
    public List<TranslationRepresentation> assemble(List<Key> keys, String locale) {
        List<TranslationRepresentation> representations = new ArrayList<>();
        for (Key key : keys) {
            representations.add(assemble(key, locale));
        }
        return representations;
    }
}
