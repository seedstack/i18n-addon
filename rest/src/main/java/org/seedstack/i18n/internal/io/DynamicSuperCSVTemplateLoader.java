/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.internal.io;

import com.google.common.collect.Sets;
import org.seedstack.i18n.rest.locale.LocaleFinder;
import org.seedstack.i18n.rest.locale.LocaleRepresentation;
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
 *         Date: 11/04/2014
 */
@JpaUnit("seed-i18n-domain")
@Transactional
public class DynamicSuperCSVTemplateLoader implements TemplateLoader {

    private static final String I18N_SUPER_CSV = "i18nSuperCSV";

    @Inject
    private LocaleFinder localeFinder;

    @Override
    public Template load(String name) {
        List<LocaleRepresentation> availableLocales = localeFinder.findAvailableLocales();
        SuperCsvTemplate superCsvTemplate = new SuperCsvTemplate(name);
        superCsvTemplate.addColumn(new Column("key", "key", new Optional(), new Optional()));
        for (LocaleRepresentation availableLocale : availableLocales) {
            superCsvTemplate.addColumn(new Column(availableLocale.getCode(), availableLocale.getCode(), new Optional(), new Optional()));
        }
        return superCsvTemplate;
    }

    @Override
    public Set<String> names() {
        return Sets.newHashSet("i8nTranslations");
    }

    @Override
    public boolean contains(String name) {
        return names().contains(name);
    }

    @Override
    public String templateRenderer() {
        return I18N_SUPER_CSV;
    }

    @Override
    public String templateParser() {
        return I18N_SUPER_CSV;
    }
}
