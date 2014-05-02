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
import java.util.logging.*;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.openepics.conf.entity.*;

/**
 *
 * @author vuppala
 */
public class PhysicalComponentLoader extends DataLoader {
    private int maxComponentId = 0;
    private int invalidRecords = 0; // number of invalid records
    
    PhysicalComponentLoader(EntityManager em) {
        super(em);
    }

    private ComponentType getCompType(String id) {
        ComponentType ctype;

        ctype = entityManager.find(ComponentType.class, id);
        return ctype;
    }

    private PhysicalComponentProperty getPCP(PhysicalComponentPropertyPK id) {
        PhysicalComponentProperty pcp;

        pcp = entityManager.find(PhysicalComponentProperty.class, id);
        if (pcp == null) {
            pcp = new PhysicalComponentProperty(id);
        }
        return pcp;
    }

    private PhysicalComponent findComponent(String serialNumber) {
        List<PhysicalComponent> components;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PhysicalComponent> query = cb.createQuery(PhysicalComponent.class);
        Root<PhysicalComponent> comp = query.from(PhysicalComponent.class);

        query.where(cb.like(comp.get(PhysicalComponent_.serialNumber), serialNumber)); // TODO: Remove the unneccessary join

        TypedQuery<PhysicalComponent> q = entityManager.createQuery(query);
        components = q.getResultList();
        logger.log(Level.FINER, "Number of logical components: {0}", components.size());
        if (components.size() > 0) {
            return components.get(0);
        } else {
            return null;
        }
    }
    
    private boolean validProperty(String id) {
        Property prop = entityManager.find(Property.class, id);

        return prop != null;
    }
    
    private void printComponent(PhysicalComponent pcomp) {
        System.out.println("ID: " + pcomp.getPhysicalComponentId());
        System.out.println("Serial #: " + pcomp.getSerialNumber());
        System.out.println("Description: " + pcomp.getDescription());
        System.out.println("Device Type: " + pcomp.getComponentType().getComponentTypeId());
    }

    @Override
    public void updateRecord(DSRecord record) throws Exception {
        PhysicalComponent pcomp;
        ComponentType ctype;
        Date today = new Date();
        String ctypeId;

        String compSerial = record.getField("SERIAL");
        if (compSerial.equals("")) {
            throw new CDLException(CDLExceptionCode.NONAME, "Cannot update or add the record");
        }
        ctypeId = record.getField("CTYPE");

//        if (entityManager.getTransaction().isActive()) {
//            entityManager.getTransaction().commit();
//        }
//        entityManager.getTransaction().begin();
        
        ctype = getCompType(ctypeId);
        if (ctype == null) {
            logger.info("Skipping record. No device type: " + ctypeId);
            invalidRecords++;
            // entityManager.getTransaction().rollback();
            return;
        }

        pcomp = findComponent(compSerial);
        if (pcomp == null) {
            pcomp = new PhysicalComponent();
            if ( maxComponentId == 0 ) {
                maxComponentId = getMaxCompId();
            }
            pcomp.setPhysicalComponentId(++maxComponentId);
            pcomp.setSerialNumber(compSerial);
            pcomp.setPhysicalComponentPropertyList(new ArrayList<PhysicalComponentProperty>());
            entityManager.persist(pcomp);
            logger.finer("allocating a new physical component");
        }

        pcomp.setDescription(record.getField("DESCRIPTION"));
        pcomp.setStatus(record.getField("STATUS").charAt(0));
        pcomp.setModifiedBy(this.dataSource);
        pcomp.setComponentType(ctype);
        pcomp.setDateModified(today);
        logger.log(Level.FINE, "Updating component properties..");

        // update properties 
        final String[] AttribArray = {"SERIAL", "DESCRIPTION", "CTYPE", "STATUS"}; // attributes of logical components
        final Set<String> AttribSet = new HashSet<String>(Arrays.asList(AttribArray));
        for (Map.Entry<String, String> e : record.getMap().entrySet()) {
            if (AttribSet.contains(e.getKey())) {
                continue; // if it is an attribute, it is not a property, so skip
            }
            if (!validProperty(e.getKey())) {
                logger.log(Level.SEVERE, "Invalid property " + e.getKey() + ". Add it to property table first.");               
                continue;
            }
            
            PhysicalComponentPropertyPK id = new PhysicalComponentPropertyPK(pcomp.getPhysicalComponentId(), e.getKey());
            PhysicalComponentProperty pcp = getPCP(id);
            if (pcp.getValue() == null || !pcp.getValue().equals(e.getValue())) {
                pcp.setValue(e.getValue());
                pcp.setDateModified(today);
                pcp.setModifiedBy("DataLoader");
            }
            pcomp.getPhysicalComponentPropertyList().add(pcp);
            logger.log(Level.FINE, "Property " + e.getKey() + ":" + e.getValue());
        }

        printComponent(pcomp);
    }
    
    private int getMaxCompId() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<PhysicalComponent> comp = query.from(PhysicalComponent.class);
        
        query.select(cb.max(comp.get(PhysicalComponent_.physicalComponentId)));
        TypedQuery<Integer> q = entityManager.createQuery(query);
        Integer maxId = q.getSingleResult();
        
        return (maxId == null? 1 : maxId); 
    }
}
