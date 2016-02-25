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

import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ent.Property;

/**
 *
 * @author vuppala
 */
@Named
public class PropertyConverter implements Converter {
    private static final Logger LOGGER = Logger.getLogger(PropertyConverter.class.getCanonicalName());

    @Inject private PropertyEJB propertyEJB;

    public PropertyConverter() {}

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Property prop;

        if (value == null || value.isEmpty()) {
            LOGGER.fine("PropertyConverter: empty property id");
            return null;
        } else {
            prop = propertyEJB.findById(Long.parseLong(value));
            return prop;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || "".equals(value)) {
            LOGGER.fine("PropertyConverter: empty property object");
            return "";
        } else {
            return ((Property) value).getId().toString();
        }
    }
}
