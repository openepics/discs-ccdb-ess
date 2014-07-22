package org.openepics.discs.conf.dl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.DataLoaderResult.RowFormatFailureReason;
import org.openepics.discs.conf.ejb.AuthEJB;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotProperty;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.As;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

@Stateless @SlotPairsLoaderQualifier public class SlotPairsDataLoader extends AbstractDataLoader implements DataLoader {

    @Inject private LoginManager loginManager;
    @Inject private AuthEJB authEJB;
    @Inject private SlotEJB slotEJB;
    @PersistenceContext private EntityManager em;
    private int childIndex, parentIndex, relationIndex;

    @Override public DataLoaderResult loadDataToDatabase(List<List<String>> inputRows) {

        if (inputRows != null && inputRows.size() > 0) {
            final List<String> fields = ImmutableList.of("CHILD", "PARENT", "RELATION");
            /*
             * List does not contain any rows that do not have a value (command)
             * in the first column. There should be no commands before "HEADER".
             */
            List<String> headerRow = inputRows.get(0);

            DataLoaderResult fieldsIndexSetupResult = setUpIndexesForFields(headerRow);
            HashMap<String, Integer> indexByPropertyName = indexByPropertyName(fields, headerRow);

            if (fieldsIndexSetupResult instanceof DataLoaderResult.FailureDataLoaderResult) {
                return fieldsIndexSetupResult;
            } else {
                for (List<String> row : inputRows.subList(1, inputRows.size())) {
                    final String rowNumber = row.get(0);
                    if (Objects.equal(row.get(1), CMD_HEADER)) {
                        headerRow = row;
                        fieldsIndexSetupResult = setUpIndexesForFields(headerRow);
                        if (fieldsIndexSetupResult instanceof DataLoaderResult.FailureDataLoaderResult) {
                            return fieldsIndexSetupResult;
                        } else {
                            continue; // skip the rest of the processing for
                                      // HEADER row
                        }
                    } else if (row.get(1).equals(CMD_END)) {
                        break;
                    }

                    final String command = As.notNull(row.get(1).toUpperCase());
                    final @Nullable String childString = row.get(childIndex);
                    final @Nullable String parentString = row.get(parentIndex);
                    final @Nullable String relationString = row.get(relationIndex);
                    final String modifiedBy = loginManager.getUserid();

                    if (childString == null || relationString == null || parentString == null) {
                        return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.REQUIRED_FIELD_MISSING);
                    }

                    switch (command) {
                    case CMD_UPDATE:

                        if (slotEJB.findSlotByName(childString) != null) {
                            if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.SLOT, EntityTypeOperation.UPDATE)) {
                                slotToAddOrUpdate = slotEJB.findSlotByName(childString);
                                final @Nullable ComponentType compType = comptypeEJB.findComponentTypeByName(parentString);
                                if (compType == null) {
                                    return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.WRONG_VALUE);
                                } else {
                                    slotToAddOrUpdate.setModifiedAt(new Date());
                                    slotToAddOrUpdate.setModifiedBy(modifiedBy);
                                    slotToAddOrUpdate.setComponentType(compType);
                                    slotToAddOrUpdate.setDescription(description);
                                    slotToAddOrUpdate.setIsHostingSlot(isHosting);
                                    slotToAddOrUpdate.setBeamlinePosition(blp);
                                    slotToAddOrUpdate.setGlobalX(globalX);
                                    slotToAddOrUpdate.setGlobalY(globalY);
                                    slotToAddOrUpdate.setGlobalZ(globalZ);
                                    slotToAddOrUpdate.setGlobalRoll(globalRoll);
                                    slotToAddOrUpdate.setGlobalPitch(globalPitch);
                                    slotToAddOrUpdate.setGlobalYaw(globalYaw);
                                    slotToAddOrUpdate.setAssemblyComment(asmComment);
                                    slotToAddOrUpdate.setAssemblyPosition(asmPosition);
                                    slotToAddOrUpdate.setComment(comment);

                                    final DataLoaderResult addOrUpdatePropertiesResult = addOrUpdateProperties(slotToAddOrUpdate, indexByPropertyName, row, rowNumber, modifiedBy);
                                    if (addOrUpdatePropertiesResult instanceof DataLoaderResult.FailureDataLoaderResult) {
                                        return addOrUpdatePropertiesResult;
                                    }
                                }
                            } else {
                                return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.UPDATE);
                            }
                        } else {
                            if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.SLOT, EntityTypeOperation.CREATE)) {
                                slotToAddOrUpdate = new Slot(childString, isHosting, modifiedBy);
                                final @Nullable ComponentType compType = comptypeEJB.findComponentTypeByName(parentString);
                                if (compType == null) {
                                    return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.WRONG_VALUE);
                                } else {
                                    slotToAddOrUpdate.setComponentType(compType);
                                    slotToAddOrUpdate.setDescription(description);
                                    slotToAddOrUpdate.setBeamlinePosition(blp);
                                    slotToAddOrUpdate.setGlobalX(globalX);
                                    slotToAddOrUpdate.setGlobalY(globalY);
                                    slotToAddOrUpdate.setGlobalZ(globalZ);
                                    slotToAddOrUpdate.setGlobalRoll(globalRoll);
                                    slotToAddOrUpdate.setGlobalPitch(globalPitch);
                                    slotToAddOrUpdate.setGlobalYaw(globalYaw);
                                    slotToAddOrUpdate.setAssemblyComment(asmComment);
                                    slotToAddOrUpdate.setAssemblyPosition(asmPosition);
                                    slotToAddOrUpdate.setComment(comment);

                                    slotEJB.addSlot(slotToAddOrUpdate);

                                    final DataLoaderResult addOrUpdatePropertiesResult = addOrUpdateProperties(slotToAddOrUpdate, indexByPropertyName, row, rowNumber, modifiedBy);
                                    if (addOrUpdatePropertiesResult instanceof DataLoaderResult.FailureDataLoaderResult) {
                                        return addOrUpdatePropertiesResult;
                                    }
                                }
                            } else {
                                return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.CREATE);
                            }
                        }
                        break;
                    case CMD_DELETE:
                        final Slot slotToDelete = slotEJB.findSlotByName(childString);
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.SLOT, EntityTypeOperation.DELETE)) {
                            if (slotToDelete == null) {
                                return new DataLoaderResult.EntityNotFoundFailureDataLoaderResult(rowNumber, EntityType.SLOT);
                            } else {
                                slotEJB.deleteLayoutSlot(slotToDelete);
                            }
                        } else {
                            return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.DELETE);
                        }
                        break;
                    case CMD_RENAME:
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.SLOT, EntityTypeOperation.RENAME)) {
                            final int startOldNameMarkerIndex = childString.indexOf("[");
                            final int endOldNameMarkerIndex = childString.indexOf("]");
                            if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                                return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.RENAME_MISFORMAT);
                            }

                            final String oldName = childString.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
                            final String newName = childString.substring(endOldNameMarkerIndex + 1).trim();

                            final Slot slotTypeToRename = slotEJB.findSlotByName(oldName);
                            if (slotTypeToRename != null) {
                                if (slotEJB.findSlotByName(newName) != null) {
                                    return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.DUPLICATE_ENTITY);
                                } else {
                                    slotTypeToRename.setName(newName);
                                    slotEJB.saveLayoutSlot(slotTypeToRename);
                                }
                            } else {
                                return new DataLoaderResult.EntityNotFoundFailureDataLoaderResult(rowNumber, EntityType.SLOT);
                            }
                        } else {
                            return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.RENAME);
                        }
                        break;
                    default:
                        return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.COMMAND_NOT_VALID);
                    }
                }
            }
        }

        return new DataLoaderResult.SuccessDataLoaderResult();
    }

    @Override protected DataLoaderResult setUpIndexesForFields(List<String> header) {
        final String rowNumber = header.get(0);
        childIndex = header.indexOf("CHILD");
        parentIndex = header.indexOf("PARENT");
        relationIndex = header.indexOf("RELATION");

        if (childIndex == -1 || relationIndex == -1 || parentIndex == -1) {
            return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.HEADER_FIELD_MISSING);
        } else {
            return new DataLoaderResult.SuccessDataLoaderResult();
        }
    }
}