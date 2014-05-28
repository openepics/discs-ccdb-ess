/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.openepics.discs.conf.ejb.SlotEJBLocal;
import org.openepics.discs.conf.ent.Slot;

/**
 *
 * @author vuppala
 */
@ManagedBean  // workaround for injecting an EJB in a converter (for older versions of Glassfish)
// @ViewScoped
public class SlotConverter implements Converter{

    @EJB
    private SlotEJBLocal slotEJB;
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    /**
     * Creates a new instance of SlotConverter
     */
    public SlotConverter() {
    }
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Slot slot;

        if (value == null || value.equals("")) {
            logger.log(Level.INFO, "Slot converter: empty slot id");
            return null;
        } else {
            slot = slotEJB.findLayoutSlot(Integer.parseInt(value));
            return slot;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            logger.log(Level.INFO, "Slot converter: Null or empty Slot object");
            return "";
        } else {
            // logger.log(Level.INFO, "Exp number: " + ((Experiment) value).getId().toString());
            return ((Slot) value).getSlotId().toString();
        }
    }
}
