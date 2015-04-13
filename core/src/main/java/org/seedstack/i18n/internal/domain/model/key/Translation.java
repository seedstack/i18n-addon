/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.key;


import org.seedstack.business.api.domain.base.BaseEntity;

import javax.persistence.*;

/**
 * Translation entity of Key aggregate. This translation is associated to a key and a locale.
 * It contains a value and metadata such as outdated and approximate.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@Entity
@Table(name="SEED_I18N_TRANSLATION")
public class Translation extends BaseEntity<TranslationId> {
    @EmbeddedId
    private TranslationId entityId;

    @ManyToOne
    @MapsId("key")
    private Key key;

    private String value;

    private boolean outdated;

    private boolean approximate;

    protected Translation() {
    }

    protected Translation(TranslationId translationId, Key key, String value, boolean outdated, boolean approximate) {
        this.entityId = translationId;
        this.key = key;
        this.value = value;
        this.outdated = outdated;
        this.approximate = approximate;
    }

    @Override
    public TranslationId getEntityId() {
        return entityId;
    }

    /**
     * Sets the entity id.
     *
     * @param entityId translation id
     */
    public void setEntityId(TranslationId entityId) {
        this.entityId = entityId;
    }

    /**
     * Returns the translation key.
     *
     * @return translation key
     */
    public Key getKey() {
        return key;
    }

    /**
     * Sets the translation key.
     *
     * @param key translation key
     */
    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * Returns the translation value.
     *
     * @return translation
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the translation value
     *
     * @param value translation
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Indicates the reference translation has changed and this translation hasn't been updated.
     *
     * @return the outdated
     */
    public boolean isOutdated() {
        return outdated;
    }

    /**
     * @param outdated the outdated to set
     */
    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    /**
     * Indicates the translator is not confident about this translation.
     *
     * @return the approximate
     */
    public boolean isApproximate() {
        return approximate;
    }

    /**
     * @param approximate the approximate to set
     */
    public void setApproximate(boolean approximate) {
        this.approximate = approximate;
    }

}
