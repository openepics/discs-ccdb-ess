/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

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
@ManagedBean
public class ComponentTypeConverter implements Converter {

    @EJB private ComptypeEJB comptypeEJB;

    /**
     * Creates a new instance of ComponentTypeConverter
     */
    public ComponentTypeConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ComponentType ctype;

        if (value == null || value.equals("")) {
            return null;
        } else {
            ctype = comptypeEJB.findById(Long.parseLong(value));
            return ctype;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            // logger.log(Level.INFO, "Exp number: " + ((Experiment) value).getId().toString());
            return ((ComponentType) value).getId().toString();
        }
    }
}
