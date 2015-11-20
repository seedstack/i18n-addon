/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.key;

import org.seedstack.business.domain.BaseAggregateRoot;
import org.seedstack.i18n.internal.domain.model.locale.LocaleCodeSpecification;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Aggregate root of Key aggregate.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@Entity
@Table(name = "SEED_I18N_KEY")
public class Key extends BaseAggregateRoot<String> implements Serializable {

    private static final long serialVersionUID = -2498537747032788365L;

    @Id
    private String entityId;

    /**
     * Describes the key.
     */
    private String description;

    private boolean outdated;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinTable(name = "SEED_I18N_KEY_TRANS")
    private Map<String, Translation> translations = new HashMap<String, Translation>();

    /**
     * Default constructor.
     */
    protected Key() {
    }

    /**
     * Constructor.
     *
     * @param name key name
     */
    public Key(String name) {
        this.entityId = name;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    /**
     * Saves or updates the translation for the specified locale.
     * If the key was outdated, checks if the key is still outdated.
     *
     * @param locale specified the translation locale
     * @param value  translation value
     * @return the new translation
     * @throws java.lang.IllegalArgumentException if the locale is null or empty
     * or contains other characters than letters and "-".
     */
    public Translation addTranslation(String locale, String value) {
        return addTranslation(locale, value, false);
    }
    /**
     * Saves or updates the translation for the specified locale.
     * If the key was outdated, checks if the key is still outdated.
     *
     * @param locale specified the translation locale
     * @param value  translation value
     * @return the new translation
     * @throws java.lang.IllegalArgumentException if the locale is null or empty
     * or contains other characters than letters and "-".
     */
    public Translation addTranslation(String locale, String value, boolean isApproximate) {
        LocaleCodeSpecification.assertCode(locale);

        // If exists, get the translation for the given locale
        Translation translation = this.translations.get(locale);
        if (translation == null) {
            translation = new Translation(new TranslationId(entityId, locale), this, value);
        } else {
            translation.updateValue(value);
        }
        translation.setApproximate(isApproximate);
        translations.put(locale, translation);
        checkOutdatedStatus();
        return translation;
    }

    /**
     * Returns the translation corresponding to the given locale code.
     *
     * @param locale locale code
     * @return the translation
     * @throws java.lang.IllegalArgumentException if the locale is null or empty
     * or contains other characters than letters and "-".
     */
    public Translation getTranslation(String locale) {
        LocaleCodeSpecification.assertCode(locale);

        return this.getTranslations().get(locale);
    }

    /**
     * Removes the translation corresponding to the given locale code.
     *
     * @param locale locale code
     * @throws java.lang.IllegalArgumentException if the locale is null or empty
     * or contains other characters than letters and "-".
     */
    public void removeTranslation(String locale) {
        LocaleCodeSpecification.assertCode(locale);

        Translation translation = getTranslation(locale);
        if (translation != null) {
            this.getTranslations().remove(locale);
        }
    }

    /**
     * Returns a sub aggregate key with only two translations.
     *
     * @param sourceLocale source locale
     * @param targetLocale target locale
     * @return Key
     */
    public Key subKey(String sourceLocale, String targetLocale) {
        Key subKey = new Key(entityId);
        subKey.setComment(description);
        if (outdated) {
            subKey.setOutdated();
        }
        Map<String, Translation> subTranslations = new HashMap<String, Translation>();
        addTranslation(subTranslations, subKey, sourceLocale);
        addTranslation(subTranslations, subKey, targetLocale);
        subKey.setTranslations(subTranslations);
        return subKey;
    }


    /**
     * Returns a sub aggregate key with just the default translation.
     *
     * @param defaultLocale source locale
     * @return Key
     */
    public Key subKey(String defaultLocale) {
        Key subKey = new Key(entityId);
        subKey.setComment(description);
        if (outdated) {
            subKey.setOutdated();
        }
        Map<String, Translation> subTranslations = new HashMap<String, Translation>();
        addTranslation(subTranslations, subKey, defaultLocale);
        subKey.setTranslations(subTranslations);
        return subKey;
    }

    private void addTranslation(Map<String, Translation> translations, Key key, String locale) {
        Translation source = this.translations.get(locale);
        if (source == null) {
            source = new Translation(new TranslationId(key.entityId, locale), key, "");
        }
        translations.put(locale, source);
    }

    /**
     * Checks if the key has outdated translations and then update the outdated status of the key.
     */
    public void checkOutdatedStatus() {
        boolean newStatus = false;
        for (Translation translation : translations.values()) {
            if (translation.isOutdated()) {
                newStatus = true;
                break;
            }
        }
        outdated = newStatus;
    }

    /**
     * Returns a comment which describes the key.
     *
     * @return the comment
     */
    public String getComment() {
        return description;
    }

    /**
     * Sets the comment.
     *
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.description = comment;
    }

    /**
     * Indicates whether the key contains an outdated translation or not.
     *
     * @return true if the key is outdated, false otherwise
     */
    public boolean isOutdated() {
        return outdated;
    }

    /**
     * Marks the key and all its translations as outdated.
     */
    public void setOutdated() {
        this.outdated = true;

        for (Translation translation : this.translations.values()) {
            translation.setOutdated();
        }
    }

    /**
     * Sets the translations.
     *
     * @param translations translations set
     */
    public void setTranslations(Map<String, Translation> translations) {
        this.translations = translations;
    }

    /**
     * Returns the translation set.
     *
     * @return the translations
     */
    public Map<String, Translation> getTranslations() {
        return translations;
    }

    public boolean isTranslated(String locale) {
        return translations.get(locale) != null;
    }
}

