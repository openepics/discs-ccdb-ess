/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
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
    private static final Logger logger = Logger.getLogger(DeviceDetail.class.getCanonicalName());

    @EJB private DeviceEJB deviceEJB;
    private Device selectedObject;
    private long id = 0; // given identifier

    public DeviceDetail() {
    }

    public void init() {
        try {
            selectedObject = deviceEJB.findById(id);
            if ( selectedObject == null ) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Device with given ID not found"," Device ID: " + id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cannot retrieve device", e.getMessage());
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in finding device", e.getMessage());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Device getSelectedObject() {
        return selectedObject;
    }
}