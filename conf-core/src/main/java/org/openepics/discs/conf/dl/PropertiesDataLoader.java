/**
 * 
 */
package org.openepics.discs.conf.dl;

import java.io.InputStream;
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

import org.openepics.discs.conf.ejb.AuthEJB;
import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ui.LoginManager;
import org.openepics.discs.conf.util.As;
import org.openepics.discs.conf.util.IllegalImportFileFormatException;
import org.openepics.discs.conf.util.NotAuthorizedException;

import com.google.common.base.Objects;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * 
 */
@Stateless public class PropertiesDataLoader extends DataLoader {

    @Inject private LoginManager loginManager;
    @Inject private AuthEJB authEJB;
    @Inject private ConfigurationEJB configurationEJB;
    @PersistenceContext private EntityManager em;
    private Map<String, Property> propertyByName;
    private List<Property> propertiesToAddOrUpdate;
    private List<Property> propertiesToDelete;
    private Map<Property, String> propertiesToRename;
    private int nameIndex, associationIndex, unitIndex, dataTypeIndex, descriptionIndex;
    private List<Property> properties;

    @Override public void loadData(InputStream stream) throws IllegalImportFileFormatException, NotAuthorizedException {
        init();

        final List<List<String>> inputRows = ExcelImportFileReader.importExcelFile(stream);

        if (inputRows != null && inputRows.size() > 0) {
            /*
             * List does not contain any rows that do not have a value (command)
             * in the first column. There should be no commands before "HEADER".
             */
            List<String> headerRow = inputRows.get(0);

            propertiesToAddOrUpdate = new ArrayList<>();
            propertiesToDelete = new ArrayList<>();
            propertiesToRename = new HashMap<>();
            setUpIndexesForFields(headerRow);

            CommandProcessing: for (List<String> row : inputRows.subList(1, inputRows.size())) {
                final String rowNumber = row.get(0);
                if (Objects.equal(row.get(1), CMD_HEADER)) {
                    headerRow = row;
                    setUpIndexesForFields(headerRow);
                    continue; // skip the rest of the processing for HEADER row
                }

                final String command = As.notNull(row.get(1).toUpperCase());
                @Nullable final String name = row.get(nameIndex);
                @Nullable final String unit = row.get(unitIndex);
                @Nullable final String dataType = row.get(dataTypeIndex);
                @Nullable final String description = row.get(descriptionIndex);
                @Nullable final String association = row.get(associationIndex);
                final Date modifiedAt = new Date();
                final String modifiedBy = loginManager.getUserid();

                if (name == null || unit == null || dataType == null || description == null || association == null) {
                    throw new IllegalImportFileFormatException("Required fields should not be empty.", rowNumber);
                }

                switch (command) {
                case CMD_UPDATE:
                    if (propertyByName.containsKey(name)) {
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.PROPERTY, EntityTypeOperation.UPDATE)) {
                            final Property propertyToUpdate = new Property(null, name, description, association, modifiedAt, modifiedBy, 0);
                            setPropertyFields(propertyToUpdate, unit, dataType, rowNumber);
                            propertiesToAddOrUpdate.add(propertyToUpdate);
                        } else {
                            throw new NotAuthorizedException(EntityTypeOperation.UPDATE, EntityType.PROPERTY);
                        }
                    } else {
                        if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.PROPERTY, EntityTypeOperation.CREATE)) {
                            final Property propertyToAdd = new Property(null, name, description, association, modifiedAt, modifiedBy, 0);
                            setPropertyFields(propertyToAdd, unit, dataType, rowNumber);

                            propertiesToAddOrUpdate.add(propertyToAdd);
                        } else {
                            throw new NotAuthorizedException(EntityTypeOperation.CREATE, EntityType.PROPERTY);
                        }
                    }
                    break;
                case CMD_DELETE:
                    if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.PROPERTY, EntityTypeOperation.DELETE)) {
                        final Property propertyToDelete = propertyByName.get(name);
                        if (propertyToDelete == null) {
                            throw new IllegalImportFileFormatException("Property to be deleted does not exist!", rowNumber);
                        } else {
                            propertiesToDelete.add(propertyToDelete);
                            propertyByName.remove(propertyToDelete);
                        }
                    } else {
                        throw new NotAuthorizedException(EntityTypeOperation.DELETE, EntityType.PROPERTY);
                    }
                    break;
                case CMD_RENAME:
                    if (authEJB.userHasAuth(loginManager.getUserid(), EntityType.PROPERTY, EntityTypeOperation.RENAME)) {
                        final int startOldNameMarkerIndex = name.indexOf("[");
                        final int endOldNameMarkerIndex = name.indexOf("]");
                        if (startOldNameMarkerIndex == -1 || endOldNameMarkerIndex == -1) {
                            throw new IllegalImportFileFormatException("RENAME command must have property name which is to be renamed, defined between [] \nfollowed by new name. Example: [old name] new name.", rowNumber);
                        }

                        final String oldName = name.substring(startOldNameMarkerIndex + 1, endOldNameMarkerIndex).trim();
                        final String newName = name.substring(endOldNameMarkerIndex + 1).trim();
                        
                        if (propertyByName.containsKey(oldName)) {
                            if (propertyByName.containsKey(newName)) {
                                throw new IllegalImportFileFormatException("Cannot rename unit to \"" + newName + "\" since unit with this name already exists.", rowNumber);
                            } else {
                                propertiesToRename.put(propertyByName.get(oldName), newName);
                            }
                        } else {
                            throw new IllegalImportFileFormatException("Property to be renamed does not exist!", rowNumber);
                        }
                    } else {
                        throw new NotAuthorizedException(EntityTypeOperation.RENAME, EntityType.PROPERTY);
                    }
                    break;
                case CMD_END:
                    break CommandProcessing;
                default:
                    throw new IllegalImportFileFormatException(command + " is not a valid command!", rowNumber);
                }
            }

            doImport();
        }

    }

    @Override protected void doImport() {
        for (Property property : propertiesToAddOrUpdate) {
            if (propertyByName.containsKey(property.getName())) {
                final Property propertyToUpdate = propertyByName.get(property.getName());
                propertyToUpdate.setDescription(property.getDescription());
                propertyToUpdate.setAssociation(property.getAssociation());
                propertyToUpdate.setDataType(property.getDataType());
                propertyToUpdate.setUnit(property.getUnit());
                propertyToUpdate.setModifiedAt(property.getModifiedAt());
                propertyToUpdate.setModifiedBy(property.getModifiedBy());
                configurationEJB.saveProperty(propertyToUpdate);
            } else {
                configurationEJB.addProperty(property);
            }
        }

        for (Property property : propertiesToDelete) {
            configurationEJB.deleteProperty(propertyByName.get(property.getName()));
        }

        final Iterator<Property> propertyRenameIterator = propertiesToRename.keySet().iterator();
        while (propertyRenameIterator.hasNext()) {
            final Property propertyToRename = propertyRenameIterator.next();
            propertyToRename.setName(propertiesToRename.get(propertyToRename));
            configurationEJB.saveProperty(propertyToRename);
        }
    }

    @Override protected void setUpIndexesForFields(List<String> header) throws IllegalImportFileFormatException {
        nameIndex = header.indexOf("NAME");
        associationIndex = header.indexOf("ASSOCIATION");
        unitIndex = header.indexOf("UNIT");
        dataTypeIndex = header.indexOf("DATA-TYPE");
        descriptionIndex = header.indexOf("DESCRIPTION");

        if (nameIndex == -1 || associationIndex == -1 || unitIndex == -1 || dataTypeIndex == -1 || descriptionIndex == -1) {
            throw new IllegalImportFileFormatException("Header row does not contain required fields!", header.get(0));
        }
    }

    private void init() {
        properties = configurationEJB.findProperties();
        propertyByName = new HashMap<>();
        for (Property property : properties) {
            propertyByName.put(property.getName(), property);
        }
    }

    private void setPropertyFields(Property property, String unit, String dataType, String rowNumber) throws IllegalImportFileFormatException {
        final Unit newUnit = configurationEJB.findUnitByName(unit);
        if (newUnit != null) {
            property.setUnit(newUnit);
        } else {
            throw new IllegalImportFileFormatException("Unit could not be found.", rowNumber);
        }

        final DataType newDataType = configurationEJB.findDataType(dataType);
        if (newDataType != null) {
            property.setDataType(newDataType);
        } else {
            throw new IllegalImportFileFormatException("Data type could not be found.", rowNumber);
        }
    }

}
