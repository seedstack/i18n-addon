/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.jpa;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.i18n.internal.domain.model.locale.Locale;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@RunWith(JMockit.class)
public class LocaleJpaRepositoryTest {

    @Tested
    private LocaleJpaRepository localeRepository;
    @Injectable
    private EntityManager entityManager;
    @Mocked
    private Locale locale;
    @Mocked
    private Locale defaultLocale;

    @Test(expected = IllegalArgumentException.class)
    public void testChangeDefaultLocaleDoNotAcceptNull() {
        localeRepository.changeDefaultLocaleTo(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeDefaultLocaleWithLocaleNotPresent() {
        localeRepository.changeDefaultLocaleTo("es-AR");
    }

    @Test
    public void testSelectDefaultLocale() {
        new Expectations() {
            {
                entityManager.find(Locale.class, "es-AR");
                result = locale;

                locale.setDefaultLocale(true);
                entityManager.merge(locale);
                times = 1;
            }
        };
        localeRepository.changeDefaultLocaleTo("es-AR");
    }

    @Test
    public void testUpdateDefaultLocale(@Mocked final TypedQuery typedQuery) {
        new Expectations() {
            {
                entityManager.find(Locale.class, "es-AR");
                result = locale;

                typedQuery.getSingleResult();
                result = defaultLocale;

                defaultLocale.setDefaultLocale(false);
                entityManager.merge(defaultLocale);
                times = 1;

                locale.setDefaultLocale(true);
                entityManager.merge(locale);
                times = 1;
            }
        };
        localeRepository.changeDefaultLocaleTo("es-AR");
    }
}
