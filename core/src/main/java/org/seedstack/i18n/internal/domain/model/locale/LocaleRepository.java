/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.locale;

import org.seedstack.business.api.domain.GenericRepository;

import java.util.List;

/**
 * Store application locales.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 20/11/13
 */
public interface LocaleRepository extends GenericRepository<Locale, String> {

    /**
     * @return all locales
     */
    List<Locale> loadAll();

    /**
     * Get the number of available locale.
     *
     * @return locale count
     */
    Long count();

    /**
     * @return default locale
     */
    Locale getDefaultLocale();

    /**
     * Changes the default locale.
     *
     * @param locale new default locale
     */
    void changeDefaultLocaleTo(String locale);

    /**
     * Saves all given locales
     *
     * @param entity locales
     */
    void persistAll(List<Locale> entity);

    /**
     * Deletes all locales.
     */
    void clear();
}
