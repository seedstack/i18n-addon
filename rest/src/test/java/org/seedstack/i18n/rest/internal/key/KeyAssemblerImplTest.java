/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.key;

import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class KeyAssemblerImplTest {

    @Tested
    private KeyAssemblerImpl underTest;
    @Injectable
    private LocaleService localeService;

    @Test
    public void testAssembleKeyInfo() throws Exception {
        Key key = new Key("keyName");
        key.setComment("comment");

        KeyRepresentation keyRepresentation = underTest.assemble(key);

        Assertions.assertThat(keyRepresentation).isNotNull();
        Assertions.assertThat(keyRepresentation.getName()).isEqualTo("keyName");
        Assertions.assertThat(keyRepresentation.getComment()).isEqualTo("comment");
        Assertions.assertThat(keyRepresentation.isOutdated()).isFalse();
    }

    @Test
    public void testAssembleDefaultLocale() throws Exception {
        givenDefaultLocale("en");
        Key key = new Key("keyName");
        KeyRepresentation keyRepresentation = underTest.assemble(key);
        Assertions.assertThat(keyRepresentation.getDefaultLocale()).isEqualTo("en");
    }

    @Test
    public void testAssembleDefaultTranslation() throws Exception {
        givenDefaultLocale("en");
        Key key = new Key("keyName");
        key.addTranslation("en", "translation");

        KeyRepresentation keyRepresentation = underTest.assemble(key);
        Assertions.assertThat(keyRepresentation.getTranslation()).isEqualTo("translation");
        Assertions.assertThat(keyRepresentation.isApprox()).isFalse();
    }

    @Test
    public void testAssembleApproximateDefaultTranslation() throws Exception {
        givenDefaultLocale("en");
        Key key = new Key("keyName");
        key.addTranslation("en", "translation", true);

        KeyRepresentation keyRepresentation = underTest.assemble(key);
        Assertions.assertThat(keyRepresentation.isApprox()).isTrue();
    }

    @Test
    public void testAssembleMissingDefaultTranslation() throws Exception {
        givenDefaultLocale("en");
        Key key = new Key("keyName");

        KeyRepresentation keyRepresentation = underTest.assemble(key);
        Assertions.assertThat(keyRepresentation.isMissing()).isTrue();
        Assertions.assertThat(keyRepresentation.getTranslation()).isNull();
    }

    @Test
    public void testAssembleOutdatedKey() throws Exception {
        givenDefaultLocale("en");
        Key key = new Key("keyName");
        key.addTranslation("en", "translation");
        key.setOutdated();

        KeyRepresentation keyRepresentation = underTest.assemble(key);
        Assertions.assertThat(keyRepresentation.isOutdated()).isTrue();
    }

    @Test
    public void testAssembleEmptyList() throws Exception {
        List<KeyRepresentation> keyRepresentations = underTest.assemble(new ArrayList<>());
        Assertions.assertThat(keyRepresentations).isNotNull();
        Assertions.assertThat(keyRepresentations).hasSize(0);
    }

    @Test
    public void testAssembleKeys() throws Exception {
        List<Key> keys = Lists.newArrayList(new Key("key1"), new Key("key2"));
        List<KeyRepresentation> keyRepresentations = underTest.assemble(keys);
        Assertions.assertThat(keyRepresentations).hasSize(2);
    }

    private void givenDefaultLocale(final String defaultLocale) {
        new Expectations() {
            {
                localeService.getDefaultLocale();
                result = defaultLocale;
            }
        };
    }
}
