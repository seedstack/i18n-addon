/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.key;

/**
 * Key representation containing all key metadata and the default translation.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
public class KeyRepresentation {

    private String name;
    private String comment;
    private String translation;
    private String defaultLocale;
    private boolean missing;
    private boolean approx;
    private boolean outdated;

    public KeyRepresentation() {
    }

    public KeyRepresentation(String name, String defaultLocale, String translation, String comment) {
        this.name = name;
        this.comment = comment;
        this.translation = translation;
        this.defaultLocale = defaultLocale;
    }

    /**
     * Indicates whether the translation for the default locale is approximate or not.
     *
     * @return true if the key is approximate, false otherwise
     */
    public boolean isApprox() {
        return approx;
    }

    /**
     * Sets approximate indicator.
     *
     * @param approximate is the default translation approximate
     */
    public void setApprox(boolean approximate) {
        this.approx = approximate;
    }

    /**
     * Gets the key description.
     *
     * @return key description
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets a key description
     *
     * @param comment key description
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Indicates whether the translation for the default locale is missing or not.
     *
     * @return true if the translation is missing false otherwise
     */
    public boolean isMissing() {
        return missing;
    }

    /**
     * Sets the missing indicator.
     *
     * @param missing is the default translation missing
     */
    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    /**
     * Gets the key name.
     *
     * @return key name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name key name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Indicates whether the key contains outdated translation.
     *
     * @return true if the key is outdated, false otherwise
     */
    public boolean isOutdated() {
        return outdated;
    }

    /**
     * Sets the outdated indicator.
     *
     * @param outdated is the key outdated
     */
    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    /**
     * Gets the default locales.
     *
     * @return default locale
     */
    public String getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * Sets the default locale
     *
     * @param defaultLocale default locale
     */
    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * Gets the key translation for the default locale.
     *
     * @return translation
     */
    public String getTranslation() {
        return translation;
    }

    /**
     * Sets the default translation.
     *
     * @param translation default translation
     */
    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
