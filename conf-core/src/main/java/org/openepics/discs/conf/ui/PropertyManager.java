/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.dl.PropertiesLoaderQualifier;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.util.Utility;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import com.google.common.io.ByteStreams;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Named
@ViewScoped
public class PropertyManager implements Serializable {
    @Inject private PropertyEJB propertyEJB;
    // @Inject private AuditRecordEJB auditRecordEJB; TODO merge conflict piece

    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject @PropertiesLoaderQualifier private DataLoader propertiesDataLoader;

    private List<Property> properties;
    private List<Property> sortedProperties;
    private List<Property> filteredProperties;

    private byte[] importData;
    private String importFileName;
    private DataLoaderResult loaderResult;

    private String name;
    private String description;
    private DataType dataType;
    private Unit unit;
    private String[] association = new String[] {};;
    private Property selectedProperty;
    private boolean unitComboEnabled;
    private boolean isPropertyUsed;

    /**
     * Creates a new instance of PropertyManager
     */
    public PropertyManager() {
    }

    private void init() {
        properties = propertyEJB.findAll();
        selectedProperty = null;
        resetFields();
    }

    public void onAdd() {
        final Property propertyToAdd = new Property(name, description);
        setAssociationOnProperty(propertyToAdd);
        propertyToAdd.setDataType(dataType);
        propertyToAdd.setUnit(unit);
        try {
            propertyEJB.add(propertyToAdd);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "New property has been created");
        } finally {
            init();
        }
    }

    public void onModify() {
        selectedProperty.setName(name);
        selectedProperty.setDescription(description);
        selectedProperty.setDataType(dataType);
        setAssociationOnProperty(selectedProperty);
        selectedProperty.setUnit(unit);

        try {
            propertyEJB.save(selectedProperty);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Property was modified");
        } finally {
            init();
        }
    }

    public void prepareAddPopup() {
        resetFields();
        isPropertyUsed = false;
        RequestContext.getCurrentInstance().update("addPropertyForm:addProperty");
    }

    public void prepareModifyPopup() {
        name = selectedProperty.getName();
        description = selectedProperty.getDescription();
        dataType = selectedProperty.getDataType();
        unit = selectedProperty.getUnit();
        association = constructSetAssociations(selectedProperty);
        isPropertyUsed = propertyEJB.isPropertyUsed(selectedProperty);
        setIsUnitComboEnabled();
        RequestContext.getCurrentInstance().update("modifyPropertyForm:modifyProperty");
    }

    public void onDelete() {
        try {
            propertyEJB.delete(selectedProperty);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Success", "Property was deleted");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Deletion failed", "The property could not be deleted because it is used.");
            } else {
                throw e;
            }
        } finally {
            init();
        }
    }

    public List<Property> getSortedProperties() {
        return sortedProperties;
    }

    public void setSortedOProperties(List<Property> sortedObjects) {
        this.sortedProperties = sortedObjects;
    }

    public List<Property> getFilteredProperties() {
        return filteredProperties;
    }

    public void setFilteredProperties(List<Property> filteredObjects) {
        this.filteredProperties = filteredObjects;
    }

    public List<Property> getProperties() {
        if (properties == null) properties = propertyEJB.findAll();
        return properties;
    }

    public String getImportFileName() {
        return importFileName;
    }

    public void doImport() {
        final InputStream inputStream = new ByteArrayInputStream(importData);
        loaderResult = dataLoaderHandler.loadData(inputStream, propertiesDataLoader);
    }

    public void prepareImportPopup() {
        importData = null;
        importFileName = null;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public DataType getDataType() {
        return dataType;
    }
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Unit getUnit() {
        return unit;
    }
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String[] getPropertyAssociation() {
        return association;
    }
    public void setPropertyAssociation(String[] association) {
        this.association = association;
    }

    private String[] constructSetAssociations(Property property) {
        ArrayList<String> associations = new ArrayList<>();
        if (property.isTypeAssociation()) {
            associations.add(PropertyAssociation.TYPE);
        }
        if (property.isSlotAssociation()) {
            associations.add(PropertyAssociation.SLOT);
        }
        if (property.isDeviceAssociation()) {
            associations.add(PropertyAssociation.DEVICE);
        }
        if (property.isAlignmentAssociation()) {
            associations.add(PropertyAssociation.ALIGNMENT);
        }
        return associations.toArray(new String[] {});
    }

    private void setAssociationOnProperty(Property property) {
        // reset all data
        property.setNoneAssociation();

        if (association == null || association.length == 0) {
            throw new RuntimeException("Property association target not selected.");
        }

        for (String assoc : association) {
            switch (assoc) {
            case PropertyAssociation.ALIGNMENT:
                property.setAlignmentAssociation(true);
                break;
            case PropertyAssociation.DEVICE:
                property.setDeviceAssociation(true);
                break;
            case PropertyAssociation.SLOT:
                property.setSlotAssociation(true);
                break;
            case PropertyAssociation.TYPE:
                property.setTypeAssociation(true);
                break;
            default:
                throw new RuntimeException("Unknow property associaton target.");
            }
        }
    }

    public void setIsUnitComboEnabled() {
        if (dataType.getName().equalsIgnoreCase("integer") || dataType.getName().equalsIgnoreCase("double")
                || dataType.getName().equalsIgnoreCase("integers vector")
                || dataType.getName().equalsIgnoreCase("doubles vector" )
                || dataType.getName().equalsIgnoreCase("doubles table")) {
            unitComboEnabled = true;
        } else {
            unitComboEnabled = false;
        }
    }

    public boolean isUnitComboEnabled() {
        return unitComboEnabled;
    }

    public Property getSelectedProperty() {
        return selectedProperty;
    }
    public void setSelectedProperty(Property selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    public Property getSelectedPropertyToModify() {
        return selectedProperty;
    }
    public void setSelectedPropertyToModify(Property selectedProperty) {
        this.selectedProperty = selectedProperty;
        prepareModifyPopup();
    }

    public DataLoaderResult getLoaderResult() {
        return loaderResult;
    }

    public void handleImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.importFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void resetFields() {
        name = null;
        description = null;
        dataType = null;
        unit = null;
        association = null;
        unitComboEnabled = true;
    }

    public boolean isPropertyUsed() {
        return isPropertyUsed;
    }
}
