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

import org.openepics.discs.conf.ent.values.Value;
import org.openepics.seds.api.io.DBConverter;
import org.openepics.seds.core.Seds;
import org.openepics.seds.core.datatypes.SimpleSedsFactory;

/**
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
abstract public class ValueConverter implements AttributeConverter<Value, String> {
    protected static final SimpleSedsFactory sedsFactory = new SimpleSedsFactory();
    protected static final DBConverter sedsDbConverter = Seds.newDBConverter();

    abstract public Class<? extends Value> getType();

    @Override
    public Value convertToEntityAttribute(String dbData) {
        throw new IllegalStateException("SedsConverter should take care of this conversion.");
    }

    @Override
    abstract public String convertToDatabaseColumn(Value attribute);

}
