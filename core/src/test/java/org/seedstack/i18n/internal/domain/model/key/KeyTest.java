/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.key;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class KeyTest {

    public static final String KEY = "i18n.key.ninety";
    public static final String COMMENT = "Indicates the number ninety";
    public static final String FR = "fr";
    public static final String FR_BE = "fr-BE";
    public static final String FR_TRANSLATION = "quatre-vingt-dix";
    public static final String FR_BE_TRANSLATION = "nonante";
    public static final String INVALID_LOCALE = "fr_FR";
    public static final String EN = "en";

    @Test
    public void testMinimalKey() {
        Key key = new Key(KEY);
        Assertions.assertThat(key.getId()).isEqualTo(KEY);
        Assertions.assertThat(key.getComment()).isEqualTo(null);
        Assertions.assertThat(key.getTranslations()).isNotNull();
        Assertions.assertThat(key.getTranslations()).isEmpty();
    }

    @Test
    public void testKeyChangeComment() {
        Key key = new Key(KEY);
        key.setComment(COMMENT);
        Assertions.assertThat(key.getComment()).isEqualTo("Indicates the number ninety");
    }

    @Test
    public void testKeyAddTranslationWithInvalidLocale() {
        try {
            new Key(KEY).addTranslation(INVALID_LOCALE, FR_TRANSLATION);
            fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e).hasMessage("The locale code should should only contain letters, numbers and " +
                    "\"-\", but \"fr_FR\" was found.");
        }
    }

    @Test
    public void testTranslationCantBeNull() {
        try {
            new Key(KEY).addTranslation(FR, null);
            fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e).hasMessage("The translation can't be blank");
        }
    }

    @Test
    public void testLocaleCantBeNull() {
        try {
            new Key(KEY).addTranslation(null, FR_TRANSLATION);
            fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e).hasMessage("The locale code should should only contain letters, numbers and " +
                    "\"-\", but \"null\" was found.");
        }
    }

    @Test
    public void testKeyAddTranslation() {
        Key key = new Key(KEY);
        key.addTranslation(FR, FR_TRANSLATION, true);
        key.addTranslation(FR_BE, FR_BE_TRANSLATION).setOutdated();

        Assertions.assertThat(key.getTranslations()).hasSize(2);

        Assertions.assertThat(key.getTranslation(FR).getValue()).isEqualTo(FR_TRANSLATION);
        Assertions.assertThat(key.getTranslation(FR).isApproximate()).isTrue();
        Assertions.assertThat(key.getTranslation(FR).isOutdated()).isFalse();

        Assertions.assertThat(key.getTranslation(FR_BE).getValue()).isEqualTo(FR_BE_TRANSLATION);
        Assertions.assertThat(key.getTranslation(FR_BE).isApproximate()).isFalse();
        Assertions.assertThat(key.getTranslation(FR_BE).isOutdated()).isTrue();
    }

    @Test
    public void testAddExistingTranslation() throws Exception {
        Key key = new Key(KEY);
        Translation initialTranslation = key.addTranslation(FR, FR_TRANSLATION);
        Translation newTranslation = key.addTranslation(FR, FR_BE_TRANSLATION);
        Assertions.assertThat(initialTranslation).isEqualTo(newTranslation);
    }

    @Test
    public void testRemoveTranslation() throws Exception {
        Key key = new Key(KEY);
        key.addTranslation(FR, FR_TRANSLATION);
        Assertions.assertThat(key.getTranslations()).hasSize(1);
        key.removeTranslation(FR);
        Assertions.assertThat(key.getTranslations()).isEmpty();
    }

    @Test
    public void testOutdatedStatus() {
        Key key = new Key(KEY);

        key.addTranslation(FR, "outdated");
        key.addTranslation(EN, "outdated");
        key.setOutdated(); // Marks all the translation as outdated

        Assertions.assertThat(key.isOutdated()).isTrue();
        Assertions.assertThat(key.getTranslation(FR).isOutdated()).isTrue();
        Assertions.assertThat(key.getTranslation(EN).isOutdated()).isTrue();

        key.addTranslation(FR, FR_TRANSLATION); // reinit the French translation

        Assertions.assertThat(key.isOutdated()).isTrue(); // The key is still outdated
        Assertions.assertThat(key.getTranslation(FR).isOutdated()).isFalse();
        Assertions.assertThat(key.getTranslation(EN).isOutdated()).isTrue();

        key.addTranslation(EN, FR_TRANSLATION); // reinit the English translation

        Assertions.assertThat(key.isOutdated()).isFalse(); // all the keys are up to date
        Assertions.assertThat(key.getTranslation(FR).isOutdated()).isFalse();
        Assertions.assertThat(key.getTranslation(EN).isOutdated()).isFalse();

    }

    @Test
    public void testIsTranslated() {
        Key key = new Key(KEY);
        Assertions.assertThat(key.isTranslated(FR)).isFalse();

        key.addTranslation(FR, FR_TRANSLATION);
        Assertions.assertThat(key.isTranslated(FR)).isTrue();
    }
}
