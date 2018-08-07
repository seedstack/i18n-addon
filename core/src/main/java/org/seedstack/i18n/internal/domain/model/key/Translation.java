/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.key;


import org.seedstack.business.domain.BaseEntity;

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

    @Column(name = "TRANSLATION")
    private String value;

    @Column(name = "OUTDATED")
    private boolean outdated;

    @Column(name = "APPROXIMATE")
    private boolean approximate;

    protected Translation() {
    }

    protected Translation(TranslationId translationId, String value) {
        this.entityId = translationId;
        this.value = value;
    }

    @Override
    public TranslationId getId() {
        return entityId;
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
     * Changes the translation value. If the translation was marked
     * as outdated, its status is reinitialized.
     *
     * @param value translation
     */
    void updateValue(String value) {
        this.value = value;
        this.outdated = false;
        this.approximate = false;
    }

    /**
     * Indicates that the reference translation has changed and this
     * translation hasn't been updated.
     *
     * @return the outdated
     */
    public boolean isOutdated() {
        return outdated;
    }

    /**
     * Marks the translation as outdated.
     */
    public void setOutdated() {
        this.outdated = true;
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
    void setApproximate(boolean approximate) {
        this.approximate = approximate;
    }

}
