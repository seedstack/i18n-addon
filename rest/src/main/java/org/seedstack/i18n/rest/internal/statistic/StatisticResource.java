/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.statistic;

import org.seedstack.i18n.rest.internal.locale.LocaleFinder;
import org.seedstack.i18n.rest.internal.locale.LocaleRepresentation;
import org.apache.commons.lang.StringUtils;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * This REST resource provide access to statistic function.
 * 
 * @author PDC
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@Path("/seed-i18n/statistic")
public class StatisticResource {

	@Inject
	private LocaleFinder localeFinder;

	@Inject
	private StatisticFinder statisticFinder;

	/**
	 * Returns a list contains the result statistic for the selected language
	 * 
	 * @param locale
	 *            locale selected filter on key name
	 * @return a list about statistic
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatistics(@QueryParam("selectLang") String locale) {
		List<StatisticRepresentation> listResult = new ArrayList<>();
		// select "All"
		if (StringUtils.isBlank(locale)) {
			List<LocaleRepresentation> availableLocales = localeFinder
					.findAvailableLocales();
			for (LocaleRepresentation localeRepresentation : availableLocales) {
				statisticFinder.findStatisticRepresentation(
						localeRepresentation, listResult);
			}
			
        // select a local
		} else {
			LocaleRepresentation localeRepresentation = localeFinder
					.findAvailableLocale(locale);
			statisticFinder.findStatisticRepresentation(localeRepresentation,
					listResult);
		}

		if (!listResult.isEmpty()) {
			return Response.ok(listResult).build();
		}
		return Response.noContent().build();
	}

}
