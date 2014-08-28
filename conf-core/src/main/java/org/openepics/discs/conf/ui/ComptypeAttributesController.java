/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ui.common.EntityAttributeView;
import org.openepics.discs.conf.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Named
@ViewScoped
public class ComptypeAttributesController implements Serializable {

    @Inject private ComptypeEJB comptypeEJB;
    @Inject private ConfigurationEJB configurationEJB;
    private ComponentType compType;
    private Property property;
    private String propertyValue;
    private String tag;
    private String artifactName;
    private String artifactDescription;
    private boolean isArtifactInternal;
    private String artifactURI;
    private List<EntityAttributeView> attributes;
    private List<Property> filteredProperties;
    private byte[] importData;
    private String importFileName;

    @PostConstruct
    public void init() {
        final Long id = Long.parseLong(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("id"));
        compType = comptypeEJB.findComponentType(id);
        populateAttributesList();
        filterProperties();
    }

    private void populateAttributesList() {

        attributes = new ArrayList<>();
        for (ComptypePropertyValue prop : compType.getComptypePropertyList()) {
            attributes.add(new EntityAttributeView(prop, EntityType.COMPONENT_TYPE));
        }

        for (ComptypeArtifact art : compType.getComptypeArtifactList()) {
            attributes.add(new EntityAttributeView(art, EntityType.COMPONENT_TYPE));
        }

        for (Tag tag : compType.getTags()) {
            attributes.add(new EntityAttributeView(tag, EntityType.COMPONENT_TYPE));
        }

    }

    private void filterProperties() {
        filteredProperties = ImmutableList.copyOf(Collections2.filter(configurationEJB.findProperties(), new Predicate<Property>() {
            @Override
            public boolean apply(Property property) {
                final PropertyAssociation propertyAssociation = property.getAssociation();
                return propertyAssociation == PropertyAssociation.ALL || propertyAssociation == PropertyAssociation.TYPE || propertyAssociation == PropertyAssociation.TYPE_DEVICE || propertyAssociation == PropertyAssociation.TYPE_SLOT;
            }
        }));
    }

    public List<EntityAttributeView> getAttributes() {
        return attributes;
    }

    public void resetFields() {
        property = null;
        propertyValue = null;
        tag = null;
        artifactDescription = null;
        isArtifactInternal = false;
        artifactName = null;
        artifactURI = null;
    }

    public ComponentType getDeviceType() {
        return compType;
    }

    public void prepareForAdd() {
        resetFields();
    }

    public void addNewPropertyValue() {
        final ComptypePropertyValue compTypePropertyValue = new ComptypePropertyValue(false);
        compTypePropertyValue.setComponentType(compType);
        compTypePropertyValue.setProperty(property);
        compTypePropertyValue.setPropValue(propertyValue);
        comptypeEJB.addCompTypeProp(compTypePropertyValue);
        RequestContext.getCurrentInstance().update("deviceTypePropertiesManagerContainer");
        if (propertyValue == null) {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "New device type property definition has been created");
        } else {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "New device type property value has been created");
        }
        populateAttributesList();
    }

    public void addNewTag() {
        RequestContext.getCurrentInstance().update("deviceTypePropertiesManagerContainer");
        Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Failure", "Not yet implemented");
        populateAttributesList();
    }

    public String getImportFileName() { return importFileName; }

    public void prepareImportPopup() {
        importData = null;
        importFileName = null;
    }

    public void handleImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.importFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void setProperty(Property property) { this.property = property; }
    public Property getProperty() { return property; }

    public void setPropertyValue(String propertyValue) { this.propertyValue = propertyValue; }
    public String getPropertyValue() { return propertyValue; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public List<Property> getFilteredProperties() { return filteredProperties; }

    public String getArtifactName() { return artifactName; }
    public void setArtifactName(String artifactName) { this.artifactName = artifactName; }

    public String getArtifactDescription() { return artifactDescription; }
    public void setArtifactDescription(String artifactDescription) { this.artifactDescription = artifactDescription; }

    public boolean getIsArtifactInternal() { return isArtifactInternal; }
    public void setIsArtifactInternal(boolean isArtifactInternal) { this.isArtifactInternal = isArtifactInternal; }

    public String getArtifactURI() { return artifactURI; }
    public void setArtifactURI(String artifactURI) { this.artifactURI = artifactURI; }





}
