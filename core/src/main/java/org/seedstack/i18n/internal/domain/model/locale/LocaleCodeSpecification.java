/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.locale;

import java.util.regex.Pattern;
import org.kametic.specifications.AbstractSpecification;

/**
 * Verifies that the locale code is not null or empty and only contains letters or "-" and "#".
 *
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class LocaleCodeSpecification extends AbstractSpecification<String> {
    private static final String LOCALE_CODE_PATTERN = "(?i)[a-z0-9\\-#]*";
    static final String MESSAGE = "The locale code should should only contain letters, numbers and \"-\", " +
            "but \"%s\" was found.";

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
