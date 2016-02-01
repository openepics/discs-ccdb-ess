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
package org.openepics.discs.conf.dl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.openepics.discs.conf.dl.annotations.SlotsLoader;
import org.openepics.discs.conf.dl.common.AbstractEntityWithPropertiesDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.ejb.SlotRelationEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.EntityWithProperties;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.util.Conversion;
import org.openepics.discs.conf.util.Utility;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * Implementation of data loader for slots.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Stateless
@SlotsLoader
public class SlotsDataLoader extends AbstractEntityWithPropertiesDataLoader<SlotPropertyValue> implements DataLoader {

    private static final Logger LOGGER = Logger.getLogger(SlotsDataLoader.class.getCanonicalName());
    private static final String LINE_SEPARATOR_PATTERN = "\r\n|\r|\n";

    protected static final String HDR_ENTITY_TYPE = "ENTITY TYPE";
    protected static final String HDR_ENTITY_DEVICE_TYPE = "ENTITY DEVICE TYPE";
    protected static final String HDR_ENTITY_NAME = "ENTITY NAME";
    protected static final String HDR_ENTITY_DESCRIPTION = "ENTITY DESCRIPTION";
    protected static final String HDR_ENTITY_PARENT = "ENTITY PARENT";
    protected static final String HDR_PROP_NAME = "PROPERTY NAME";
    protected static final String HDR_PROP_VALUE = "PROPERTY VALUE";
    protected static final String HDR_RELATION_TYPE = "RELATIONSHIP TYPE";
    protected static final String HDR_RELATION_ENTITY_NAME = "RELATIONSHIP ENTITY NAME";
    protected static final String HDR_INSTALLATION = "INSTALLATION";

    private static final int COL_INDEX_ENTITY_TYPE = 1;
    private static final int COL_INDEX_ENTITY_DEVICE_TYPE = 2;
    private static final int COL_INDEX_ENTITY_NAME = 3;
    private static final int COL_INDEX_ENTITY_DESCRIPTION = 4;
    private static final int COL_INDEX_ENTITY_PARENT = 5;
    private static final int COL_INDEX_PROP_NAME = 6;
    private static final int COL_INDEX_PROP_VALUE = 7;
    private static final int COL_INDEX_RELATION_TYPE = 8;
    private static final int COL_INDEX_RELATION_ENTITY_NAME = 9;
    private static final int COL_INDEX_INSTALLATION = 10;

    private static class RelationshipInfo {
        SlotRelationName relationship;
        Slot parent;
        Slot child;
    }

    private String entityTypeFld, entityDeviceTypeFld, entityNameFld, entityDescriptionFld, entityParentFld;
    private String propNameFld, propValueFld, relationTypeFld, relationEntityNameFld, installationFld;
    private boolean isHostingSlot;

    private List<Slot> newSlots;

    @Inject private SlotEJB slotEJB;
    @Inject private ComptypeEJB comptypeEJB;
    @Inject private PropertyEJB propertyEJB;
    @Inject private InstallationEJB installationEJB;
    @Inject private DeviceEJB deviceEjb;
    @Inject private SlotPairEJB slotPairEJB;
    @Inject private SlotRelationEJB slotRelationEJB;

    @Override
    protected void init() {
        super.init();
        newSlots = new ArrayList<>();

        result.getContextualData().put(DataLoaderResult.CTX_NEW_SLOTS, newSlots);
    }

    @Override
    protected @Nullable Integer getUniqueColumnIndex() {
        return COL_INDEX_ENTITY_NAME;
    }

    @Override
    protected void assignMembersForCurrentRow() {
        entityTypeFld = readCurrentRowCellForHeader(COL_INDEX_ENTITY_TYPE);
        entityDeviceTypeFld = readCurrentRowCellForHeader(COL_INDEX_ENTITY_DEVICE_TYPE);
        entityNameFld = readCurrentRowCellForHeader(COL_INDEX_ENTITY_NAME);
        entityDescriptionFld = readCurrentRowCellForHeader(COL_INDEX_ENTITY_DESCRIPTION);
        entityParentFld = readCurrentRowCellForHeader(COL_INDEX_ENTITY_PARENT);
        propNameFld = readCurrentRowCellForHeader(COL_INDEX_PROP_NAME);
        propValueFld = readCurrentRowCellForHeader(COL_INDEX_PROP_VALUE);
        relationTypeFld = readCurrentRowCellForHeader(COL_INDEX_RELATION_TYPE);
        relationEntityNameFld = readCurrentRowCellForHeader(COL_INDEX_RELATION_ENTITY_NAME);
        installationFld = readCurrentRowCellForHeader(COL_INDEX_INSTALLATION);
    }

