/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.data.key;

/**
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 20/03/14
 */
public class TranslationDTO {

    private String locale;

    private String value;

    private boolean outdated;

    private boolean approximate;

    /**
     * Default constructor.
     */
    public TranslationDTO() {
    }

    /**
     * Full properties constructor.
     *
     * @param locale      locale
     * @param value       value
     * @param outdated    outdated
     * @param approximate approximate
     */
    public TranslationDTO(String locale, String value, boolean outdated, boolean approximate) {
        this.locale = locale;
        this.value = value;
        this.outdated = outdated;
        this.approximate = approximate;
    }

    /**
     * Gets the locale.
     *
     * @return locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the locale.
     *
     * @param locale locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Get the translation value.
     *
     * @return translation value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the translation value.
     *
     * @param value translation value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Is the translation outdated.
     *
     * @return outdated indicator
     */
    public boolean isOutdated() {
        return outdated;
    }

    /**
     * Get the translation outdated.
     *
     * @param outdated translation outdated
     */
    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    /**
     * Is the translation approximate.
     *
     * @return approximate indicator
     */
    public boolean isApproximate() {
        return approximate;
    }

    /**
     * Get the translation approximate.
     *
     * @param approximate translation approximate
     */
    public void setApproximate(boolean approximate) {
        this.approximate = approximate;
    }
}
