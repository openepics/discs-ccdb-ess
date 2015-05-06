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
import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.AbstractEntityWithPropertiesDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.PositionInformation;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPropertyValue;

/**
 * Implementation of data loader for slots.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Stateless
@SlotsDataLoaderQualifier
public class SlotsDataLoader extends AbstractEntityWithPropertiesDataLoader<SlotPropertyValue> implements DataLoader {

    private static final Logger LOGGER = Logger.getLogger(SlotsDataLoader.class.getCanonicalName());

    public static final int DATA_WIDTH = 14;  // TODO write proper value

    private static final String HDR_NAME = "NAME";
    private static final String HDR_CTYPE = "CTYPE";
    private static final String HDR_DESCRIPTION = "DESCRIPTION";
    private static final String HDR_IS_HOSTING_SLOT = "IS-HOSTING-SLOT";
    private static final String HDR_BLP = "BLP";
    private static final String HDR_GCX = "GCX";
    private static final String HDR_GCY = "GCY";
    private static final String HDR_GCZ = "GCZ";
    private static final String HDR_GL_ROLL = "GL-ROLL";
    private static final String HDR_GL_YAW = "GL-YAW";
    private static final String HDR_GL_PITCH = "GL-PITCH";
    private static final String HDR_ASM_COMMENT = "ASM-COMMENT";
    private static final String HDR_ASM_POSITION = "ASM-POSITION";
    private static final String HDR_COMMENT = "COMMENT";

    private static final int COL_INDEX_NAME = -1; // TODO fix
    private static final int COL_INDEX_CTYPE = -1; // TODO fix
    private static final int COL_INDEX_DESCRIPTION = -1; // TODO fix
    private static final int COL_INDEX_IS_HOSTING_SLOT = -1; // TODO fix
    private static final int COL_INDEX_BLP = -1; // TODO fix
    private static final int COL_INDEX_GCX = -1; // TODO fix
    private static final int COL_INDEX_GCY = -1; // TODO fix
    private static final int COL_INDEX_GCZ = -1; // TODO fix
    private static final int COL_INDEX_GL_ROLL = -1; // TODO fix
    private static final int COL_INDEX_GL_YAW = -1; // TODO fix
    private static final int COL_INDEX_GL_PITCH = -1; // TODO fix
    private static final int COL_INDEX_ASM_COMMENT = -1; // TODO fix
    private static final int COL_INDEX_ASM_POSITION = -1; // TODO fix
    private static final int COL_INDEX_COMMENT = -1; // TODO fix

    private static final Set<String> REQUIRED_COLUMNS = new HashSet<>(Arrays.asList(HDR_IS_HOSTING_SLOT, HDR_CTYPE));

    private String name, description, componentTypeString, asmComment, asmPosition, comment;
    private Double blp, globalX, globalY, globalZ, globalRoll, globalPitch, globalYaw;
    private Boolean isHosting;

    private List<Slot> newSlots;

    @Inject private SlotEJB slotEJB;
    @Inject private ComptypeEJB comptypeEJB;

    @Override
    protected void init() {
        super.init();
        setPropertyValueClass(SlotPropertyValue.class);
        newSlots = new ArrayList<>();

        result.getContextualData().put(DataLoaderResult.CTX_NEW_SLOTS, newSlots);
    }

    @Override
    protected Set<String> getRequiredColumnNames() {
        return REQUIRED_COLUMNS;
    }

    @Override
    protected @Nullable Integer getUniqueColumnIndex() {
        return new Integer(COL_INDEX_NAME);
    }

    @Override
    protected void assignMembersForCurrentRow() {
        name               = readCurrentRowCellForHeader(COL_INDEX_NAME);
        description        = readCurrentRowCellForHeader(COL_INDEX_DESCRIPTION);
        componentTypeString= readCurrentRowCellForHeader(COL_INDEX_CTYPE);
        asmComment         = readCurrentRowCellForHeader(COL_INDEX_ASM_COMMENT);
        asmPosition        = readCurrentRowCellForHeader(COL_INDEX_ASM_POSITION);
        comment            = readCurrentRowCellForHeader(COL_INDEX_COMMENT);

        @Nullable String isHostingString = readCurrentRowCellForHeader(COL_INDEX_IS_HOSTING_SLOT);
        isHosting = null;
        if (isHostingString != null && !isHostingString.equalsIgnoreCase(Boolean.FALSE.toString())
                && !isHostingString.equalsIgnoreCase(Boolean.TRUE.toString())) {
            result.addRowMessage(ErrorMessage.SHOULD_BE_BOOLEAN_VALUE, HDR_IS_HOSTING_SLOT);
        } else {
            isHosting = isHostingString != null ? Boolean.parseBoolean(isHostingString) : null;
        }

        blp                = readCurrentRowCellForHeaderAsDouble(HDR_BLP);
        globalX            = readCurrentRowCellForHeaderAsDouble(HDR_GCX);
        globalY            = readCurrentRowCellForHeaderAsDouble(HDR_GCY);
        globalZ            = readCurrentRowCellForHeaderAsDouble(HDR_GCZ);
        globalRoll         = readCurrentRowCellForHeaderAsDouble(HDR_GL_ROLL);
        globalPitch        = readCurrentRowCellForHeaderAsDouble(HDR_GL_PITCH);
        globalYaw          = readCurrentRowCellForHeaderAsDouble(HDR_GL_YAW);
    }

    @Override
    protected void handleUpdate() {
        @Nullable final ComponentType compType = checkSlotType();
        if (compType == null) {
            return;
        }

        @Nullable Slot slot = slotEJB.findByName(name);
        if (slot != null) {
            try {
                addOrUpdateSlot(slot, compType);
                addOrUpdateProperties(slot);
            } catch (EJBTransactionRolledbackException e) {
                handleLoadingError(LOGGER, e);
            }
        } else {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
        }
    }

    @Override
    protected void handleCreate() {
        @Nullable final ComponentType compType = checkSlotType();
        if (compType == null) {
            return;
        }

        @Nullable Slot slot = slotEJB.findByName(name);
        if (slot == null) {
            try {
                slot = new Slot(name, isHosting);
                addOrUpdateSlot(slot, compType);
                newSlots.add(slot);

                slotEJB.addSlotToParentWithPropertyDefs(slot, null, true);
                addOrUpdateProperties(slot);
            } catch (EJBTransactionRolledbackException e) {
                handleLoadingError(LOGGER, e);
            }
        } else {
            result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS, HDR_NAME);
        }
    }

    private ComponentType checkSlotType() {
        @Nullable final ComponentType compType = comptypeEJB.findByName(componentTypeString);
        if (compType == null) {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_CTYPE);
            return null;
        }

        if (SlotEJB.ROOT_COMPONENT_TYPE.equals(compType.getName())) {
            result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, AbstractDataLoader.HDR_OPERATION);
            return null;
        }

        return compType;
    }

    @Override
    protected void handleDelete() {
        final @Nullable Slot slotToDelete = slotEJB.findByName(name);
        try {
            if (slotToDelete == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
                return;
            }

            final ComponentType compType = slotToDelete.getComponentType();
            if (SlotEJB.ROOT_COMPONENT_TYPE.equals(compType.getName())) {
                result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, AbstractDataLoader.HDR_OPERATION);
                return;
            }
            slotEJB.delete(slotToDelete);
        } catch (EJBTransactionRolledbackException e) {
            handleLoadingError(LOGGER, e);
        }
    }

    @Override
    protected void handleRename() {
        try {
            final int startOldNameMarkerIndex = name.indexOf("[");
            final int endOldNameMarkerIndex = name.indexOf("]");
            if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                result.addRowMessage(ErrorMessage.RENAME_MISFORMAT, HDR_NAME);
                return;
            }

            final String oldName = name.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
            final String newName = name.substring(endOldNameMarkerIndex + 1).trim();
            final Slot slotToRename = slotEJB.findByName(oldName);
            if (slotToRename == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
                return;

            }
            if (slotEJB.findByName(newName) != null) {
                result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS, HDR_NAME);
                return;
            }

            final ComponentType compType = slotToRename.getComponentType();
            if (compType.getName().equals(SlotEJB.ROOT_COMPONENT_TYPE)) {
                result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, AbstractDataLoader.HDR_OPERATION);
                return;
            }
            slotToRename.setName(newName);
            slotEJB.save(slotToRename);
        } catch (EJBTransactionRolledbackException e) {
            handleLoadingError(LOGGER, e);
        }
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

    private void addOrUpdateSlot(Slot slotToAddOrUpdate, ComponentType compType) {
        slotToAddOrUpdate.setComponentType(compType);
        slotToAddOrUpdate.setDescription(description);
        slotToAddOrUpdate.setHostingSlot(isHosting);
        slotToAddOrUpdate.setBeamlinePosition(blp);
        slotToAddOrUpdate.setAssemblyComment(asmComment);
        slotToAddOrUpdate.setAssemblyPosition(asmPosition);
        slotToAddOrUpdate.setComment(comment);
        final PositionInformation positionInfo = slotToAddOrUpdate.getPositionInformation();
        positionInfo.setGlobalX(globalX);
        positionInfo.setGlobalY(globalY);
        positionInfo.setGlobalZ(globalZ);
        positionInfo.setGlobalRoll(globalRoll);
        positionInfo.setGlobalPitch(globalPitch);
        positionInfo.setGlobalYaw(globalYaw);
    }

    @Override
    public int getDataWidth() {
       return DATA_WIDTH;
    }

    @Override
    protected void setUpIndexesForFields() {
        // TODO implement
        throw new NotImplementedException();
    }
}
