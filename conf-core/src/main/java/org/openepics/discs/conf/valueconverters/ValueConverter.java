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

import org.openepics.discs.conf.ent.values.Value;
import org.openepics.seds.api.io.DBConverter;
import org.openepics.seds.core.Seds;
import org.openepics.seds.core.datatypes.SimpleSedsFactory;

/**
 * @param <T> the actual Value type this converter is for.
 *
 * @author Miha Vitorovič &lt;miha.vitorovic@cosylab.com&gt;
 *
 */
public abstract class ValueConverter<T extends Value> {
    // we only want the factories to be instantiated once and for all descendants.
    protected static final SimpleSedsFactory SEDS_FACTORY = new SimpleSedsFactory();
    protected static final DBConverter SEDS_DB_CONVERTER = Seds.newDBConverter();

    /**
     * @return the class of the {@link Value} used in this converter.
     */
    public abstract Class<T> getType();

    /** The method converts the actual {@link Value} instance into a string representation (serialization) to store in
     * the database.
     * @param attribute The {@link Value} instance to serialize
     * @return the serialized String to store into the database.
     */
    public abstract String convertToDatabaseColumn(T attribute);
}
