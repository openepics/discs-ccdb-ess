/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.openepics.discs.conf.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ent.values.EnumValue;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.util.Conversion;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.EntityAttributeViewKind;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 * Controller bean for manipulation of {@link ComponentType} attributes
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Named
@ViewScoped
public class ComptypeAttributesController extends AbstractAttributesController<ComptypePropertyValue, ComptypeArtifact> {
    private static final long serialVersionUID = 1156974438243970794L;

    private static final Logger LOGGER = Logger.getLogger(ComptypeAttributesController.class.getCanonicalName());

    @Inject private transient ComptypeEJB comptypeEJB;
    @Inject private transient PropertyEJB propertyEJB;
    @Inject private transient SlotEJB slotEJB;
    @Inject private transient DeviceEJB deviceEJB;

    private ComponentType compType;
    private Map<Long, Value> propertyValues;
    private SimpleDateFormat defaultFormatter;

    @Override
    @PostConstruct
    public void init() {
        try {
            super.init();
            setArtifactClass(ComptypeArtifact.class);
            setPropertyValueClass(ComptypePropertyValue.class);
            setDao(comptypeEJB);
            defaultFormatter = new SimpleDateFormat(Conversion.DATE_ONLY_FORMAT);
        } catch(Exception e) {
            throw new UIException("Device type details display initialization fialed: " + e.getMessage(), e);
        }
    }

    @Override
    public void addNewPropertyValue() {
        super.addNewPropertyValue();

        if (propertyValueInstance.isPropertyDefinition()) {
            if (propertyValueInstance.isDefinitionTargetSlot()) {
                for (Slot slot : slotEJB.findByComponentType(compType)) {
                    if (canAddProperty(slot.getSlotPropertyList(), propertyValueInstance.getProperty())) {
                        final SlotPropertyValue newSlotProperty = new SlotPropertyValue();
                        newSlotProperty.setProperty(propertyValueInstance.getProperty());
                        newSlotProperty.setSlot(slot);
                        slotEJB.addChild(newSlotProperty);
                    } else {
                        LOGGER.log(Level.FINE, "Type: " + compType.getName() + "; Slot: " + slot.getName()
                                + ";  Trying to add the same property value again: "
                                + propertyValueInstance.getProperty().getName());
                    }
                }
            }

            if (propertyValueInstance.isDefinitionTargetDevice()) {
                for (Device device : deviceEJB.findDevicesByComponentType(compType)) {
                    if (canAddProperty(device.getDevicePropertyList(), propertyValueInstance.getProperty())) {
                        final DevicePropertyValue newDeviceProperty = new DevicePropertyValue();
                        newDeviceProperty.setProperty(propertyValueInstance.getProperty());
                        newDeviceProperty.setDevice(device);
                        deviceEJB.addChild(newDeviceProperty);
                    } else {
                        LOGGER.log(Level.FINE, "Type: " + compType.getName() + "; Device: " + device.getSerialNumber()
                                + ";  Trying to add the same property value again: "
                                + propertyValueInstance.getProperty().getName());
                    }
                }
            }
        }
    }

