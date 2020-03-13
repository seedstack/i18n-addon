/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.key;


import org.seedstack.business.domain.BaseValueObject;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * {@link Translation} embedded id, composed of {@link Key} and locale identifier.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@Embeddable
public class TranslationId extends BaseValueObject {

    private static final long serialVersionUID = 2487221653197332337L;


    @Column(name = "KEY_ID")
    private String key;

    @Column(name = "LOCALE")
    private String locale;

    protected TranslationId() {
    }

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
     * Returns the translation locale.
     *
     * @return locale
     */
    public String getLocale() {
        return locale;
    }

}
