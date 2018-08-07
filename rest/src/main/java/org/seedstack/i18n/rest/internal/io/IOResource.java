/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.io;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.I18nPermissions;
import org.seedstack.i18n.rest.internal.infrastructure.csv.I18nCSVTemplateLoader;
import org.seedstack.i18n.rest.internal.shared.BadRequestException;
import org.seedstack.i18n.rest.internal.shared.WebAssertions;
import org.seedstack.io.Render;
import org.seedstack.io.Renderer;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.security.RequiresPermissions;
import org.seedstack.seed.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This REST resource provide an API to import/export CSV file.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@Path("/seed-i18n/keys/file")
@JpaUnit("seed-i18n-domain")
@Transactional
public class IOResource {

    public static final Logger LOGGER = LoggerFactory.getLogger(IOResource.class);
    public static final String APPLICATION_CSV = "application/csv";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String ATTACHMENT_FILENAME_I18N_CSV = "attachment; filename=i18n.csv";
    public static final String LOADED_KEYS_MESSAGE = "Loaded %d keys with their translations";

    @Render(I18nCSVTemplateLoader.I18N_CSV_TEMPLATE)
    private Renderer renderer;

    @Inject
    private ImportService importService;
    @Inject
    private KeyRepository keyRepository;

    /**
     * Imports one or more CSV files containing i18n keys with their translations.
     *
     * @param multiPart multipart data
     * @return status code 200, or 400 if the multipart is null
     */
    @POST
    @Consumes("multipart/form-data")
    @RequiresPermissions(I18nPermissions.KEY_WRITE)
    public Response importTranslations(FormDataMultiPart multiPart) {
        WebAssertions.assertNotNull(multiPart, "Missing input file");

        int importedKeys = 0;
        for (BodyPart bodyPart : multiPart.getBodyParts()) {
            importedKeys += importFile(bodyPart);
        }

        String importedKeysMessage = String.format(LOADED_KEYS_MESSAGE, importedKeys);
        LOGGER.debug(importedKeysMessage);
        return Response.ok(importedKeysMessage, MediaType.TEXT_PLAIN_TYPE).build();
    }

    private int importFile(BodyPart bodyPart) {
        if (!fileHasCSVExtension(bodyPart)) {
            throw new BadRequestException("Incorrect file extension. Expected *.csv");
        }
        InputStream inputStream = bodyPart.getEntityAs(InputStream.class);
        return importService.importKeysWithTranslations(inputStream);
    }

    private boolean fileHasCSVExtension(BodyPart bodyPart) {
        ContentDisposition contentDisposition = bodyPart.getContentDisposition();
        return contentDisposition != null && contentDisposition.getFileName() != null && contentDisposition
                .getFileName().endsWith(".csv");
    }

    /**
     * Streams a CSV file with all keys with their translations.
     *
     * @return an i18n.csv file
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RequiresPermissions(I18nPermissions.KEY_READ)
    public Response exportTranslations() {
        final List<Key> keys = keyRepository.loadAll();

        return Response.ok(new StreamingOutput() {

            private boolean isFirstLine = true;

            @Override
            public void write(OutputStream os) throws IOException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                for (Key key : keys) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    renderer.render(byteArrayOutputStream, key, APPLICATION_CSV, printHeader(isFirstLine));
                    writer.write(byteArrayOutputStream.toString("UTF-8"));
                    isFirstLine = false;
                }
                writer.flush();
            }
        }).header(CONTENT_DISPOSITION, ATTACHMENT_FILENAME_I18N_CSV).build();
    }

    private Map<String, Object> printHeader(boolean shouldPrintHeader) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("printHeader", shouldPrintHeader);
        return parameters;
    }
}
