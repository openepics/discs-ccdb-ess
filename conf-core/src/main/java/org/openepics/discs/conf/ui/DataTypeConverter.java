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

        if (value == null || value.isEmpty()) {
            return null;
        } else {
            dtype = dataTypeEJB.findById(Long.valueOf(value));
            return dtype;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || "".equals(value)) {
            return "";
        } else {
            return String.valueOf(((DataType) value).getId());
        }
    }
}
