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
package org.openepics.discs.conf.valueconverters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.openepics.discs.conf.ent.values.Value;

/**
 * A JPA converter to convert between the Value object and the data serialized in the database.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Converter(autoApply=true)
public class SedsConverter implements AttributeConverter<Value, String> {

    public SedsConverter() {}

    @Override
    public String convertToDatabaseColumn(Value attribute) {
        if (attribute == null) {
            return null;
        }

        return SedsConverters.convertToDatabaseColumn(attribute);
    }

    @Override
    public Value convertToEntityAttribute(String dbData) {
        return ValueConverter.convertToEntityAttribute(dbData);
    }
}
