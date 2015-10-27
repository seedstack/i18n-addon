/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.i18n.rest.io;

import org.seedstack.i18n.internal.domain.model.key.Key;
import org.seedstack.i18n.internal.domain.model.key.KeyFactory;
import org.seedstack.i18n.internal.domain.model.key.KeyRepository;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.seedstack.io.api.Parse;
import org.seedstack.io.api.Parser;
import org.seedstack.io.api.Render;
import org.seedstack.io.api.Renderer;
import org.seedstack.seed.persistence.jpa.api.JpaUnit;
import org.seedstack.seed.security.api.annotations.RequiresPermissions;
import org.seedstack.seed.transaction.api.Transactional;
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
 *         Date: 14/04/2014
 */
@Path("/seed-i18n/keys/file")
@JpaUnit("seed-i18n-domain")
@Transactional
public class IOResource {

    private static final String PRINT_HEADER = "printHeader";
    private static final String APPLICATION_CSV = "application/csv";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String ATTACHMENT_FILENAME_I18N_CSV = "attachment; filename=i18n.csv";

    @Render("i8nTranslations")
    private Renderer renderer;

    @Parse("i8nTranslations")
    private Parser<DataRepresentation> parser;

    private static final Logger LOGGER = LoggerFactory.getLogger(IOResource.class);

    @Inject
    private DataAssembler dataAssembler;

    @Inject
    private KeyRepository keyRepository;

    @Inject
    private KeyFactory factory;

    /**
     * Import a CSV file or a list of CSV file.
     *
     * @param multiPart multipart data
     * @return status code 200, or 400 if the multipart is null
     */
    @POST
    @Consumes("multipart/form-data")
    @RequiresPermissions("seed:i18n:key:read")
    public Response uploadTranslations(FormDataMultiPart multiPart) {
        List<DataRepresentation> translations = new ArrayList<DataRepresentation>();
        if (multiPart != null) {
            List<FormDataBodyPart> fields = multiPart.getFields("file[]");
            for (FormDataBodyPart field : fields) {
                // Check file extension
                if (field.getContentDisposition() == null || !field.getContentDisposition().getFileName().endsWith(".csv")) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Incorrect input file").type(MediaType.TEXT_PLAIN_TYPE).build();
                }
                translations.addAll(parser.parse(field.getValueAs(InputStream.class), DataRepresentation.class));
            }

            List<Key> keys = mergeOrCreateKeyWithDto(translations);
            keyRepository.persist(keys);

            LOGGER.debug("Loaded {} keys with their translations", translations.size());
        } else {
            // Check multipart nullity
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing input file").type(MediaType.TEXT_PLAIN_TYPE).build();
        }

        return Response.ok(String.format("Loaded %d translations", translations.size()), MediaType.TEXT_PLAIN_TYPE).build();
    }

    private List<Key> mergeOrCreateKeyWithDto(List<DataRepresentation> translations) {
        List<Key> keys = new ArrayList<Key>(translations.size());
        for (DataRepresentation dataRepresentation : translations) {
            Key key = keyRepository.load(dataRepresentation.getKey());
            if (key == null) {
                key = factory.createKey(dataRepresentation.getKey());
            }
            dataAssembler.mergeAggregateWithDto(key, dataRepresentation);
            keys.add(key);
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
    @RequiresPermissions("seed:i18n:key:read")
    public Response getTranslations() {
        final List<Key> keys = keyRepository.loadAll();
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PRINT_HEADER, true);

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                for (Key key : keys) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    renderer.render(baos, key, APPLICATION_CSV, parameters);
                    writer.write(baos.toString());
                    // the header will be printed only once
                    if ((Boolean) parameters.get(PRINT_HEADER)) {
                        parameters.put(PRINT_HEADER, false);
                    }
                }
                writer.flush();
            }
        };
        return Response.ok(stream).header(CONTENT_DISPOSITION, ATTACHMENT_FILENAME_I18N_CSV).build();
    }
}
