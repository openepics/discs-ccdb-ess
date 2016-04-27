/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Cable Database.
 * Cable Database is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.ccdb.jaxrs;

import java.util.List;
import javax.ws.rs.DefaultValue;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.openepics.discs.ccdb.jaxb.InstallationSlot;

/**
 * This resource provides bulk and specific installation slot data.
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 */
@Path("slot")
public interface InstallationSlotResource {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML}) 
    public List<InstallationSlot> getInstallationSlots(@DefaultValue("undefined") 
        @QueryParam("deviceType") String deviceType);    
    
    /**
     * Returns a specific installation slot
     *
     * @param name the name of the installation slot to retrieve
     * @return the installation slot instance data
     */
    @GET
    @Path("{name}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public InstallationSlot getInstallationSlot(@PathParam("name") String name);
    
    /**
     * Retrieves a list of slots of a given device type
     * 
     * @param deviceType the name of the device type to retrieve slots for
     * @return list of slots of given device 
     */    
}
