/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.translation;

/**
 * Translation representation.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 26/11/13
 */
public class TranslationValueRepresentation {

    private String locale;

    private String translation;

    private boolean outdated;

    private boolean approx;

    public TranslationValueRepresentation() {
    }

    public TranslationValueRepresentation(String locale, String translation) {
        this.locale = locale;
        this.translation = translation;
    }

    /**
     * Gets the translation locale.
     *
     * @return locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the translation locale
     *
     * @param locale translation locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Indicates whether the translation is approximate or not.
     *
     * @return true if the translation is approximate, false otherwise
     */
    public boolean isApprox() {
        return approx;
    }

    /**
     * Sets the translation approximate indicator.
     *
     * @param approximate is the translation approximate
     */
    public void setApprox(boolean approximate) {
        this.approx = approximate;
    }

    /**
     * Indicates whether the translation is outdated or not
     *
     * @return outdated indicator
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
     * Gets the translation value.
     *
     * @return translation value
     */
    public String getTranslation() {
        return translation;
    }

    /**
     * Sets the translation value.
     *
     * @param translation translation value
     */
    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
