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

import java.util.List;

import org.openepics.discs.conf.ent.values.IntVectorValue;
import org.openepics.seds.api.datatypes.SedsScalarArray;

/**
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public class IntVectorValueConverter extends ValueConverter<IntVectorValue> {

    @Override
    public Class<IntVectorValue> getType() {
        return IntVectorValue.class;
    }

    @Override
    public String convertToDatabaseColumn(IntVectorValue attribute) {
        final List<Integer> intVector = attribute.getIntVectorValue();
        final Integer[] intVectorArray = intVector.toArray(new Integer[intVector.size()]);
        final SedsScalarArray<Integer> sedsScalarArray = sedsFactory.newScalarArray(intVectorArray);
        return sedsDbConverter.serialize(sedsScalarArray).toString();
    }
}
