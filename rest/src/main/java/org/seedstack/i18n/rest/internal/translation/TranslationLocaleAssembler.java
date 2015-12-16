/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.translation;

import org.seedstack.business.Service;
import org.seedstack.i18n.internal.domain.model.key.Key;

import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@Service
public interface TranslationLocaleAssembler {

    TranslationRepresentation assemble(Key key, String locale);

    List<TranslationRepresentation> assemble(List<Key> key, String locale);
}
