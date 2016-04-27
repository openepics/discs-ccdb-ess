/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.ccdb.gui.converter;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Named;
import org.openepics.discs.ccdb.core.ejb.DeviceEJB;
import org.openepics.discs.ccdb.model.Device;

/**
 *
 * @author vuppala
 */
@Named
@RequestScoped // Can be ApplicationScoped but have to be careful with state
public class DeviceConverter implements Converter {

    @EJB
    private DeviceEJB deviceEJB;

    /**
     * Creates a new instance of DeviceConverter
     */
    public DeviceConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String stringValue) {
        if (stringValue == null || stringValue.isEmpty()) {
            return null;
        } 
        
        try {
            return deviceEJB.findById(Long.valueOf(stringValue));
        } catch (Exception e){
            throw new ConverterException("Not a valid device ID");
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object modelObject) {
        if (modelObject == null) {
            return "";
        }
        
        if (modelObject instanceof Device) {
            return ((Device) modelObject).getId().toString();
        } else {
            throw new ConverterException("Not a valid device");
        }
    }
}
