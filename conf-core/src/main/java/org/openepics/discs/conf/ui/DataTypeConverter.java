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

import org.openepics.discs.conf.ejb.DataTypeEJB;
import org.openepics.discs.conf.ent.DataType;

/**
 *
 * @author vuppala
 */
@ManagedBean
public class DataTypeConverter implements Converter {

    @EJB
    private DataTypeEJB dataTypeEJB;
    /**
     * Creates a new instance of DataTypeConverter
     */
    public DataTypeConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        DataType dtype;

        if (value == null || value.equals("")) {
            return null;
        } else {
            dtype = dataTypeEJB.findById(Long.valueOf(value));
            return dtype;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            // logger.log(Level.INFO, "Exp number: " + ((Experiment) value).getId().toString());
            return String.valueOf(((DataType) value).getId());
        }
    }
}
