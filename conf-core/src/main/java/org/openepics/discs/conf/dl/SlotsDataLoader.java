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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.NotImplementedException;
import org.openepics.discs.conf.dl.annotations.SlotsLoader;
import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.AbstractEntityWithPropertiesDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPropertyValue;

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

    private static final String HDR_ENTITY_TYPE = "ENTITY TYPE";
    private static final String HDR_ENTITY_DEVICE_TYPE = "ENTITY DEVICE TYPE";
    private static final String HDR_ENTITY_NAME = "ENTITY NAME";
    private static final String HDR_ENTITY_DESCRIPTION = "ENTITY DESCRIPTION";
    private static final String HDR_ENTITY_PARENT = "ENTITY PARENT";
    private static final String HDR_PROP_NAME = "PROPERTY NAME";
    private static final String HDR_PROP_VALUE = "PROPERTY VALUE";
    private static final String HDR_RELATION_TYPE = "RELATIONSHIP TYPE";
    private static final String HDR_RELATION_ENTITY_NAME = "RELATIONSHIP ENTITY NAME";
    private static final String HDR_INSTALLATION = "INSTALLATION";

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

    private static final Set<String> REQUIRED_COLUMNS = new HashSet<>(Arrays.asList(HDR_ENTITY_TYPE, HDR_ENTITY_NAME));

    private String entityTypeFld, entityDeviceTypeFld, entityNameFld, entityDescriptionFld, entityParentFld;
    private String propNameFld, propValueFld, relationTypeFld, relationEntityNameFld, installationFld;

    private List<Slot> newSlots;

    @Inject private SlotEJB slotEJB;
    @Inject private ComptypeEJB comptypeEJB;

    @Override
    protected void init() {
        super.init();
        newSlots = new ArrayList<>();

        result.getContextualData().put(DataLoaderResult.CTX_NEW_SLOTS, newSlots);
    }

    @Override
    protected Set<String> getRequiredColumnNames() {
        return REQUIRED_COLUMNS;
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
            case DataLoader.CMD_UPDATE_RELATION:
                updateSlotRelationship();
                break;
            default:
                result.addRowMessage(ErrorMessage.COMMAND_NOT_VALID, HDR_OPERATION);
        }
    }

    private void updateSlot() {
        throw new NotImplementedException();
    }

    private void updateSlotProperty() {
        throw new NotImplementedException();
    }

    private void updateSlotRelationship() {
        throw new NotImplementedException();
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
                result.addRowMessage(ErrorMessage.COMMAND_NOT_VALID, HDR_OPERATION);
        }
    }

    private void createSlot() {
        throw new NotImplementedException();
    }

    private void createSlotProperty() {
        throw new NotImplementedException();
    }

    private void createSlotRelationship() {
        throw new NotImplementedException();
    }

    private void installIntoSlot() {
        throw new NotImplementedException();
    }

    private ComponentType checkSlotType() {
        throw new NotImplementedException();
    }

    @Override
    protected void handleDelete(String actualCommand) {
        switch (actualCommand) {
            case DataLoader.CMD_DELETE_ENTITY:
                deleteSlot();
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
                result.addRowMessage(ErrorMessage.COMMAND_NOT_VALID, HDR_OPERATION);
        }
    }

      private void deleteSlot() {
        throw new NotImplementedException();
    }

    private void deleteSlotProperty() {
        throw new NotImplementedException();
    }

    private void deleteSlotRelationship() {
        throw new NotImplementedException();
    }

    private void uninstallFromSlot() {
        throw new NotImplementedException();
    }

    @Override
    protected void handleRename() {
        result.addRowMessage(ErrorMessage.COMMAND_NOT_VALID, CMD_RENAME);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected DAO<Slot> getDAO() {
        return slotEJB;
    }

    private Double readCurrentRowCellForHeaderAsDouble(String columnName) {
        @Nullable String stringValue = readCurrentRowCellForHeader(indicies.get(columnName));

        if (stringValue == null)
            return null;

        try {
            return Double.parseDouble(stringValue);
        } catch (NumberFormatException e) {
            result.addRowMessage(ErrorMessage.SHOULD_BE_NUMERIC_VALUE, columnName);
            return null;
        }
    }

    /*
    private void addOrUpdateSlot(Slot slotToAddOrUpdate, ComponentType compType) {
        slotToAddOrUpdate.setComponentType(compType);
        slotToAddOrUpdate.setDescription(description);
        slotToAddOrUpdate.setHostingSlot(isHosting);
        slotToAddOrUpdate.setAssemblyComment(asmComment);
        slotToAddOrUpdate.setAssemblyPosition(asmPosition);
        slotToAddOrUpdate.setComment(comment);
    }
    */

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
        // index of the first import data Excel row is 10 (0 based 9)
        return 9;
    }
}
