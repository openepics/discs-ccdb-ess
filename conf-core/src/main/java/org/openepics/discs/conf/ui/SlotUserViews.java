/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.Slot;

/**
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class SlotUserViews implements Serializable {
    
    @Inject private SlotEJB slotEJB;
    @Inject private ComptypeEJB comptypeEJB;
    private static final Logger logger = Logger.getLogger(SlotListView.class.getName());

    private List<Slot> feslots;
    private List<Slot> psslots;
    private Slot selectedSlot;
    
    public SlotUserViews() {
    }

    @PostConstruct
    public void init() {
        feslots = slotEJB.findSlotByNameContainingString("FE%");
        ComponentType psComponentType = comptypeEJB.findByName("PSOL");
        
        psslots = slotEJB.findByComponentType(psComponentType);
        logger.log(Level.FINE, "Found number of slots: {0}", feslots.size());       
    }
    
    // --- getters/setters

    public List<Slot> getFeslots() {
        return feslots;
    } 

    public Slot getSelectedSlot() {
        return selectedSlot;
    }

    public List<Slot> getPsslots() {
        return psslots;
    }

    public void setSelectedSlot(Slot selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

}
