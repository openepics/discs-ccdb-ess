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
package org.openepics.seds.core.io;

import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import org.openepics.seds.api.SedsFactory;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsMetadata;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.api.io.DBConverter;
import org.openepics.seds.core.JsonMapper;
import org.openepics.seds.util.ArrayUtil;
import static org.openepics.seds.util.JsonUtil.ValueBuilder.builder;
import static org.openepics.seds.util.JsonUtil.ValueParser.parser;
import org.openepics.seds.util.ScalarType;
import org.openepics.seds.util.SedsException;
import static org.openepics.seds.util.SedsException.assertNotNull;
import org.openepics.seds.util.TypeUtil;

/**
 * Implementation of a Database converter.
 *
 * @author Aaron Barber
 */
public class BaseDBConverter implements DBConverter {

    //API Access
    //--------------------------------------------------------------------------
    private final SedsFactory factory;
    private final JsonMapper mapper;
    //--------------------------------------------------------------------------

    //Constructors
    //--------------------------------------------------------------------------
    BaseDBConverter(SedsFactory factory, JsonMapper mapper) {
        assertNotNull(factory, SedsFactory.class, "Factory for the DBConverter");
        assertNotNull(mapper, JsonMapper.class, "Mapper (JSON) for the DBConverter");

        this.factory = factory;
        this.mapper = mapper;
    }
    //--------------------------------------------------------------------------

    //Raw Layer
    //--------------------------------------------------------------------------
    private final static String KEY_META = "meta";
    private final static String KEY_RAW = "data";
    private final static String KEY_TYPE = "type";

    private static class Deserializer {

        private static JsonObject toEnum(JsonObject raw, JsonObject type) {
            return builder()
                    .put("selected", parser().asString(raw, "selected"))
                    .put("elements", parser().asArray(type, "elements"))
                    .build();
        }

        private static JsonObject toScalarEnum(JsonObject raw, JsonObject type) {
            return builder()
                    .forcePut("value",
                            builder()
                            .put("selected", parser().asString(raw, "value"))
                            .put("elements", parser().asArray(type, "elements"))
                            .build()
                    )
                    .put("representation", parser().asString(raw, "representation"))
                    .put("alarm", parser().asObject(raw, "alarm"))
                    .put("control", parser().asObject(raw, "control"))
                    .put("display", parser().asObject(raw, "display"))
                    .put("time", parser().asObject(raw, "time"))
                    .build();
        }

        private static JsonObject toScalarArrayEnum(JsonObject raw, JsonObject type) {
            JsonArray valueArray = parser().asArray(raw, "valueArray");
            JsonArrayBuilder builder = Json.createArrayBuilder();

            for (JsonValue element : valueArray) {
                builder.add(
                        builder()
                        .put("selected", element)
                        .put("elements", parser().asArray(type, "elements"))
                        .build()
                );
            }

            return builder()
                    .forcePut("valueArray", builder.build())
                    .put("alarm", parser().asObject(raw, "alarm"))
                    .put("control", parser().asObject(raw, "control"))
                    .put("display", parser().asObject(raw, "display"))
                    .put("time", parser().asObject(raw, "time"))
                    .build();
        }

        private static JsonObject toTable(JsonObject raw, JsonObject type) {
            return builder()
                    .put("numRows", parser().asInteger(raw, "numRows"))
                    .put("numColumns", parser().asInteger(type, "numColumns"))
                    .put("names", parser().asArray(type, "names"))
                    .put("columnTypes", parser().asArray(type, "columnTypes"))
                    .put("values", parser().asArray(raw, "values"))
                    .build();
        }
    }

    private static class RawJson {

        private static JsonObject ofEnum(SedsEnum value) {
            if (value == null) {
                return null;
            }

            return builder()
                    .put("selected", value.getSelected())
                    .build();
        }

        private static JsonObject ofScalarEnum(SedsScalar<SedsEnum> value, JsonMapper mapper) {
            if (value == null) {
                return null;
            }

            //NPE Preventions
            Object valueField = null;
            if (value.getValue() != null) {
                valueField = value.getValue().getSelected();
            }

            return builder()
                    .put("value", valueField)
                    .put("representation", value.getRepresentation())
                    .put("alarm", mapper.fromSedsAlarm(value.getAlarm()))
                    .put("control", mapper.fromSedsControl(value.getControl()))
                    .put("display", mapper.fromSedsDisplay(value.getDisplay()))
                    .put("time", mapper.fromSedsTime(value.getTime()))
                    .build();
        }

