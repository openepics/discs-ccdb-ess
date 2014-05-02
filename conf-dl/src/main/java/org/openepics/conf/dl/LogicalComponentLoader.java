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
import javax.persistence.*;
import javax.persistence.criteria.*;
import org.openepics.conf.entity.*;

/**
 *
 * @author vuppala
 */
public class LogicalComponentLoader extends DataLoader {
    private int maxComponentId = 0;

    LogicalComponentLoader(EntityManager em) {
        super(em);
        maxComponentId = getMaxCompId();
    }

    private ComponentType getCompType(String id) {
        ComponentType ctype;

        ctype = entityManager.find(ComponentType.class, id);
        return ctype;
    }

    private int getMaxCompId() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<LogicalComponent> comp = query.from(LogicalComponent.class);
        
        query.select(cb.max(comp.get(LogicalComponent_.logicalComponentId)));
        TypedQuery<Integer> q = entityManager.createQuery(query);
        Integer maxId = q.getSingleResult();
        
        return (maxId == null? 1 : maxId); 
    }
    
    private LogicalComponentProperty getLCP(LogicalComponentPropertyPK id) {
        LogicalComponentProperty lcp; 

        lcp = entityManager.find(LogicalComponentProperty.class, id);
        if (lcp == null) {
            lcp = new LogicalComponentProperty(id);
        }
        return lcp;
    }

    private LogicalComponent findComponent(String name) { // ToDo: improve the search query
        List<LogicalComponent> components;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<LogicalComponent> query = cb.createQuery(LogicalComponent.class);
        Root<LogicalComponent> comp = query.from(LogicalComponent.class);

        query.where(cb.like(comp.get(LogicalComponent_.name), name));

        TypedQuery<LogicalComponent> q = entityManager.createQuery(query);
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

    private void printRecord(DSRecord record) throws Exception {
        String message = record.getField("NAME") + " " + record.getField("BLP");
        
        logger.log(Level.INFO, message);        
    }
    
    private void printComponent(LogicalComponent lcomp) {
        System.out.println("ID: " + lcomp.getLogicalComponentId());
        System.out.println("Name: " + lcomp.getName());
        System.out.println("Description: " + lcomp.getDescription());
        System.out.println("Device Type: " + lcomp.getComponentType().getComponentTypeId());
        System.out.println("BLP: " + lcomp.getBeamlinePosition());
        System.out.println("GCX: " + lcomp.getGlobalX());
        System.out.println("GCY: " + lcomp.getGlobalY());
        System.out.println("GCZ: " + lcomp.getGlobalZ());
        System.out.println("Comment: " + lcomp.getComment());
    }

    @Override
    public void updateRecord(DSRecord record) throws Exception {
        LogicalComponent lcomp;
        ComponentType ctype;
        Date today = new Date();
        String ctypeId;

        printRecord(record);
        String compName = record.getField("NAME");
        if (compName.equals("")) {
            throw new CDLException(CDLExceptionCode.NONAME, "Cannot update or add the record");
        }
        ctypeId = record.getField("CTYPE").isEmpty() ? record.getField("DEVICE") : record.getField("CTYPE");

        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        }
        entityManager.getTransaction().begin();
        ctype = getCompType(ctypeId);
        if (ctype == null) {
            logger.info("Skipping record. Invalid device type: " + ctypeId);
            // throw new CDLException(CDLExceptionCode.INVALIDFLD, "Invalid component type " + ctypeId);
            return;
        }

        lcomp = findComponent(compName);
        if (lcomp == null) {
            lcomp = new LogicalComponent();
            if ( maxComponentId == 0 ) {
                maxComponentId = getMaxCompId();
            }
            lcomp.setLogicalComponentId(++maxComponentId);
            lcomp.setName(compName);
            lcomp.setLogicalComponentPropertyList(new ArrayList<LogicalComponentProperty>());
            entityManager.persist(lcomp);
            logger.finer("allocating a new logical component");
        }

        if (!record.getField("BLP").trim().isEmpty()) {            
            lcomp.setBeamlinePosition(Double.parseDouble(record.getField("BLP")));
        }
        if (!record.getField("GCX").trim().isEmpty()) {
            lcomp.setGlobalX(Double.parseDouble(record.getField("GCX")));
        }
        if (!record.getField("GCY").trim().isEmpty()) {
            lcomp.setGlobalY(Double.parseDouble(record.getField("GCY")));
        }
        if (!record.getField("GCZ").trim().isEmpty()) {
            lcomp.setGlobalZ(Double.parseDouble(record.getField("GCZ")));
        }
        lcomp.setComment(record.getField("COMMENT"));
        lcomp.setDescription(record.getField("DESCRIPTION"));
        lcomp.setModifiedBy(this.dataSource);
        lcomp.setComponentType(ctype);
        lcomp.setDateModified(today);
        logger.log(Level.FINE, "Updating component properties..");

        // update properties 
        final String[] AttribArray = {"NAME", "DESCRIPTION", "GCX", "GCY", "GCZ", "BLP", "COMMENT", "DEVICE", "CTYPE"}; // attributes of logical components
        final Set<String> AttribSet = new HashSet<String>(Arrays.asList(AttribArray));
        for (Map.Entry<String, String> e : record.getMap().entrySet()) {
            if (AttribSet.contains(e.getKey())) {
                continue; // if it is an attribute, it is not a property, so skip
            }
            if (!validProperty(e.getKey())) {
                logger.log(Level.SEVERE, "Invalid property " + e.getKey() + ". Add it to property table first.");
                continue;
            }
            if ( e.getValue().trim().isEmpty() ) {
                logger.log(Level.FINER, "Property value is empty ");
                continue;
            }
            LogicalComponentPropertyPK id = new LogicalComponentPropertyPK(lcomp.getLogicalComponentId(), e.getKey());
            LogicalComponentProperty lcp = getLCP(id);
            if (lcp.getValue() == null || !lcp.getValue().equals(e.getValue())) {
                lcp.setValue(e.getValue());
                lcp.setDateModified(today);
                lcp.setModifiedBy("DataLoader");
            }
            lcomp.getLogicalComponentPropertyList().add(lcp);
            logger.log(Level.FINE, "Property " + e.getKey() + ":" + e.getValue());
        }
        
        //printComponent(lcomp);
    }
}