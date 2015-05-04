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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.UnitEJB;
import org.openepics.discs.conf.ent.Unit;

/**
 * Implementation of loader for units.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@Stateless
@UnitsLoaderQualifier
public class UnitsDataLoader extends AbstractDataLoader implements DataLoader {

    private static final Logger LOGGER = Logger.getLogger(UnitsDataLoader.class.getCanonicalName());

    // Header column name constants
    private static final String HDR_NAME = "NAME";
    private static final String HDR_QUANTITY = "QUANTITY";
    private static final String HDR_SYMBOL = "SYMBOL";
    private static final String HDR_DESC = "DESCRIPTION";

    private static final List<String> KNOWN_COLUMNS = Arrays.asList(HDR_NAME, HDR_QUANTITY, HDR_SYMBOL, HDR_DESC);
    private static final Set<String> REQUIRED_COLUMNS = new HashSet<>(Arrays.asList(HDR_QUANTITY,
            HDR_SYMBOL, HDR_DESC));

    @Inject private UnitEJB unitEJB;

    /**
     * Cache of {@link Unit}s, indexed by name
     */
    private Map<String, Unit> unitByName;

    // Row data for individual cells within a row
    private String nameFld, quantityFld, symbolFld, descriptionFld;

    @Override
    protected void init() {
        super.init();

        // Reload unit-cache
        unitByName = new HashMap<>();
        for (Unit unit : unitEJB.findAll())
            unitByName.put(unit.getName(), unit);
    }

    @Override
    protected List<String> getKnownColumnNames() {
        return KNOWN_COLUMNS;
    }

    @Override
    protected Set<String> getRequiredColumnNames() {
        return REQUIRED_COLUMNS;
    }

    @Override
    protected String getUniqueColumnName() {
        return HDR_NAME;
    }

    @Override
    protected void assignMembersForCurrentRow() {
        nameFld = readCurrentRowCellForHeader(HDR_NAME);
        quantityFld = readCurrentRowCellForHeader(HDR_QUANTITY);
        symbolFld = readCurrentRowCellForHeader(HDR_SYMBOL);
        descriptionFld = readCurrentRowCellForHeader(HDR_DESC);
    }

    @Override
    protected void handleUpdate() {
        if (unitByName.containsKey(nameFld)) {
            try {
                final Unit unitToUpdate = unitByName.get(nameFld);
                // if unit is in use, we cannot modify name or symbol attributes.
                if (unitEJB.isUnitUsed(unitToUpdate) && !unitToUpdate.getSymbol().equals(symbolFld)) {
                    result.addRowMessage(ErrorMessage.MODIFY_IN_USE, HDR_SYMBOL);
                } else {
                    unitToUpdate.setDescription(descriptionFld);
                    unitToUpdate.setQuantity(quantityFld);
                    unitToUpdate.setSymbol(symbolFld);
                    unitEJB.save(unitToUpdate);
                }
            } catch (EJBTransactionRolledbackException e) {
                handleLoadingError(LOGGER, e);
            }
        } else {
            try {
                final Unit unitToAdd = new Unit(nameFld, quantityFld, symbolFld, descriptionFld);
                unitEJB.add(unitToAdd);
                unitByName.put(unitToAdd.getName(), unitToAdd);
            } catch (EJBTransactionRolledbackException e) {
                handleLoadingError(LOGGER, e);
            }
        }
    }

    @Override
    protected void handleDelete() {
        try {
            final Unit unitToDelete = unitByName.get(nameFld);
            if (unitToDelete == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
            } else if (unitEJB.isUnitUsed(unitToDelete)) {
                result.addRowMessage(ErrorMessage.DELETE_IN_USE);
            } else {
                unitEJB.delete(unitToDelete);
                unitByName.remove(unitToDelete.getName());
            }
        } catch (EJBTransactionRolledbackException e) {
            handleLoadingError(LOGGER, e);
        }
    }

    @Override
    protected void handleRename() {
        try {
            final int startOldNameMarkerIndex = nameFld.indexOf("[");
            final int endOldNameMarkerIndex = nameFld.indexOf("]");
            if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                result.addRowMessage(ErrorMessage.RENAME_MISFORMAT, HDR_NAME);
                return;
            }

            final String oldName = nameFld.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
            final String newName = nameFld.substring(endOldNameMarkerIndex + 1).trim();

            if (unitByName.containsKey(oldName)) {
                if (unitByName.containsKey(newName)) {
                    result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS, HDR_NAME);
                } else {
                    final Unit unitToRename = unitByName.get(oldName);
                    // if unit is in use, we cannot modify name or symbol attributes.
                    if (unitEJB.isUnitUsed(unitToRename)) {
                        result.addRowMessage(ErrorMessage.MODIFY_IN_USE, HDR_NAME);
                    } else {
                        unitToRename.setName(newName);
                        unitEJB.save(unitToRename);
                        unitByName.remove(oldName);
                        unitByName.put(newName, unitToRename);
                    }
                }
            } else {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
            }
        } catch (EJBTransactionRolledbackException e) {
            handleLoadingError(LOGGER, e);
        }
    }
}