        private static JsonObject ofScalarArrayEnum(SedsScalarArray<SedsEnum> value, JsonMapper mapper) {
            if (value == null) {
                return null;
            }

            JsonArrayBuilder arr = Json.createArrayBuilder();

            for (SedsEnum v : value.getValueArray()) {
                arr.add(v.getSelected());
            }

            return builder()
                    .put("valueArray", arr)
                    .put("alarm", mapper.fromSedsAlarm(value.getAlarm()))
                    .put("control", mapper.fromSedsControl(value.getControl()))
                    .put("display", mapper.fromSedsDisplay(value.getDisplay()))
                    .put("time", mapper.fromSedsTime(value.getTime()))
                    .build();
        }

        private static JsonObject ofTable(SedsTable value, JsonMapper mapper) {
            if (value == null) {
                return null;
            }

            JsonArray array = null;

            if (value.getValues() != null) {
                JsonArrayBuilder builder = Json.createArrayBuilder();

                for (SedsScalarArray element : value.getValues()) {
                    switch (element.getType()) {
                        case BOOLEAN:
                            builder.add(mapper.fromSedsScalarArrayBoolean(element));
                            break;
                        case ENUM:
                            builder.add(mapper.fromSedsScalarArrayEnum(element));
                            break;
                        case INTEGER:
                            builder.add(mapper.fromSedsScalarArrayInteger(element));
                            break;
                        case NUMBER:
                            builder.add(mapper.fromSedsScalarArrayNumber(element));
                            break;
                        case STRING:
                            builder.add(mapper.fromSedsScalarArrayString(element));
                            break;
                        case UNKNOWN:
                        default:
                            builder.addNull();
                            break;
                    }
                }

                array = builder.build();
            }

            return builder()
                    .put("numRows", value.getNumRows())
                    .put("values", array)
                    .build();
        }

    }

    private static class TypeJson {

        private static JsonObject ofEnum(SedsEnum value) {
            if (value == null) {
                return null;
            }

            return builder()
                    .put("elements", ArrayUtil.AsJsonArray.typeJson(value.getElements()))
                    .build();
        }

        private static JsonObject ofScalarEnum(SedsScalar<SedsEnum> value) {
            if (value == null) {
                return null;
            }

            return ofEnum(value.getValue());
        }

        private static JsonObject ofScalarArrayEnum(SedsScalarArray<SedsEnum> value) {
            if (value == null) {
                return null;
            }

            //Prevent NPE
            if (value.getValueArray() == null || value.getValueArray().length == 0) {
                return null;
            }

            //Validation (All enums have the same elements)
            String[] init = value.getValueArray()[0] == null
                    ? null
                    : value.getValueArray()[0].getElements();
            for (SedsEnum sedsEnum : value.getValueArray()) {
                boolean fails = false;

                if (sedsEnum == null && init != null) {
                    fails = true;
                }
                if (sedsEnum != null && !Arrays.deepEquals(sedsEnum.getElements(), init)) {
                    fails = true;
                }

                if (fails) {
                    throw SedsException.buildIAE(
                            Arrays.toString(value.getValueArray()),
                            "The same 'elements' field for all enums in the scalarArray",
                            "DB serialization of type information for a scalar array of enums"
                    );
                }
            }

            return ofEnum(value.getValueArray()[0]);
        }

        private static JsonObject ofTable(SedsTable value) {
            if (value == null) {
                return null;
            }

            return builder()
                    .put("numColumns", value.getNumColumns())
                    .put("names", ArrayUtil.AsJsonArray.typeJson(value.getNames()))
                    .put("columnTypes", ArrayUtil.AsJsonArray.typeJson(value.getColumnTypes()))
                    .build();
        }

    }
    //--------------------------------------------------------------------------

