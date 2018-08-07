/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.key;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class TranslationTest {

    public static final String KEY = "i18n.key.ninety";
    public static final String FR = "fr";
    public static final String FR_TRANSLATION = "quatre-vingt-dix";

    private Translation underTest;
    private Key key;

    @Before
    public void before() {
        key = new Key(KEY);
        underTest = new Translation(new TranslationId(KEY, FR), FR_TRANSLATION);
    }

    @Test
    public void testMinimalTranslation() {
        Assertions.assertThat(underTest.getId().getKey()).isEqualTo(KEY);
        Assertions.assertThat(underTest.getId().getLocale()).isEqualTo(FR);
        Assertions.assertThat(underTest.getValue()).isEqualTo(FR_TRANSLATION);
        Assertions.assertThat(underTest.isApproximate()).isFalse();
        Assertions.assertThat(underTest.isOutdated()).isFalse();
    }

    @Test
    public void testChangeStatus() {
        underTest.setApproximate(true);
        underTest.setOutdated();

        Assertions.assertThat(underTest.isApproximate()).isTrue();
        Assertions.assertThat(underTest.isOutdated()).isTrue();
    }

    @Test
    public void testUpdateTranslationResetOutdated() {
        Assertions.assertThat(underTest.isOutdated()).isFalse();

        underTest.setOutdated();
        Assertions.assertThat(underTest.isOutdated()).isTrue();

        underTest.updateValue("new translation");
        Assertions.assertThat(underTest.getValue()).isEqualTo("new translation");
        Assertions.assertThat(underTest.isOutdated()).isFalse();
    }

    @Test
    public void testUpdateValueWithNull() {
        underTest.updateValue(null);
        Assertions.assertThat(underTest.getValue()).isNull();
        underTest.updateValue("");
        Assertions.assertThat(underTest.getValue()).isEqualTo("");
    }
}
