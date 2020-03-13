/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.key.KeyFinder;
import org.seedstack.i18n.rest.internal.key.KeyRepresentation;
import org.seedstack.i18n.rest.internal.key.KeySearchCriteria;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

/**
 * This integration test checks key finder methods.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class KeyJpaFinderIT {

    private static final Page FIRST_RANGE = new Page(0, 5);
    private static final String COMMENT = "Add...";
    private static final String FR = "fr";
    private static final String EN = "en";
    private static final String TRANSLATION_EN = "Add...";
    private static final String TRANSLATION_FR = "translationFr";

    public static final String KEY_OUTDATED = "key_key_outdated";
    public static final String KEY_DEFAULT = "key_default";
    public static final String KEY_TLN_APPROX = "key_tln_approx";
    public static final String KEY_TLN_OUTDATED = "key_tln_outdated";
    public static final String KEY_TLN_MISSING = "key_tln_missing";
    public static final String KEY_WITH_MISSING_TRANSLATION = "missing_blank";

    @Inject
    private KeyFinder keyFinder;
    @Inject
    private KeyRepository keyRepository;
    @Inject
    private KeyFactory keyFactory;
    @Inject
    private LocaleService localeService;

    /**
     * Initializes the test.
     */
    @Before
    public void setUp() {
        defineApplicationLocales();

        clearKeys();

        List<Key> keys = new ArrayList<>();
        keys.add(createKey(KEY_DEFAULT, false, false, false));

        Key outdatedKey = createKey(KEY_OUTDATED, false, false, false);
        outdatedKey.setOutdated();
        keys.add(outdatedKey);

        keys.add(createKey(KEY_TLN_APPROX, true, false, false));
        keys.add(createKey(KEY_TLN_OUTDATED, false, true, false));
        keys.add(createKey(KEY_TLN_MISSING, false, false, true));

        Key key = keyFactory.createKey(KEY_WITH_MISSING_TRANSLATION);
        key.addTranslation(FR, TRANSLATION_FR);
        keys.add(key);

        for (Key keyToPersist : keys) {
            keyRepository.add(keyToPersist);
        }
    }

    void clearKeys() {
        keyRepository.delete(keyRepository.loadAll());
    }

    private void defineApplicationLocales() {
        localeService.addLocale(FR);
        localeService.addLocale(EN);
        localeService.changeDefaultLocaleTo(EN);
    }

    @Test
    public void findKeysWithoutCriteria() {
        KeySearchCriteria criteria = new KeySearchCriteria(null, null, null, null);
        PaginatedView<KeyRepresentation> keyRepresentations = keyFinder
                .findKeysWithTheirDefaultTranslation(FIRST_RANGE, criteria);

        assertThat(keyRepresentations.getPageSize()).isEqualTo(5);

        KeyRepresentation representation = keyRepresentations.getView().get(0);
        assertThat(representation.getComment()).isEqualTo(COMMENT);
        assertThat(representation.getDefaultLocale()).isEqualTo(EN);
        assertThat(representation.getTranslation()).isEqualTo(TRANSLATION_EN);
    }

    /**
     * Request all the keys marked as missing.
     */
    @Test
    public void get_missing_default_translation() {
        KeySearchCriteria criteria = new KeySearchCriteria(true, null, null, null);
        PaginatedView<KeyRepresentation> keyRepresentations = keyFinder
                .findKeysWithTheirDefaultTranslation(FIRST_RANGE, criteria);

        assertThat(keyRepresentations.getPageSize()).isEqualTo(2);

        KeyRepresentation representation = keyRepresentations.getView().get(0);
        assertThat(StringUtils.isBlank(representation.getTranslation())).isTrue();
    }

    /**
     * Request all the keys marked as approximate.
     */
    @Test
    public void get_approx_default_translation() {
        KeySearchCriteria criteria = new KeySearchCriteria(null, true, null, null);
        PaginatedView<KeyRepresentation> keyRepresentations = keyFinder
                .findKeysWithTheirDefaultTranslation(FIRST_RANGE, criteria);

        assertThat(keyRepresentations.getPageSize()).isEqualTo(1);

        KeyRepresentation representation = keyRepresentations.getView().get(0);
        assertThat(representation.getDefaultLocale()).isEqualTo(EN);
        assertThat(representation.getTranslation()).isEqualTo(TRANSLATION_EN);
    }

    @Test
    public void testFindKeysByName() {
        KeySearchCriteria criteria = new KeySearchCriteria(null, null, null, KEY_DEFAULT);
        PaginatedView<KeyRepresentation> keyRepresentations = keyFinder
                .findKeysWithTheirDefaultTranslation(FIRST_RANGE, criteria);

        assertThat(keyRepresentations.getPageSize()).isEqualTo(1);

        KeyRepresentation keyRepresentation = keyRepresentations.getView().get(0);
        assertThat(keyRepresentation.getDefaultLocale()).isEqualTo(EN);
        assertThat(keyRepresentation.getTranslation()).isEqualTo(TRANSLATION_EN);
    }

    @Test
    public void testFindOutdatedKeys() {
        KeySearchCriteria criteria = new KeySearchCriteria(null, null, true, null);
        keyRepository.loadAll();
        PaginatedView<KeyRepresentation> keyRepresentations = keyFinder
                .findKeysWithTheirDefaultTranslation(FIRST_RANGE, criteria);

        assertThat(keyRepresentations.getPageSize()).isEqualTo(2);

        KeyRepresentation keyRepresentation = keyRepresentations.getView().get(0);
        assertThat(keyRepresentation.getDefaultLocale()).isEqualTo(EN);
        assertThat(keyRepresentation.getTranslation()).isEqualTo(TRANSLATION_EN);
    }

    private Key createKey(String name, boolean approx, boolean outdated, boolean missing) {
        Key key = keyFactory.createKey(name);
        key.setComment(COMMENT);
        if (!missing) {
            key.addTranslation(EN, TRANSLATION_EN, approx);
        }
        if (outdated) {
            key.setOutdated();
        }
        key.addTranslation(FR, TRANSLATION_FR);
        return key;
    }
}