    //Deserialize
    //--------------------------------------------------------------------------
    private SedsType toSeds(JsonObject meta, JsonObject raw, JsonObject type) {
        if (meta == null || raw == null) {
            return null;
        }

        SedsMetadata sedsMeta = mapper.toSedsMetadata(meta);
        Class clz = TypeUtil.classOf(sedsMeta.getType());

        //Deserialize Special
        //----------------------------------------------------------------------
        if (clz == null) {
        } else if (clz.equals(SedsEnum.class)) {
            raw = Deserializer.toEnum(raw, type);
        } else if (clz.equals(SedsScalar.class) && TypeUtil.scalarTypeOf(sedsMeta.getType()) == ScalarType.ENUM) {
            raw = Deserializer.toScalarEnum(raw, type);
        } else if (clz.equals(SedsScalarArray.class) && TypeUtil.scalarTypeOf(sedsMeta.getType()) == ScalarType.ENUM) {
            raw = Deserializer.toScalarArrayEnum(raw, type);
        } else if (clz.equals(SedsTable.class)) {
            raw = Deserializer.toTable(raw, type);
        }
        //----------------------------------------------------------------------

        return mapper.toSedsType(raw, sedsMeta);
    }
    //--------------------------------------------------------------------------

    //Serialize
    //--------------------------------------------------------------------------
    private JsonObject toMeta(SedsType value) {
        if (value == null) {
            return null;
        }

        return mapper.fromSedsType(factory.newMetadata(value));
    }

    private JsonObject toRaw(SedsType value) {
        if (value == null) {
            return null;
        }

        //Serialize Special
        //----------------------------------------------------------------------
        if (value instanceof SedsEnum) {
            return RawJson.ofEnum((SedsEnum) value);
        } else if (value instanceof SedsScalar && ((SedsScalar) value).getType() == ScalarType.ENUM) {
            return RawJson.ofScalarEnum((SedsScalar<SedsEnum>) value, mapper);
        } else if (value instanceof SedsScalarArray && ((SedsScalarArray) value).getType() == ScalarType.ENUM) {
            return RawJson.ofScalarArrayEnum((SedsScalarArray<SedsEnum>) value, mapper);
        } else if (value instanceof SedsTable) {
            return RawJson.ofTable((SedsTable) value, mapper);
        }
        //----------------------------------------------------------------------

        return mapper.fromSedsType(value);
    }

    private JsonObject toType(SedsType value) {
        if (value == null) {
            return null;
        }

        //Serialize Special
        //----------------------------------------------------------------------
        if (value instanceof SedsEnum) {
            return TypeJson.ofEnum((SedsEnum) value);
        } else if (value instanceof SedsScalar && ((SedsScalar) value).getType() == ScalarType.ENUM) {
            return TypeJson.ofScalarEnum((SedsScalar<SedsEnum>) value);
        } else if (value instanceof SedsScalarArray && ((SedsScalarArray) value).getType() == ScalarType.ENUM) {
            return TypeJson.ofScalarArrayEnum((SedsScalarArray<SedsEnum>) value);
        } else if (value instanceof SedsTable) {
            return TypeJson.ofTable((SedsTable) value);
        }
        //----------------------------------------------------------------------

        return null;
    }
    //--------------------------------------------------------------------------

    //API Layer
    //--------------------------------------------------------------------------
    @Override
    public JsonObject parseMetaComponent(JsonObject value) {
        return parser().asObject(value, KEY_META);
    }

    @Override
    public JsonObject parseRawComponent(JsonObject value) {
        return parser().asObject(value, KEY_RAW);
    }

    @Override
    public JsonObject parseTypeComponent(JsonObject value) {
        return parser().asObject(value, KEY_TYPE);
    }

    @Override
    public JsonObject combineComponents(JsonObject meta, JsonObject raw, JsonObject type) {
        return builder()
                .put(KEY_META, meta)
                .put(KEY_RAW, raw)
                .put(KEY_TYPE, type)
                .build();
    }
    //--------------------------------------------------------------------------

    //API Layer
    //--------------------------------------------------------------------------
    @Override
    public JsonObject serialize(SedsType value) {
        if (value == null) {
            return null;
        }

        return combineComponents(
                toMeta(value),
                toRaw(value),
                toType(value)
        );
    }

    @Override
    public SedsType deserialize(JsonObject value) {
        if (value == null) {
            return null;
        }

        return toSeds(
                parseMetaComponent(value),
                parseRawComponent(value),
                parseTypeComponent(value)
        );
    }
    //--------------------------------------------------------------------------
}
