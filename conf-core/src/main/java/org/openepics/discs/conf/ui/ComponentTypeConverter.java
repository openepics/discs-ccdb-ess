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
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ent.ComponentType;

/**
 *
 * @author vuppala
 */
@ManagedBean // workaround for injecting an EJB in a converter (for older versions of Glassfish)
// @FacesConverter(value = "experimentConverter")
// @ViewScoped
public class ComponentTypeConverter implements Converter {
    @EJB
    private ComptypeEJB comptypeEJB;
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    
    /**
     * Creates a new instance of ComponentTypeConverter
     */
    public ComponentTypeConverter() {
    }
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ComponentType ctype;

        if (value == null || value.equals("")) {
            logger.log(Level.INFO, "CompType converter: empty Component Type id");
            return null;
        } else {
            ctype = comptypeEJB.findComponentType(Integer.parseInt(value));
            return ctype;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            logger.log(Level.INFO, "Null object");
            return "";
        } else {
            // logger.log(Level.INFO, "Exp number: " + ((Experiment) value).getId().toString());
            return ((ComponentType) value).getComponentTypeId().toString();
        }
    }
}
