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
package org.openepics.discs.conf.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.openepics.discs.conf.ent.Rack;
import org.openepics.discs.conf.ent.RackSlot;

/**
 *
 * @author vuppala
 *
 */
@Stateless
public class RackEJB extends DAO<Rack> {    
    private static final Logger logger = Logger.getLogger(RackEJB.class.getName());
    
    @Override
    protected Class<Rack> getEntityClass() {
        return Rack.class;
    }

    /**
     * All the tacks
     * 
     * @return a list of all {@link Rack}s ordered by name.
     */
    public List<Rack> findAllOrdered() {
        return em.createNamedQuery("Rack.findAllOrdered", Rack.class).getResultList();
    }
    
    public List<Rack> findRacks(String rackName, String deviceName, int startPage, int pageSize) {
        List<Rack> result;
        TypedQuery<Rack> query;
        
        
        
        if (deviceName == null || deviceName.isEmpty()) {
            query = em.createQuery("SELECT r FROM Rack r  WHERE r.name LIKE :rackName ", Rack.class)
                    .setParameter("rackName", "%" + rackName + "%");
        } else {
//            query = em.createQuery("SELECT DISTINCT rs.rack FROM RackSlot rs  WHERE rs.rack.name LIKE :rackName ", Rack.class);
            query = em.createQuery("SELECT DISTINCT rs.rack FROM Slot s JOIN s.rackSlots rs WHERE s.name LIKE :deviceName AND rs.rack.name LIKE :rackName ESCAPE '\\'", Rack.class)
                    .setParameter("rackName", rackName)
                    .setParameter("deviceName", deviceName);
        }
        result = query.setFirstResult(startPage*pageSize).setMaxResults(pageSize).getResultList();
        
        logger.log(Level.INFO, "Number of racks found {0}", result.size());
        return result;
    }
   
    public long findNumberOfRacks(String rackName, String deviceName) {    
        TypedQuery<Long> query;
        
        if (rackName == null || rackName.isEmpty()) {
            rackName = "%";
        }
        
        if (deviceName == null || deviceName.isEmpty()) {
            query = em.createQuery("SELECT COUNT(r.id) FROM Rack r  WHERE LOWER(r.name) LIKE :rackName ", Long.class)
                    .setParameter("rackName", rackName.toLowerCase());
        } else {
//            query = em.createQuery("SELECT DISTINCT rs.rack FROM RackSlot rs  WHERE rs.rack.name LIKE :rackName ", Rack.class);
            query = em.createQuery("SELECT COUNT(DISTINCT rs.rack) FROM Slot s JOIN s.rackSlots rs WHERE LOWER(s.name) LIKE :deviceName ESCAPE '\\' AND LOWER(rs.rack.name) LIKE :rackName ESCAPE '\\'", Long.class)
                    .setParameter("rackName", rackName.toLowerCase())
                    .setParameter("deviceName", deviceName.toLowerCase());
        }
        long result = query.getSingleResult();
        
        logger.log(Level.INFO, "Number of racks found from count {0}", result);
        return result;
    }
    
    /**
     * All slots in a rack
     * 
     * @param rack
     * @return a list of {@link RackSlot}s in the given rack ordered by slot number, side.
     */
    public List<RackSlot> findSlotsOrdered(Rack rack) {
        return em.createNamedQuery("RackSlot.findSlotsByRack", RackSlot.class).setParameter("rack", rack).getResultList();
    }
    
    
    
}
