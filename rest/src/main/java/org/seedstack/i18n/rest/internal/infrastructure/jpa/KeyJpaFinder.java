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
import org.seedstack.i18n.rest.internal.key.KeyAssembler;
import org.seedstack.i18n.rest.internal.key.KeyFinder;
import org.seedstack.i18n.rest.internal.key.KeyRepresentation;
import org.seedstack.i18n.rest.internal.key.KeySearchCriteria;
import org.seedstack.jpa.BaseJpaRangeFinder;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * KeyFinder implementation for JPA.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
class KeyJpaFinder extends BaseJpaRangeFinder<KeyRepresentation> implements KeyFinder {

    private final EntityManager entityManager;
    private final LocaleService localeService;
    private final KeyAssembler keyAssembler;
    private final KeyRepository keyRepository;

    @Inject
    public KeyJpaFinder(EntityManager entityManager, LocaleService localeService, KeyAssembler keyAssembler, KeyRepository keyRepository) {
        this.entityManager = entityManager;
        this.localeService = localeService;
        this.keyAssembler = keyAssembler;
        this.keyRepository = keyRepository;
    }

    @Override
    public PaginatedView<KeyRepresentation> findKeysWithTheirDefaultTranslation(Page page, KeySearchCriteria criteria) {
        Range range = Range.rangeFromPageInfo(page.getIndex(), page.getCapacity());
        Result<KeyRepresentation> keyRepresentationResult = find(range, criteria != null ? criteria.convertToMap() : null);
        return new PaginatedView<KeyRepresentation>(keyRepresentationResult, page);
    }

    @Override
    public List<KeyRepresentation> findKeysWithTheirDefaultTranslation(KeySearchCriteria criteria) {
        return computeResultList(null, criteria != null ? criteria.convertToMap() : null);
    }

    @Override
    protected List<KeyRepresentation> computeResultList(Range range, Map<String, Object> criteria) {
        KeysQuery queryBuilder = new KeysQuery(entityManager);

        KeySearchCriteria keySearchCriteria = KeySearchCriteria.fromMap(criteria);
        keySearchCriteria.setLocale(localeService.getDefaultLocale());

        queryBuilder.select().withCriteria(keySearchCriteria);

        List<Key> keys = queryBuilder.getResultList(range);
        return assembleRepresentationsFromEntities(keys);
    }

    private List<KeyRepresentation> assembleRepresentationsFromEntities(List<Key> keys) {
        List<KeyRepresentation> keyRepresentations = new ArrayList<KeyRepresentation>(keys.size());
        String defaultLocale = localeService.getDefaultLocale();
        for (Key key : keys) {
            keyRepresentations.add(keyAssembler.assembleDtoFromAggregate(key.subKey(defaultLocale)));
        }
        return keyRepresentations;
    }

    @Override
    protected long computeFullRequestSize(Map<String, Object> criteria) {
        KeysQuery queryBuilder = new KeysQuery(entityManager);

        KeySearchCriteria keySearchCriteria = KeySearchCriteria.fromMap(criteria);
        keySearchCriteria.setLocale(localeService.getDefaultLocale());

        queryBuilder.count().withCriteria(keySearchCriteria);
        return queryBuilder.getResult();
    }

    @Override
    public KeyRepresentation findKeyWithName(String name) {
        Key key = keyRepository.load(name);
        if (key != null) {
            return keyAssembler.assembleDtoFromAggregate(key.subKey(localeService.getDefaultLocale()));
        } else {
            return null;
        }
    }
}
