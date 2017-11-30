/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.i18n.internal.data.key;

import java.util.ArrayList;
import java.util.List;
import org.seedstack.business.assembler.MatchingEntityId;
import org.seedstack.business.assembler.MatchingFactoryParameter;
import org.seedstack.business.data.DataSet;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@DataSet(group = "seed-i18n", name = "key")
public class KeyDTO {
    private String name;
    private String comment;
    private boolean outdated;
    private List<TranslationDTO> translations = new ArrayList<>();

    /**
     * Adds a translation to the keyDTO
     *
     * @param locale      translation locale
     * @param value       translation value
     * @param outdated    is the translation outdated
     * @param approximate is the translation approximate
     */
    public void addTranslationDTO(String locale, String value, boolean outdated, boolean approximate) {
        translations.add(new TranslationDTO(locale, value, outdated, approximate));
    }

    /**
     * Gets the key name.
     *
     * @return key name
     */
    @MatchingEntityId(index = 0)
    @MatchingFactoryParameter(index = 0)
    public String getName() {
        return name;
    }

    /**
     * Sets the key name.
     *
     * @param name key name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the key comment.
     *
     * @return key comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the key comment.
     *
     * @param comment key comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets the translations
     *
     * @return list of translation
     */
    public List<TranslationDTO> getTranslations() {
        return translations;
    }

    /**
     * Sets the list of translations.
     *
     * @param translations key translations
     */
    public void setTranslations(List<TranslationDTO> translations) {
        this.translations = translations;
    }

    /**
     * Is the key outdated.
     *
     * @return outdated indicator
     */
    public boolean isOutdated() {
        return outdated;
    }

    /**
     * Set the outdated indicator.
     *
     * @param outdated is the key outdated
     */
    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }
}
