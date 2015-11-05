/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.infrastructure.finders.jpa;

import org.seedstack.i18n.api.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.key.KeyFinder;
import org.seedstack.i18n.rest.key.KeyRepresentation;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.Assertions;
import org.javatuples.Triplet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.jpa.JpaUnit;
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

	private static final String COMMENT = "Add...";
	private static final String FR = "fr";
	private static final String EN = "en";
	private static final String TRANSLATION_EN = "Add...";
	private static final String TRANSLATION_FR = "translationFr";

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
		localeService.addLocale(FR);
		localeService.addLocale(EN);
		localeService.changeDefaultLocaleTo(EN);

		// the first call will initialize the base for the test execution
		List<Key> list = keyRepository.loadAll();
		if (!list.isEmpty()) {
			keyRepository.deleteAll();
		}
		if (keyRepository.loadAll().isEmpty()) {
			List<Key> keys = new ArrayList<Key>();
			keys.add(buildKey("key_default", false, false, false));
			Key key_key_outdated = buildKey("key_key_outdated", false, false,
					false);
			key_key_outdated.setOutdated(true);
			keys.add(key_key_outdated);
			keys.add(buildKey("key_tln_approx", true, false, false));
			keys.add(buildKey("key_tln_outdated", false, true, false));
			keys.add(buildKey("key_tln_missing", false, false, true));

			Map<String, Triplet<String, Boolean, Boolean>> translations = new HashMap<String, Triplet<String, Boolean, Boolean>>();
			translations.put(FR, Triplet.with(TRANSLATION_FR, false, false));
			translations.put(EN, Triplet.with("", false, false));
			keys.add(keyFactory.createKey("missing_blank", COMMENT,
					translations));

			keyRepository.persist(keys);
		}
	}

	/**
	 * Call finder without any criteria. Should get all the keys.
	 */
	@Test
	public void get_all() {
		Map<String, Object> criteria = buildCriteria(null, null, null, null);
		Result<KeyRepresentation> keyRepresentations = keyFinder.findAllKeys(
				Range.rangeFromPageInfo(0, 5), criteria);
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
		Result<KeyRepresentation> keyRepresentations = keyFinder.findAllKeys(
				Range.rangeFromPageInfo(0, 5), criteria);
		Assertions.assertThat(keyRepresentations.getSize()).isEqualTo(2);
		KeyRepresentation representation = keyRepresentations.getResult()
				.iterator().next();
		Assertions.assertThat(
				StringUtils.isBlank(representation.getTranslation())).isTrue();
	}

	/**
	 * Request all the keys marked as approximate.
	 */
	@Test
	public void get_approx_default_translation() {
		Map<String, Object> criteria = buildCriteria(null, true, null, null);
		Result<KeyRepresentation> keyRepresentations = keyFinder.findAllKeys(
				Range.rangeFromPageInfo(0, 5), criteria);
		Assertions.assertThat(keyRepresentations.getSize()).isEqualTo(1);
		KeyRepresentation representation = keyRepresentations.getResult()
				.iterator().next();
		Assertions.assertThat(representation.getDefaultLocale()).isEqualTo(EN);
		Assertions.assertThat(representation.getTranslation()).isEqualTo(
				TRANSLATION_EN);
	}

	/**
	 * Request the keys with the name: "key_default".
	 */
	@Test
	public void get_search_key() {
		Map<String, Object> criteria = buildCriteria(null, null, null,
				"key_default");
		Result<KeyRepresentation> keyRepresentations = keyFinder.findAllKeys(
				Range.rangeFromPageInfo(0, 5), criteria);
		Assertions.assertThat(keyRepresentations.getSize()).isEqualTo(1);
		KeyRepresentation representation = keyRepresentations.getResult()
				.iterator().next();
		Assertions.assertThat(representation.getDefaultLocale()).isEqualTo(EN);
		Assertions.assertThat(representation.getTranslation()).isEqualTo(
				TRANSLATION_EN);
	}

	/**
	 * Request outdated keys.
	 */
	@Test
	public void get_outdated_key() {
		Map<String, Object> criteria = buildCriteria(true, null, null, null);
		Result<KeyRepresentation> keyRepresentations = keyFinder.findAllKeys(
				Range.rangeFromPageInfo(0, 5), criteria);
		Assertions.assertThat(keyRepresentations.getSize()).isEqualTo(2);
		KeyRepresentation representation = keyRepresentations.getResult()
				.iterator().next();
		Assertions.assertThat(representation.getDefaultLocale()).isEqualTo(EN);
		Assertions.assertThat(representation.getTranslation()).isEqualTo(
				TRANSLATION_EN);
	}

	private Key buildKey(String name, boolean approx, boolean outdated,
			boolean missing) {
		Map<String, Triplet<String, Boolean, Boolean>> translations = new HashMap<String, Triplet<String, Boolean, Boolean>>();
		translations.put(FR, Triplet.with(TRANSLATION_FR, false, false));
		if (!missing) {
			translations
					.put(EN, Triplet.with(TRANSLATION_EN, approx, outdated));
		}
		return keyFactory.createKey(name, COMMENT, translations);
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
