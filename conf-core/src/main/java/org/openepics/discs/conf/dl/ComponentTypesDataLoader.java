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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.NotImplementedException;
import org.openepics.discs.conf.dl.common.AbstractEntityWithPropertiesDataLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;

/**
 * Implementation of data loader for device types.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 */
@Stateless
@ComponentTypesLoaderQualifier
public class ComponentTypesDataLoader extends AbstractEntityWithPropertiesDataLoader<ComptypePropertyValue>
    implements DataLoader {

    private static final Logger LOGGER = Logger.getLogger(ComponentTypesDataLoader.class.getCanonicalName());

    // Header column name constants
    private static final String HDR_NAME = "NAME";
    private static final String HDR_DESC = "DESCRIPTION";

    private static final int COL_INDEX_NAME = 1;  // TODO fix
    private static final int COL_INDEX_DESC = 2;  // TODO fix


    private static final Set<String> REQUIRED_COLUMNS = new HashSet<>();

    // Fields for row cells
    private String nameFld, descriptionFld;

    @Inject private ComptypeEJB comptypeEJB;

    @Override
    protected void init() {
        super.init();
        setPropertyValueClass(ComptypePropertyValue.class);
    }

    @Override
    protected Set<String> getRequiredColumnNames() {
        return REQUIRED_COLUMNS;
    }

    @Override
    protected @Nullable Integer getUniqueColumnIndex() {
        return new Integer(COL_INDEX_NAME);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected DAO<ComponentType> getDAO() {
        return comptypeEJB;
    }

    @Override
    protected void assignMembersForCurrentRow() {
        nameFld = readCurrentRowCellForHeader(COL_INDEX_NAME);
        descriptionFld = readCurrentRowCellForHeader(COL_INDEX_DESC);
    }

    @Override
    protected void handleUpdate() {
        final ComponentType componentTypeToUpdate = comptypeEJB.findByName(nameFld);
        if (componentTypeToUpdate != null) {
            if (componentTypeToUpdate.getName().equals(SlotEJB.ROOT_COMPONENT_TYPE)) {
                result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, CMD_HEADER);
            }
            try {
                componentTypeToUpdate.setDescription(descriptionFld);
                addOrUpdateProperties(componentTypeToUpdate);
            } catch (EJBTransactionRolledbackException e) {
                handleLoadingError(LOGGER, e);
            }
        } else {
            try {
                final ComponentType compTypeToAdd = new ComponentType(nameFld);
                compTypeToAdd.setDescription(descriptionFld);
                comptypeEJB.add(compTypeToAdd);
                addOrUpdateProperties(compTypeToAdd);
            } catch (EJBTransactionRolledbackException e) {
                handleLoadingError(LOGGER, e);
            }
        }
    }

    @Override
    protected void handleDelete() {
        final ComponentType componentTypeToDelete = comptypeEJB.findByName(nameFld);
        try {
            if (componentTypeToDelete == null) {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
            } else {
                if (SlotEJB.ROOT_COMPONENT_TYPE.equals(componentTypeToDelete.getName())) {
                    result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, CMD_HEADER);
                }
                comptypeEJB.delete(componentTypeToDelete);
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

            final ComponentType componentTypeToRename = comptypeEJB.findByName(oldName);
            if (componentTypeToRename != null) {
                if (SlotEJB.ROOT_COMPONENT_TYPE.equals(componentTypeToRename.getName())) {
                    result.addRowMessage(ErrorMessage.NOT_AUTHORIZED, CMD_HEADER);
                    return;
                }
                if (comptypeEJB.findByName(newName) != null) {
                    result.addRowMessage(ErrorMessage.NAME_ALREADY_EXISTS, HDR_NAME);
                } else {
                    componentTypeToRename.setName(newName);
                    comptypeEJB.save(componentTypeToRename);
                }
            } else {
                result.addRowMessage(ErrorMessage.ENTITY_NOT_FOUND, HDR_NAME);
            }
        } catch (EJBTransactionRolledbackException e) {
            handleLoadingError(LOGGER, e);
        }
    }

    @Override
    public int getDataWidth() {
        // TODO set the data width
        throw new NotImplementedException();
    }

    @Override
    protected void setUpIndexesForFields() {
        // TODO implement
        throw new NotImplementedException();
    }
}
