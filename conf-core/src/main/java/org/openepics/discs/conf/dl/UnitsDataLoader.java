package org.openepics.discs.conf.dl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.ejb.AuthEJB;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.As;
import org.openepics.discs.conf.util.IllegalImportFileFormatException;
import org.openepics.discs.conf.util.NotAuthorizedException;

/**
 * Data loader for units.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Stateless public class UnitsDataLoader extends DataLoader {

    @Inject private LoginManager loginManager;
    @Inject private AuthEJB authEJB;
    @Inject private ConfigurationEJB configurationEJB;
    @PersistenceContext private EntityManager em;
    private Map<String, Unit> unitByName;
    private List<Unit> unitsToAddOrUpdate;
    private List<Unit> unitsToDelete;
    private Map<Unit, String> unitsToRename;
    private int nameIndex, quantityIndex, symbolIndex, baseUnitExprIndex, descriptionIndex;
    private List<Unit> units;

    @Override public void loadData(InputStream stream) throws IllegalImportFileFormatException, NotAuthorizedException {
        init();

        final List<List<String>> inputRows = ExcelImportFileReader.importExcelFile(stream);

        if (inputRows != null && inputRows.size() > 0) {
            /*
             * List does not contain any rows that do not have a value (command)
             * in the first column. There should be no commands before "HEADER".
             */
            List<String> headerRow = inputRows.get(0);

            unitsToAddOrUpdate = new ArrayList<>();
            unitsToDelete = new ArrayList<>();
            unitsToRename = new HashMap<>();
            setUpIndexesForFields(headerRow);

            for (List<String> row : inputRows.subList(1, inputRows.size())) {
                final String rowNumber = row.get(0);
                if (row.get(1).equals(CMD_HEADER)) {
                    headerRow = row;
                    setUpIndexesForFields(headerRow);
                    continue; // skip the rest of the processing for HEADER row
                } else if (row.get(1).equals(CMD_END)) {
                    break;
                }

                final String command = As.notNull(row.get(1).toUpperCase());
                @Nullable final String name = row.get(nameIndex);
                @Nullable final String quantity = row.get(quantityIndex);
                @Nullable final String symbol = row.get(symbolIndex);
                @Nullable final String description = row.get(descriptionIndex);
                @Nullable final String baseUnitExpr = row.get(baseUnitExprIndex);
                final String modifiedBy = loginManager.getUserid();

                if (name == null || quantity == null || symbol == null || description == null) {
                    throw new IllegalImportFileFormatException("Required fields should not be empty.", rowNumber);
                }

                switch (command) {
                case CMD_UPDATE:
                    if (unitByName.containsKey(name)) {
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.UPDATE)) {
                            unitsToAddOrUpdate.add(new Unit(name, quantity, symbol, baseUnitExpr, description, modifiedBy));
                        } else {
                            throw new NotAuthorizedException(EntityTypeOperation.UPDATE, EntityType.UNIT);
                        }
                    } else {
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.CREATE)) {
                            final Unit unitToAdd = new Unit(name, quantity, symbol, baseUnitExpr, description, modifiedBy);
                            unitsToAddOrUpdate.add(unitToAdd);
                        } else {
                            throw new NotAuthorizedException(EntityTypeOperation.CREATE, EntityType.UNIT);
                        }
                    }
                    break;
                case CMD_DELETE:
                    if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.DELETE)) {
                        final Unit unitToDelete = unitByName.get(name);
                        if (unitToDelete == null) {
                            throw new IllegalImportFileFormatException("Unit to be deleted does not exist!", rowNumber);
                        } else {
                            unitsToDelete.add(unitToDelete);
                            unitByName.remove(unitToDelete);
                        }
                    } else {
                        throw new NotAuthorizedException(EntityTypeOperation.DELETE, EntityType.UNIT);
                    }
                    break;
                case CMD_RENAME:
                    if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.UNIT, EntityTypeOperation.RENAME)) {
                        final int startOldNameMarkerIndex = name.indexOf("[");
                        final int endOldNameMarkerIndex = name.indexOf("]");
                        if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                            throw new IllegalImportFileFormatException("RENAME command must have unit name which is to be renamed, defined between [] \nfollowed by new name. Example: [old name] new name.", rowNumber);
                        } else if (unitByName.containsKey(name.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex))) {
                            if (unitByName.containsKey(name.substring(endOldNameMarkerIndex + 2).trim())) {
                                throw new IllegalImportFileFormatException("Cannot rename unit to \"" + name.substring(endOldNameMarkerIndex + 2).trim() + "\" since unit with this name already exists.", rowNumber);
                            } else {
                                unitsToRename.put(unitByName.get(name.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex)), name.substring(endOldNameMarkerIndex + 2).trim());
                            }
                        } else {
                            throw new IllegalImportFileFormatException("Unit to be renamed does not exist!", rowNumber);
                        }
                    } else {
                        throw new NotAuthorizedException(EntityTypeOperation.RENAME, EntityType.UNIT);
                    }
                    break;
                default:
                    throw new IllegalImportFileFormatException(command + " is not a valid command!", rowNumber);
                }
            }

            doImport();
        }
    }

    @Override protected void doImport() {
        for (Unit unit : unitsToAddOrUpdate) {
            if (unitByName.containsKey(unit.getUnitName())) {
                final Unit unitToUpdate = unitByName.get(unit.getUnitName());
                unitToUpdate.setBaseUnitExpr(unit.getBaseUnitExpr());
                unitToUpdate.setDescription(unit.getDescription());
                unitToUpdate.setModifiedAt(unit.getModifiedAt());
                unitToUpdate.setQuantity(unit.getQuantity());
                unitToUpdate.setSymbol(unit.getSymbol());
                unitToUpdate.setModifiedBy(unit.getModifiedBy());
                unitToUpdate.setModifiedAt(unit.getModifiedAt());
                configurationEJB.saveUnit(unitToUpdate);
            } else {
                configurationEJB.addUnit(unit);
                unitByName.put(unit.getUnitName(), unit);
            }
        }

        for (Unit unit : unitsToDelete) {
            configurationEJB.deleteUnit(unitByName.get(unit.getUnitName()));
        }

        final Iterator<Unit> unitRenameIterator = unitsToRename.keySet().iterator();
        while (unitRenameIterator.hasNext()) {
            final Unit unitToRename = unitRenameIterator.next();
            unitToRename.setUnitName(unitsToRename.get(unitToRename));
            configurationEJB.saveUnit(unitToRename);
        }
    }

    private void init() {
        units = configurationEJB.findUnits();
        unitByName = new HashMap<>();
        for (Unit unit : units) {
            unitByName.put(unit.getUnitName(), unit);
        }
    }

    @Override protected void setUpIndexesForFields(List<String> header) throws IllegalImportFileFormatException {
        nameIndex = header.indexOf("NAME");
        quantityIndex = header.indexOf("QUANTITY");
        symbolIndex = header.indexOf("SYMBOL");
        baseUnitExprIndex = header.indexOf("EXPR");
        descriptionIndex = header.indexOf("DESCRIPTION");

        if (nameIndex == -1 || quantityIndex == -1 || symbolIndex == -1 || baseUnitExprIndex == -1 || descriptionIndex == -1) {
            throw new IllegalImportFileFormatException("Header row does not contain required fields!", header.get(0));
        }
    }
}
