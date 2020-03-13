/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.jpa;

import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.seedstack.i18n.internal.domain.model.locale.Locale;
import org.seedstack.i18n.internal.domain.model.locale.LocaleRepository;
import org.seedstack.i18n.rest.internal.locale.LocaleRepresentation;
import org.seedstack.i18n.rest.internal.statistic.StatisticFinder;
import org.seedstack.i18n.rest.internal.statistic.StatisticRepresentation;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

/**
 * @author PDC Date: 29/07/14
 */
@JpaUnit("seed-i18n-domain")
@Transactional
class StatisticJpaFinder implements StatisticFinder {

    @Inject
    private LocaleRepository localeRepository;

    @Inject
    private KeyRepository keyRepository;

    @Override
    public void findStatisticRepresentation(
            LocaleRepresentation localeRepresentation,
            List<StatisticRepresentation> listResult) {
        StatisticRepresentation statisticRepresentation = new StatisticRepresentation();
        Locale selectLocale = localeRepository.get(localeRepresentation
                .getCode())
                .orElseThrow(() -> new IllegalArgumentException("Locale not found " + localeRepresentation.getCode()));
        List<Key> keys = keyRepository.loadAll();
        if (keys != null) {
            int translatedCount = 0;
            for (Key key : keys) {
                Translation translation = key.getTranslation(selectLocale
                        .getId());
                if (translation != null
                        && StringUtils.isNotBlank(translation.getValue())) {
                    translatedCount++;
                }
            }
            statisticRepresentation.setKeytotal(keys.size());
            statisticRepresentation.setLocale(localeRepresentation.getCode());
            statisticRepresentation.setEnglishLanguage(localeRepresentation
                    .getEnglishLanguage());
            statisticRepresentation.setTranslated(translatedCount);
            statisticRepresentation.setTotranslate(keys.size()
                    - translatedCount);
            listResult.add(statisticRepresentation);
        }

    }
}
