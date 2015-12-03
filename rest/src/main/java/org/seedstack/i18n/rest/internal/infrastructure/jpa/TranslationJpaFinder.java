/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.jpa;

import org.seedstack.business.finder.Range;
import org.seedstack.business.finder.Result;
import org.seedstack.business.view.Page;
import org.seedstack.business.view.PaginatedView;
import org.seedstack.i18n.LocaleService;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.key.KeySearchCriteria;
import org.seedstack.i18n.rest.internal.translation.TranslationAssembler;
import org.seedstack.i18n.rest.internal.translation.TranslationFinder;
import org.seedstack.i18n.rest.internal.translation.TranslationRepresentation;
import org.seedstack.jpa.BaseJpaRangeFinder;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.core.utils.SeedCheckUtils;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This class provides methods to find translations.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
class TranslationJpaFinder extends BaseJpaRangeFinder<TranslationRepresentation> implements TranslationFinder {

    private final KeyRepository keyRepository;
    private final LocaleService localeService;
    private final TranslationAssembler translationAssembler;
    private final EntityManager entityManager;

    @Inject
    public TranslationJpaFinder(KeyRepository keyRepository, LocaleService localeService, TranslationAssembler translationAssembler, EntityManager entityManager) {
        this.keyRepository = keyRepository;
        this.localeService = localeService;
        this.translationAssembler = translationAssembler;
        this.entityManager = entityManager;
    }

    @Override
    public List<TranslationRepresentation> findTranslations(String localeId) {
        List<Key> keys = keyRepository.loadAll();
        List<TranslationRepresentation> translationRepresentations = new ArrayList<TranslationRepresentation>(keys.size());
        String defaultLocale = localeService.getDefaultLocale();
        for (Key key : keys) {
            translationRepresentations.add(translationAssembler.assembleDtoFromAggregate(key.subKey(defaultLocale, localeId)));
        }

        return translationRepresentations;
    }

    @Override
    public TranslationRepresentation findTranslation(String localeId, String keyId) {
        Key key = keyRepository.load(keyId);
        String defaultLocale = localeService.getDefaultLocale();
        SeedCheckUtils.checkIfNotNull(defaultLocale);
        if (key != null) {
            return translationAssembler.assembleDtoFromAggregate(key.subKey(defaultLocale, localeId));
        }
        return null;
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
        return assembleRepresentations(keySearchCriteria, keys);
    }

    private List<TranslationRepresentation> assembleRepresentations(KeySearchCriteria keySearchCriteria, List<Key> keys) {
        List<TranslationRepresentation> translationRepresentations = new ArrayList<TranslationRepresentation>(keys.size());
        String defaultLocale = localeService.getDefaultLocale();
        for (Key key : keys) {
            Key subKey = key.subKey(defaultLocale, keySearchCriteria.getLocale());
            translationRepresentations.add(translationAssembler.assembleDtoFromAggregate(subKey));
        }
        return translationRepresentations;
    }

    @Override
    protected long computeFullRequestSize(Map<String, Object> criteria) {
        KeysQuery queryBuilder = new KeysQuery(entityManager);
        queryBuilder.count().withCriteria(KeySearchCriteria.fromMap(criteria));
        return queryBuilder.getResult();
    }
}
