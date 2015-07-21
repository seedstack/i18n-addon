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


import org.seedstack.business.api.domain.BaseValueObject;

import javax.persistence.Embeddable;

/**
 * {@link Translation} embedded id, composed of {@link Key} and locale identifier.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 13/05/2014
 */
@Embeddable
public class TranslationId extends BaseValueObject {

    private static final long serialVersionUID = 2487221653197332337L;

    private String key;

    private String locale;

    /**
     * Default constructor.
     */
    protected TranslationId() {
    }

    /**
     * Constructor.
     *
     * @param key    key entity
     * @param locale translation locale
     */
    protected TranslationId(String key, String locale) {
        this.key = key;
        this.locale = locale;
    }

    /**
     * Returns the key entity id.
     *
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key entity id.
     *
     * @param key entityId
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the translation locale.
     *
     * @return locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the translation locale.
     *
     * @param locale translation locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }
}
