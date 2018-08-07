/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class I18nPermissions {

    public static final String LOCALE_READ = "seed:i18n:locale:read";
    public static final String LOCALE_WRITE = "seed:i18n:locale:write";
    public static final String LOCALE_DELETE = "seed:i18n:locale:delete";

    public static final String KEY_READ = "seed:i18n:key:read";
    public static final String KEY_WRITE = "seed:i18n:key:write";
    public static final String KEY_DELETE = "seed:i18n:key:delete";

    public static final String TRANSLATION_READ = "seed:i18n:translation:read";
    public static final String TRANSLATION_WRITE = "seed:i18n:translation:write";
    public static final String TRANSLATION_DELETE = "seed:i18n:translation:delete";
}
