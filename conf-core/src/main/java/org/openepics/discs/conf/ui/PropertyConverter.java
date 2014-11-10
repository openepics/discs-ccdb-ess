/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.Property;

/**
 *
 * @author vuppala
 */
@ManagedBean
public class PropertyConverter implements Converter {
    @EJB
    private PropertyEJB propertyEJB;
    private static final Logger logger = Logger.getLogger(PropertyConverter.class.getCanonicalName());

    public PropertyConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Property prop;

        if (value == null || value.equals("")) {
            logger.fine("PropertyConverter: empty property id");
            return null;
        } else {
            prop = propertyEJB.findById(Long.parseLong(value));
            return prop;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            logger.fine("PropertyConverter: empty property object");
            return "";
        } else {
            return ((Property) value).getId().toString();
        }
    }
}
