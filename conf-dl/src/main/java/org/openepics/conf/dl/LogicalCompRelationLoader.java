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


import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.openepics.conf.entity.*;

/**
 *
 * @author vuppala
 */
public class LogicalCompRelationLoader extends DataLoader {
    LogicalCompRelationLoader (EntityManager em) {
        super(em);
    }
    
    private ComponentRelation getRelation(String name) { // ToDo: improve the search query
        List<ComponentRelation> relations;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<ComponentRelation> root = cq.from(ComponentRelation.class);

        cq.where(cb.like(root.get(ComponentRelation_.name),name));
        TypedQuery<ComponentRelation> query = entityManager.createQuery(cq);
        relations = query.getResultList();
        logger.log(Level.FINE, "Number of relationshiups: {0}", relations.size());        
        if ( relations.size() > 0 ) {
            return relations.get(0);
        } else {
            return null;
        }
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
    
    private List<LogicalComponent> findComponents(String pattern) { // ToDo: improve the search query
        List<LogicalComponent> components;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<LogicalComponent> query = cb.createQuery(LogicalComponent.class);
        Root<LogicalComponent> comp = query.from(LogicalComponent.class);

        query.where(cb.like(comp.get(LogicalComponent_.name), pattern)); 

        TypedQuery<LogicalComponent> q = entityManager.createQuery(query);
        components = q.getResultList();
        logger.log(Level.FINER, "Number of logical components: {0}", components.size());
        if (components.size() > 0) {
            return components;
        } else {
            return null;
        }
    }
    
    private boolean existsCompPair(ComponentPairPK id) {
        ComponentPair compPair;

        compPair = entityManager.find(ComponentPair.class, id);
        return compPair != null;
    }
    
    @Override
    public void updateRecord(DSRecord record) throws Exception {
        LogicalComponent parent;
        List<LogicalComponent> children;
        ComponentRelation relation;
        Date today = new Date();

        String relName = record.getField("RELATION");
        if (relName.equals("")) {
            throw new CDLException(CDLExceptionCode.NONAME, "Cannot update or add the record");
        }
 
//        if (entityManager.getTransaction().isActive()) {
//            entityManager.getTransaction().commit();
//        }
//        entityManager.getTransaction().begin();
        relation = getRelation(relName);
        if (relation == null) {
            logger.info("Invalid relation name: " + relName);            
        }

        String parentName = record.getField("PARENT");
        parent = findComponent(parentName);
        if (parent == null) {
            logger.info("Skipping record. Invalid parent: " + parentName);
        }
       
        String childName = record.getField("CHILD");
        children = findComponents(childName);
        if (children == null) {
            logger.info("No children found: " + childName);
        }
        
        if ( relation == null || parent == null || children == null) {
            // throw new CDLException(CDLExceptionCode.NONAME, "Cannot update or add the record");
            return;           
        }
        
        logger.log(Level.FINER,"Updating relationship " + parentName + " " + relName + " " + childName);
        for (LogicalComponent child : children) {
            ComponentPairPK id = new ComponentPairPK(relation.getComponentRelationId(), parent.getLogicalComponentId(), child.getLogicalComponentId());
            if (existsCompPair(id)) {
                // logger.info("Skipping record. Component pair already exists");
                continue;
            }
            ComponentPair compPair = new ComponentPair(id);
            entityManager.persist(compPair);
            logger.finer("Added component pair:" + parentName + " " + relName + " " + child.getName());
        }
    }
}
