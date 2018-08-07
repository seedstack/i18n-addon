/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.translation;


import org.seedstack.business.finder.Finder;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.i18n.rest.internal.key.KeySearchCriteria;

import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com
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
     * Returns request ranged result of translation representations.
     *
     * @param page     the page to query
     * @param criteria criteria filters
     * @return paginated keys
     */
    PaginatedView<TranslationRepresentation> findAllTranslations(Page page, KeySearchCriteria criteria);

}
