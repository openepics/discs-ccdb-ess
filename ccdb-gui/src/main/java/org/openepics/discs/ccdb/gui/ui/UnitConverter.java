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
package org.openepics.discs.ccdb.gui.ui;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.ccdb.core.ejb.UnitEJB;
import org.openepics.discs.ccdb.model.Unit;

/**
 *
 * @author vuppala
 */
@Named
public class UnitConverter implements Converter {

    @Inject private UnitEJB unitEJB;

    /** Creates a new instance of UnitConverter */
    public UnitConverter() {}

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Unit unit;

        if (value == null || value.isEmpty()) {
            return null;
        } else {
            unit = unitEJB.findById(Long.valueOf(value));
            return unit;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || "".equals(value)) {
            return "";
        } else {
            return String.valueOf(((Unit) value).getId());
        }
    }
}
