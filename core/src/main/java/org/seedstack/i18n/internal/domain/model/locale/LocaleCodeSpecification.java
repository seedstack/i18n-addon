/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.locale;

import org.kametic.specifications.AbstractSpecification;

import java.util.regex.Pattern;

/**
 * Verifies that the locale code is not null or empty and only contains letters or "-" and "#".
 *
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class LocaleCodeSpecification extends AbstractSpecification<String> {

    public static final String LOCALE_CODE_PATTERN = "(?i)[a-z\\-#]*";
    public static final String MESSAGE = "The locale should not be null or empty and should only contains letters or \"-\", but \"%s\" was found.";

    public static void assertCode(String locale) {
        if (!new LocaleCodeSpecification().isSatisfiedBy(locale)) {
            throw new IllegalArgumentException(String.format(LocaleCodeSpecification.MESSAGE, locale));
        }
    }

    @Override
    public boolean isSatisfiedBy(String candidate) {
        return !(candidate == null || candidate.equals(""))
                && Pattern.compile(LOCALE_CODE_PATTERN).matcher(candidate).matches();
    }
}
