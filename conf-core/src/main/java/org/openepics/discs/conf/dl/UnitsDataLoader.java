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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.ejb.UnitEJB;
import org.openepics.discs.conf.ent.Unit;

import com.google.common.base.Preconditions;

/**
 * Implementation of loader for units.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Stateless
@UnitsLoaderQualifier
public class UnitsDataLoader extends AbstractDataLoader implements DataLoader {
    private static final Logger LOGGER = Logger.getLogger(UnitsDataLoader.class.getCanonicalName());

    @Inject private UnitEJB unitEJB;

    private Map<String, Unit> unitByName;
    private int nameIndex, quantityIndex, symbolIndex, descriptionIndex;

    @Override public DataLoaderResult loadDataToDatabase(List<List<String>> inputRows) {
        init();

        /*
         * List does not contain any rows that do not have a value (command)
         * in the first column. There should be no commands before "HEADER".
         */
        List<String> headerRow = inputRows.get(0);

        checkForDuplicateHeaderEntries(headerRow);
        if (rowResult.isError()) {
            loaderResult.addResult(rowResult);
            return loaderResult;
        }
        setUpIndexesForFields(headerRow);

        if (!rowResult.isError()) {
fileProcessing:
            for (List<String> row : inputRows.subList(1, inputRows.size())) {
                final String rowNumber = row.get(0);
                loaderResult.addResult(rowResult);
                rowResult = new DataLoaderResult();
                if (row.get(commandIndex).equals(CMD_HEADER)) {
                    headerRow = row;
                    checkForDuplicateHeaderEntries(headerRow);
                    if (rowResult.isError()) {
                        loaderResult.addResult(rowResult);
                    } else {
                        setUpIndexesForFields(headerRow);
                    }
                    if (rowResult.isError()) {
                        return loaderResult;
                    } else {
                        continue; // skip the rest of the processing for HEADER row
                    }
                } else if (row.get(1).equals(CMD_END)) {
                    break;
                }

                final String command = Preconditions.checkNotNull(row.get(commandIndex).toUpperCase());
                final @Nullable String name = row.get(nameIndex);
                final @Nullable String quantity = row.get(quantityIndex);
                final @Nullable String symbol = row.get(symbolIndex);
                final @Nullable String description = row.get(descriptionIndex);

                final Date modifiedAt = new Date();

                if (name == null) {
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, rowNumber, headerRow.get(nameIndex)));
                } else if (quantity == null && !command.equals(CMD_RENAME) && !command.equals(CMD_DELETE)) {
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, rowNumber, headerRow.get(quantityIndex)));
                } else if (symbol == null && !command.equals(CMD_RENAME) && !command.equals(CMD_DELETE)) {
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, rowNumber, headerRow.get(symbolIndex)));
                } else if (description == null && !command.equals(CMD_RENAME) && !command.equals(CMD_DELETE)) {
                    rowResult.addMessage(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, rowNumber, headerRow.get(descriptionIndex)));
                }

                if (!rowResult.isError()) {
                    switch (command) {
                        case CMD_UPDATE:
                            if (unitByName.containsKey(name)) {
                                try {
                                    final Unit unitToUpdate = unitByName.get(name);
                                    unitToUpdate.setDescription(description);
                                    unitToUpdate.setQuantity(quantity);
                                    unitToUpdate.setSymbol(symbol);
                                    unitToUpdate.setModifiedAt(modifiedAt);
                                    unitEJB.save(unitToUpdate);
                                } catch (EJBTransactionRolledbackException e) {
                                    LOGGER.log(Level.FINE, e.getMessage(), e);
                                    if (e.getCause() instanceof org.openepics.discs.conf.security.SecurityException) {
                                        rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                                    } else {
                                        rowResult.addMessage(new ValidationMessage(ErrorMessage.UNKNOWN, rowNumber, headerRow.get(commandIndex)));
                                    }
                                    // cannot continue when the transaction is already rolled back
                                    break fileProcessing;
                                }
                            } else {
                                try {
                                    final Unit unitToAdd = new Unit(name, quantity, symbol, description);
                                    unitEJB.add(unitToAdd);
                                    unitByName.put(unitToAdd.getName(), unitToAdd);
                                } catch (EJBTransactionRolledbackException e) {
                                    LOGGER.log(Level.FINE, e.getMessage(), e);
                                    if (e.getCause() instanceof org.openepics.discs.conf.security.SecurityException) {
                                        rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                                    } else {
                                        rowResult.addMessage(new ValidationMessage(ErrorMessage.UNKNOWN, rowNumber, headerRow.get(commandIndex)));
                                    }
                                    // cannot continue when the transaction is already rolled back
                                    break fileProcessing;
                                }
                            }
                            break;
                        case CMD_DELETE:
                            try {
                                final Unit unitToDelete = unitByName.get(name);
                                if (unitToDelete == null) {
                                    rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, headerRow.get(nameIndex)));
                                    continue;
                                } else {
                                    unitEJB.delete(unitToDelete);
                                    unitByName.remove(unitToDelete.getName());
                                }
                            } catch (EJBTransactionRolledbackException e) {
                                LOGGER.log(Level.FINE, e.getMessage(), e);
                                if (e.getCause() instanceof org.openepics.discs.conf.security.SecurityException) {
                                    rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                                } else {
                                    rowResult.addMessage(new ValidationMessage(ErrorMessage.UNKNOWN, rowNumber, headerRow.get(commandIndex)));
                                }
                                // cannot continue when the transaction is already rolled back
                                break fileProcessing;
                            }
                            break;
                        case CMD_RENAME:
                            try {
                                final int startOldNameMarkerIndex = name.indexOf("[");
                                final int endOldNameMarkerIndex = name.indexOf("]");
                                if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                                    rowResult.addMessage(new ValidationMessage(ErrorMessage.RENAME_MISFORMAT, rowNumber, headerRow.get(nameIndex)));
                                    continue;
                                }

                                final String oldName = name.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
                                final String newName = name.substring(endOldNameMarkerIndex + 1).trim();

                                if (unitByName.containsKey(oldName)) {
                                    if (unitByName.containsKey(newName)) {
                                        rowResult.addMessage(new ValidationMessage(ErrorMessage.NAME_ALREADY_EXISTS, rowNumber, headerRow.get(nameIndex)));
                                        continue;
                                    } else {
                                        final Unit unitToRename = unitByName.get(oldName);
                                        unitToRename.setName(newName);
                                        unitEJB.save(unitToRename);
                                        unitByName.remove(oldName);
                                        unitByName.put(newName, unitToRename);
                                    }
                                } else {
                                    rowResult.addMessage(new ValidationMessage(ErrorMessage.ENTITY_NOT_FOUND, rowNumber, headerRow.get(nameIndex)));
                                    continue;
                                }
                            } catch (EJBTransactionRolledbackException e) {
                                LOGGER.log(Level.FINE, e.getMessage(), e);
                                if (e.getCause() instanceof org.openepics.discs.conf.security.SecurityException) {
                                    rowResult.addMessage(new ValidationMessage(ErrorMessage.NOT_AUTHORIZED, rowNumber, headerRow.get(commandIndex)));
                                } else {
                                    rowResult.addMessage(new ValidationMessage(ErrorMessage.UNKNOWN, rowNumber, headerRow.get(commandIndex)));
                                }
                                // cannot continue when the transaction is already rolled back
                                break fileProcessing;
                            }
                            break;
                        default:
                            rowResult.addMessage(new ValidationMessage(ErrorMessage.COMMAND_NOT_VALID, rowNumber, headerRow.get(commandIndex)));
                    }
                }
            }
        }
        loaderResult.addResult(rowResult);
        return loaderResult;
    }

    /**
     * Local cache of all units by their names to speed up operations.
     */
    private void init() {
        loaderResult = new DataLoaderResult();
        unitByName = new HashMap<>();
        for (Unit unit : unitEJB.findAll()) {
            unitByName.put(unit.getName(), unit);
        }
    }

    @Override protected void setUpIndexesForFields(List<String> header) {
        final String rowNumber = header.get(0);
        rowResult = new DataLoaderResult();
        nameIndex = header.indexOf("NAME");
        quantityIndex = header.indexOf("QUANTITY");
        symbolIndex = header.indexOf("SYMBOL");
        descriptionIndex = header.indexOf("DESCRIPTION");

        if (nameIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "NAME"));
        } else if (quantityIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "QUANTITY"));
        } else if (symbolIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "SYMBOL"));
        } else if (descriptionIndex == -1) {
            rowResult.addMessage(new ValidationMessage(ErrorMessage.HEADER_FIELD_MISSING, rowNumber, "DESCRIPTION"));
        }
    }
}
