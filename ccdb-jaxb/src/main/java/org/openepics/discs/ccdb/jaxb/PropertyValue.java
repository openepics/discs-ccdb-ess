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
package org.openepics.discs.ccdb.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is data transfer object representing a CCDB property value for JSON and XML serialization.
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@XmlRootElement(name = "propertyValue")
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyValue {
    private String name;
    private String value;
    private String dataType;
    private String unit;
    private PropertyKind propertyKind;

    public PropertyValue() { }

    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public String getValue() { return value; }
    public void setValue(final String value) { this.value = value; }

    public String getDataType() { return dataType; }
    public void setDataType(final String dataType) { this.dataType = dataType; }

    public String getUnit() { return unit; }
    public void setUnit(final String unit) { this.unit = unit; }

    public PropertyKind getPropertyKind() { return propertyKind; }
    public void setPropertyKind(final PropertyKind propertyKind) { this.propertyKind = propertyKind; }
}
