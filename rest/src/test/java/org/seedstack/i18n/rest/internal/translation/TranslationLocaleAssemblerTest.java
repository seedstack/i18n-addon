/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.translation;

import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class TranslationLocaleAssemblerTest {

    @Tested
    private TranslationLocaleAssemblerImpl underTest;
    @Injectable
    private LocaleService localeService;

    @Test
    public void testAssembleKeyInfo() throws Exception {
        Key key = new Key("keyName");
        key.setComment("comment");

        TranslationRepresentation translation = underTest.assemble(key, "fr");

        assertThat(translation).isNotNull();
        assertThat(translation.getName()).isEqualTo("keyName");
        assertThat(translation.getComment()).isEqualTo("comment");
    }
    @Test
    public void testAssembleSupportMissingTranslations() throws Exception {
        givenEnglishDefaultLocale();
        Key key = new Key("keyName");

        TranslationRepresentation translation = underTest.assemble(key, "fr");

        assertThat(translation.getSource()).isNull();
        assertThat(translation.getTarget()).isNull();
    }

    private void givenEnglishDefaultLocale() {
        new Expectations() {
            {
                localeService.getDefaultLocale();
                result = "en";
            }
        };
    }

    @Test
    public void testAssembleSupportMissingDefaultLocale() throws Exception {
        Key key = new Key("keyName");
        TranslationRepresentation translation = underTest.assemble(key, "fr");
        assertThat(translation.getSource()).isNull();
    }

    @Test
    public void testAssembleMissingTranslation() throws Exception {
        Key key = new Key("keyName");
        TranslationRepresentation translation = underTest.assemble(key, "fr");
        assertThat(translation.isMissing()).isTrue();
    }

    @Test
    public void testAssembleSourceTranslation() throws Exception {
        givenEnglishDefaultLocale();
        Key key = new Key("keyName");
        key.addTranslation("en", "translation", true);
        key.setOutdated();

        TranslationRepresentation translation = underTest.assemble(key, "fr");

        assertTranslation(translation.getSource());
    }

    @Test
    public void testAssembleTargetTranslation() throws Exception {
        Key key = new Key("keyName");
        key.addTranslation("en", "translation", true);
        key.setOutdated();

        TranslationRepresentation translation = underTest.assemble(key, "en");

        assertTranslation(translation.getTarget());
    }

    private void assertTranslation(TranslationValueRepresentation source) {
        assertThat(source).isNotNull();
        assertThat(source.getLocale()).isEqualTo("en");
        assertThat(source.getTranslation()).isEqualTo("translation");
        assertThat(source.isApprox()).isTrue();
        assertThat(source.isOutdated()).isTrue();
    }

    @Test
    public void testAssembleEmptyList() throws Exception {
        List<Key> keys = Lists.newArrayList();
        List<TranslationRepresentation> representations = underTest.assemble(keys, "fr");
        Assertions.assertThat(representations).isNotNull();
        Assertions.assertThat(representations).isEmpty();
    }

    @Test
    public void testAssembleList() throws Exception {
        List<Key> keys = Lists.newArrayList(new Key("key1"), new Key("key2"));
        List<TranslationRepresentation> representations = underTest.assemble(keys, "fr");
        Assertions.assertThat(representations).hasSize(2);
    }
}
