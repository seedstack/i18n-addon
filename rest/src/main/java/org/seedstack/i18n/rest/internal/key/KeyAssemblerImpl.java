/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.key;

import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.Translation;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
class KeyAssemblerImpl implements KeyAssembler {

    private LocaleService localeService;

    @Inject
    public KeyAssemblerImpl(LocaleService localeService) {
        this.localeService = localeService;
    }

    @Override
    public List<KeyRepresentation> assemble(List<Key> keys) {
        List<KeyRepresentation> keyRepresentations = new ArrayList<>();
        for (Key key : keys) {
            keyRepresentations.add(assemble(key));
        }
        return keyRepresentations;
    }

    @Override
    public KeyRepresentation assemble(Key key) {
        KeyRepresentation keyRepresentation = new KeyRepresentation();
        keyRepresentation.setName(key.getId());
        keyRepresentation.setComment(key.getComment());
        keyRepresentation.setOutdated(key.isOutdated());
        String defaultLocale = localeService.getDefaultLocale();
        keyRepresentation.setDefaultLocale(defaultLocale);
        assembleDefaultTranslation(key, keyRepresentation, defaultLocale);
        return keyRepresentation;
    }

    private void assembleDefaultTranslation(Key key, KeyRepresentation keyRepresentation, String defaultLocale) {
        if (key.isTranslated(defaultLocale)) {
            Translation translation = key.getTranslation(defaultLocale);
            keyRepresentation.setTranslation(translation.getValue());
            keyRepresentation.setApprox(translation.isApproximate());
        } else {
            keyRepresentation.setMissing(true);
        }
    }
}
