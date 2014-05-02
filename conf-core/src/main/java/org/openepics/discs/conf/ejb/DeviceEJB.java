/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.openepics.discs.conf.ent.Device;

/**
 *
 * @author vuppala
 */
@Stateless
public class DeviceEJB implements DeviceEJBLocal {

    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");   
    @PersistenceContext(unitName = "org.openepics.discs.conf.data")
    private EntityManager em;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    // ----------------  Physical Component  -------------------------
    @Override
    public List<Device> findDevice() {
        List<Device> comps;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Device> cq = cb.createQuery(Device.class);
        Root<Device> prop = cq.from(Device.class);       
        
        TypedQuery<Device> query = em.createQuery(cq);
        comps = query.getResultList();
        logger.log(Level.INFO, "Number of physical components: {0}", comps.size());
        
        return comps;        
    }
    
    @Override
    public Device findDevice(int id) {
        return em.find(Device.class, id);
    }
    
}
