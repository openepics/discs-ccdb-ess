package org.openepics.discs.conf.dl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.openepics.discs.conf.dl.common.AbstractEntityWithPropertiesDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.PositionInformation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPropertyValue;

@Stateless
@SlotsDataLoaderQualifier
public class SlotsDataLoader extends AbstractEntityWithPropertiesDataLoader<SlotPropertyValue> implements DataLoader {
    /**
     * A key for {@link DataLoaderResult#getContextualData()} that will hold a {@link Set} of {@link Slot}s
     */
    public static final String CTX_NEW_SLOTS = "CTX_NEW_SLOTS";

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

    private static final List<String> KNOWN_COLUMNS = Arrays.asList(HDR_NAME, HDR_CTYPE, HDR_DESCRIPTION,
            HDR_IS_HOSTING_SLOT, HDR_BLP, HDR_GCX, HDR_GCY, HDR_GCZ, HDR_GL_ROLL, HDR_GL_YAW,
            HDR_GL_PITCH, HDR_ASM_COMMENT, HDR_ASM_POSITION, HDR_COMMENT);

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
        newSlots = new ArrayList<>();

        result.getContextualData().put(CTX_NEW_SLOTS, newSlots);
    }

    @Override
    protected List<String> getKnownColumnNames() { return KNOWN_COLUMNS; }

    @Override
    protected Set<String> getRequiredColumnNames() { return REQUIRED_COLUMNS; }

    @Override
    protected String getUniqueColumnName() { return HDR_NAME; }

    @Override
    protected void assignMembersForCurrentRow() {
        name               = readCurrentRowCellForHeader(HDR_NAME);
        description        = readCurrentRowCellForHeader(HDR_DESCRIPTION);
        componentTypeString= readCurrentRowCellForHeader(HDR_CTYPE);
        asmComment         = readCurrentRowCellForHeader(HDR_ASM_COMMENT);
        asmPosition        = readCurrentRowCellForHeader(HDR_ASM_POSITION);
        comment            = readCurrentRowCellForHeader(HDR_COMMENT);

        @Nullable String isHostingString = readCurrentRowCellForHeader(HDR_IS_HOSTING_SLOT);
        isHosting = null;
        if (isHostingString != null && !isHostingString.equalsIgnoreCase(Boolean.FALSE.toString()) && !isHostingString.equalsIgnoreCase(Boolean.TRUE.toString())) {
            result.addRowMessage(ErrorMessage.SHOULD_BE_BOOLEAN_VALUE, HDR_IS_HOSTING_SLOT);
        }

        try {
            isHosting = Boolean.parseBoolean(isHostingString);
        } catch (Exception e) {
            isHosting = null;
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
        @Nullable Slot slot = slotEJB.findByName(name);
        @Nullable final ComponentType compType = comptypeEJB.findByName(componentTypeString);
        setPropertyValueClass(SlotPropertyValue.class);
        if (compType == null) {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_CTYPE);
            return;
        }

        if (SlotEJB.ROOT_COMPONENT_TYPE.equals(compType.getName())) {
            result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, HDR_CTYPE);
            return;
        }

        try {
            // Handle insertion
            if (slot == null) {
                slot = new Slot(name, isHosting);
                addOrUpdateSlot(slot, compType);
                newSlots.add(slot);

                slotEJB.addSlotToParentWithPropertyDefs(slot, null, true);
            } else {
                addOrUpdateSlot(slot, compType);
            }
            addOrUpdateProperties(slot);

        } catch (Exception e) {
            // ToDo add proper handling of security exception and other exceptions (already in commit 1a78b93)
            result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, CMD_HEADER);
        }
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
                result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, CMD_HEADER);
                return;
            }
            slotEJB.delete(slotToDelete);
        } catch (Exception e) {
            // ToDo add proper handling of security exception and other exceptions (already in commit 1a78b93)
            result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, CMD_HEADER);
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
                result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, CMD_HEADER);
                return;
            }
            slotToRename.setName(newName);
            slotEJB.save(slotToRename);
        } catch (Exception e) {
            // ToDo add proper handling of security exception and other exceptions (already in commit 1a78b93)
            result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, CMD_HEADER);
        }
    }

    @Override
    protected boolean checkPropertyAssociation(Property property) { return property.isSlotAssociation(); }

    @SuppressWarnings("unchecked")
    @Override
    protected DAO<Slot> getDAO() {
        return slotEJB;
    }

    private Double readCurrentRowCellForHeaderAsDouble(String columnName) {
        @Nullable String stringValue = readCurrentRowCellForHeader(columnName);

        if (stringValue == null)
            return null;

        try {
            double doubleValue = Double.parseDouble(stringValue);
                return doubleValue;
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
}
