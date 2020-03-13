/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.utils;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.Translation;
import org.seedstack.i18n.internal.domain.model.key.TranslationId;
import org.seedstack.i18n.internal.domain.model.locale.Locale;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/** Utility to be used with i18n-addon in order to update DDL scripts */
public class SchemaGenerator {

    public static void main(String[] args) {
        generateScript("org.hibernate.dialect.MySQL57Dialect", "mysql-create-tables.sql", true, ";", SchemaExport.Action.CREATE);
        generateScript("org.hibernate.dialect.Oracle10gDialect", "oracle-create-tables.sql", true, ";", SchemaExport.Action.CREATE);
        generateScript("org.hibernate.dialect.HSQLDialect", "hsql-create-tables.sql", false, "", SchemaExport.Action.BOTH);
        generateScript("org.hibernate.dialect.PostgreSQL95Dialect", "pgsql-create-tables.sql", true, ";", SchemaExport.Action.CREATE);
    }

    private static void generateScript(String dialect, String outputFile, boolean format, String delimiter, SchemaExport.Action action) {
        Map<String, String> settings = new HashMap<>();
        settings.put("hibernate.dialect", dialect);
        settings.put("hibernate.hbm2ddl.auto", "create");
        settings.put("show_sql", "true");

        MetadataSources metadata = buildMetadata(settings);

        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setHaltOnError(true);
        schemaExport.setFormat(format);
        schemaExport.setDelimiter(delimiter);
        schemaExport.setOutputFile("sql/" + outputFile);
        schemaExport.execute(EnumSet.of(TargetType.SCRIPT), action, (MetadataImplementor) metadata.buildMetadata());

    }

    private static MetadataSources buildMetadata(Map<String, String> settings) {
        MetadataSources metadata = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .applySettings(settings)
                        .build());
        metadata.addAnnotatedClass(Translation.class);
        metadata.addAnnotatedClass(TranslationId.class);
        metadata.addAnnotatedClass(Locale.class);
        metadata.addAnnotatedClass(Key.class);
        return metadata;
    }
}

