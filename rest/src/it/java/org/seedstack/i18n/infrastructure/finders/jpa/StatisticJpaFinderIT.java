/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Creation : 1 août 2014
 */
package org.seedstack.i18n.infrastructure.finders.jpa;

import org.seedstack.i18n.rest.locale.LocaleRepresentation;
import org.seedstack.i18n.rest.statistic.StatisticFinder;
import org.seedstack.i18n.rest.statistic.StatisticRepresentation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.it.SeedITRunner;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class StatisticJpaFinderIT {

	@Inject
	StatisticFinder statisticFinder;

	@Before
	public void setUp() {

	}

	@Test
	public void testFindStatisticRepresentation() {
		LocaleRepresentation localeRepresentation = new LocaleRepresentation();
		localeRepresentation.setCode("fr");
		List<StatisticRepresentation> listResult = new ArrayList<StatisticRepresentation>();
		statisticFinder.findStatisticRepresentation(localeRepresentation,
				listResult);
		assertThat(listResult.size()).isEqualTo(1);

	}

}
