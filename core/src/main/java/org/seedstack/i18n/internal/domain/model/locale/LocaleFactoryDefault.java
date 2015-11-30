/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.domain.model.locale;

import org.seedstack.business.domain.BaseFactory;

import static java.util.Locale.ENGLISH;

/**
 * Locale factory implementation.
 */
public class LocaleFactoryDefault extends BaseFactory<Locale> implements LocaleFactory {

    private LocaleCodeSpecification localeCodeSpecification = new LocaleCodeSpecification();

    @Override
    public Locale createFromLanguage(String language) {
        return createFromLocale(new java.util.Locale(language));
    }

    @Override
    public Locale createFromLanguageAndRegion(String language, String region) {
        return createFromLocale(new java.util.Locale(language, region));
    }

    @Override
    public Locale createFromCode(String localeCode) {
        LocaleCodeSpecification.assertCode(localeCode);

        String[] localeFragments = localeCode.split("\\-");
        java.util.Locale locale;
        switch (localeFragments.length) {
            case 1:
                locale = new java.util.Locale(localeFragments[0]);
                break;
            case 2:
                locale = new java.util.Locale(localeFragments[0], localeFragments[1]);
                break;
            default:
                locale = new java.util.Locale(localeFragments[0], localeFragments[1], localeFragments[2]);
                break;
        }

        return this.createFromLocale(locale);
    }

    @Override
    public Locale createFromLocale(java.util.Locale locale) {
        String normalizedLocaleCode = Locale.formatLocaleCode(locale.toString());
        String nativeLanguageName = locale.getDisplayName(locale);
        String englishLanguageName = locale.getDisplayName(new java.util.Locale(ENGLISH.toString()));
        return new Locale(normalizedLocaleCode, nativeLanguageName, englishLanguageName);
    }
}
