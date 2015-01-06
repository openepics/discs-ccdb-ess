package org.openepics.discs.conf.dl.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.openepics.discs.conf.ejb.DAO;
import org.openepics.discs.conf.ejb.EntityWithProperties;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyAssociation;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.util.Conversion;

/**
 * Abstract data loader for entities that have properties.
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
public abstract class AbstractEntityWithPropertiesDataLoader<S extends PropertyValue> extends AbstractDataLoader implements DataLoader {
    @Inject protected PropertyEJB propertyEJB;

    private Class<S> propertyValueClass;

    @Override
    protected boolean indexPropertyColumns() { return true; }

    @Override
    protected boolean checkPropertyHeader(String propertyName) {
        final @Nullable Property property = propertyEJB.findByName(propertyName);
        if (property == null) {
            result.addRowMessage(ErrorMessage.PROPERTY_NOT_FOUND, propertyName);
        } else {
            if (!checkPropertyAssociation(property)) {
                result.addGlobalMessage(ErrorMessage.PROPERTY_ASSOCIATION_FAILURE, propertyName);
            }
        }
        return !result.isError();
    }

    /**
     * Used by sub-classes to update properties
     *
     * @param entity the database entity for which to update or insert the properties
     */
    protected void addOrUpdateProperties(EntityWithProperties entity) {
        final List<PropertyValue> propertyList = entity.getEntityPropertyList();
        final List<PropertyValue> entityProperties = new ArrayList<>();
        if (propertyList != null) {
            entityProperties.addAll(propertyList);
        }

        final Map<Property, PropertyValue> entityPropertyByProperty = new HashMap<>();
        for (PropertyValue entityProperty : entityProperties) {
            entityPropertyByProperty.put(entityProperty.getProperty(), entityProperty);
        }

        for (String propertyName : getProperties()) {
            final @Nullable Property property = propertyEJB.findByName(propertyName);
            final @Nullable String propertyValue = readCurrentRowCellForProperty(propertyName);
            if (entityPropertyByProperty.containsKey(property)) {
                final PropertyValue entityPropertyToUpdate = entityPropertyByProperty.get(property);

                if (propertyValue == null) {
                    getDAO().deleteChild(entityPropertyToUpdate);
                } else {
                    entityPropertyToUpdate.setPropValue(Conversion.stringToValue(propertyValue, property.getDataType()));
                    getDAO().saveChild(entityPropertyToUpdate);
                }

            } else if (propertyValue != null) {
                try {
                    final S propertyValueToAdd = propertyValueClass.newInstance();
                    propertyValueToAdd.setProperty(property);
                    propertyValueToAdd.setPropValue(Conversion.stringToValue(propertyValue, property.getDataType()));
                    propertyValueToAdd.setPropertiesParent(entity);
                    getDAO().addChild(propertyValueToAdd);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Sets the class of a {@link PropertyValue} sub-class from which new instance of
     * that class can be created
     *
     * @param propertyValueClass
     */
    protected void setPropertyValueClass(Class<S> propertyValueClass) {
        this.propertyValueClass = propertyValueClass;
    }


    /**
     * To be implemented by sub-classes. Should check whether a property association is appropriate for the entities
     * covered by this data loader.
     *
     * @param propAssociation the {@link PropertyAssociation} to be checked against
     * @return <code>false</code> if the property association type is not valid for the entities covered by this data loadaer
     */
    protected abstract boolean checkPropertyAssociation(final Property propAssociation);

    /**
     * To be implemented by sub-classes. Returns a DAO EJB for accessing properties
     * @return
     */
    protected abstract <T> DAO<T> getDAO();
}
