/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.key.KeyFinder;
import org.seedstack.i18n.rest.internal.key.KeyRepresentation;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This integration test checks key finder methods.
 *
 * @author pierre.thirouin@ext.mpsa.com Date: 15/05/2014
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class KeyJpaFinderIT {

    private static final Range FIRST_RANGE = Range.rangeFromPageInfo(0, 5);
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
        // Define the application locales
        localeService.addLocale(FR);
        localeService.addLocale(EN);
        localeService.changeDefaultLocaleTo(EN);

        // Clean the database
        keyRepository.deleteAll();

        List<Key> keys = new ArrayList<Key>();
        keys.add(createKey(KEY_DEFAULT, false, false, false));

        Key outdatedKey = createKey(KEY_OUTDATED, false, false, false);
        outdatedKey.setOutdated();
        keys.add(outdatedKey);

        keys.add(createKey(KEY_TLN_APPROX, true, false, false));
        keys.add(createKey(KEY_TLN_OUTDATED, false, true, false));
        keys.add(createKey(KEY_TLN_MISSING, false, false, true));

        Key key = keyFactory.createKey(KEY_WITH_MISSING_TRANSLATION);
        key.addTranslation(FR, TRANSLATION_FR);
        key.addTranslation(FR, "");
        keys.add(key);

        keyRepository.persist(keys);

    }

    /**
     * Call finder without any criteria. Should get all the keys.
     */
    @Test
    public void get_all() {
        Map<String, Object> criteria = buildCriteria(null, null, null, null);
        Result<KeyRepresentation> keyRepresentations = keyFinder.findAllKeys(FIRST_RANGE, criteria);
        Assertions.assertThat(keyRepresentations.getSize()).isEqualTo(5);
        KeyRepresentation representation = keyRepresentations.getResult()
                .iterator().next();
        Assertions.assertThat(representation.getComment()).isEqualTo(COMMENT);
        Assertions.assertThat(representation.getDefaultLocale()).isEqualTo(EN);
        Assertions.assertThat(representation.getTranslation()).isEqualTo(
                TRANSLATION_EN);
    }

    /**
     * Request all the keys marked as missing.
     */
    @Test
    public void get_missing_default_translation() {
        Map<String, Object> criteria = buildCriteria(null, null, true, null);
        Result<KeyRepresentation> keyRepresentations = keyFinder.findAllKeys(FIRST_RANGE, criteria);
        Assertions.assertThat(keyRepresentations.getSize()).isEqualTo(2);
        KeyRepresentation representation = keyRepresentations.getResult().get(0);
        Assertions.assertThat(StringUtils.isBlank(representation.getTranslation())).isTrue();
    }

    /**
     * Request all the keys marked as approximate.
     */
    @Test
    public void get_approx_default_translation() {
        Map<String, Object> criteria = buildCriteria(null, true, null, null);
        Result<KeyRepresentation> keyRepresentations = keyFinder.findAllKeys(FIRST_RANGE, criteria);

        Assertions.assertThat(keyRepresentations.getSize()).isEqualTo(1);

        KeyRepresentation representation = keyRepresentations.getResult().get(0);
        Assertions.assertThat(representation.getDefaultLocale()).isEqualTo(EN);
        Assertions.assertThat(representation.getTranslation()).isEqualTo(TRANSLATION_EN);
    }

    /**
     * Request the keys with the name: "key_default".
     */
    @Test
    public void get_search_key() {
        Map<String, Object> criteria = buildCriteria(null, null, null, KEY_DEFAULT);
        Result<KeyRepresentation> keyRepresentations = keyFinder.findAllKeys(FIRST_RANGE, criteria);

        Assertions.assertThat(keyRepresentations.getSize()).isEqualTo(1);

        KeyRepresentation keyRepresentation = keyRepresentations.getResult().get(0);
        Assertions.assertThat(keyRepresentation.getDefaultLocale()).isEqualTo(EN);
        Assertions.assertThat(keyRepresentation.getTranslation()).isEqualTo(TRANSLATION_EN);
    }

    /**
     * Request outdated keys.
     */
    @Test
    public void get_outdated_key() {
        Map<String, Object> criteria = buildCriteria(true, null, null, null);
        keyRepository.loadAll();
        Result<KeyRepresentation> keyRepresentations = keyFinder.findAllKeys(FIRST_RANGE, criteria);

        Assertions.assertThat(keyRepresentations.getSize()).isEqualTo(2);

        KeyRepresentation keyRepresentation = keyRepresentations.getResult().get(0);
        Assertions.assertThat(keyRepresentation.getDefaultLocale()).isEqualTo(EN);
        Assertions.assertThat(keyRepresentation.getTranslation()).isEqualTo(TRANSLATION_EN);
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

    private Map<String, Object> buildCriteria(Boolean isOutdated,
                                              Boolean isApprox, Boolean isMissing, String searchName) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("isOutdated", isOutdated);
        criteria.put("isApprox", isApprox);
        criteria.put("isMissing", isMissing);
        criteria.put("searchName", searchName);
        return criteria;
    }
}
