package org.openepics.discs.client.impl;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

/**
 * JAX RS Does not support the {@link AutoCloseable} for responses.
 *
 * This is a wrapper class to alleviate this unfortunate design error..
 *
 * @author <a href=mailto:miroslav.pavleski@cosylab.com>Miroslav Pavleski</a>
 */
public class ClosableResponse implements AutoCloseable {
    private Response response;

    public ClosableResponse(Response response) {
        this.response = response;
    }

    @Override
    public int hashCode() {
        return response.hashCode();
    }

    public int getStatus() {
        return response.getStatus();
    }

    @Override
    public boolean equals(Object obj) {
        return response.equals(obj);
    }

    public StatusType getStatusInfo() {
        return response.getStatusInfo();
    }

    public Object getEntity() {
        return response.getEntity();
    }

    public <T> T readEntity(Class<T> entityType) {
        return response.readEntity(entityType);
    }

    public <T> T readEntity(GenericType<T> entityType) {
        return response.readEntity(entityType);
    }

    @Override
    public String toString() {
        return response.toString();
    }

    public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
        return response.readEntity(entityType, annotations);
    }

    public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
        return response.readEntity(entityType, annotations);
    }

    public boolean hasEntity() {
        return response.hasEntity();
    }

    public boolean bufferEntity() {
        return response.bufferEntity();
    }

    @Override
    public void close() {
        response.close();
    }

    public MediaType getMediaType() {
        return response.getMediaType();
    }

    public Locale getLanguage() {
        return response.getLanguage();
    }

    public int getLength() {
        return response.getLength();
    }

    public Set<String> getAllowedMethods() {
        return response.getAllowedMethods();
    }

    public Map<String, NewCookie> getCookies() {
        return response.getCookies();
    }

    public EntityTag getEntityTag() {
        return response.getEntityTag();
    }

    public Date getDate() {
        return response.getDate();
    }

    public Date getLastModified() {
        return response.getLastModified();
    }

    public URI getLocation() {
        return response.getLocation();
    }

    public Set<Link> getLinks() {
        return response.getLinks();
    }

    public boolean hasLink(String relation) {
        return response.hasLink(relation);
    }

    public Link getLink(String relation) {
        return response.getLink(relation);
    }

    public Builder getLinkBuilder(String relation) {
        return response.getLinkBuilder(relation);
    }

    public MultivaluedMap<String, Object> getMetadata() {
        return response.getMetadata();
    }

    public MultivaluedMap<String, Object> getHeaders() {
        return response.getHeaders();
    }

    public MultivaluedMap<String, String> getStringHeaders() {
        return response.getStringHeaders();
    }

    public String getHeaderString(String name) {
        return response.getHeaderString(name);
    }
}
