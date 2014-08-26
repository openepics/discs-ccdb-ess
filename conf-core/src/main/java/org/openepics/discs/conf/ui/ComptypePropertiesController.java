/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ui;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ent.ComponentType;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Named
@RequestScoped
public class ComptypePropertiesController implements Serializable {

    @Inject private ComptypeEJB comptypeEJB;
    private ComponentType compType;

    @PostConstruct
    public void init() {
        final Long id = Long.parseLong(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("id"));
        compType = comptypeEJB.findComponentType(id);
    }

    public ComponentType getDeviceType() {
        return compType;
    }


}
