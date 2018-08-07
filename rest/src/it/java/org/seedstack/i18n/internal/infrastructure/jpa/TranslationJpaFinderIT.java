/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.infrastructure.jpa;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.i18n.rest.internal.key.KeySearchCriteria;
import org.seedstack.i18n.rest.internal.translation.TranslationFinder;
import org.seedstack.i18n.rest.internal.translation.TranslationRepresentation;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.testing.junit4.SeedITRunner;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
@JpaUnit("seed-i18n-domain")
@Transactional
@RunWith(SeedITRunner.class)
public class TranslationJpaFinderIT {

    @Inject
    private TranslationFinder translationFinder;

    @Test
    public void testMissingTranslations() {
        KeySearchCriteria criteria = new KeySearchCriteria(true, null, null, null);
        criteria.setLocale("fr");
        PaginatedView<TranslationRepresentation> view = translationFinder.findAllTranslations(new Page(0, 5), criteria);
        expectOneKeyWithName(view, "test.foo");
    }

    private void expectOneKeyWithName(PaginatedView<TranslationRepresentation> view, String expected2) {
        Assertions.assertThat(view.getView()).hasSize(1);
        Assertions.assertThat(view.getView().get(0).getName()).isEqualTo(expected2);
    }

    @Test
    public void testOutdatedTranslations() {
        KeySearchCriteria criteria = new KeySearchCriteria(null, null, true, null);
        criteria.setLocale("fr");
        PaginatedView<TranslationRepresentation> view = translationFinder.findAllTranslations(new Page(0, 5), criteria);
        expectOneKeyWithName(view, "test.outdated");
    }

    @Test
    public void testApproxTranslations() {
        KeySearchCriteria criteria = new KeySearchCriteria(null, true, null, null);
        criteria.setLocale("fr");
        PaginatedView<TranslationRepresentation> view = translationFinder.findAllTranslations(new Page(0, 5), criteria);
        expectOneKeyWithName(view, "test.approximate");
    }
}
