/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.data.locale;

import org.seedstack.business.assembler.AggregateId;
import org.seedstack.business.assembler.DtoOf;
import org.seedstack.business.assembler.FactoryArgument;
import org.seedstack.business.data.DataSet;
import org.seedstack.i18n.internal.domain.model.locale.Locale;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
@DtoOf(Locale.class)
@DataSet(group = "seed-i18n", name = "locales")
public class LocaleDTO {
    private static final int PARAM_0 = 0;
    private String code;
    private String language;
    private String englishLanguage;
    private boolean defaultLocale;

    @AggregateId(index = PARAM_0)
    @FactoryArgument(index = PARAM_0)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEnglishLanguage() {
        return englishLanguage;
    }

    public void setEnglishLanguage(String englishLanguage) {
        this.englishLanguage = englishLanguage;
    }

    public boolean isDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(boolean defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
}
