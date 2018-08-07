/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.infrastructure.csv;

import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.io.CSVRepresentation;
import org.seedstack.i18n.rest.internal.io.ImportService;
import org.seedstack.io.Parse;
import org.seedstack.io.Parser;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.transaction.Transactional;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.List;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class CSVImportService implements ImportService {

    @Parse(I18nCSVTemplateLoader.I18N_CSV_TEMPLATE)
    private Parser<CSVRepresentation> parser;

    private FluentAssembler fluentAssembler;
    private KeyRepository keyRepository;

    @Inject
    public CSVImportService(FluentAssembler fluentAssembler, KeyRepository keyRepository) {
        this.fluentAssembler = fluentAssembler;
        this.keyRepository = keyRepository;
    }

    /**
     * Non injected constructor used for testing.
     *
     * @param fluentAssembler the assembler DSL
     * @param keyRepository   the key repository
     * @param parser          the CSV parser
     */
    CSVImportService(FluentAssembler fluentAssembler, KeyRepository keyRepository, Parser<CSVRepresentation> parser) {
        this.fluentAssembler = fluentAssembler;
        this.keyRepository = keyRepository;
        this.parser = parser;
    }

    @JpaUnit("seed-i18n-domain")
    @Transactional
    @Override
    public int importKeysWithTranslations(InputStream inputStream) {
        List<CSVRepresentation> CSVRepresentations = parser.parse(inputStream, CSVRepresentation.class);
        for (CSVRepresentation dto : CSVRepresentations) {
            Key key = fluentAssembler.merge(dto).into(Key.class).fromRepository().orFromFactory();
            keyRepository.add(key);
        }
        return CSVRepresentations.size();
    }
}
