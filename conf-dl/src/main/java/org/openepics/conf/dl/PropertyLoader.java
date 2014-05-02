/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.conf.dl;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.*;
import org.openepics.discs.conf.ent.Property;

/**
 *
 * @author vuppala
 */
public class PropertyLoader extends DataLoader {

    // private static final Logger logger = Logger.getLogger("org.openepics.conf.dl");
    // private EntityManager entityManager;

    PropertyLoader(EntityManager em) {
        super(em);
    }

    private Property getProperty(String id) {
        Property ctype;

        ctype = entityManager.find(Property.class, id);
        return ctype;
    }

    private void printProperty(Property prop) {
        System.out.println("ID: " + prop.getPropertyId());
        System.out.println("Description: " + prop.getDescription());
        System.out.println("Units: " + prop.getUnit());
        System.out.println("Data Type: " + prop.getDataType());
    }

    @Override
    public void updateRecord(DSRecord record) throws Exception {
        Property property;
        Date today = new Date();
        String propertyId = record.getField("ID");

        if (propertyId.trim().isEmpty()) {
            throw new CDLException(CDLExceptionCode.NONAME, "Property ID is empty");
        }

        // System.out.println("-------------- Update Start:" + propertyId + "------");
        logger.log(Level.FINER, "-------------- Update Start:" + propertyId + "------");
        property = getProperty(propertyId);
        if (property == null) {
            property = new Property();
            property.setPropertyId(propertyId);
            entityManager.persist(property);
        }

        property.setDescription(record.getField("DESCRIPTION"));
        // property.setUnit(record.getField("UNITS"));
        // property.setDataType(record.getField("DATA-TYPE"));
        property.setModifiedBy("DataLoaderP");
        property.setAssociation("");
        property.setModifiedAt(today);

        printProperty(property);     
    }
}
