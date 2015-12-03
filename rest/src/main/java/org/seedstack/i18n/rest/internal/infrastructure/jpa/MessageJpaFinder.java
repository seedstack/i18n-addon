/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.jpa;

import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.messages.MessageFinder;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@JpaUnit("seed-i18n-domain")
@Transactional
class MessageJpaFinder implements MessageFinder {

    @Inject
    private KeyRepository keyRepository;

    @Override
    public Map<String, String> findByLocale(String locale) {
        List<Key> keys = keyRepository.loadAll();
        Map<String, String> message = new HashMap<String, String>(keys.size());
        for (Key key : keys) {
            key.subKey(locale);
            message.put(key.getEntityId(), key.getTranslations().values().iterator().next().getValue());
        }
        return message;
    }
}
