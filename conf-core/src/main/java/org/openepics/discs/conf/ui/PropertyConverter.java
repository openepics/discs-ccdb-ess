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

import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.Property;

/**
 *
 * @author vuppala
 */
@ManagedBean // workaround for injecting an EJB in a converter (for older versions of Glassfish)
// @FacesConverter(value = "experimentConverter")
// @ViewScoped
public class PropertyConverter implements Converter {
    @EJB
    private ConfigurationEJB configurationEJB;
    private static final Logger logger = Logger.getLogger(PropertyConverter.class.getCanonicalName());

    public PropertyConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Property prop;

        if (value == null || value.equals("")) {
            logger.log(Level.INFO, "PropertyConverter: empty property id");
            return null;
        } else {
            prop = configurationEJB.findProperty(Long.parseLong(value));
            return prop;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            logger.log(Level.INFO, "PropertyConverter: empty property object");
            return "";
        } else {
            // logger.log(Level.INFO, "Exp number: " + ((Experiment) value).getId().toString());
            return ((Property) value).getId().toString();
        }
    }
}
