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

import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.DataType;

/**
 *
 * @author vuppala
 */
@ManagedBean // workaround for injecting an EJB in a converter (for older versions of Glassfish)
// @FacesConverter(value = "experimentConverter")
// @ViewScoped
public class DataTypeConverter implements Converter {

    @EJB
    private ConfigurationEJB configurationEJB;
    private static final Logger logger = Logger.getLogger(DataTypeConverter.class.getCanonicalName());
    /**
     * Creates a new instance of DataTypeConverter
     */
    public DataTypeConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        DataType dtype;

        if (value == null || value.equals("")) {
            logger.fine("exp converter: empty experiemnt id");
            return null;
        } else {
            dtype = configurationEJB.findDataType(Long.valueOf(value));
            return dtype;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            logger.fine("Null object");
            return "";
        } else {
            // logger.log(Level.INFO, "Exp number: " + ((Experiment) value).getId().toString());
            return String.valueOf(((DataType) value).getId());
        }
    }
}
