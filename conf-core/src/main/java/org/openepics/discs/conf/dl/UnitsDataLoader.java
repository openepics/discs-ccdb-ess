package org.openepics.discs.conf.dl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.DataLoaderResult.RowFormatFailureReason;
import org.openepics.discs.conf.ejb.AuthEJB;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.As;

/**
 * Implementation of loader for units.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Stateless
@UnitLoaderQualifier
public class UnitsDataLoader extends AbstractDataLoader implements DataLoader {

    @Inject private LoginManager loginManager;
    @Inject private AuthEJB authEJB;
    @Inject private ConfigurationEJB configurationEJB;
    @PersistenceContext private EntityManager em;
    @Resource private EJBContext context;

    private Map<String, Unit> unitByName;
    private int nameIndex, quantityIndex, symbolIndex, descriptionIndex;

    @Override public DataLoaderResult loadDataToDatabase(List<List<String>> inputRows) {
        init();

        /*
         * List does not contain any rows that do not have a value (command)
         * in the first column. There should be no commands before "HEADER".
         */
        List<String> headerRow = inputRows.get(0);
        DataLoaderResult fieldsIndexSetupResult = setUpIndexesForFields(headerRow);

        if (fieldsIndexSetupResult instanceof DataLoaderResult.FailureDataLoaderResult) {
            return fieldsIndexSetupResult;
        } else {
            for (List<String> row : inputRows.subList(1, inputRows.size())) {
                final String rowNumber = row.get(0);
                if (row.get(1).equals(CMD_HEADER)) {
                    headerRow = row;
                    fieldsIndexSetupResult = setUpIndexesForFields(headerRow);
                    if (fieldsIndexSetupResult instanceof DataLoaderResult.FailureDataLoaderResult) {
                        return fieldsIndexSetupResult;
                    } else {
                        continue; // skip the rest of the processing for HEADER row
                    }
                } else if (row.get(1).equals(CMD_END)) {
                    break;
                }

                final String command = As.notNull(row.get(1).toUpperCase());
                final @Nullable String name = row.get(nameIndex);
                final @Nullable String quantity = row.get(quantityIndex);
                final @Nullable String symbol = row.get(symbolIndex);
                final @Nullable String description = row.get(descriptionIndex);
                final Date modifiedAt = new Date();
                final String modifiedBy = loginManager.getUserid();

                if (name == null || quantity == null || symbol == null || description == null) {
                    return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.REQUIRED_FIELD_MISSING);
                }

                switch (command) {
                case CMD_UPDATE:
                    if (unitByName.containsKey(name)) {
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.UPDATE)) {
                            final Unit unitToUpdate = unitByName.get(name);
                            unitToUpdate.setDescription(description);
                            unitToUpdate.setQuantity(quantity);
                            unitToUpdate.setSymbol(symbol);
                            unitToUpdate.setModifiedAt(modifiedAt);
                            configurationEJB.saveUnit(unitToUpdate);
                        } else {
                            return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.UPDATE);
                        }
                    } else {
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.CREATE)) {
                            final Unit unitToAdd = new Unit(name, quantity, symbol, description, modifiedBy);
                            configurationEJB.addUnit(unitToAdd);
                            unitByName.put(unitToAdd.getUnitName(), unitToAdd);
                        } else {
                            return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.CREATE);
                        }
                    }
                    break;
                case CMD_DELETE:
                    if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.DELETE)) {
                        final Unit unitToDelete = unitByName.get(name);
                        if (unitToDelete == null) {
                           return new DataLoaderResult.EntityNotFoundFailureDataLoaderResult(rowNumber, EntityType.UNIT);
                        } else {
                            configurationEJB.deleteUnit(unitToDelete);
                            unitByName.remove(unitToDelete.getUnitName());
                        }
                    } else {
                        return new DataLoaderResult.NotAuthorizedFailureDataLoaderResult(EntityTypeOperation.DELETE);
                    }
                    break;
                case CMD_RENAME:
                    if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.RENAME)) {
                        final int startOldNameMarkerIndex = name.indexOf("[");
                        final int endOldNameMarkerIndex = name.indexOf("]");
                        if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                            return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.RENAME_MISFORMAT);
                        }

                        final String oldName = name.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
                        final String newName = name.substring(endOldNameMarkerIndex + 1).trim();

                        if (unitByName.containsKey(oldName)) {
                            if (unitByName.containsKey(newName)) {
                                return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.DUPLICATE_ENTITY);
                            } else {
                                final Unit unitToRename = unitByName.get(oldName);
                                unitToRename.setUnitName(newName);
                                configurationEJB.saveUnit(unitToRename);
                                unitByName.remove(oldName);
                                unitByName.put(newName, unitToRename);
                            }
                        } else {
                            return new DataLoaderResult.EntityNotFoundFailureDataLoaderResult(rowNumber, EntityType.UNIT);
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

        return new DataLoaderResult.SuccessDataLoaderResult();
    }

    /**
     * Local cache of all units by their names to speed up operations.
     */
    private void init() {
        unitByName = new HashMap<>();
        for (Unit unit : configurationEJB.findUnits()) {
            unitByName.put(unit.getUnitName(), unit);
        }
    }

    @Override protected DataLoaderResult setUpIndexesForFields(List<String> header) {
        final String rowNumber = header.get(0);
        nameIndex = header.indexOf("NAME");
        quantityIndex = header.indexOf("QUANTITY");
        symbolIndex = header.indexOf("SYMBOL");
        descriptionIndex = header.indexOf("DESCRIPTION");

        if (nameIndex == -1 || quantityIndex == -1 || symbolIndex == -1 || descriptionIndex == -1) {
            return new DataLoaderResult.RowFormatFailureDataLoaderResult(rowNumber, RowFormatFailureReason.HEADER_FIELD_MISSING);
        } else {
            return new DataLoaderResult.SuccessDataLoaderResult();
        }
    }
}
