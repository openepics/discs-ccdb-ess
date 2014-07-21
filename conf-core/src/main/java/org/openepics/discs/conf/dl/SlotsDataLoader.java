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

@Stateless @SlotsLoaderQualifier public class SlotsDataLoader extends AbstractDataLoader implements DataLoader {

    @Inject private LoginManager loginManager;
    @Inject private AuthEJB authEJB;
    @Inject private SlotEJB slotEJB;
    @Inject private ConfigurationEJB configurationEJB;
    @Inject private ComptypeEJB comptypeEJB;
    @PersistenceContext private EntityManager em;
    private int nameIndex, compTypeIndex, isHostingIndex, descriptionIndex, blpIndex, globalXIndex, globalYIndex, globalZIndex, globalRollIndex, globalYawIndex, globalPitchIndex, asmCommentIndex, asmPositionIndex, commentIndex;

    @Override public DataLoaderResult loadDataToDatabase(List<List<String>> inputRows) {

        if (inputRows != null && inputRows.size() > 0) {
            final List<String> fields = ImmutableList.of("NAME", "CTYPE", "DESCRIPTION", "IS-HOSTING-SLOT", "BLP", "GCX", "GCY", "GCZ", "GL-ROLL", "GL-YAW", "GL-PITCH", "ASM-COMMENT", "ASM-POSITION", "COMMENT");
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
                        indexByPropertyName = indexByPropertyName(fields, headerRow);
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
                    final @Nullable String name = row.get(nameIndex);
                    final @Nullable String description = descriptionIndex == -1 ? null : row.get(descriptionIndex);
                    final @Nullable String componentType = row.get(compTypeIndex);
                    final @Nullable String isHostingString = row.get(isHostingIndex);
                    final @Nullable String asmComment = asmCommentIndex == -1 ? null : row.get(asmCommentIndex);
                    final @Nullable String asmPosition = asmPositionIndex == -1 ? null : row.get(asmPositionIndex);
                    final @Nullable String comment = commentIndex == -1 ? null : row.get(commentIndex);
                    final String modifiedBy = loginManager.getUserid();

                    @Nullable Double blp = null;
                    @Nullable Double globalX = null;
                    @Nullable Double globalY = null;
                    @Nullable Double globalZ = null;
                    @Nullable Double globalRoll = null;
                    @Nullable Double globalPitch = null;
                    @Nullable Double globalYaw = null;

                    try {
                        blp = parseDouble(blpIndex == -1 ? null : row.get(blpIndex));
                        globalX = parseDouble(globalXIndex == -1 ? null : row.get(globalXIndex));
                        globalY = parseDouble(globalYIndex == -1 ? null : row.get(globalYIndex));
                        globalZ = parseDouble(globalZIndex == -1 ? null : row.get(globalZIndex));
                        globalRoll = parseDouble(globalRollIndex == -1 ? null : row.get(globalRollIndex));
                        globalPitch = parseDouble(globalPitchIndex == -1 ? null : row.get(globalPitchIndex));
                        globalYaw = parseDouble(globalYawIndex == -1 ? null : row.get(globalYawIndex));
                    } catch (NumberFormatException e) {
                        return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.WRONG_VALUE);
                    }

                    if (name == null || isHostingString == null || componentType == null) {
                        return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.REQUIRED_FIELD_MISSING);
                    }

                    final boolean isHosting;

