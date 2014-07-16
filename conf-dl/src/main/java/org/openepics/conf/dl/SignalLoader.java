/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Vasu V <vuppala@frib.msu.org>
 */
public class SignalLoader extends DataLoader {
       private int maxRelationId = 0;
    
    SignalLoader(EntityManager em) {
        super(em);
    }

    private Signal getSignal(String name) { // ToDo: improve the search query
        return entityManager.find(Signal.class, name);
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
    
    
    private void printRelation(ComponentRelation rel) {
        System.out.println("Name: " + rel.getName());
        System.out.println("Description: " + rel.getDescription());
        System.out.println("Inverse: " + rel.getIname());
    }
    
    @Override
    public void updateRecord(DSRecord record) throws Exception {
        LogicalComponent device;
        List<LogicalComponent> children;
        Signal signal;
        Date today = new Date();

//        if (entityManager.getTransaction().isActive()) {
//            entityManager.getTransaction().commit();
//        }
//        entityManager.getTransaction().begin();        

        String deviceName = record.getField("DEVICE");
        device = findComponent(deviceName);
        if (device == null) {
            logger.info("Skipping record. Invalid Device: " + deviceName);
            return;
        }
       
        String signalName = record.getField("SIGNAL");
        signal = getSignal(signalName);
        if (signal == null) {
            signal = new Signal();
            signal.setLogicalComponent(device);
            signal.setSignalName(signalName);
        }
        
        String description = record.getField("DESCRIPTION");
        signal.setDescription(description);
        signal.setModifiedBy(this.dataSource);
        signal.setDateModified(today);       
        entityManager.persist(signal);
        
        logger.log(Level.FINER,"Updating signal " + signalName);
    }
       
}
