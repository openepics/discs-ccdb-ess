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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.util.BuiltInDataType;

/**
 * This class initializes a mapping between various Value classes and the implementation of the converter. The class is a
 * singleton so that the converters are converter mapping is initialized at application startup and only once.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Singleton
@Startup
public class SedsConverters {

    private static final Logger logger = Logger.getLogger(SedsConverter.class.getCanonicalName());

    private static final Map<Class<? extends Value>, ValueConverter<? extends Value>> converters = new ConcurrentHashMap<>();

    public SedsConverters() {}

    /**
     * Constructs the item. Expects injected iterator of all EntityLogger implementations
     *
     * @param allLoggers CDI will inject all logger types in this constructor parameter
     */
    @Inject
    public SedsConverters(@Any Instance<ValueConverter<? extends Value>> allConverters) {
        int convertersFound = 0;
        for (ValueConverter<? extends Value> converter : allConverters) {
            converters.put(converter.getType(), converter);
            convertersFound++;
        }

        logger.log(Level.INFO, "Loaded " + convertersFound + " data type converters.");
        if (convertersFound != BuiltInDataType.values().length) {
            logger.log(Level.SEVERE, "Converter data type implementation number mismatch. Expected: " + BuiltInDataType.values().length
                    + ", found: " + convertersFound);
        }
    }

    public static <T extends Value> String convertToDatabaseColumn(T attribute) {
        @SuppressWarnings("unchecked")
        ValueConverter<T> converter = (ValueConverter<T>) converters.get(attribute.getClass());
        if (converter == null) {
            throw new IllegalStateException("Could not find converter for " + attribute.getClass().getName());
        }

        return converter.convertToDatabaseColumn(attribute);
    }

    /**
     * A method for initializing the <code>converters</code> static variable to be used from unit tests only.
     * @param converters
     */
    protected static void setConverters(Map<Class<? extends Value>, ValueConverter<? extends Value>> converters) {
        SedsConverters.converters.putAll(converters);
    }
}
