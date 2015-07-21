/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.locale;


import org.seedstack.business.api.domain.BaseAggregateRoot;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Locale aggregate root.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@Entity
@Table(name="SEED_I18N_LOCALE")
public class Locale extends BaseAggregateRoot<String> implements Serializable {

    private static final long serialVersionUID = 5353966062091859950L;
    /**
     * Identifies the locale with the ISO 639 alpha-2 or alpha-3 language code and the ISO 3166 alpha-2 country code.
     * Example: en-US, fr-FR
     */
    @Id
    private String code;

    /**
     * Language name in their own language
     */
    private String language;

    /**
     * Language name in english
     */
    private String englishLanguage;

    /**
     * Indicate if the locale is the locale by default.
     */
    private boolean defaultLocale;

    /**
     * Constructor.
     */
    protected Locale() {
    }

    /**
     * Constructor.
     *
     * @param code locale code
     */
    protected Locale(String code) {
        this.code = code;
    }

    /**
     * Constructor.
     *
     * @param code            locale code
     * @param language        locale language
     * @param englishLanguage locale english language
     * @param defaultLocale   is default locale
     */
    protected Locale(String code, String language, String englishLanguage, boolean defaultLocale) {
        this.code = code;
        this.language = language;
        this.englishLanguage = englishLanguage;
        this.defaultLocale = defaultLocale;
    }

    @Override
    public String getEntityId() {
        return code;
    }

    /**
     * Sets the locale code.
     *
     * @param code entity id
     */
    public void setEntityId(String code) {
        this.code = code;
    }

    /**
     * Returns the locale language.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the locale language.
     *
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Returns the locale english language
     *
     * @return english language
     */
    public String getEnglishLanguage() {
        return englishLanguage;
    }

    /**
     * Sets the english language.
     *
     * @param englishLanguage locale english language
     */
    public void setEnglishLanguage(String englishLanguage) {
        this.englishLanguage = englishLanguage;
    }

    /**
     * Indicates whether the current locale is the default locale or not.
     *
     * @return true if this locale is the default, false otherwise
     */
    public boolean isDefaultLocale() {
        return defaultLocale;
    }

    /**
     * Sets the default locale.
     *
     * @param defaultLocale application default locale
     */
    public void setDefaultLocale(boolean defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
}
