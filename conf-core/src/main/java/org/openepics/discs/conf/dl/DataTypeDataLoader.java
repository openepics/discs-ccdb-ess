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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;

import org.openepics.discs.conf.dl.annotations.DataTypeLoader;
import org.openepics.discs.conf.dl.common.AbstractDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.DataTypeEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.util.Conversion;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.core.Seds;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;

/**
 * Implementation of loader for enumerations.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
@Stateless
@DataTypeLoader
public class DataTypeDataLoader extends AbstractDataLoader implements DataLoader {

    private static final Logger LOGGER = Logger.getLogger(DataTypeDataLoader.class.getCanonicalName());

    // Header column name constants
    private static final String HDR_NAME = "NAME";
    private static final String HDR_DESC = "DESCRIPTION";
    private static final String HDR_DEFINITION = "DEFINITION";

    private static final int COL_INDEX_NAME = 1;
    private static final int COL_INDEX_DESC = 2;
    private static final int COL_INDEX_DEFINITION = 3;

    private static final Set<String> REQUIRED_COLUMNS = new HashSet<>(Arrays.asList(HDR_DESC, HDR_DEFINITION));

    // Row data for individual cells within a row
    private String nameFld, descriptionFld, definitionFld;

    @Inject private DataTypeEJB dataTypeEJB;

    @Override
    public int getDataWidth() {
        return 4;
    }

    @Override
    protected Set<String> getRequiredColumnNames() {
       return REQUIRED_COLUMNS;
    }

    @Override
    protected Integer getUniqueColumnIndex() {
        return COL_INDEX_NAME;
    }

    @Override
    protected void assignMembersForCurrentRow() {
        nameFld = readCurrentRowCellForHeader(COL_INDEX_NAME);
        descriptionFld = readCurrentRowCellForHeader(COL_INDEX_DESC);
        definitionFld = readCurrentRowCellForHeader(COL_INDEX_DEFINITION);
    }

    @Override
    protected void setUpIndexesForFields() {
        final Builder<String, Integer> mapBuilder = ImmutableMap.builder();

        mapBuilder.put(HDR_NAME, COL_INDEX_NAME);
        mapBuilder.put(HDR_DEFINITION, COL_INDEX_DEFINITION);
        mapBuilder.put(HDR_DESC, COL_INDEX_DESC);

        indicies = mapBuilder.build();
    }

    @Override
    protected void handleUpdate(String actualCommand) {
        final DataType modifiedEnum = dataTypeEJB.findByName(nameFld);
        final List<String> enumValues = parseEnumDefinitions();

        if (isEnumModificationSafe(enumValues, modifiedEnum) && !result.isRowError()) {
            modifiedEnum.setName(nameFld);
            modifiedEnum.setDescription(descriptionFld);
            modifiedEnum.setDefinition(jsonDefinitionFromList(enumValues));
            dataTypeEJB.save(modifiedEnum);
        }
    }

    @Override
    protected void handleDelete(String actualCommand) {
        final DataType enumToDelete = dataTypeEJB.findByName(nameFld);
        if (enumToDelete == null) {
            result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
        } else {
            if (dataTypeEJB.isDataTypeUsed(enumToDelete, true)) {
                result.addRowMessage(ErrorMessage.DELETE_IN_USE, HDR_NAME);
            } else {
                dataTypeEJB.delete(enumToDelete);
            }
        }
    }

    @Override
    protected void handleRename() {
        result.addRowMessage(ErrorMessage.COMMAND_NOT_VALID, CMD_RENAME);
    }

    @Override
    protected void handleCreate(String actualCommand) {
        if (dataTypeEJB.findByName(nameFld) != null) {
            result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS, HDR_NAME);
        }
        final List<String> newDefs = parseEnumDefinitions();
        if (newDefs.size() < 2) {
            result.addRowMessage(ErrorMessage.ENUM_NOT_ENOUGH_ELEMENTS, HDR_NAME);
        }

        if (!result.isRowError()) {
            final DataType newEnum = new DataType(nameFld, descriptionFld, false, jsonDefinitionFromList(newDefs));
            dataTypeEJB.add(newEnum);
        }
    }

    /**
     * The method puts the error into the <code>result</code> property.
     *
     * @return A list of definitions.
     */
    private List<String> parseEnumDefinitions() {
        final String[] rawDefs = definitionFld.split("\\s*,\\s*");
        final List<String> defs = Lists.newArrayList();

        for (final String def : rawDefs) {
            if (!Strings.isNullOrEmpty(def.trim()) && isValidDef(def) && !isInList(def, defs)) {
                defs.add(def);
            }
        }

        return defs;
    }

    private boolean isValidDef(String def) {
        if (!def.matches("^\\w*$")) {
            result.addRowMessage(ErrorMessage.ENUM_INVALID_CHARACTERS, HDR_DEFINITION);
            return false;
        }
        return true;
    }

    private boolean isInList(String element, List<String> elements) {
        for (final String existingDef : elements) {
            if (existingDef.equalsIgnoreCase(element)) {
                return true;
            }
        }
        return false;
    }

    private String jsonDefinitionFromList(List<String> definitionValues) {
        Preconditions.checkNotNull(definitionValues);
        Preconditions.checkArgument(!definitionValues.isEmpty());
        final SedsEnum testEnum = Seds.newFactory().newEnum(definitionValues.get(0),
                definitionValues.toArray(new String[] {}));
        final JsonObject jsonEnum = Seds.newDBConverter().serialize(testEnum);
        return jsonEnum.toString();
    }

    /**
     * The method puts the error into the <code>result</code> property.
     * @param newDefinition
     * @param oldDefinition
     * @return
     */
    private boolean isEnumModificationSafe(List<String> newDefinition, DataType oldDefinition) {
        if (!dataTypeEJB.isDataTypeUsed(oldDefinition)) {
            return true;
        }
        // enumeration already used.
        // for each value in the old definition
        for (String enumValue : Conversion.prepareEnumSelections(oldDefinition)) {
            // check if it exists in the new definition
            if (!newDefinition.contains(enumValue)) {
                result.addRowMessage(ErrorMessage.MODIFY_IN_USE, HDR_NAME);
                return false;
            }
        }
        return true;
    }
}
