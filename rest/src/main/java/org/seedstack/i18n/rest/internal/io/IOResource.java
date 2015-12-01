/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.internal.io;

import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.seedstack.business.assembler.FluentAssembler;
import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import org.seedstack.i18n.rest.internal.I18nPermissions;
import org.seedstack.i18n.rest.internal.shared.BadRequestException;
import org.seedstack.i18n.rest.internal.shared.WebAssertions;
import org.seedstack.io.Parse;
import org.seedstack.io.Parser;
import org.seedstack.io.Render;
import org.seedstack.io.Renderer;
import org.seedstack.jpa.JpaUnit;
import org.seedstack.seed.security.RequiresPermissions;
import org.seedstack.seed.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This REST resource provide an API to import/export CSV file.
 *
 * @author pierre.thirouin@ext.mpsa.com
 */
@Path("/seed-i18n/keys/file")
@JpaUnit("seed-i18n-domain")
@Transactional
public class IOResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOResource.class);
    private static final String APPLICATION_CSV = "application/csv";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String ATTACHMENT_FILENAME_I18N_CSV = "attachment; filename=i18n.csv";
    public static final String LOADED_KEYS_MESSAGE = "Loaded %d keys with their translations";

    @Render(I18nCSVTemplateLoader.I18N_CSV_TEMPLATE)
    private Renderer renderer;

    @Parse(I18nCSVTemplateLoader.I18N_CSV_TEMPLATE)
    private Parser<I18nCSVRepresentation> parser;

    @Inject
    private FluentAssembler fluentAssembler;
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

        int totalKeyImported = 0;
        for (BodyPart bodyPart : multiPart.getBodyParts()) {
            if (fileHasCSVExtension(bodyPart)) {
                throw new BadRequestException("Incorrect file extension. Expected *.csv");
            }
            List<Key> keys = readKeysFromFile(bodyPart);
            keyRepository.persistAll(keys);
            totalKeyImported += keys.size();
        }

        String loadedKeysMessage = String.format(LOADED_KEYS_MESSAGE, totalKeyImported);
        LOGGER.debug(loadedKeysMessage);
        return Response.ok(loadedKeysMessage, MediaType.TEXT_PLAIN_TYPE).build();
    }

    private boolean fileHasCSVExtension(BodyPart bodyPart) {
        ContentDisposition contentDisposition = bodyPart.getContentDisposition();
        return contentDisposition == null || !contentDisposition.getFileName().endsWith(".csv");
    }

    private List<Key> readKeysFromFile(BodyPart bodyPart) {
        InputStream inputStream = bodyPart.getEntityAs(InputStream.class);
        List<I18nCSVRepresentation> i18nCSVRepresentations = parser.parse(inputStream, I18nCSVRepresentation.class);
        List<Key> keys = new ArrayList<Key>();
        for (I18nCSVRepresentation i18nCSVRepresentation : i18nCSVRepresentations) {
            keys.add(fluentAssembler.merge(i18nCSVRepresentation).into(Key.class).fromRepository().orFromFactory());
        }
        return keys;
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
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                for (Key key : keys) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    renderer.render(byteArrayOutputStream, key, APPLICATION_CSV, printHeader(isFirstLine));
                    writer.write(byteArrayOutputStream.toString());
                    isFirstLine = false;
                }
                writer.flush();
            }
        }).header(CONTENT_DISPOSITION, ATTACHMENT_FILENAME_I18N_CSV).build();
    }

    private Map<String, Object> printHeader(boolean shouldPrintHeader) {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(I18nCSVRenderer.PRINT_HEADER, shouldPrintHeader);
        return parameters;
    }
}
