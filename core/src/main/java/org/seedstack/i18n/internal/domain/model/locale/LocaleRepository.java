/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.locale;

import java.util.List;
import org.seedstack.business.domain.Repository;

/**
 * Store application locales.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 20/11/13
 */
public interface LocaleRepository extends Repository<Locale, String> {

    /**
     * @return all locales
     */
    List<Locale> loadAll();

    /**
     * @return default locale
     */
    Locale getDefaultLocale();

    /**
     * Changes the default locale.
     *
     * @param locale the new default locale
     * @throws java.lang.IllegalArgumentException if the given locale is blank or is not available
     */
    void changeDefaultLocaleTo(String locale);
}
