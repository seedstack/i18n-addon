/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.key;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.seedstack.business.domain.BaseAggregateRoot;
import org.seedstack.i18n.internal.domain.model.locale.LocaleCodeSpecification;

/**
 * Aggregate root of Key aggregate.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@Entity
@Table(name = "SEED_I18N_KEY")
public class Key extends BaseAggregateRoot<String> {
    @Id
    @Column(name = "ID")
    private String entityId;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "OUTDATED")
    private boolean outdated;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "SEED_I18N_KEY_TRANS")
    @MapKey
    private Map<TranslationId, Translation> translations = new HashMap<>();

    protected Key() {
    }

    public Key(String name) {
        this.entityId = name;
    }

    @Override
    public String getId() {
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
     *                                            or contains other characters than letters and "-".
     */
    public Translation addTranslation(String locale, String value) {
        return addTranslation(locale, value, false);
    }

    /**
     * Saves or updates the translation for the specified locale.
     * If the key was outdated, checks if the key is still outdated.
     *
     * @param locale        specified the translation locale
     * @param value         translation value
     * @param isApproximate true if the translation is not exact.
     * @return the new translation
     * @throws java.lang.IllegalArgumentException if the locale is null or empty
     *                                            or contains other characters than letters and "-".
     */
    public Translation addTranslation(String locale, String value, boolean isApproximate) {
        LocaleCodeSpecification.assertCode(locale);
        if (isBlank(value)) {
            throw new IllegalArgumentException("The translation can't be blank");
        }
        Translation translation = createOrUpdateTranslation(locale, value, isApproximate);
        checkOutdatedStatus();
        return translation;
    }

    private Translation createOrUpdateTranslation(String locale, String value, boolean isApproximate) {
        Translation translation;
        if (isTranslated(locale)) {
            translation = this.translations.get(new TranslationId(entityId, locale));
            translation.updateValue(value);
        } else {
            translation = new Translation(new TranslationId(entityId, locale), value);
        }
        translation.setApproximate(isApproximate);
        translations.put(new TranslationId(entityId, locale), translation);
        return translation;
    }

    /**
     * Returns the translation corresponding to the given locale code.
     *
     * @param locale locale code
     * @return the translation
     * @throws java.lang.IllegalArgumentException if the locale is null or empty
     *                                            or contains other characters than letters and "-".
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
     *                                            or contains other characters than letters and "-".
     */
    public void removeTranslation(String locale) {
        LocaleCodeSpecification.assertCode(locale);

        Translation translation = getTranslation(locale);
        if (translation != null) {
            this.translations.remove(translation.getId());
        }
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
     * Returns the translation set.
     *
     * @return the translations
     */
    public Map<String, Translation> getTranslations() {
        Map<String, Translation> map = new HashMap<>();
        for (Map.Entry<TranslationId, Translation> translationEntry : translations.entrySet()) {
            map.put(translationEntry.getKey().getLocale(), translationEntry.getValue());
        }
        return map;
    }

    public boolean isTranslated(String locale) {
        return translations.get(new TranslationId(entityId, locale)) != null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().equals("");
    }
}

