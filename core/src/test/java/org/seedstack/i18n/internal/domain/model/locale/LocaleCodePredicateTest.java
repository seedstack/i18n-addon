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
public class LocaleCodePredicateTest {

    private final LocaleCodePredicate underTest = new LocaleCodePredicate();

    @Test
    public void testSatisfyByWithValidCodes() {
        assertThat(underTest.test("fr")).isTrue();
        assertThat(underTest.test("fr-BE")).isTrue();
        assertThat(underTest.test("ja-JP-JP-#u-ca-japanese")).isTrue();
    }

    @Test
    public void testSatisfyByWithInvalidCodes() {
        assertThat(underTest.test("")).isFalse();
        assertThat(underTest.test(null)).isFalse();
        assertThat(underTest.test("fr_BE")).isFalse();
        assertThat(underTest.test("fr BE")).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssertWit() {
        LocaleCodePredicate.assertCode("fr_BE");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssert() {
        LocaleCodePredicate.assertCode(null);
    }
}
