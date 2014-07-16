/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2012.
 * 
 * You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *       http://www.gnu.org/licenses/gpl.txt
 * 
 * Contact Information:
 *   Facilitty for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 * 
 */
package org.openepics.conf.dl;

import java.util.*;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import org.openepics.conf.entity.*;

/**
 *
 * @author vuppala
 */
public class ComponentTypeLoader extends DataLoader {
    
    ComponentTypeLoader(EntityManager em) {
        super(em);
    }

    private ComponentType getCompType(String id) {
        ComponentType ctype;

        ctype = entityManager.find(ComponentType.class, id);
        return ctype;
    }

    private boolean validProperty (String id) {
        Property prop = entityManager.find(Property.class, id);
        
        return prop != null;        
    }
    
    private ComponentTypeProperty getCTP(ComponentTypePropertyPK id) {
        ComponentTypeProperty ctp;

        ctp = entityManager.find(ComponentTypeProperty.class, id);
        if (ctp == null) {
            ctp = new ComponentTypeProperty(id);
        }
        return ctp;
    }
    
    private void printComponentType(ComponentType ctype) {
        System.out.println("ID: " + ctype.getComponentTypeId());
        System.out.println("Description: " + ctype.getDescription());
    }
    
    @Override
    public void updateRecord(DSRecord record) throws Exception {
        ComponentType ctype;
        Date today = new Date();
 
        String compTypeId = record.getField("CTYPE");
        if (compTypeId.trim().isEmpty()) {
            throw new CDLException(CDLExceptionCode.NONAME, "Component Type ID is empty");
        }
        //ctypeId = record.getField("DEVICE-TYPE").isEmpty() ? record.getField("DEVICE") : record.getField("DEVICE-TYPE");

        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        }
        entityManager.getTransaction().begin();
        ctype = getCompType(compTypeId);
        if (ctype == null) {
            ctype = new ComponentType();
            ctype.setComponentTypeId(compTypeId);           
        }       

        ctype.setDescription(record.getField("DESCRIPTION"));
        ctype.setModifiedBy(this.dataSource);
        ctype.setDateModified(today);
        logger.log(Level.FINE, "Updating component properties..");

        // update properties 
        final String[] AttribArray = {"CTYPE", "DESCRIPTION"}; // attributes (i.e. not properties) ofcomponent types
        final Set<String> AttribSet = new HashSet<>(Arrays.asList(AttribArray));
        for (Map.Entry<String, String> e : record.getMap().entrySet()) {
            if (AttribSet.contains(e.getKey())) {
                continue;
            } // if it is not an attribute, it is assumed to be a property
            if ( !validProperty(e.getKey()) ) {
               logger.log(Level.SEVERE, "Invalid property " + e.getKey() + ". Add it to property table first.");
               continue;
            }
            ComponentTypePropertyPK id = new ComponentTypePropertyPK(ctype.getComponentTypeId(), e.getKey());
            ComponentTypeProperty ctp = getCTP(id);
            //if (ctp.getField() == null || !ctp.getField().equals(e.getField())) {
                ctp.setValue(e.getValue());
                ctp.setDateModified(today);
                ctp.setModifiedBy("DataLoader");
            //}
            ctype.getComponentTypePropertyList().add(ctp);
            logger.log(Level.FINE, "Property " + e.getKey() + ":" + e.getValue());
        }

        entityManager.persist(ctype);
        printComponentType(ctype);        
    }
}
