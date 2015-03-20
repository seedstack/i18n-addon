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

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.seedstack.business.jpa.domain.id.SimpleJpaAggregateRoot;

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
@Table(name="SEED_I18N_KEY")
public class Key extends SimpleJpaAggregateRoot<String> implements Serializable {

    private static final long serialVersionUID = -2498537747032788365L;
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
     * @param name         key name
     * @param comment      key comment
     * @param translations key translations
     * @param outdated     is the key outdated
     */
    protected Key(String name, String comment, Map<String, Translation> translations, boolean outdated) {
        this.entityId = name;
        this.description = comment;
        this.translations = translations;
        this.outdated = outdated;
    }

    /**
     * Sets entity id.
     *
     * @param entityId key name
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * Saves or updates the translation for the specified locale.
     * If the key was outdated, checks if the key is still outdated.
     *
     * @param locale      specified the translation locale
     * @param value       translation value
     * @param approximate translation approximate indicator
     * @param outdated    translation outdated indicator
     * @throws java.lang.IllegalArgumentException if locale is blank
     */
    public void addTranslation(String locale, String value, boolean approximate, boolean outdated) {
        Preconditions.checkArgument(StringUtils.isNotBlank(locale));
        // If exists, get the translation for the given locale
        Translation translation = this.translations.get(locale);
        if (translation == null) {
            translation = new Translation();
            translation.setEntityId(new TranslationId(entityId, locale));
        }
        translation.setValue(value);
        translation.setApproximate(approximate);
        translation.setOutdated(outdated);
        translation.setKey(this);
        translations.put(locale, translation);
        updateOutdated();
    }

    /**
     * Returns the translation corresponding to the given locale code.
     *
     * @param locale locale code
     * @return translation
     * @throws java.lang.IllegalArgumentException if locale is blank
     */
    public Translation getTranslation(String locale) {
        Preconditions.checkArgument(StringUtils.isNotBlank(locale), "The locale should not be blank");
        return this.getTranslations().get(locale);
    }

    /**
     * Remove the translation corresponding to the given locale code.
     *
     * @param locale locale code
     * @throws java.lang.IllegalArgumentException if locale is blank
     */
    public void removeTranslation(String locale) {
        Preconditions.checkArgument(StringUtils.isNotBlank(locale));
        Translation translation = getTranslation(locale);
        if (translation != null) {
            this.getTranslations().remove(translation);
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
        Key subKey = new Key();
        subKey.setComment(description);
        subKey.setOutdated(outdated);
        subKey.setEntityId(entityId);
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
        Key subKey = new Key();
        subKey.setComment(description);
        subKey.setOutdated(outdated);
        subKey.setEntityId(entityId);
        Map<String, Translation> subTranslations = new HashMap<String, Translation>();
        addTranslation(subTranslations, subKey, defaultLocale);
        subKey.setTranslations(subTranslations);
        return subKey;
    }

    private void addTranslation(Map<String, Translation> translations, Key key, String locale) {
        Translation source = this.translations.get(locale);
        if (source == null) {
            source = new Translation(new TranslationId(key.entityId, locale), key, "", false, false);
        }
        translations.put(locale, source);
    }

    /**
     * Checks if the key has outdated translation
     * and then update the outdated indicator of the key.
     */
    public void updateOutdated() {
        outdated = false;
        for (Translation translation : translations.values()) {
            if (translation.isOutdated()) {
                outdated = true;
            }
        }
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
     * Sets the outdated indicator.
     *
     * @param outdated outdated indicator
     */
    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
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
}

