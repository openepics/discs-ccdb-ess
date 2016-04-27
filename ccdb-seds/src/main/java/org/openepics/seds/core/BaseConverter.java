/* 
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013, 2014.
 *  
 *  You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *    http://www.gnu.org/licenses/gpl.txt
 *  
 *  Contact Information:
 *       Facility for Rare Isotope Beam
 *       Michigan State University
 *       East Lansing, MI 48824-1321
 *        http://frib.msu.edu
 */
package org.openepics.seds.core;

import javax.json.JsonObject;
import org.openepics.seds.api.AbstractMapper;
import org.openepics.seds.api.SedsConverter;
import org.openepics.seds.api.SedsDeserializer;
import org.openepics.seds.api.SedsSerializer;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.util.SedsException;
import static org.openepics.seds.util.SedsException.assertNotNull;

class BaseConverter<T extends AbstractMapper> implements SedsConverter<AbstractMapper> {

    private final SedsSerializer s;
    private final SedsDeserializer d;
    private final AbstractMapper m;

    BaseConverter(T m, SedsSerializer s, SedsDeserializer d) {
        assertNotNull(m, AbstractMapper.class, "Mapper for the Converter");
        assertNotNull(s, SedsSerializer.class, "Serializer for the Converter");
        assertNotNull(d, SedsDeserializer.class, "Deserializer for the Converter");

        this.s = s;
        this.d = d;
        this.m = m;
    }

    //Co-domain: VTYPE
    //--------------------------------------------------------------------------
    @Override
    public Object toClientType(SedsType value) {
        assertNotNull(value, SedsType.class, "To client type from seds type");
        return m.fromSedsType(value);
    }

    @Override
    public Object toClientType(JsonObject value) throws SedsException {
        assertNotNull(value, JsonObject.class, "To client type from json type");
        return m.fromSedsType(toSEDS(value));
    }
    //--------------------------------------------------------------------------

    //Co-domain: SEDS
    //--------------------------------------------------------------------------
    @Override
    public SedsType toSEDS(Object value) {
        assertNotNull(value, Object.class, "To seds type from client type");
        return m.toSedsType(value);
    }

    @Override
    public SedsType toSEDS(JsonObject value) throws SedsException {
        assertNotNull(value, JsonObject.class, "To seds type from json type");
        return d.deserializeSEDS(value);
    }
    //--------------------------------------------------------------------------

    //Co-domain: JSON
    //--------------------------------------------------------------------------
    @Override
    public JsonObject toJSON(Object value) throws SedsException {
        assertNotNull(value, Object.class, "To json type from client type");
        return toJSON(toSEDS(value));
    }

    @Override
    public JsonObject toJSON(SedsType value) throws SedsException {
        assertNotNull(value, SedsType.class, "To json type from seds type");
        return s.serializeSEDS(value);
    }
    //--------------------------------------------------------------------------

    //Helper
    //--------------------------------------------------------------------------
    @Override
    public boolean isClientType(Object value) {
        return m.isClientType(value);
    }
    //--------------------------------------------------------------------------

}
