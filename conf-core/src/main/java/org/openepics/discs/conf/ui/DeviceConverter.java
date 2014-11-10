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

import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ent.Device;

/**
 *
 * @author vuppala
 */
@ManagedBean
public class DeviceConverter implements Converter {
    @EJB
    private DeviceEJB deviceEJB;

    /**
     * Creates a new instance of DeviceConverter
     */
    public DeviceConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Device dev;

        if (value == null || value.equals("")) {
            return null;
        } else {
            dev = deviceEJB.findById(Long.parseLong(value));
            return dev;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return ((Device) value).getId().toString();
        }
    }
}
