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

import org.openepics.discs.conf.ent.values.EnumValue;
import org.openepics.seds.api.datatypes.SedsEnum;

/**
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public class EnumValueConverter extends ValueConverter<EnumValue> {

    @Override
    public Class<EnumValue> getType() { return EnumValue.class; }

    @Override
    public String convertToDatabaseColumn(EnumValue attribute) {
        final SedsEnum sedsEnum = sedsFactory.getFactory().newEnum(attribute.getEnumValue(), new String[] { attribute.getEnumValue() });
        return sedsDbConverter.serialize(sedsEnum).toString();
    }
}