    @Override
    protected void handleUpdate(String actualCommand) {
        switch (actualCommand) {
            case DataLoader.CMD_UPDATE_ENTITY:
                updateSlot();
                break;
            case DataLoader.CMD_UPDATE_PROPERTY:
                updateSlotProperty();
                break;
            default:
                result.addRowMessage(ErrorMessage.COMMAND_NOT_VALID, HDR_OPERATION, actualCommand);
        }
    }

    private void updateSlot() {
        Slot workingSlot = getWorkingSlot();
        if (result.isRowError()) {
            return;
        }
        if (isHostingSlot) {
            final ComponentType importType = checkSlotType();
            if (result.isRowError()) {
                return;
            }

            if (!Strings.isNullOrEmpty(entityDescriptionFld)) {
                // if description is omitted, we leave it as it is
                workingSlot.setDescription(entityDescriptionFld);
            }
            if (!importType.equals(workingSlot.getComponentType())) {
                final InstallationRecord activeInstallationRecord =
                        installationEJB.getActiveInstallationRecordForSlot(workingSlot);
                if (activeInstallationRecord != null) {
                    result.addRowMessage(ErrorMessage.INSTALLATION_EXISTING, HDR_ENTITY_DEVICE_TYPE,
                                                                                            entityDeviceTypeFld);
                    return;
                } else {
                    workingSlot = slotEJB.changeSlotType(workingSlot, importType);
                }
            }
            updateSlotParent(workingSlot);
            if (result.isRowError()) {
                return;
            }
        } else {
            if (Strings.isNullOrEmpty(entityDescriptionFld)) {
                result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_ENTITY_DESCRIPTION);
                return;
            }
            // for container only description can be updated, parent is used to locate the container
            workingSlot.setDescription(entityDescriptionFld);
        }
        slotEJB.save(workingSlot);
    }

    private void updateSlotProperty() {
        final Slot workingSlot = getWorkingSlot();
        if (Strings.isNullOrEmpty(propNameFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_PROP_NAME);
        }
        if (Strings.isNullOrEmpty(propValueFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_PROP_VALUE);
        }
        final SlotPropertyValue slotPropertyValue;
        if (!result.isRowError()) {
            slotPropertyValue = (SlotPropertyValue) getPropertyValue(workingSlot, propNameFld, HDR_PROP_NAME);
        } else {
            slotPropertyValue = null;
        }
        if (result.isRowError()) {
            return;
        }

        if (slotPropertyValue == null) {
            result.addRowMessage(ErrorMessage.PROPERTY_NOT_FOUND, HDR_PROP_NAME, propNameFld);
            return;
        }

        try {
            final Property property = slotPropertyValue.getProperty();
            slotPropertyValue.setPropValue(Conversion.stringToValue(propValueFld, property.getDataType()));
            slotEJB.saveChild(slotPropertyValue);
        } catch (RuntimeException e) {
            LOGGER.log(Level.FINE, "Error in property value conversion", e);
            result.addRowMessage(ErrorMessage.CONVERSION_ERROR, HDR_PROP_VALUE, propValueFld);
            return;
        }
    }

    private void updateSlotParent(final Slot slot) {
        // try to locate the import parent
        final Slot importParent = getParentSlot(entityParentFld, HDR_ENTITY_PARENT);
        if (result.isRowError()) {
            return;
        }
        // get a list of all existing parents
        final List<Slot> existingParents = slot.getPairsInWhichThisSlotIsAChildList().stream().
                filter((pair) -> pair.getSlotRelation().getName() == SlotRelationName.CONTAINS).
                map(SlotPair::getParentSlot).collect(Collectors.toList());

        if (!existingParents.contains(importParent)) {
            // import parent is not amongst the current parents
            if (existingParents.size() != 1) {
                // but we cannot determine which parent to update
                result.addRowMessage(ErrorMessage.AMBIGUOUS_PARENT_SLOT, HDR_ENTITY_PARENT, entityParentFld);
                return;
            } else {
                // we know that the slot has exactly one parent
                for (final SlotPair slotPair : slot.getPairsInWhichThisSlotIsAChildList()) {
                    if (slotPair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
                        // the correct slot pair was found, let's update it to a new parent
                        slotPair.setParentSlot(importParent);
                        slotPairEJB.save(slotPair);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void handleCreate(String actualCommand) {
        switch (actualCommand) {
            case DataLoader.CMD_CREATE_ENTITY:
                createSlot();
                break;
            case DataLoader.CMD_CREATE_PROPERTY:
                createSlotProperty();
                break;
            case DataLoader.CMD_CREATE_RELATION:
                createSlotRelationship();
                break;
            case DataLoader.CMD_INSTALL:
                installIntoSlot();
                break;
            default:
                result.addRowMessage(ErrorMessage.COMMAND_NOT_VALID, HDR_OPERATION, actualCommand);
        }
    }

    private void createSlot() {
        if (Strings.isNullOrEmpty(entityDescriptionFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_ENTITY_DESCRIPTION);
        }
        if (Strings.isNullOrEmpty(entityTypeFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_ENTITY_TYPE);
            return;
        }
        isHostingSlot = isHostingSlot();
        // device type must be defined for installation slots
        if (isHostingSlot && Strings.isNullOrEmpty(entityDeviceTypeFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_ENTITY_DEVICE_TYPE);
        }
        // parent must be defined for installation slots
        if (isHostingSlot && Strings.isNullOrEmpty(entityParentFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_ENTITY_PARENT);
        }

        if (!result.isRowError()) {
            if (isHostingSlot) {
                createInstallationSlot();
            } else {
                createContainer();
            }
        }
    }

    private void createSlotProperty() {
        final Slot workingSlot = getWorkingSlot();
        if (Strings.isNullOrEmpty(propNameFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_PROP_NAME);
        }
        if (Strings.isNullOrEmpty(propValueFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_PROP_VALUE);
        }
        final SlotPropertyValue slotPropertyValue;
        if (!result.isRowError()) {
            slotPropertyValue = (SlotPropertyValue) getPropertyValue(workingSlot, propNameFld, HDR_PROP_NAME);
        } else {
            slotPropertyValue = null;
        }
        if (result.isRowError()) {
            return;
        }

        if (slotPropertyValue != null) {
            // property value found, maybe it is still OK
            if (!isHostingSlot) {
                // container must not have such a value
                result.addRowMessage(ErrorMessage.CREATE_VALUE_EXISTS, HDR_PROP_NAME, propNameFld);
                return;
            } else {
                // installation slot must have value set to null
                if (slotPropertyValue.getPropValue() != null) {
                    // value already set
                    result.addRowMessage(ErrorMessage.CREATE_VALUE_EXISTS, HDR_PROP_NAME, propNameFld);
                    return;
                } else {
                    // add a new value
                    try {
                        final Property property = slotPropertyValue.getProperty();
                        slotPropertyValue.setPropValue(Conversion.stringToValue(propValueFld, property.getDataType()));
                        slotEJB.saveChild(slotPropertyValue);
                    } catch (RuntimeException e) {
                        LOGGER.log(Level.FINE, "Error in property value conversion", e);
                        result.addRowMessage(ErrorMessage.CONVERSION_ERROR, HDR_PROP_VALUE, propValueFld);
                        return;
                    }
                }
            }
        } else {
            // no property value found
            if (isHostingSlot) {
                // this is an error for installation slot
                result.addRowMessage(ErrorMessage.PROPERTY_NOT_FOUND, HDR_PROP_NAME, propNameFld);
                return;
            } else {
                try {
                    final Property property = propertyEJB.findByName(propNameFld);
                    if (property == null) {
                        result.addRowMessage(ErrorMessage.PROPERTY_NOT_FOUND, HDR_PROP_NAME, propNameFld);
                        return;
                    }
                    final SlotPropertyValue pv = new SlotPropertyValue(false);
                    pv.setProperty(property);
                    pv.setSlot(workingSlot);
                    pv.setPropValue(Conversion.stringToValue(propValueFld, property.getDataType()));
                    slotEJB.addChild(pv);
                } catch (RuntimeException e) {
                    LOGGER.log(Level.FINE, "Error in property value conversion", e);
                    result.addRowMessage(ErrorMessage.CONVERSION_ERROR, HDR_PROP_VALUE, propValueFld);
                    return;
                }
            }
        }
    }

    private void createSlotRelationship() {
        final Slot workingSlot = getWorkingSlot();
        final Slot newRelationshipTarget;
        if (Strings.isNullOrEmpty(relationEntityNameFld)) {
            // we can set to null, since it will target out anyhow
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_RELATION_ENTITY_NAME);
            newRelationshipTarget = null;
        } else {
            newRelationshipTarget = getParentSlot(relationEntityNameFld, HDR_RELATION_ENTITY_NAME);
        }
        if (Strings.isNullOrEmpty(relationTypeFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_RELATION_TYPE);
        }
        if (result.isRowError()) {
            return;
        }

        RelationshipInfo info = determineParentChild(workingSlot, newRelationshipTarget);
        if (result.isRowError()) {
            return;
        }

        // check relationship restrictions
        switch (info.relationship) {
            case CONTAINS:
                if (info.parent.isHostingSlot() && !info.child.isHostingSlot()) {
                    result.addRowMessage(ErrorMessage.INSTALL_CANT_CONTAIN_CONTAINER, HDR_RELATION_ENTITY_NAME,
                                                                                                relationEntityNameFld);
                    return;
                }
                break;
            case POWERS:
                if (!info.parent.isHostingSlot() || !info.child.isHostingSlot()) {
                    result.addRowMessage(ErrorMessage.POWER_RELATIONSHIP_RESTRICTIONS, HDR_RELATION_TYPE,
                                                                                                    relationTypeFld);
                    return;
                }
                break;
            case CONTROLS:
                if (!info.parent.isHostingSlot() || !info.child.isHostingSlot()) {
                    result.addRowMessage(ErrorMessage.CONTROL_RELATIONSHIP_RESTRICTIONS, HDR_RELATION_TYPE,
                                                                                                    relationTypeFld);
                    return;
                }
                break;
            default:
                break;
        }

        // create new relationship
        final SlotRelation relation = slotRelationEJB.findBySlotRelationName(info.relationship);
        final SlotPair newRelationship = new SlotPair(info.child, info.parent, relation);
        if ((info.relationship == SlotRelationName.CONTAINS)
                && slotPairEJB.slotPairCreatesLoop(newRelationship, info.child)) {
            result.addRowMessage(ErrorMessage.SAME_CHILD_AND_PARENT, HDR_RELATION_ENTITY_NAME, relationEntityNameFld);
            return;
        }

        slotPairEJB.add(newRelationship);
    }

    private void installIntoSlot() {
        final Slot workingSlot = getWorkingSlot();
        if (Strings.isNullOrEmpty(installationFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_INSTALLATION);
        }
        if (result.isRowError()) {
            return;
        }

        final InstallationRecord record = installationEJB.getActiveInstallationRecordForSlot(workingSlot);

        if (record != null) {
            result.addRowMessage(ErrorMessage.INSTALLATION_EXISTING, HDR_INSTALLATION, installationFld);
            return;
        }

        final Device device = deviceEjb.findDeviceBySerialNumber(installationFld.trim());
        if (device == null) {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_INSTALLATION, installationFld);
            return;
        }

        if (workingSlot.getComponentType() != device.getComponentType()) {
            result.addRowMessage(ErrorMessage.DEVICE_TYPE_ERROR, HDR_INSTALLATION, installationFld);
            return;
        }

        final Date today = new Date();
        final InstallationRecord newRecord = new InstallationRecord(Long.toString(today.getTime()), today);
        newRecord.setSlot(workingSlot);
        newRecord.setDevice(device);
        installationEJB.save(newRecord);
    }

    private ComponentType checkSlotType() {
        final String deviceTypeName = entityDeviceTypeFld.trim();
        final ComponentType deviceType = comptypeEJB.findByName(deviceTypeName);
        if (deviceType == null) {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_ENTITY_DEVICE_TYPE, entityDeviceTypeFld);
        }
        return deviceType;
    }

    private void createInstallationSlot() {
        final ComponentType deviceType = checkSlotType();
        if (deviceType == null) {
            return;
        }
        final Slot parent = getParentSlot(entityParentFld, HDR_ENTITY_PARENT);
        if (parent == null) {
            return;
        }
        final String newSlotName = entityNameFld.trim();
        if (!slotEJB.isInstallationSlotNameUnique(newSlotName)) {
            result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS, HDR_ENTITY_NAME, entityNameFld);
            return;
        }

        final Slot newSlot = new Slot(entityNameFld.trim(), true);
        newSlot.setComponentType(deviceType);
        newSlot.setDescription(entityDescriptionFld);
        slotEJB.addSlotToParentWithPropertyDefs(newSlot, parent, false);
    }

    private void createContainer() {
        final Slot parent = getParentSlot(entityParentFld, HDR_ENTITY_PARENT);
        if (parent == null) {
            return;
        }
        if (parent.isHostingSlot()) {
            result.addRowMessage(ErrorMessage.INSTALL_CANT_CONTAIN_CONTAINER);
            return;
        }

        final Slot newContainer = new Slot(entityNameFld.trim(), false);
        newContainer.setComponentType(comptypeEJB.findByName(SlotEJB.GRP_COMPONENT_TYPE));
        newContainer.setDescription(entityDescriptionFld);

        if (!slotEJB.isContainerNameUnique(newContainer.getName(), parent, newContainer)) {
            result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS_UNDER_PARENT, HDR_ENTITY_NAME, entityNameFld);
        } else {
            slotEJB.addSlotToParentWithPropertyDefs(newContainer, parent, false);
        }
    }

    private Slot getParentSlot(final String parentPath, final String headerName) {
        // check for "child of root"
        if ((parentPath == null || parentPath.trim().isEmpty()) && HDR_ENTITY_PARENT.equals(headerName)) {
            if (isHostingSlot) {
                result.addRowMessage(ErrorMessage.ORPHAN_SLOT, HDR_ENTITY_NAME, entityNameFld);
                return null;
            } else {
                return slotEJB.getRootNode();
            }
        }
        final String[] parents = parentPath.split(LINE_SEPARATOR_PATTERN);
        if (parents.length == 1) {
            // find parent if only one in the database
            final String theParent = parents[0].trim();
            final List<Slot> parentCandidates = slotEJB.findAllByName(theParent);
            if (parentCandidates.isEmpty()) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, headerName, theParent);
                return null;
            }
            if (parentCandidates.size() > 1) {
                result.addRowMessage(ErrorMessage.AMBIGUOUS_PARENT_SLOT, headerName, theParent);
                return null;
            }
            return parentCandidates.get(0);
        } else {
            Slot parent = slotEJB.getRootNode();
            for (final String parentName : parents) {
                final String normalizedName = parentName.trim();
                // ignore empty rows
                if (!normalizedName.isEmpty()) {
                    parent = findChildSlot(parent, normalizedName, headerName);
                    if (parent == null) {
                        // requested child not found
                        return null;
                    }
                }
            }
            return parent;
        }
    }

    private Slot findChildSlot(final Slot parent, final String name, final String headerName) {
        for (final SlotPair slotPair : parent.getPairsInWhichThisSlotIsAParentList()) {
            if (slotPair.getSlotRelation().getName() == SlotRelationName.CONTAINS
                    && slotPair.getChildSlot().getName().equals(name)) {
                return slotPair.getChildSlot();
            }
        }
        result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, headerName, name);
        return null;
    }

    private Slot getWorkingSlot() {
        if (Strings.isNullOrEmpty(entityTypeFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_ENTITY_TYPE);
            return null;
        }

        isHostingSlot = isHostingSlot();
        if (isHostingSlot) {
            // installation slot must be found by its unique name
            final Slot slot = slotEJB.findByName(entityNameFld);
            if (slot == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_ENTITY_NAME, entityNameFld);
            } else if (slot.isHostingSlot() != isHostingSlot) {
                result.addRowMessage(ErrorMessage.VALUE_NOT_IN_DATABASE, HDR_ENTITY_TYPE, entityTypeFld);
            }
            return slot;
        } else {
            // container should be located by its parent
            final Slot parentSlot = getParentSlot(entityParentFld, HDR_ENTITY_PARENT);
            return findChildSlot(parentSlot, entityNameFld.trim(), HDR_ENTITY_NAME);
        }
    }

    @Override
    protected void handleDelete(String actualCommand) {
        switch (actualCommand) {
            case DataLoader.CMD_DELETE_ENTITY:
                deleteSlot();
                break;
            case DataLoader.CMD_DELETE_ENTITY_AND_CHILDREN:
                deleteSlotWithChildren();
                break;
            case DataLoader.CMD_DELETE_PROPERTY:
                deleteSlotProperty();
                break;
            case DataLoader.CMD_DELETE_RELATION:
                deleteSlotRelationship();
                break;
            case DataLoader.CMD_UNINSTALL:
                uninstallFromSlot();
                break;
            default:
                result.addRowMessage(ErrorMessage.COMMAND_NOT_VALID, HDR_OPERATION, actualCommand);
        }
    }

    private void deleteSlot() {
        final Slot workingSlot = getWorkingSlot();
        if (result.isRowError()) {
            return;
        }

        // check that removing this slot will not create any orphans
        for (final SlotPair slotPair : workingSlot.getPairsInWhichThisSlotIsAParentList()) {
            if ((slotPair.getSlotRelation().getName() == SlotRelationName.CONTAINS)
                    && !slotPairEJB.slotHasMoreThanOneContainsRelation(slotPair.getChildSlot())) {
                result.addRowMessage(ErrorMessage.ORPHAN_CREATED);
                return;
            }
        }
        // remove all relations with parents and children
        final ListIterator<SlotPair> pairsWithChildren = workingSlot.getPairsInWhichThisSlotIsAParentList().listIterator();
        // all child relationships are NOT contains
        while (pairsWithChildren.hasNext()) {
            final SlotPair pair = pairsWithChildren.next();
            pairsWithChildren.remove();
            slotPairEJB.delete(pair);
        }

        final ListIterator<SlotPair> pairsWithParents = workingSlot.getPairsInWhichThisSlotIsAChildList().listIterator();
        // all parent relationships need to be removed
        while (pairsWithParents.hasNext()) {
            final SlotPair pair = pairsWithParents.next();
            pairsWithParents.remove();
            slotPairEJB.delete(pair);
        }

        slotEJB.delete(workingSlot);
    }

    private void deleteSlotWithChildren() {
        final Slot workingSlot = getWorkingSlot();
        if (result.isRowError()) {
            return;
        }

        slotEJB.deleteWithChildren(workingSlot);
    }

    private void deleteSlotProperty() {
        final Slot workingSlot = getWorkingSlot();
        final SlotPropertyValue slotPropertyValue;
        if (Strings.isNullOrEmpty(propNameFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_PROP_NAME);
            // setting to null explicitly - we know it will not be used because of error
            slotPropertyValue = null;
        } else if (workingSlot != null) {
            slotPropertyValue = (SlotPropertyValue) getPropertyValue(workingSlot, propNameFld, HDR_PROP_NAME);
        } else {
            // setting to null explicitly - we know it will not be used because of error
            slotPropertyValue = null;
        }

        if (result.isRowError()) {
            return;
        }

        if (isHostingSlot) {
            slotPropertyValue.setPropValue(null);
            slotEJB.saveChild(slotPropertyValue);
        } else {
            slotEJB.deleteChild(slotPropertyValue);
        }
    }

    private void deleteSlotRelationship() {
        final Slot workingSlot = getWorkingSlot();
        final Slot relationshipTarget;
        if (Strings.isNullOrEmpty(relationEntityNameFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_RELATION_ENTITY_NAME);
            // setting to null explicitly - we know it will not be used because of error
            relationshipTarget = null;
        } else {
            relationshipTarget = getParentSlot(relationEntityNameFld, HDR_RELATION_ENTITY_NAME);
        }
        if (Strings.isNullOrEmpty(relationTypeFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_RELATION_TYPE);
        }
        if (result.isRowError()) {
            return;
        }

        RelationshipInfo info = determineParentChild(workingSlot, relationshipTarget);
        if (result.isRowError()) {
            return;
        }
        for (final SlotPair pair : info.parent.getPairsInWhichThisSlotIsAParentList()) {
            if ((pair.getSlotRelation().getName() == info.relationship) && pair.getChildSlot().equals(info.child)) {
                // find the relationship and check whether it can be deleted
                if ((info.relationship == SlotRelationName.CONTAINS)
                        && !slotPairEJB.slotHasMoreThanOneContainsRelation(info.child)) {
                    result.addRowMessage(ErrorMessage.ORPHAN_CREATED, HDR_RELATION_ENTITY_NAME, relationEntityNameFld);
                } else {
                    slotPairEJB.delete(pair);
                }
                return;
            }
        }
        result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_RELATION_ENTITY_NAME, relationEntityNameFld);
    }

    private void uninstallFromSlot() {
        final Slot workingSlot = getWorkingSlot();
        if (Strings.isNullOrEmpty(installationFld)) {
            result.addRowMessage(ErrorMessage.REQUIRED_FIELD_MISSING, HDR_INSTALLATION);
        }
        if (result.isRowError()) {
            return;
        }

        final InstallationRecord record = installationEJB.getActiveInstallationRecordForSlot(workingSlot);

        if (record == null) {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_INSTALLATION, installationFld);
            return;
        }

        final Device device = record.getDevice();
        if (!device.getSerialNumber().equals(installationFld)) {
            result.addRowMessage(ErrorMessage.VALUE_NOT_IN_DATABASE, HDR_INSTALLATION, installationFld);
            return;
        }

        record.setUninstallDate(new Date());
        installationEJB.save(record);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected DAO<Slot> getDAO() {
        return slotEJB;
    }

    @Override
    public int getDataWidth() {
        return 11;
    }

    @Override
    protected void setUpIndexesForFields() {
        final ImmutableMap.Builder<String, Integer> mapBuilder = ImmutableMap.builder();

        mapBuilder.put(HDR_ENTITY_TYPE, COL_INDEX_ENTITY_TYPE);
        mapBuilder.put(HDR_ENTITY_DEVICE_TYPE, COL_INDEX_ENTITY_DEVICE_TYPE);
        mapBuilder.put(HDR_ENTITY_NAME, COL_INDEX_ENTITY_NAME);
        mapBuilder.put(HDR_ENTITY_DESCRIPTION, COL_INDEX_ENTITY_DESCRIPTION);
        mapBuilder.put(HDR_ENTITY_PARENT, COL_INDEX_ENTITY_PARENT);

        mapBuilder.put(HDR_PROP_NAME, COL_INDEX_PROP_NAME);
        mapBuilder.put(HDR_PROP_VALUE, COL_INDEX_PROP_VALUE);

        mapBuilder.put(HDR_RELATION_TYPE, COL_INDEX_RELATION_TYPE);
        mapBuilder.put(HDR_RELATION_ENTITY_NAME, COL_INDEX_RELATION_ENTITY_NAME);

        mapBuilder.put(HDR_INSTALLATION, COL_INDEX_INSTALLATION);

        indicies = mapBuilder.build();
    }

    @Override
    public int getImportDataStartIndex() {
        // index of the first import data Excel row is 11 (0 based 10)
        return 10;
    }

    private boolean isHostingSlot() {
        // entityTypeFld is in REQUIRED_COLUMNS
        return DataLoader.ENTITY_TYPE_SLOT.equalsIgnoreCase(entityTypeFld);
    }

    private RelationshipInfo determineParentChild(final Slot orignator, final Slot target) {
        // check if relationship name exists, and set parent and child appropriately
        final RelationshipInfo info = new RelationshipInfo();

        info.relationship = Utility.getRelationByName(relationTypeFld);
        if (info.relationship != null) {
            info.parent = orignator;
            info.child = target;
        } else {
            info.relationship = Utility.getRelationBasedOnInverseName(relationTypeFld);
            if (info.relationship == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_RELATION_TYPE, relationTypeFld);
                return null;
            } else {
                info.parent = target;
                info.child = orignator;
            }
        }
        return info;
    }

    /* This version does not create an error, if the property value was not found.
     * (non-Javadoc)
     * @see org.openepics.discs.conf.dl.common.AbstractEntityWithPropertiesDataLoader#getPropertyValue(org.openepics.discs.conf.ent.EntityWithProperties, java.lang.String, java.lang.String)
     */
    @Override
    protected PropertyValue getPropertyValue(EntityWithProperties entity, String propertyName, String propNameHeader) {
        Preconditions.checkNotNull(propertyName);
        Preconditions.checkNotNull(propNameHeader);
        final List<PropertyValue> propertyList = entity.getEntityPropertyList();

        final @Nullable Property property = propertyEJB.findByName(propertyName);
        if (property == null) {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, propNameHeader, propertyName);
            return null;
        }

        for (final PropertyValue value : propertyList) {
            if (value.getProperty().equals(property)) {
                return value;
            }
        }

        return null;
    }
}