    /** Checks whether it is safe to add a new property (definition) to the entity.
     * @param entityProperties the list of properties the entity already has
     * @param propertyToAdd the property we want to add
     * @return <code>true</code> if the property is safe to add, <code>false</code> otherwise (it already exists).
     */
    private <T extends PropertyValue> boolean canAddProperty(final List<T> entityProperties,
                                                                        final Property propertyToAdd) {
        for (T entityProperty : entityProperties) {
            if (entityProperty.getProperty().equals(propertyToAdd)) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected void deletePropertyValue() {
        final ComptypePropertyValue propValue = (ComptypePropertyValue) selectedAttribute.getEntity();
        if (propValue.isPropertyDefinition()) {
            if (propValue.isDefinitionTargetSlot()) {
                for (Slot slot : slotEJB.findByComponentType(compType)) {
                    removeUndefinedProperty(slot.getSlotPropertyList(), propValue.getProperty(), slotEJB);
                }
            }

            if (propValue.isDefinitionTargetDevice()) {
                for (Device device : deviceEJB.findDevicesByComponentType(compType)) {
                    removeUndefinedProperty(device.getDevicePropertyList(), propValue.getProperty(), deviceEJB);
                }
            }
        }
        super.deletePropertyValue();
    }

    private <T extends PropertyValue> void removeUndefinedProperty(final List<T> entityProperties,
                                                final Property propertyToDelete, final DAO<?> daoEJB) {
        T propValueToDelete = null;
        for (T entityPropValue : entityProperties) {
            if (entityPropValue.getProperty().equals(propertyToDelete)) {
                if (entityPropValue.getPropValue() == null) {
                    // value not defined, safe to delete
                    propValueToDelete = entityPropValue;
                }
                // attribute found
                break;
            }
        }
        if (propValueToDelete != null) {
            daoEJB.deleteChild(propValueToDelete);
        }
    }

    @Override
    protected void populateAttributesList() {
        attributes = new ArrayList<>();
        // refresh the component type from database. This refreshes all related collections as well.
        compType = comptypeEJB.findById(this.compType.getId());

        for (ComptypePropertyValue prop : compType.getComptypePropertyList()) {
            attributes.add(new EntityAttributeView(prop));
        }

        for (ComptypeArtifact art : compType.getComptypeArtifactList()) {
            attributes.add(new EntityAttributeView(art, EntityAttributeViewKind.DEVICE_TYPE_ARTIFACT));
        }

        for (Tag tagAttr : compType.getTags()) {
            attributes.add(new EntityAttributeView(tagAttr, EntityAttributeViewKind.DEVICE_TYPE_TAG));
        }
    }

    @Override
    protected void filterProperties() {
        List<Property> propertyCandidates = propertyEJB.findAllOrderedByName();

        for (ComptypePropertyValue comptypePropertyValue : compType.getComptypePropertyList()) {
            final Property currentProperty = comptypePropertyValue.getProperty();
            // in modify dialog the 'property' is set to the property of the current value
            if (!currentProperty.equals(property)) {
                propertyCandidates.remove(currentProperty);
            }
        }

        filteredProperties = propertyCandidates;
    }

    /**
     * Returns {@link ComponentType} for which attributes are being manipulated
     * @return the {@link ComponentType}
     */
    public ComponentType getDeviceType() {
        return compType;
    }

    public void prepareDeviceType(ComponentType deviceType) {
        compType = deviceType;
        selectedAttribute = null;
        populateAttributesList();
        filterProperties();
    }

    public void clearDeviceType() {
        compType = null;
        attributes = null;
        filteredProperties = null;
        selectedAttribute = null;
    }

    @Override
    public void saveNewName() {
        // TODO ready for removal
    }

    @Override
    protected void setPropertyValueParent(ComptypePropertyValue child) {
        child.setComponentType(compType);
    }

    @Override
    protected void setArtifactParent(ComptypeArtifact child) {
        child.setComponentType(compType);
    }

    @Override
    protected void setTagParent(Tag tag) {
        final Set<Tag> existingTags = compType.getTags();
        if (!existingTags.contains(tag)) {
            existingTags.add(tag);
            comptypeEJB.save(compType);
        }
    }

    @Override
    protected void deleteTagFromParent(Tag tag) {
        compType.getTags().remove(tag);
        comptypeEJB.save(compType);
    }

    @Override
    public void prepareForPropertyValueAdd() {
        isPropertyDefinition = false;
        super.prepareForPropertyValueAdd();
        prepareDefaultPropertyValues();
    }

    private void prepareDefaultPropertyValues() {
        // TODO re-think this. If we leave this unitialized until the user sets a value, we can check whether something is missing
        propertyValues = new HashMap<Long, Value>(filteredProperties.size());
        final Date today = new Date();
        for (Property prop : filteredProperties) {
            Value valueToStore;
            switch (Conversion.getBuiltInDataType(prop.getDataType())) {
            case DOUBLE:
            case INTEGER:
            case INT_VECTOR:
            case DBL_VECTOR:
            case DBL_TABLE:
                valueToStore = Conversion.stringToValue("0", prop.getDataType());
                break;
            case STRING:
            case STRING_LIST:
                valueToStore = Conversion.stringToValue("", prop.getDataType());
                break;
            case TIMESTAMP:
                valueToStore = Conversion.stringToValue(defaultFormatter.format(today), prop.getDataType());
                break;
            case USER_DEFINED_ENUM:
                valueToStore = new EnumValue("");
                break;
            default:
                throw new UnhandledCaseException();
            }
            propertyValues.put(prop.getId(), valueToStore);
        }
    }

    @Override
    protected void populateParentTags() {
        // Nothing to do since component types don't inherit anything
    }

    @Override
    public void modifyBuiltInProperty() {
        // no built in property to modify
    }

    /**
     * Prepares the data for slot property (definition) creation
     */
    public void prepareForSlotPropertyAdd() {
        definitionTarget = AbstractAttributesController.DefinitionTarget.SLOT;
        isPropertyDefinition = true;
        super.prepareForPropertyValueAdd();
    }

    /**
     * Prepares the data for device property (definition) creation
     */
    public void prepareForDevicePropertyAdd() {
        definitionTarget = AbstractAttributesController.DefinitionTarget.DEVICE;
        isPropertyDefinition = true;
        super.prepareForPropertyValueAdd();
    }

    @Override
    public boolean canEdit(EntityAttributeView attribute) {
        final EntityAttributeViewKind attributeKind = attribute.getKind();
        return attributeKind != EntityAttributeViewKind.INSTALL_SLOT_PROPERTY
                && attributeKind != EntityAttributeViewKind.DEVICE_PROPERTY
                && attributeKind != EntityAttributeViewKind.DEVICE_TYPE_TAG
                && attributeKind != EntityAttributeViewKind.TAG;
    }

    @Override
    public boolean canDelete(EntityAttributeView attribute) {
        return attribute.getKind() == EntityAttributeViewKind.DEVICE_TYPE_ARTIFACT
                || attribute.getKind() == EntityAttributeViewKind.DEVICE_TYPE_TAG
                || super.canDelete(attribute);
    }

    public boolean canEdit(Property prop) {
        return getSelectedProperties() != null && getSelectedProperties().contains(prop);
    }

    // TODO remove
    public void propertyRowSelect(SelectEvent event) {
        final List<Property> uiProps = getSelectionPropertiesFiltered() == null ? filteredProperties : getSelectionPropertiesFiltered();
        int lineIndex = uiProps.indexOf(event.getObject());
        RequestContext.getCurrentInstance().update("addPropertyValueForm:propertySelect:" + lineIndex + ":valuePanel");
    }

    // TODO remove
    public void propertyRowUnselect(UnselectEvent event) {
        final List<Property> uiProps = getSelectionPropertiesFiltered() == null ? filteredProperties : getSelectionPropertiesFiltered();
        int lineIndex = uiProps.indexOf(event.getObject());
        RequestContext.getCurrentInstance().update("addPropertyValueForm:propertySelect:" + lineIndex + ":valuePanel");
    }

    public String displayPropertyValue(Property prop) {
        final Value val = propertyValues.get(prop.getId());
        return val == null ? "No default value" : Conversion.valueToString(propertyValues.get(prop.getId()));
    }
}
