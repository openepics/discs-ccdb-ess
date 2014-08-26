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

import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ent.Device;

/**
 *
 * @author vuppala
 */
@ManagedBean  // workaround for injecting an EJB in a converter (for older versions of Glassfish)
// @RequestScoped
public class DeviceConverter implements Converter {
    @EJB
    private DeviceEJB deviceEJB;
    private static final Logger logger = Logger.getLogger(DeviceConverter.class.getCanonicalName());

    /**
     * Creates a new instance of DeviceConverter
     */
    public DeviceConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Device dev;

        if (value == null || value.equals("")) {
            logger.fine( "Device converter: empty device id");
            return null;
        } else {
            dev = deviceEJB.findDevice(Long.parseLong(value));
            return dev;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            logger.fine("Device converter: Null or empty device object");
            return "";
        } else {
            // logger.log(Level.INFO, "Exp number: " + ((Experiment) value).getId().toString());
            return ((Device) value).getId().toString();
        }
    }
}
