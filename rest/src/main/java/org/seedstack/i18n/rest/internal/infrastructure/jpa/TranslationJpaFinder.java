/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.jpa;

import org.seedstack.business.finder.BaseRangeFinder;
import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.key.KeySearchCriteria;
import org.seedstack.i18n.rest.internal.translation.TranslationFinder;
import org.seedstack.i18n.rest.internal.translation.TranslationLocaleAssembler;
import org.seedstack.i18n.rest.internal.translation.TranslationRepresentation;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;


/**
 * This class provides methods to find translations.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
class TranslationJpaFinder extends BaseRangeFinder<TranslationRepresentation, Map<String, Object>> implements TranslationFinder {

    private final KeyRepository keyRepository;
    private final TranslationLocaleAssembler translationLocaleAssembler;
    private final EntityManager entityManager;

    @Inject
    public TranslationJpaFinder(KeyRepository keyRepository, TranslationLocaleAssembler translationLocaleAssembler, EntityManager entityManager) {
        this.keyRepository = keyRepository;
        this.translationLocaleAssembler = translationLocaleAssembler;
        this.entityManager = entityManager;
    }

    @Override
    public List<TranslationRepresentation> findTranslations(String localeId) {
        return translationLocaleAssembler.assemble(keyRepository.loadAll(), localeId);
    }

    @Override
    public TranslationRepresentation findTranslation(String localeId, String keyId) {
        Key key = keyRepository.load(keyId);
        return key != null ? translationLocaleAssembler.assemble(key, localeId) : null;
    }

    @Override
    public PaginatedView<TranslationRepresentation> findAllTranslations(Page page, KeySearchCriteria criteria) {
        Range range = Range.rangeFromPageInfo(page.getIndex(), page.getCapacity());
        Result<TranslationRepresentation> translationRepresentationResult = find(range, criteria.convertToMap());
        return new PaginatedView<TranslationRepresentation>(translationRepresentationResult, page);
    }

    @Override
    protected List<TranslationRepresentation> computeResultList(Range range, Map<String, Object> criteria) {
        KeysQuery queryBuilder = new KeysQuery(entityManager);
        KeySearchCriteria keySearchCriteria = KeySearchCriteria.fromMap(criteria);
        queryBuilder.select().withCriteria(keySearchCriteria);
        List<Key> keys = queryBuilder.getResultList(range);
        return translationLocaleAssembler.assemble(keys, keySearchCriteria.getLocale());
    }

    @Override
    protected long computeFullRequestSize(Map<String, Object> criteria) {
        KeysQuery queryBuilder = new KeysQuery(entityManager);
        queryBuilder.count().withCriteria(KeySearchCriteria.fromMap(criteria));
        return queryBuilder.getResult();
    }
}
