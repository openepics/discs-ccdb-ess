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

import java.util.ArrayList;
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
import javax.servlet.http.HttpServletRequest;

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
import org.openepics.discs.conf.export.ExportTable;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;
import org.openepics.discs.conf.ui.common.AbstractComptypeAttributesController;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.ui.export.ExportSimpleTableDialog;
import org.openepics.discs.conf.ui.export.SimpleTableExporter;
import org.openepics.discs.conf.util.Conversion;
import org.openepics.discs.conf.util.PropertyValueNotUniqueException;
import org.openepics.discs.conf.util.PropertyValueUIElement;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.ComponentTypeView;
import org.openepics.discs.conf.views.EntityAttrArtifactView;
import org.openepics.discs.conf.views.EntityAttrPropertyValueView;
import org.openepics.discs.conf.views.EntityAttrTagView;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.EntityAttributeViewKind;
import org.openepics.discs.conf.views.MultiPropertyValueView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Controller bean for manipulation of {@link ComponentType} attributes
 *
 * @author vuppala
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 */
@Named
@ViewScoped
public class ComponentTypeManager extends AbstractComptypeAttributesController implements SimpleTableExporter {
    private static final long serialVersionUID = 1156974438243970794L;

    private static final Logger LOGGER = Logger.getLogger(ComponentTypeManager.class.getCanonicalName());

    @Inject private transient ComptypeEJB comptypeEJB;
    @Inject private transient PropertyEJB propertyEJB;
    @Inject private transient SlotEJB slotEJB;
    @Inject private transient DeviceEJB deviceEJB;

    private ComponentTypeView selectedComponent;
    private transient List<MultiPropertyValueView> filteredPropertyValues;
    private transient List<MultiPropertyValueView> selectedPropertyValues;
    private transient List<MultiPropertyValueView> selectionPropertyValuesFiltered;
    private boolean selectAllRows;

    private List<Property> filteredProperties;
    private List<Property> selectedProperties;
    private List<Property> selectionPropertiesFiltered;
    private boolean isPropertyDefinition;
    private DefinitionTarget definitionTarget;

    private List<ComponentTypeView> deviceTypes;
    private List<ComponentTypeView> filteredDeviceTypes;
    private List<ComponentTypeView> selectedDeviceTypes;
    private List<ComponentTypeView> usedDeviceTypes;
    private List<ComponentTypeView> filteredDialogTypes;

    private transient ExportSimpleTableDialog simpleTableExporterDialog;

    private class ExportSimpleDevTypeTableDialog extends ExportSimpleTableDialog {
        @Override
        protected String getTableName() {
            return "Device types";
        }

        @Override
        protected String getFileName() {
            return "ccdb_device_types";
        }

        @Override
        protected void addHeaderRow(ExportTable exportTable) {
            exportTable.addHeaderRow("Name", "Description");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<ComponentTypeView> exportData = Utility.isNullOrEmpty(filteredDeviceTypes)
                                                            ? deviceTypes
                                                            : filteredDeviceTypes;
            for (final ComponentTypeView devType : exportData) {
                exportTable.addDataRow(devType.getName(), devType.getDescription());
            }
        }
    }

