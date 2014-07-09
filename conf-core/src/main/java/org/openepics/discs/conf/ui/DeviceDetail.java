/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.util.Utility;

/**
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class DeviceDetail implements Serializable {

    @EJB
    private DeviceEJB deviceEJB;
    private static final Logger logger = Logger.getLogger(DeviceDetail.class.getCanonicalName());
    private Device selectedObject;
    private int id = 0; // given identifier

    public DeviceDetail() {
    }

    public void init() {
        // logger.entering(DeviceDetail.class.getName(), "init", this);
        // logger.log(Level.INFO, "entering init {0}", id);
        try {
            selectedObject = deviceEJB.findDevice(id);
            if ( selectedObject == null ) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Device with given ID not found"," Device ID: " + id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cannot retrieve device", e.getMessage());
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in finding device", e.getMessage());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Device getSelectedObject() {
        return selectedObject;
    }
}