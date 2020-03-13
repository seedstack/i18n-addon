/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.locale;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class LocaleCodeSpecificationTest {

    private final LocaleCodeSpecification underTest = new LocaleCodeSpecification();

    @Test
    public void testSatisfyByWithValidCodes() {
        assertThat(underTest.isSatisfiedBy("fr")).isTrue();
        assertThat(underTest.isSatisfiedBy("fr-BE")).isTrue();
        assertThat(underTest.isSatisfiedBy("ja-JP-JP-#u-ca-japanese")).isTrue();
    }

    @Test
    public void testSatisfyByWithInvalidCodes() {
        assertThat(underTest.isSatisfiedBy("")).isFalse();
        assertThat(underTest.isSatisfiedBy(null)).isFalse();
        assertThat(underTest.isSatisfiedBy("fr_BE")).isFalse();
        assertThat(underTest.isSatisfiedBy("fr BE")).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssertWit() {
        LocaleCodeSpecification.assertCode("fr_BE");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssert() {
        LocaleCodeSpecification.assertCode(null);
    }
}
