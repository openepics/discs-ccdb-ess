/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any
 * newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.openepics.discs.conf.ui;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;

/**
 * Controller bean for manipulation of {@link Device} attributes
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Named
@ViewScoped
public class DeviceDetailsAttributesController extends AbstractAttributesController<DevicePropertyValue, DeviceArtifact> {

    @Inject private DeviceEJB deviceEJB;

    private Device device;


    @PostConstruct
    public void init() {
        final Long id = Long.parseLong(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("id"));
        device = deviceEJB.findById(id);
        super.setArtifactClass(DeviceArtifact.class);
        super.setPropertyValueClass(DevicePropertyValue.class);
        super.setDao(deviceEJB);
        populateAttributesList();
    }


    @Override
    protected void setPropertyValueParent(DevicePropertyValue child) {
        child.setDevice(device);
    }

    @Override
    protected void setArtifactParent(DeviceArtifact child) {
        child.setDevice(device);
    }

    @Override
    protected void updateAndOpenArtifactModifyDialog() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void updateAndOpenPropertyValueModifyDialog() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void filterProperties() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void populateAttributesList() {
        // TODO Auto-generated method stub

    }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

}
