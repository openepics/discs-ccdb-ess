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
import org.openepics.discs.conf.ejb.ConfigurationEJBLocal;
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
    private ConfigurationEJBLocal configurationEJB;
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    /**
     * Creates a new instance of DataTypeConverter
     */
    public DataTypeConverter() {
    }
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        DataType dtype;

        if (value == null || value.equals("")) {
            logger.log(Level.INFO, "exp converter: empty experiemnt id");
            return null;
        } else {
            dtype = configurationEJB.findDataType(value);
            return dtype;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            logger.log(Level.INFO, "Null object");
            return "";
        } else {
            // logger.log(Level.INFO, "Exp number: " + ((Experiment) value).getId().toString());
            return ((DataType) value).getDataTypeId();
        }
    }
}
