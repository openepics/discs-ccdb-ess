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
package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PropertyEntityLoggerTest {
    private final Unit unit = new Unit("Ampere", "Current", "A", "BlahBlha");
    private final DataType dt = new DataType("Float", "Float", true, "Well.. a scalar float");
    private final Property prop = new Property("TestProperty", "Description of test Property");

    private PropertyEntityLogger pel = new PropertyEntityLogger();


    @Before
    public void setUp() throws Exception {
        prop.setUnit(unit);
        prop.setDataType(dt);
    }

    @Test
    public void testGetType() {
        assertTrue(Property.class.equals(pel.getType()));
    }

    @Test
    public void testSerializeEntity() throws JsonProcessingException, IOException {
        final String RESULT = "{\"description\":\"Description of test Property\","
                + "\"typeAssociation\":true,\"slotAssociation\":false,\"deviceAssociation\":false,"
                + "\"alignmentAssociation\":false,\"dataType\":\"Float\",\"unit\":\"Ampere\"}";

        // create an ObjectMapper instance.
        ObjectMapper mapper = new ObjectMapper();
        // use the ObjectMapper to read the json string and create a tree
        JsonNode node = mapper.readTree(RESULT);

        assertEquals(node.get("description").asText(), "Description of test Property");
        assertEquals(node.get("typeAssociation").asBoolean(), true);
        assertEquals(node.get("slotAssociation").asBoolean(), false);
        assertEquals(node.get("deviceAssociation").asBoolean(), false);
        assertEquals(node.get("alignmentAssociation").asBoolean(), false);
        assertEquals(node.get("dataType").asText(), "Float");
        assertEquals(node.get("unit").asText(), "Ampere");
    }
}
