package com.rgbconsulting.prestashop.common.rest;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author LuisCarlosGonzalez
 */
@RestServiceLog
@Provider
@Priority(value = 100)
public class RestServiceLogging implements ContainerRequestFilter, ContainerResponseFilter {

    @Context
    private Providers providers;

    private final String REST_LOG_DIR = "/tmp/JakartaEEAppLogs";

    private void initialize() {
        //Acciones de Inicialización
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        initialize();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final StringBuilder sb = new StringBuilder();
        StringBuilder logPath = new StringBuilder();
        byte[] buffer = new byte[8192]; //8Kb
        byte[] requestByteArray = null;
        int bytesReaded = 0;
        try (InputStream is = requestContext.getEntityStream()) {
            while ((bytesReaded = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesReaded);
            }
        } catch (Exception ex) {
        }
        requestByteArray = baos.toByteArray();
        requestContext.setEntityStream(new ByteArrayInputStream(requestByteArray));
        sb.append("- User: ").append(requestContext.getSecurityContext().getUserPrincipal() == null ? "unknown" : requestContext.getSecurityContext().getUserPrincipal()).append("\n")
                .append("- Path: ").append(requestContext.getUriInfo().getPath()).append("\n")
                .append("- Header: ").append(requestContext.getHeaders()).append("\n")
                .append("- Entity: ").append(new String(requestByteArray));

        String dateFolder = new SimpleDateFormat("yyyyMMdd").format(new Date());
        logPath.append("AppFormacion_").append(System.currentTimeMillis()).append("_").append(requestContext.getMethod())
                .append("_I[").append(Thread.currentThread().getId()).append("].log");
        this.writeTextToFile(sb.toString(), REST_LOG_DIR + File.separator + dateFolder + File.separator + logPath.toString());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        initialize();
        final StringBuilder sb = new StringBuilder();
        StringBuilder logPath = new StringBuilder();
        sb.append("- Header: ").append(responseContext.getHeaders()).append("\n");
        sb.append("- Entity: ");
        if (responseContext.hasEntity()) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
                Class<?> entityClass = responseContext.getEntityClass();
                Type entityType = responseContext.getEntityType();
                Annotation[] entityAnnotations = responseContext.getEntityAnnotations();
                MediaType mediaType = responseContext.getMediaType();
                @SuppressWarnings("unchecked")
                MessageBodyWriter<Object> bodyWriter = (MessageBodyWriter<Object>) providers.getMessageBodyWriter(entityClass,
                        entityType,
                        entityAnnotations,
                        mediaType);
                bodyWriter.writeTo(responseContext.getEntity(),
                        entityClass,
                        entityType,
                        entityAnnotations,
                        mediaType,
                        responseContext.getHeaders(),
                        baos);
                sb.append(new String(baos.toByteArray()));
            } catch (Exception e) {
            }
        }

        String dateFolder = new SimpleDateFormat("yyyyMMdd").format(new Date());
        logPath.append("AppFormacion_").append(System.currentTimeMillis()).append("_").append(requestContext.getMethod())
                .append("_O[").append(Thread.currentThread().getId()).append("].log");
        this.writeTextToFile(sb.toString(), REST_LOG_DIR + File.separator + dateFolder + File.separator + logPath.toString());
    }

    public void writeTextToFile(final String text, final String filePath) {
        try {
            new File(Paths.get(filePath).getParent().toString()).mkdirs();
            try (FileWriter fw = new FileWriter(new File(filePath))) {
                fw.write(text);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
