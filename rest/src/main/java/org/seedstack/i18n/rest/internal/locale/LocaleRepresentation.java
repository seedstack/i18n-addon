/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.locale;

/**
 * Locale representation.
 *
 * @author pierre.thirouin@ext.mpsa.com
 *         Date: 25/11/13
 */
public class LocaleRepresentation implements Comparable<LocaleRepresentation> {

    // avoid magic number
    private static final int INT = 31;

    private String code;

    private String language;

    private String englishLanguage;

    /**
     * Gets the locale code.
     *
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the locale code
     *
     * @param code locale code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the locale language.
     *
     * @return locale language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language locale language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets the english language.
     *
     * @return english language
     */
    public String getEnglishLanguage() {
        return englishLanguage;
    }

    /**
     * Sets the english language.
     *
     * @param englishLanguage english language
     */
    public void setEnglishLanguage(String englishLanguage) {
        this.englishLanguage = englishLanguage;
    }

    @Override
    public int compareTo(LocaleRepresentation o) {
        if (o == null) {
            return -1;
        }
        return englishLanguage.compareTo(o.englishLanguage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LocaleRepresentation that = (LocaleRepresentation) o;

        boolean isEquals = true;
        if (code != null) {
            if (!code.equals(that.code)) {
                isEquals = false;
            }
        } else {
            if (that.code != null) {
                isEquals = false;
            }
        }
        if (englishLanguage != null) {
            if (!englishLanguage.equals(that.englishLanguage)) {
                isEquals = false;
            }
        } else {
            if (that.englishLanguage != null) {
                isEquals = false;
            }
        }
        if (language != null) {
            if (!language.equals(that.language)) {
                isEquals = false;
            }
        } else {
            if (that.language != null) {
                isEquals = false;
            }
        }

        return isEquals;
    }

    @Override
    public int hashCode() {
        int result;
        if (code != null) {
            result = code.hashCode();
        } else {
            result = 0;
        }
        if (language != null) {
            result = INT * result + language.hashCode();
        } else {
            result = INT * result;
        }
        if (englishLanguage != null) {
            result = INT * result + englishLanguage.hashCode();
        } else {
            result = INT * result;
        }
        return result;
    }
}