    @Override
    @PostConstruct
    public void init() {
        final String deviceTypeIdStr = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().
                getRequest()).getParameter("id");
        try {
            super.init();
            simpleTableExporterDialog = new ExportSimpleDevTypeTableDialog();
            reloadDeviceTypes();
            setDao(comptypeEJB);
            resetFields();

            if (!Strings.isNullOrEmpty(deviceTypeIdStr)) {
                final long deviceTypeId = Long.parseLong(deviceTypeIdStr);
                int elementPosition = 0;
                for (final ComponentTypeView deviceType : deviceTypes) {
                    if (deviceType.getId() == deviceTypeId) {
                        RequestContext.getCurrentInstance().execute("selectEntityInTable(" + elementPosition
                                + ", 'deviceTypeTableVar');");
                        return;
                    }
                    ++elementPosition;
                }
            }
        } catch (NumberFormatException e) {
            // just log
            LOGGER.log(Level.WARNING, "URL contained strange device type ID: " + deviceTypeIdStr);
        } catch(Exception e) {
            throw new UIException("Device type display initialization fialed: " + e.getMessage(), e);
        }
    }

    private void reloadDeviceTypes() {
        deviceTypes = comptypeEJB.findAll().stream().map(ComponentTypeView::new).collect(Collectors.toList());
    }

    private void addPropertyValueBasedOnDef(ComptypePropertyValue definition) {
        if (definition.isDefinitionTargetSlot()) {
            for (final Slot slot : slotEJB.findByComponentType(selectedComponent.getComponentType())) {
                if (canAddProperty(slot.getSlotPropertyList(), definition.getProperty())) {
                    final SlotPropertyValue newSlotProperty = new SlotPropertyValue();
                    newSlotProperty.setProperty(definition.getProperty());
                    newSlotProperty.setSlot(slot);
                    slotEJB.addChild(newSlotProperty);
                } else {
                    LOGGER.log(Level.FINE, "Type: " + selectedComponent.getName() + "; Slot: " + slot.getName()
                            + ";  Trying to add the same property value again: "
                            + definition.getProperty().getName());
                }
            }
        }

        if (definition.isDefinitionTargetDevice()) {
            for (final Device device : deviceEJB.findDevicesByComponentType(selectedComponent.getComponentType())) {
                if (canAddProperty(device.getDevicePropertyList(), definition.getProperty())) {
                    final DevicePropertyValue newDeviceProperty = new DevicePropertyValue();
                    newDeviceProperty.setProperty(definition.getProperty());
                    newDeviceProperty.setDevice(device);
                    deviceEJB.addChild(newDeviceProperty);
                } else {
                    LOGGER.log(Level.FINE, "Type: " + selectedComponent.getName() + "; Device: " + device.getSerialNumber()
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
    protected void deletePropertyValue(final ComptypePropertyValue propValueToDelete) {
        if (propValueToDelete.isPropertyDefinition()) {
            if (propValueToDelete.isDefinitionTargetSlot()) {
                for (final Slot slot : slotEJB.findByComponentType(selectedComponent.getComponentType())) {
                    removeUndefinedProperty(slot.getSlotPropertyList(), propValueToDelete.getProperty(), slotEJB);
                }
            }

            if (propValueToDelete.isDefinitionTargetDevice()) {
                for (final Device device : deviceEJB.findDevicesByComponentType(selectedComponent.getComponentType())) {
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
        Preconditions.checkNotNull(selectedDeviceTypes);
        attributes = new ArrayList<>();

        for (final ComponentTypeView selectedMember : selectedDeviceTypes) {
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
        if (selectedDeviceTypes == null || (selectedDeviceTypes.size() != 1)) {
            filteredProperties = null;
            return;
        }

        final List<Property> propertyCandidates = propertyEJB.findAllOrderedByName();
        final Property dialogProperty = getDialogAttrPropertyValue() != null ? getDialogAttrPropertyValue().getProperty() : null;

        for (ComponentTypeView compType : selectedDeviceTypes) {
            for (final ComptypePropertyValue comptypePropertyValue : compType.getComponentType().getComptypePropertyList()) {
                final Property currentProperty = comptypePropertyValue.getProperty();
                if (!currentProperty.equals(dialogProperty)) {
                    propertyCandidates.remove(currentProperty);
                }
            }
        }
        filteredProperties = propertyCandidates;
    }

    /** Called when user selects a row */
    public void onRowSelect() {
        if (selectedDeviceTypes != null && !selectedDeviceTypes.isEmpty()) {
            // selectedDeviceTypes = getFreshTypes(deviceTypes);
            if (selectedDeviceTypes.size() == 1) {
                selectedComponent = selectedDeviceTypes.get(0);
            } else {
                selectedComponent = null;
            }
            selectedAttributes = null;
            filteredAttributes = null;
            populateAttributesList();
        } else {
            clearDeviceTypeRelatedInformation();
        }
    }

    public void prepareForPropertyValueAdd() {
        filterProperties();
        filteredPropertyValues = transformIntoViewList(filteredProperties);
        selectedPropertyValues = Lists.newArrayList();
        selectionPropertyValuesFiltered = null;
        selectAllRows = false;
    }

    private List<MultiPropertyValueView> transformIntoViewList(final List<Property> properties) {
        return properties.stream().map(MultiPropertyValueView::new).collect(Collectors.toList());
    }

    /** Prepares the data for slot property (definition) creation */
    public void prepareForSlotPropertyAdd() {
        definitionTarget = AbstractAttributesController.DefinitionTarget.SLOT;
        isPropertyDefinition = true;
        filterProperties();
    }

    /** Prepares the data for device property (definition) creation */
    public void prepareForDevicePropertyAdd() {
        definitionTarget = AbstractAttributesController.DefinitionTarget.DEVICE;
        isPropertyDefinition = true;
        filterProperties();
    }

    @Override
    public boolean canEdit(EntityAttributeView<ComponentType> attribute) {
        final EntityAttributeViewKind attributeKind = attribute.getKind();
        return attributeKind != EntityAttributeViewKind.INSTALL_SLOT_PROPERTY
                && attributeKind != EntityAttributeViewKind.DEVICE_PROPERTY
                && attributeKind != EntityAttributeViewKind.DEVICE_TYPE_TAG;
    }

    @Override
    protected boolean canDelete(EntityAttributeView<ComponentType> attribute) {
        return true;
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
                                    Utility.MESSAGE_SUMMARY_ERROR, "A value must be selected."));
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
                FacesContext.getCurrentInstance().addMessage("inputValidationFail", e.getFacesMessage());
                FacesContext.getCurrentInstance().validationFailed();
            } catch (EJBException e) {
                if (Utility.causedBySpecifiedExceptionClass(e, PropertyValueNotUniqueException.class)) {
                    FacesContext.getCurrentInstance().addMessage("inputValidationFail",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                    "Value is not unique."));
                    FacesContext.getCurrentInstance().validationFailed();
                } else {
                    throw e;
                }
            }
        }
    }

    private String getEditEventValue(final Object val, final PropertyValueUIElement propValueUIElement) {
        if (val == null) {
            return null;
        }
        if (val instanceof String) {
            return val.toString();
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
     * This method duplicates selected device types. This method actually copies
     * selected device type name, description, tags, artifacts and properties
     * into new device type. If property has set universally unique value,
     * copied property value is set to null.
     */
    public void duplicate() {
        try {
            Preconditions.checkState(!Utility.isNullOrEmpty(selectedDeviceTypes));

            final int duplicated = comptypeEJB.duplicate(selectedDeviceTypes.stream().
                                    map(ComponentTypeView::getComponentType).collect(Collectors.toList()));
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Duplicated " + duplicated + " device types.");
        } finally {
            reloadDeviceTypes();
        }
    }

    /** This method returns a String representation of the property value.
     * @param prop the value of the property to show value for
     * @return the string representation
     */
    public String displayPropertyValue(MultiPropertyValueView prop) {
        final Value val = prop.getValue();
        return val == null ? "<Please define>" : Conversion.valueToString(val);
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

    private ComptypePropertyValue createPropertyValue(final Property prop, final Value value) {
        final ComptypePropertyValue pv = new ComptypePropertyValue();
        pv.setComponentType(selectedComponent.getComponentType());
        pv.setProperty(prop);
        pv.setPropValue(value);
        return pv;
    }

    /** The save action for adding multiple property values to a device type. */
    public void saveMultiplePropertyValues() {
        // check if all values are set, because we want to save all in one batch
        for (final MultiPropertyValueView pv : selectedPropertyValues) {
            if (pv.getValue() == null) {
                FacesContext.getCurrentInstance().addMessage("inputValidationFail",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
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
                selectedComponent.setComponentType(comptypeEJB.findById(selectedComponent.getComponentType().getId()));
                ++created;
            }
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Created " + created + " device type properties.");
        } finally {
            resetFields();
            populateAttributesList();
        }
    }

    private void clearDeviceTypeRelatedInformation() {
        selectedDeviceTypes = null;
        filteredDeviceTypes = null;
        selectedComponent = null;
        attributes = null;
        filteredAttributes = null;
        filteredProperties = null;
        selectedAttributes = null;
        resetFields();
    }

    // ---------------------------------------------------------------------------------------------------------
    //
    //      Old ComponentTypeManager methods
    //
    // ---------------------------------------------------------------------------------------------------------

    /** Prepares the UI data for the "Add a new device type" dialog. */
    public void prepareAddPopup() {
        resetFields();
        selectedComponent = new ComponentTypeView(new ComponentType());
        RequestContext.getCurrentInstance().update("addDeviceTypeForm:addDeviceType");
    }

    @Override
    public void resetFields() {
        super.resetFields();
        if (selectedDeviceTypes != null && selectedDeviceTypes.size() == 1) {
            selectedComponent = selectedDeviceTypes.get(0);
        } else {
            selectedComponent = null;
        }
        filteredProperties = null;
        selectedProperties = null;
        selectionPropertiesFiltered = null;
    }

    /** Called when the user presses the "Save" button in the "Add a new device type" dialog. */
    public void onAdd() {
        try {
            comptypeEJB.add(selectedComponent.getComponentType());
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "New device type has been created");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                        "Device type could not be added because a device type instance with same name already exists.");
            } else {
                throw e;
            }
        } finally {
            reloadDeviceTypes();
        }
    }

    /** Prepares the data for the device type editing dialog fields based on the selected device type. */
    public void prepareEditPopup() {
        Preconditions.checkState(isSingleDeviceTypeSelected());
    }

    /** Saves the new device type data (name and/or description) */
    public void onChange() {
        Preconditions.checkNotNull(selectedComponent);
        try {
            comptypeEJB.save(selectedComponent.getComponentType());
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Device type updated");
        } catch (Exception e) {
            if (Utility.causedByPersistenceException(e)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                        "Device type could not be modified because a device type instance with same name already exists.");
            } else {
                throw e;
            }
        } finally {
            selectedComponent.setComponentType(comptypeEJB.findById(selectedComponent.getComponentType().getId()));
            reloadDeviceTypes();
        }
    }

    /**
     * The method builds a list of device types that are already used. If the list is not empty, it is displayed
     * to the user and the user is prevented from deleting them.
     */
    public void checkDeviceTypesForDeletion() {
        Preconditions.checkNotNull(selectedDeviceTypes);
        Preconditions.checkState(!selectedDeviceTypes.isEmpty());

        usedDeviceTypes = Lists.newArrayList();
        for (final ComponentTypeView deviceTypeToDelete : selectedDeviceTypes) {
            List<String> usedBy = comptypeEJB.findWhereIsComponentTypeUsed(deviceTypeToDelete.getComponentType(), 2);
            if (!usedBy.isEmpty()) {
                deviceTypeToDelete.setUsedBy(usedBy.get(0) + (usedBy.size() > 1 ? ", ..." : ""));
                usedDeviceTypes.add(deviceTypeToDelete);
            }
        }
    }

    /** Called when the user presses the "Delete" button under table listing the devices types. */
    public void onDelete() {
        try {
            Preconditions.checkNotNull(selectedDeviceTypes);
            Preconditions.checkState(!selectedDeviceTypes.isEmpty());
            Preconditions.checkNotNull(usedDeviceTypes);
            Preconditions.checkState(usedDeviceTypes.isEmpty());

            int deletedDeviceTypes = 0;
            for (final ComponentTypeView deviceTypeToDelete : selectedDeviceTypes) {
                final ComponentType freshEntity = comptypeEJB.findById(deviceTypeToDelete.getId());
                freshEntity.getTags().clear();
                comptypeEJB.delete(freshEntity);
                ++deletedDeviceTypes;
            }
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Deleted " + deletedDeviceTypes + " device types.");
        } finally {
            clearDeviceTypeRelatedInformation();
            reloadDeviceTypes();
        }
    }

    /** @return <code>true</code> if a single device type is selected , <code>false</code> otherwise */
    public boolean isSingleDeviceTypeSelected() {
        return (selectedDeviceTypes != null) && (selectedDeviceTypes.size() == 1);
    }

    @Override
    public void doImport() {
        super.doImport();
        reloadDeviceTypes();
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
            selectedComponent.setComponentType(comptypeEJB.findById(selectedComponent.getComponentType().getId()));
            addPropertyValueBasedOnDef(newPropertyValueInstance);
        }
        resetFields();
        populateAttributesList();
    }

    // -------------------- Getters and Setters ---------------------------------------

    /** @return the selectedDeviceTypes */
    public List<ComponentTypeView> getSelectedDeviceTypes() {
        return selectedDeviceTypes;
    }
    /** @param selectedDeviceTypes the selectedDeviceTypes to set */
    public void setSelectedDeviceTypes(List<ComponentTypeView> selectedDeviceTypes) {
        this.selectedDeviceTypes = selectedDeviceTypes;
    }

    /** @return the {@link List} of used device types */
    public List<ComponentTypeView> getUsedDeviceTypes() {
        return usedDeviceTypes;
    }

    /** @return The list of filtered device types used by the PrimeFaces filter field. */
    public List<ComponentTypeView> getFilteredDeviceTypes() {
        return filteredDeviceTypes;
    }
    /** @param filteredDeviceTypes The list of filtered device types used by the PrimeFaces filter field. */
    public void setFilteredDeviceTypes(List<ComponentTypeView> filteredDeviceTypes) {
        this.filteredDeviceTypes = filteredDeviceTypes;
    }

    /** @return The list of all device types in the database. */
    public List<ComponentTypeView> getDeviceTypes() {
        return deviceTypes;
    }

    @Override
    public ExportSimpleTableDialog getSimpleTableDialog() {
        return simpleTableExporterDialog;
    }

    /** @return the filteredDialogTypes */
    public List<ComponentTypeView> getFilteredDialogTypes() {
        return filteredDialogTypes;
    }

    /** @param filteredDialogTypes the filteredDialogTypes to set */
    public void setFilteredDialogTypes(List<ComponentTypeView> filteredDialogTypes) {
        this.filteredDialogTypes = filteredDialogTypes;
    }

    /** @return the selectedComponent */
    public ComponentTypeView getSelectedComponent() {
        return selectedComponent;
    }

    @Override
    protected ComponentType getSelectedEntity() {
        if (selectedComponent != null) {
            ComponentType componentType = comptypeEJB.findById(selectedComponent.getId());
            selectedComponent.setComponentType(componentType);
            return componentType;
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
