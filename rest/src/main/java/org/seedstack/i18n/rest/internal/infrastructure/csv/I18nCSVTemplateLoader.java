/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.csv;

import com.google.common.collect.Sets;
import org.seedstack.i18n.rest.internal.locale.LocaleFinder;
import org.seedstack.i18n.rest.internal.locale.LocaleRepresentation;
import org.seedstack.io.spi.Template;
import org.seedstack.io.spi.TemplateLoader;
import org.seedstack.io.supercsv.Column;
import org.seedstack.io.supercsv.SuperCsvTemplate;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;
import org.supercsv.cellprocessor.Optional;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * @author pierre.thirouin@ext.mpsa.com
 */
public class I18nCSVTemplateLoader implements TemplateLoader {

    public static final String I18N_CSV_TEMPLATE = "i18nTranslations";
    public static final String KEY = "key";

    @Inject
    private LocaleFinder localeFinder;

    @JpaUnit("seed-i18n-domain")
    @Transactional
    @Override
    public Template load(String name) {
        List<LocaleRepresentation> availableLocales = localeFinder.findAvailableLocales();
        SuperCsvTemplate superCsvTemplate = new SuperCsvTemplate(name);
        superCsvTemplate.addColumn(new Column(KEY, KEY, new Optional(), new Optional()));
        for (LocaleRepresentation availableLocale : availableLocales) {
            superCsvTemplate.addColumn(new Column(availableLocale.getCode(), availableLocale.getCode(), new Optional(), new Optional()));
        }
        return superCsvTemplate;
    }

    @Override
    public Set<String> names() {
        return Sets.newHashSet(I18N_CSV_TEMPLATE);
    }

    @Override
    public boolean contains(String name) {
        return names().contains(name);
    }

    @Override
    public String templateRenderer() {
        return I18nCSVRenderer.I18N_RENDERER;
    }

    @Override
    public String templateParser() {
        return CSVParser.I18N_PARSER;
    }
}
