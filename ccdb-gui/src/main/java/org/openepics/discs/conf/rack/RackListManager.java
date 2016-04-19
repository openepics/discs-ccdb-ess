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
package org.openepics.discs.conf.rack;

import org.openepics.discs.conf.ejb.RackEJB;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.openepics.discs.conf.ent.Rack;

/**
 * Bean to support rack layout view
 *
 * @author vuppala
 *
 */
@Named
@ViewScoped
public class RackListManager implements Serializable {

    private static final Logger logger = Logger.getLogger(RackListManager.class.getName());
    @EJB
    private RackEJB rackEJB;
    
    private List<Rack> racks;
    private List<Rack> filteredRacks;
  
    public RackListManager() {
        
    }

    /**
     *
     */
    @PostConstruct
    public void init() {
        racks = rackEJB.findAllOrdered();
    }

    // getters and setters
    public List<Rack> getRacks() {
        return racks;
    }

    public List<Rack> getFilteredRacks() {
        return filteredRacks;
    }

    public void setFilteredRacks(List<Rack> filteredRacks) {
        this.filteredRacks = filteredRacks;
    }
    
    
}
