/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.ccdb.gui.ui;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.ccdb.core.ejb.SlotEJB;
import org.openepics.discs.ccdb.model.Slot;

/**
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class SlotListView implements Serializable {
    
    @Inject private SlotEJB slotEJB;
    private static final Logger logger = Logger.getLogger(SlotListView.class.getName());

    private List<Slot> slots;
    private Slot selectedSlot;
    
    public SlotListView() {
    }

    @PostConstruct
    public void init() {
        slots = slotEJB.findAll();
        logger.log(Level.FINE, "Found number of slots: {0}", slots.size());       
    }
    
    // --- getters/setters

    public List<Slot> getSlots() {
        return slots;
    } 

    public Slot getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(Slot selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

}