                    if (isHostingString.equalsIgnoreCase("Y")) {
                        isHosting = true;
                    } else if (isHostingString.equalsIgnoreCase("N")) {
                        isHosting = false;
                    } else {
                        return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.WRONG_VALUE);
                    }

                    switch (command) {
                    case CMD_UPDATE:
                        final @Nullable Slot slotToAddOrUpdate;
                        if (slotEJB.findSlotByName(name) != null) {
                            if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.SLOT, EntityTypeOperation.UPDATE)) {
                                slotToAddOrUpdate = slotEJB.findSlotByName(name);
                                final @Nullable ComponentType compType = comptypeEJB.findComponentTypeByName(componentType);
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
                                slotToAddOrUpdate = new Slot(name, isHosting, modifiedBy);
                                final @Nullable ComponentType compType = comptypeEJB.findComponentTypeByName(componentType);
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
                        final Slot slotToDelete = slotEJB.findSlotByName(name);
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
                            final int startOldNameMarkerIndex = name.indexOf("[");
                            final int endOldNameMarkerIndex = name.indexOf("]");
                            if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                                return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.RENAME_MISFORMAT);
                            }

                            final String oldName = name.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
                            final String newName = name.substring(endOldNameMarkerIndex + 1).trim();

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

    private Double parseDouble(@Nullable String stringValue) {
        if (stringValue == null) {
            return null;
        } else {
            return Double.parseDouble(stringValue);
        }
    }

    @Override protected DataLoaderResult setUpIndexesForFields(List<String> header) {
        final String rowNumber = header.get(0);
        nameIndex = header.indexOf("NAME");
        descriptionIndex = header.indexOf("DESCRIPTION");
        compTypeIndex = header.indexOf("CTYPE");
        isHostingIndex = header.indexOf("IS-HOSTING-SLOT");
        blpIndex = header.indexOf("BLP");
        globalXIndex = header.indexOf("GCX");
        globalYIndex = header.indexOf("GCY");
        globalZIndex = header.indexOf("GCZ");
        globalRollIndex = header.indexOf("GL-ROLL");
        globalYawIndex = header.indexOf("GL-YAW");
        globalPitchIndex = header.indexOf("GL-PITCH");
        asmCommentIndex = header.indexOf("ASM-COMMENT");
        asmPositionIndex = header.indexOf("ASM-POSITION");
        commentIndex = header.indexOf("COMMENT");

        if (nameIndex == -1 || isHostingIndex == -1 || compTypeIndex == -1) {
            return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.HEADER_FIELD_MISSING);
        } else {
            return new DataLoaderResult.SuccessDataLoaderResult();
        }
    }

    private DataLoaderResult addOrUpdateProperties(Slot slot, Map<String, Integer> properties, List<String> row, String rowNumber, String modifiedBy) {
        final Iterator<String> propertiesIterator = properties.keySet().iterator();
        final List<SlotProperty> slotProperties = new ArrayList<>();
        if (slot.getSlotPropertyList() != null) {
            slotProperties.addAll(slot.getSlotPropertyList());
        }
        final Map<Property, SlotProperty> slotPropertyByProperty = new HashMap<>();

        for (SlotProperty slotProperty : slotProperties) {
            slotPropertyByProperty.put(slotProperty.getProperty(), slotProperty);
        }

        while (propertiesIterator.hasNext()) {
            final String propertyName = propertiesIterator.next();
            final int propertyIndex = properties.get(propertyName);
            final @Nullable Property property = configurationEJB.findPropertyByName(propertyName);
            final @Nullable String propertyValue = row.get(propertyIndex);
            if (property == null) {
                return new DataLoaderResult.EntityNotFoundFailureDataLoaderResult(rowNumber, EntityType.PROPERTY);
            } else if (slotPropertyByProperty.containsKey(property)) {
                final SlotProperty slotPropertyToUpdate = slotPropertyByProperty.get(property);
                if (propertyValue == null) {
                    slotEJB.deleteSlotProp(slotPropertyToUpdate);
                } else {
                    slotPropertyToUpdate.setPropValue(propertyValue);
                    slotPropertyToUpdate.setModifiedBy(modifiedBy);
                    slotEJB.saveSlotProp(slotPropertyToUpdate, false);
                }

            } else if (propertyValue != null) {
                final PropertyAssociation propAss = property.getAssociation();
                if (propAss == PropertyAssociation.ALL || propAss == PropertyAssociation.SLOT || propAss == PropertyAssociation.SLOT_DEVICE || propAss == PropertyAssociation.TYPE_SLOT) {
                    final SlotProperty slotPropertyToAdd = new SlotProperty(false, modifiedBy);
                    slotPropertyToAdd.setProperty(property);
                    slotPropertyToAdd.setPropValue(propertyValue);
                    slotPropertyToAdd.setSlot(slot);
                    slotEJB.addSlotProperty(slotPropertyToAdd);
                } else {
                    return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.WRONG_VALUE);
                }
            }
        }

        return new DataLoaderResult.SuccessDataLoaderResult();
    }
}