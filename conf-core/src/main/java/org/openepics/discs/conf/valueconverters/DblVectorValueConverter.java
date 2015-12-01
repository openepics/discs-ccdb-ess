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

import org.openepics.discs.conf.ent.values.DblVectorValue;
import org.openepics.seds.api.datatypes.SedsScalarArray;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public class DblVectorValueConverter extends ValueConverter<DblVectorValue> {

    @Override
    public Class<DblVectorValue> getType() {
        return DblVectorValue.class;
    }

    @Override
    public String convertToDatabaseColumn(DblVectorValue attribute) {
        final List<Double> dblVector = attribute.getDblVectorValue();
        final List<String> reps = attribute.getRepresentations();
        final Double[] dblVectorArray = dblVector.toArray(new Double[dblVector.size()]);
        final String[] reprArray = reps.toArray(new String[reps.size()]);
        final SedsScalarArray<Number> sedsScalarArray = I_SEDS_FACTORY.newScalarArray(dblVectorArray, reprArray, null, null, null, null);
        return SEDS_DB_CONVERTER.serialize(sedsScalarArray).toString();
    }

}
