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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;
import org.openepics.discs.conf.ui.common.EntityAttributeView;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Named
@ViewScoped
public class ComptypeAttributesController extends AbstractAttributesController {

    @Inject private ComptypeEJB comptypeEJB;
    @Inject private PropertyEJB propertyEJB;
    private ComptypePropertyValue selectedComptypePropertyValue;
    private ComptypeArtifact selectedCompTypeArtifact;

    private ComponentType compType;

    @PostConstruct
    public void init() {
        final Long id = Long.parseLong(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("id"));
        compType = comptypeEJB.findById(id);
        populateAttributesList();
        filterProperties();
    }

    public void deviceTypeRedirect() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("device-types-manager.xhtml");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void populateAttributesList() {
        attributes = new ArrayList<>();
        compType = comptypeEJB.findById(this.compType.getId());
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

    @Override
    protected void filterProperties() {
        filteredProperties = ImmutableList.copyOf(Collections2.filter(propertyEJB.findAll(), new Predicate<Property>() {
            @Override
            public boolean apply(Property property) {
                final PropertyAssociation propertyAssociation = property.getAssociation();
                return propertyAssociation == PropertyAssociation.ALL || propertyAssociation == PropertyAssociation.TYPE || propertyAssociation == PropertyAssociation.TYPE_DEVICE || propertyAssociation == PropertyAssociation.TYPE_SLOT;
            }
        }));
    }

    @Override
    public void addNewPropertyValue() {
        final ComptypePropertyValue compTypePropertyValue = new ComptypePropertyValue(false);
        compTypePropertyValue.setComponentType(compType);
        compTypePropertyValue.setProperty(property);
        compTypePropertyValue.setPropValue(propertyValue);
        comptypeEJB.addChild(compTypePropertyValue);

        if (propertyValue == null) {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "New device type property definition has been created");
        } else {
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "New device type property value has been created");
        }
        populateAttributesList();
        RequestContext.getCurrentInstance().update("deviceTypePropertiesManagerContainer");
    }

    public ComponentType getDeviceType() {
        return compType;
    }

    @Override
    public void addNewTag() {
        Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Failure", "Not yet implemented");
        populateAttributesList();
        RequestContext.getCurrentInstance().update("deviceTypePropertiesManagerContainer");
    }

    @Override
    public void addNewArtifact() throws IOException {
        if (importData != null) {
            artifactURI = blobStore.storeFile(new ByteArrayInputStream(importData));
        }

        final ComptypeArtifact compTypeArtifact = new ComptypeArtifact(importData != null ? importFileName : artifactURI, isArtifactInternal, artifactDescription, artifactURI);
        compTypeArtifact.setComponentType(compType);
        comptypeEJB.addChild(compTypeArtifact);
        RequestContext.getCurrentInstance().update("deviceTypePropertiesManagerContainer");
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "New artifact has been created");
        populateAttributesList();
    }

    @Override
    public void deleteAttribute() {
        if (selectedAttribute.getEntity() instanceof ComptypePropertyValue) {
            final ComptypePropertyValue compTypePropValue = (ComptypePropertyValue) selectedAttribute.getEntity();
            comptypeEJB.deleteChild(compTypePropValue);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Device type property has been deleted");
        } else if (selectedAttribute.getEntity() instanceof ComptypeArtifact) {
            final ComptypeArtifact compTypeArtifact = (ComptypeArtifact) selectedAttribute.getEntity();
            comptypeEJB.deleteChild(compTypeArtifact);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Device type artifact has been deleted");
        } else if (selectedAttribute.getEntity() instanceof Tag) {
            final Tag tag = (Tag) selectedAttribute.getEntity();
            //TODO yet to come
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Failure", "Not yet implemented");
        } else {
            throw new UnhandledCaseException();
        }
        populateAttributesList();
    }

    @Override
    protected void preparedModifyPropertyPopUp() {
        if (selectedAttribute.getEntity() instanceof ComptypePropertyValue) {
            this.selectedComptypePropertyValue = (ComptypePropertyValue) selectedAttribute.getEntity();
            this.property = selectedComptypePropertyValue.getProperty();
            this.propertyValue = selectedComptypePropertyValue.getPropValue();
            RequestContext.getCurrentInstance().update("modifyDeviceTypePropertyForm:modifyDeviceTypeProperty");
            RequestContext.getCurrentInstance().execute("PF('modifyDeviceTypeProperty').show()");
        } else if (selectedAttribute.getEntity() instanceof ComptypeArtifact) {
            importData = null;
            importFileName = null;
            this.selectedCompTypeArtifact = (ComptypeArtifact) selectedAttribute.getEntity();
            this.artifactDescription = selectedCompTypeArtifact.getDescription();
            this.isArtifactInternal = selectedCompTypeArtifact.isInternal();
            this.artifactURI = selectedCompTypeArtifact.getUri();
            this.isArtifactBeingModified = true;
            RequestContext.getCurrentInstance().update("modifyDeviceTypeArtifactForm:modifyDeviceTypeArtifact");
            RequestContext.getCurrentInstance().execute("PF('modifyDeviceTypeArtifact').show()");
        } else {
            throw new UnhandledCaseException();
        }

    }

    @Override
    public void modifyPropertyValue() {
        selectedComptypePropertyValue.setProperty(property);
        selectedComptypePropertyValue.setPropValue(propertyValue);
        comptypeEJB.saveChild(selectedComptypePropertyValue);
        RequestContext.getCurrentInstance().update("deviceTypePropertiesManagerContainer");
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Device type property value has been modified");
        populateAttributesList();
    }

    @Override
    public void modifyArtifact() {
        selectedCompTypeArtifact.setDescription(artifactDescription);
        selectedCompTypeArtifact.setUri(artifactURI);
        selectedCompTypeArtifact.setName(artifactURI);
        comptypeEJB.saveChild(selectedCompTypeArtifact);
        RequestContext.getCurrentInstance().update("deviceTypePropertiesManagerContainer");
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Device type artifact has been modified");
        populateAttributesList();
    }

    @Override
    public StreamedContent getDownloadFile() throws FileNotFoundException {
        final ComptypeArtifact compTypeArtifact = (ComptypeArtifact) selectedAttribute.getEntity();

        final String fileType;
        if (compTypeArtifact.getName().split(".").length > 1) {
            fileType = compTypeArtifact.getName().split(".")[compTypeArtifact.getName().split(".").length - 1];
        } else {
            fileType = "";
        }

        return new DefaultStreamedContent(new FileInputStream(blobStore.getBlobStoreRoot() + "/" + compTypeArtifact.getUri()), fileType, compTypeArtifact.getName());
    }


}
