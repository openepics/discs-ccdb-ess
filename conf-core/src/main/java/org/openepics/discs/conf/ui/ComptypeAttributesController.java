/*
 * Copyright (c) 2016 European Spallation Source
 * Copyright (c) 2016 Cosylab d.d.
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

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
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
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.util.Conversion;
import org.openepics.discs.conf.util.PropertyValueNotUniqueException;
import org.openepics.discs.conf.util.PropertyValueUIElement;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.views.ComponentTypeView;
import org.openepics.discs.conf.views.EntityAttrArtifactView;
import org.openepics.discs.conf.views.EntityAttrPropertyValueView;
import org.openepics.discs.conf.views.EntityAttrTagView;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.EntityAttributeViewKind;
import org.openepics.discs.conf.views.MultiPropertyValueView;
import org.primefaces.event.CellEditEvent;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Named
@ViewScoped
public class ComptypeAttributesController
        extends AbstractAttributesController<ComponentType, ComptypePropertyValue, ComptypeArtifact>
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ComptypeAttributesController.class.getCanonicalName());

    private static enum DefinitionTarget { SLOT, DEVICE }

    @Inject private ComptypeEJB comptypeEJB;
    @Inject private PropertyEJB propertyEJB;
    @Inject private SlotEJB slotEJB;
    @Inject private DeviceEJB deviceEJB;
    private transient ComponentTypeManager componentTypeManager;

    private List<Property> filteredProperties;
    private List<Property> selectedProperties;
    private List<Property> selectionPropertiesFiltered;
    private boolean isPropertyDefinition;
    private DefinitionTarget definitionTarget;

    private transient List<MultiPropertyValueView> filteredPropertyValues;
    private transient List<MultiPropertyValueView> selectedPropertyValues;
    private transient List<MultiPropertyValueView> selectionPropertyValuesFiltered;
    private boolean selectAllRows;

    /** Java EE post construct life-cycle method. */
    @PostConstruct
    public void init() {
        setDao(comptypeEJB);
        resetFields();
    }

    protected void setUIParent(ComponentTypeManager componentTypeManager) {
        this.componentTypeManager = componentTypeManager;
    }

    @Override
    public void clearRelatedAttributeInformation() {
        super.clearRelatedAttributeInformation();
        filteredProperties = null;
        selectedProperties = null;
    }

    @Override
    protected void deletePropertyValue(final ComptypePropertyValue propValueToDelete) {
        if (propValueToDelete.isPropertyDefinition()) {
            if (propValueToDelete.isDefinitionTargetSlot()) {
                for (final Slot slot : slotEJB.findByComponentType(componentTypeManager.getSelectedComponent()
                        .getComponentType())) {
                    removeUndefinedProperty(slot.getSlotPropertyList(), propValueToDelete.getProperty(), slotEJB);
                }
            }

            if (propValueToDelete.isDefinitionTargetDevice()) {
                for (final Device device : deviceEJB.findDevicesByComponentType(componentTypeManager.getSelectedComponent()
                        .getComponentType())) {
                    removeUndefinedProperty(device.getDevicePropertyList(), propValueToDelete.getProperty(), deviceEJB);
                }
            }
        }
        super.deletePropertyValue(propValueToDelete);
    }

    private <T extends PropertyValue> void removeUndefinedProperty(final List<T> entityProperties,
            final Property propertyToDelete, final DAO<?> daoEJB) {
        T propValueToDelete = null;
        for (final T entityPropValue : entityProperties) {
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
        Preconditions.checkNotNull(componentTypeManager.getSelectedDeviceTypes());
        attributes = Lists.newArrayList();

        for (final ComponentTypeView selectedMember : componentTypeManager.getSelectedDeviceTypes()) {
            // refresh the component type from database. This refreshes all related collections as well.
            final ComponentType freshComponentType = comptypeEJB.findById(selectedMember.getId());

            for (final ComptypePropertyValue prop : freshComponentType.getComptypePropertyList()) {
                attributes.add(new EntityAttrPropertyValueView<ComponentType>(prop, freshComponentType));
            }

            for (final ComptypeArtifact art : freshComponentType.getComptypeArtifactList()) {
                attributes.add(new EntityAttrArtifactView<ComponentType>(art, freshComponentType));
            }

            for (final Tag tagAttr : freshComponentType.getTags()) {
                attributes.add(new EntityAttrTagView<ComponentType>(tagAttr, freshComponentType));
            }
        }
    }

    @Override
    protected void filterProperties() {
        if ((componentTypeManager.getSelectedDeviceTypes() == null)
                || (componentTypeManager.getSelectedDeviceTypes().size() != 1)) {
            filteredProperties = null;
            return;
        }

        final List<Property> propertyCandidates = propertyEJB.findAllOrderedByName();
        final Property dialogProperty = getDialogAttrPropertyValue() != null ? getDialogAttrPropertyValue().getProperty() : null;

        for (ComponentTypeView compType : componentTypeManager.getSelectedDeviceTypes()) {
            for (final ComptypePropertyValue comptypePropertyValue : compType.getComponentType().getComptypePropertyList()) {
                final Property currentProperty = comptypePropertyValue.getProperty();
                if (!currentProperty.equals(dialogProperty)) {
                    propertyCandidates.remove(currentProperty);
                }
            }
        }
        filteredProperties = propertyCandidates;
    }

    @Override
    public void resetFields() {
        super.resetFields();
        filteredProperties = null;
        selectedProperties = null;
        selectionPropertiesFiltered = null;
    }

    @Override
    public boolean canEdit(EntityAttributeView<ComponentType> attribute) {
        final EntityAttributeViewKind attributeKind = attribute.getKind();
        return attributeKind != EntityAttributeViewKind.INSTALL_SLOT_PROPERTY
                && attributeKind != EntityAttributeViewKind.DEVICE_PROPERTY
                && attributeKind != EntityAttributeViewKind.DEVICE_TYPE_TAG;
    }

    /** Prepares the data for device type property creation */
    public void prepareForPropertyValueAdd() {
        filterProperties();
        filteredPropertyValues = filteredProperties.stream().map(MultiPropertyValueView::new).
                                    collect(Collectors.toList());
        selectedPropertyValues = Lists.newArrayList();
        selectionPropertyValuesFiltered = null;
        selectAllRows = false;
    }

    /** Prepares the data for slot property (definition) creation */
    public void prepareForSlotPropertyAdd() {
        definitionTarget = DefinitionTarget.SLOT;
        isPropertyDefinition = true;
        filterProperties();
    }

    /** Prepares the data for device property (definition) creation */
    public void prepareForDevicePropertyAdd() {
        definitionTarget = DefinitionTarget.DEVICE;
        isPropertyDefinition = true;
        filterProperties();
    }

    /** The event handler for when user clicks on the check-box in the "Add property values" dialog.
     * @param prop the property value to handle the event for
     */
    public void rowSelectListener(final MultiPropertyValueView prop) {
        if (prop.isSelected()) {
            selectedPropertyValues.add(prop);
        } else {
            selectedPropertyValues.remove(prop);
        }
    }

    /** The function to handle the state of the "Select all" checkbox after the filter change */
    public void updateToggle() {
        final List<MultiPropertyValueView> pvList = selectionPropertyValuesFiltered == null
                                                        ? filteredPropertyValues : selectionPropertyValuesFiltered;
        if (pvList.isEmpty()) {
            selectAllRows = false;
            return;
        }
        for (final MultiPropertyValueView pv : pvList) {
            if (!pv.isSelected()) {
                selectAllRows = false;
                return;
            }
        }
        selectAllRows = true;
    }

    /** The event handler for toggling selection of all property values */
    public void handleToggleAll() {
        final List<MultiPropertyValueView> pvList = selectionPropertyValuesFiltered == null
                ? filteredPropertyValues : selectionPropertyValuesFiltered;
        if (pvList.isEmpty()) return;
        if (selectAllRows) {
            selectAllFiltered(pvList);
        } else {
            unselectAllFiltered(pvList);
        }
    }

    private void selectAllFiltered(final List<MultiPropertyValueView> pvList) {
        for (final MultiPropertyValueView pv : pvList) {
            if (!pv.isSelected()) {
                pv.setSelected(true);
                selectedPropertyValues.add(pv);
            }
        }
    }

    private void unselectAllFiltered(final List<MultiPropertyValueView> pvList) {
        for (final MultiPropertyValueView pv : pvList) {
            if (pv.isSelected()) {
                pv.setSelected(false);
                selectedPropertyValues.remove(pv);
            }
        }
    }

    /** This method handled the value once the users is done putting it in. This method actually performs the
     * input validation.
     * @param event the event
     */
    public void onEditCell(CellEditEvent event) {
        final Object newValue = event.getNewValue();
        final Object oldValue = event.getOldValue();

        if (newValue != null && !newValue.equals(oldValue)) {
            final MultiPropertyValueView editedPropVal = selectionPropertyValuesFiltered == null
                                                            ? filteredPropertyValues.get(event.getRowIndex())
                                                            : selectionPropertyValuesFiltered.get(event.getRowIndex());
            final DataType dataType = editedPropVal.getDataType();
            final String newValueStr = getEditEventValue(newValue, editedPropVal.getPropertyValueUIElement());
            try {
                switch (editedPropVal.getPropertyValueUIElement()) {
                    case INPUT:
                        EntityAttrPropertyValueView.validateSingleLine(newValueStr, dataType);
                        break;
                    case TEXT_AREA:
                        EntityAttrPropertyValueView.validateMultiLine(newValueStr, dataType);
                        break;
                    case SELECT_ONE_MENU:
                        if (Strings.isNullOrEmpty(newValueStr)) {
                            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    UiUtility.MESSAGE_SUMMARY_ERROR, "A value must be selected."));
                        }
                        break;
                    case NONE:
                    default:
                        throw new UnhandledCaseException();
                }
                final Value val = Conversion.stringToValue(newValueStr, dataType);
                comptypeEJB.checkPropertyValueUnique(createPropertyValue(editedPropVal.getProperty(), val));
                editedPropVal.setValue(val);
            } catch (ValidatorException e) {
                editedPropVal.setUiValue(oldValue == null ? null : getEditEventValue(oldValue, null));
                FacesContext.getCurrentInstance().addMessage("addPropertyValueForm:inputValidationFail", e.getFacesMessage());
                FacesContext.getCurrentInstance().validationFailed();
            } catch (EJBException e) {
                if (UiUtility.causedBySpecifiedExceptionClass(e, PropertyValueNotUniqueException.class)) {
                    FacesContext.getCurrentInstance().addMessage("addPropertyValueForm:inputValidationFail",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                                    "Value is not unique."));
                    FacesContext.getCurrentInstance().validationFailed();
                } else {
                    throw e;
                }
            }
        }
    }

    private String getEditEventValue(final Object val, final PropertyValueUIElement propValueUIElement) {
        if ((val == null) || (val instanceof String)){
            return (String)val;
        }
        if (val instanceof List<?>) {
            final List<?> valList = (List<?>)val;
            if (propValueUIElement == null) {
                for (final Object v : valList) {
                    if (v != null) {
                        return v.toString();
                    }
                }
                return null;
            } else {
                switch (propValueUIElement) {
                    case INPUT:
                        return valList.get(0).toString();
                    case TEXT_AREA:
                        return valList.get(1).toString();
                    case SELECT_ONE_MENU:
                        return valList.get(2).toString();
                    case NONE:
                    default:
                        throw new UnhandledCaseException();
                }
            }
        }
        throw new RuntimeException("MultiPropertyValue: UI string value cannot be extracted.");
    }

    /**
     * A method that adds either installation slot or device instance properties. It adds the definition to the device
     * type and property values to already existing installation slots or device instances.
     */
    public void addNewPropertyValueDefs() {
        for (Property selectedProperty : selectedProperties) {
            final ComptypePropertyValue newPropertyValueInstance = newPropertyValue();
            newPropertyValueInstance.setInRepository(false);
            newPropertyValueInstance.setProperty(selectedProperty);
            newPropertyValueInstance.setPropValue(null);
            newPropertyValueInstance.setPropertiesParent(getSelectedEntity());

            if ((newPropertyValueInstance instanceof ComptypePropertyValue) && isPropertyDefinition) {
                final ComptypePropertyValue ctPropValueInstance = newPropertyValueInstance;
                ctPropValueInstance.setPropertyDefinition(true);
                if (definitionTarget == DefinitionTarget.SLOT) {
                    ctPropValueInstance.setDefinitionTargetSlot(true);
                } else {
                    ctPropValueInstance.setDefinitionTargetDevice(true);
                }
            }
            comptypeEJB.addChild(newPropertyValueInstance);
            componentTypeManager.refreshSelectedComponent();
            addPropertyValueBasedOnDef(newPropertyValueInstance);
        }
        resetFields();
        populateAttributesList();
    }

    private void addPropertyValueBasedOnDef(ComptypePropertyValue definition) {
        if (definition.isDefinitionTargetSlot()) {
            for (final Slot slot : slotEJB.findByComponentType(componentTypeManager.getSelectedComponent().getComponentType())) {
                if (canAddProperty(slot.getSlotPropertyList(), definition.getProperty())) {
                    final SlotPropertyValue newSlotProperty = new SlotPropertyValue();
                    newSlotProperty.setProperty(definition.getProperty());
                    newSlotProperty.setSlot(slot);
                    slotEJB.addChild(newSlotProperty);
                } else {
                    LOGGER.log(Level.FINE, "Type: " + componentTypeManager.getSelectedComponent().getName()
                            + "; Slot: " + slot.getName()
                            + ";  Trying to add the same property value again: "
                            + definition.getProperty().getName());
                }
            }
        }

        if (definition.isDefinitionTargetDevice()) {
            for (final Device device : deviceEJB.findDevicesByComponentType(componentTypeManager.getSelectedComponent().
                                                                                getComponentType())) {
                if (canAddProperty(device.getDevicePropertyList(), definition.getProperty())) {
                    final DevicePropertyValue newDeviceProperty = new DevicePropertyValue();
                    newDeviceProperty.setProperty(definition.getProperty());
                    newDeviceProperty.setDevice(device);
                    deviceEJB.addChild(newDeviceProperty);
                } else {
                    LOGGER.log(Level.FINE, "Type: " + componentTypeManager.getSelectedComponent().getName()
                            + "; Device: " + device.getSerialNumber()
                            + ";  Trying to add the same property value again: "
                            + definition.getProperty().getName());
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
        for (final T entityProperty : entityProperties) {
            if (entityProperty.getProperty().equals(propertyToAdd)) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected boolean canDelete(EntityAttributeView<ComponentType> attribute) {
        return true;
    }

    @Override
    protected ComponentType getSelectedEntity() {
        final ComponentTypeView selectedComponent = componentTypeManager.getSelectedComponent();
        if (selectedComponent != null) {
            final Long typeId = selectedComponent.getId();
            // for "Add new device type" the selectedComponent will contain a ComponentType which has not been persisted
            if (typeId != null)
                return comptypeEJB.findById(selectedComponent.getId());
            else
                return selectedComponent.getComponentType();
        }
        throw new IllegalArgumentException("No device type selected");
    }

    @Override
    protected ComptypePropertyValue newPropertyValue() {
        return new ComptypePropertyValue();
    }

    @Override
    protected ComptypeArtifact newArtifact() {
        return new ComptypeArtifact();
    }

    /** This method returns a String representation of the property value.
     * @param prop the value of the property to show value for
     * @return the string representation
     */
    public String displayPropertyValue(MultiPropertyValueView prop) {
        final Value val = prop.getValue();
        return val == null ? "<Please define>" : Conversion.valueToString(val);
    }

    private ComptypePropertyValue createPropertyValue(final Property prop, final Value value) {
        final ComptypePropertyValue pv = new ComptypePropertyValue();
        pv.setComponentType(getSelectedEntity());
        pv.setProperty(prop);
        pv.setPropValue(value);
        return pv;
    }

    /** The save action for adding multiple property values to a device type. */
    public void saveMultiplePropertyValues() {
        // check if all values are set, because we want to save all in one batch
        for (final MultiPropertyValueView pv : selectedPropertyValues) {
            if (pv.getValue() == null) {
                FacesContext.getCurrentInstance().addMessage("addPropertyValueForm:inputValidationFail",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                                pv.getName() + ": value not set."));
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
        }
        try {
            int created = 0;
            for (final MultiPropertyValueView pv : selectedPropertyValues) {
                ComptypePropertyValue newValue = createPropertyValue(pv.getProperty(), pv.getValue());
                comptypeEJB.addChild(newValue);
                ++created;
            }
            componentTypeManager.refreshSelectedComponent();
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                    "Created " + created + " device type properties.");
        } finally {
            resetFields();
            populateAttributesList();
        }
    }

    /** @return the filteredPropertyValues */
    public List<MultiPropertyValueView> getFilteredPropertyValues() {
        return filteredPropertyValues;
    }

    /** @param filteredPropertyValues the filteredPropertyValues to set */
    public void setFilteredPropertyValues(List<MultiPropertyValueView> filteredPropertyValues) {
        this.filteredPropertyValues = filteredPropertyValues;
    }

    /** @return the selectedPropertyValues */
    public List<MultiPropertyValueView> getSelectedPropertyValues() {
        return selectedPropertyValues;
    }

    /** @param selectedPropertyValues the selectedPropertyValues to set */
    public void setSelectedPropertyValues(List<MultiPropertyValueView> selectedPropertyValues) {
        this.selectedPropertyValues = selectedPropertyValues;
    }

    /** @return the selectionPropertyValuesFiltered */
    public List<MultiPropertyValueView> getSelectionPropertyValuesFiltered() {
        return selectionPropertyValuesFiltered;
    }

    /** @param selectionPropertyValuesFiltered the selectionPropertyValuesFiltered to set */
    public void setSelectionPropertyValuesFiltered(List<MultiPropertyValueView> selectionPropertyValuesFiltered) {
        this.selectionPropertyValuesFiltered = selectionPropertyValuesFiltered;
    }

    /** @return the selectAllRows */
    public boolean isSelectAllRows() {
        return selectAllRows;
    }

    /** @param selectAllRows the selectAllRows to set */
    public void setSelectAllRows(boolean selectAllRows) {
        this.selectAllRows = selectAllRows;
    }

    /** @return The list of {@link Property} entities the user can select in the property table. */
    public List<Property> getFilteredProperties() {
        return filteredProperties;
    }

    /** @return the selectedProperties */
    public List<Property> getSelectedProperties() {
        return selectedProperties;
    }

    /** @param selectedProperties the selectedProperties to set */
    public void setSelectedProperties(List<Property> selectedProperties) {
        this.selectedProperties = selectedProperties;
    }

    /** @return the selectionPropertiesFiltered */
    public List<Property> getSelectionPropertiesFiltered() {
        return selectionPropertiesFiltered;
    }

    /** @param selectionPropertiesFiltered the selectionPropertiesFiltered to set */
    public void setSelectionPropertiesFiltered(List<Property> selectionPropertiesFiltered) {
        this.selectionPropertiesFiltered = selectionPropertiesFiltered;
    }
}
