/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.statistic;

import org.seedstack.i18n.rest.internal.locale.LocaleRepresentation;
import org.seedstack.business.finder.Finder;

import java.util.List;

/**
 * @author PDC Date: 29/07/14
 */
@Finder
public interface StatisticFinder {

	/**
	 * build the list statistic results by locale selected.
	 * 
	 * @param localeRepresentation
	 *            the specified locale
	 * @param listResult
	 *            list of statistic results
	 */
	void findStatisticRepresentation(LocaleRepresentation localeRepresentation,
			List<StatisticRepresentation> listResult); // TODO WTF

}
