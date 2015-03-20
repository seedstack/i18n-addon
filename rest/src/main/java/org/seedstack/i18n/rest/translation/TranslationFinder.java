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


import org.seedstack.business.api.interfaces.query.finder.Finder;
import org.seedstack.business.api.interfaces.query.range.Range;
import org.seedstack.business.api.interfaces.query.result.Result;

import java.util.List;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 26/11/13
 */
@Finder
public interface TranslationFinder {
    /**
     * Find translations for a locale.
     *
     * @param localeId locale identifier
     * @return Translation representation
     */
    List<TranslationRepresentation> findTranslations(String localeId);

    /**
     * Find a translation
     *
     * @param localeId locale identifier
     * @param keyId    key name
     * @return Translation representation
     */
    TranslationRepresentation findTranslation(String localeId, String keyId);

    /**
     *
     * Returns request ranged result of translation representations.
     *
     * @param range    range to query
     * @param criteria criteria filters
     * @return paginated keys
     */
    Result<TranslationRepresentation> findAllTranslations(Range range, Map<String, Object> criteria);

}
