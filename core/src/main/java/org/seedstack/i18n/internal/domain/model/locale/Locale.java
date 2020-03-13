/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.seedstack.business.domain.BaseAggregateRoot;

/**
 * Locale aggregate root.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@Entity
@Table(name = "SEED_I18N_LOCALE")
public class Locale extends BaseAggregateRoot<String> {
    /**
     * Identifies the locale with the ISO 639 alpha-2 or alpha-3 language code and the ISO 3166 alpha-2 country code.
     * Example: en-US, fr-FR
     */
    @Id
    @Column(name = "CODE")
    private String code;

    /**
     * Language name in their own language
     */
    @Column(name = "LANGUAGE")
    private String language;

    /**
     * Language name in english
     */
    @Column(name = "ENGLISH_LANGUAGE")
    private String englishLanguage;

    /**
     * Indicate if the locale is the locale by default.
     */
    @Column(name = "DEFAULT_LOCALE")
    private boolean defaultLocale;

    /**
     * Constructor.
     */
    protected Locale() {
    }

    /**
     * Constructor.
     *
     * @param code            locale code
     * @param language        locale language
     * @param englishLanguage locale english language
     */
    protected Locale(String code, String language, String englishLanguage) {
        if (!new LocaleCodeSpecification().isSatisfiedBy(code)) {
            throw new IllegalArgumentException(String.format(LocaleCodeSpecification.MESSAGE, code));
        }
        this.code = code;
        this.language = language;
        this.englishLanguage = englishLanguage;
    }

    @Override
    public String getId() {
        return code;
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
     * Returns the locale english language
     *
     * @return english language
     */
    public String getEnglishLanguage() {
        return englishLanguage;
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
