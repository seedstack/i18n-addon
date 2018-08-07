/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Creation : 1 août 2014
 */
package org.seedstack.i18n.internal.infrastructure.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.rest.internal.locale.LocaleRepresentation;
import org.seedstack.i18n.rest.internal.statistic.StatisticFinder;
import org.seedstack.i18n.rest.internal.statistic.StatisticRepresentation;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.seedstack.seed.testing.junit4.SeedITRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SeedITRunner.class)
public class StatisticJpaFinderIT {

	@Inject
	private StatisticFinder statisticFinder;

	@Test
	public void testFindStatisticRepresentation() {
		LocaleRepresentation localeRepresentation = new LocaleRepresentation();
		localeRepresentation.setCode("fr");
		List<StatisticRepresentation> listResult = new ArrayList<>();
		statisticFinder.findStatisticRepresentation(localeRepresentation, listResult);
		assertThat(listResult).hasSize(1);
	}
}
