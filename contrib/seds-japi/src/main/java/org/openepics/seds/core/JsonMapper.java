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

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import org.epics.util.time.Timestamp;
import org.openepics.seds.api.AbstractMapper;
import org.openepics.seds.api.SedsDeserializer;
import org.openepics.seds.api.SedsFactory;
import org.openepics.seds.api.SedsSerializer;
import org.openepics.seds.api.datatypes.SedsAlarm;
import org.openepics.seds.api.datatypes.SedsControl;
import org.openepics.seds.api.datatypes.SedsDisplay;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.api.datatypes.SedsMetadata;
import org.openepics.seds.api.datatypes.SedsScalar;
import org.openepics.seds.api.datatypes.SedsScalarArray;
import org.openepics.seds.api.datatypes.SedsTable;
import org.openepics.seds.api.datatypes.SedsTime;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.util.AlarmType;
import org.openepics.seds.util.ArrayUtil;
import org.openepics.seds.util.JsonUtil.ValueBuilder;
import static org.openepics.seds.util.JsonUtil.ValueParser.parser;
import static org.openepics.seds.util.SedsException.assertNotNull;
import org.openepics.seds.util.TypeUtil;

/**
 * A SEDS processor to provide a bidirectional mapping from {@link JsonObject}
 * values to {@link SedsType} values.
 *
 * <p>
 * The primary methods should be used to perform generic mapping:
 * <ul>
 * <li>Serialization:
 * {@link #fromSedsType(org.openepics.seds.api.datatypes.SedsType)}
 * <li>Deserialization:
 * {@link #toSedsType(javax.json.JsonObject, org.openepics.seds.api.datatypes.SedsMetadata)}
 * </ul>
 *
 * <p>
 * SEDS protocol provides JSON schemas to check if data is valid SEDS data.
 * There is <b>NO</b> validation performed by the {@code JsonMapper}.
 *
 * <p>
 * Additionally, metadata is not included when converting to JSON.
 *
 * <p>
 * To follow standards (with validation and the structuring of the data/metadata
 * combination), use a {@link SedsSerializer} and a {@link SedsDeserializer}.
 *
 *
 * @author Aaron Barber
 */
public class JsonMapper extends AbstractMapper<JsonObject, JsonObject, JsonObject, JsonObject, JsonObject, JsonObject, JsonObject, JsonObject, JsonObject, JsonObject, JsonObject, JsonObject, JsonObject, JsonObject, JsonObject, JsonObject> {

    //API
    //--------------------------------------------------------------------------
    private final SedsFactory factory;
    private final RawSerializer raw;
    //--------------------------------------------------------------------------

    //Constructor
    //--------------------------------------------------------------------------
    /**
     * Creates a mapper that maps JsonObjects to and from SEDS types.
     *
     * @param factory factory to create SEDS types (used in the "toSeds"
     * methods)
     */
    public JsonMapper(SedsFactory factory) {
        assertNotNull(factory, SedsFactory.class, "Factory for the Json mapper");

        this.factory = factory;
        this.raw = new RawSerializer();
    }
    //--------------------------------------------------------------------------

    //Helper
    //--------------------------------------------------------------------------
    class RawSerializer {

        JsonObject alarm(
                String severity,
                String status,
                String message
        ) {
            return ValueBuilder.builder()
                    .put("severity", severity)
                    .put("status", status)
                    .put("message", message)
                    .build();
        }

        JsonObject control(
                Number lowLimit,
                Number highLimit
        ) {
            return ValueBuilder.builder()
                    .put("lowLimit", lowLimit)
                    .put("highLimit", highLimit)
                    .build();
        }

        JsonObject display(
                Number lowAlarm,
                Number highAlarm,
                Number lowDisplay,
                Number highDisplay,
                Number lowWarning,
                Number highWarning,
                String description,
                String units
        ) {
            return ValueBuilder.builder()
                    .put("lowAlarm", lowAlarm)
                    .put("highAlarm", highAlarm)
                    .put("lowDisplay", lowDisplay)
                    .put("highDisplay", highDisplay)
                    .put("lowWarning", lowWarning)
                    .put("highWarning", highWarning)
                    .put("description", description)
                    .put("units", units)
                    .build();
        }

        JsonObject enumerated(
                String selected,
                String[] elements
        ) {
            return ValueBuilder.builder()
                    .put("selected", selected)
                    .put("elements", ArrayUtil.AsJsonArray.typeJson(elements))
                    .build();
        }

        JsonObject time(
                Long unixSec,
                Integer nanoSec,
                Integer userTag
        ) {
            return ValueBuilder.builder()
                    .put("unixSec", unixSec)
                    .put("nanoSec", nanoSec)
                    .put("userTag", userTag)
                    .build();
        }

        JsonObject scalar(
                Object value,
                SedsAlarm alarm,
                SedsControl control,
                SedsDisplay display,
                SedsTime time
        ) {
            return ValueBuilder.builder()
                    .forcePut("value", value)
                    .put("alarm", fromSedsAlarm(alarm))
                    .put("control", fromSedsControl(control))
                    .put("display", fromSedsDisplay(display))
                    .put("time", fromSedsTime(time))
                    .build();
        }

        JsonObject scalarArray(
                JsonArray value,
                SedsAlarm alarm,
                SedsControl control,
                SedsDisplay display,
                SedsTime time
        ) {
            return ValueBuilder.builder()
                    .forcePut("valueArray", value)
                    .put("alarm", fromSedsAlarm(alarm))
                    .put("control", fromSedsControl(control))
                    .put("display", fromSedsDisplay(display))
                    .put("time", fromSedsTime(time))
                    .build();
        }

        JsonObject table(
                Integer numRows,
                Integer numColumns,
                String[] names,
                String[] columnTypes,
                JsonArray array
        ) {
            return ValueBuilder.builder()
                    .put("numRows", numRows)
                    .put("numColumns", numColumns)
                    .put("names", ArrayUtil.AsJsonArray.typeJson(names))
                    .put("columnTypes", ArrayUtil.AsJsonArray.typeJson(columnTypes))
                    .put("values", array)
                    .build();
        }

        JsonObject metadata(
                String type,
                String protocol,
                String version
        ) {
            return ValueBuilder.builder()
                    .put("type", type)
                    .put("protocol", protocol)
                    .put("version", version)
                    .build();
        }
    }
    //--------------------------------------------------------------------------

    //Deserializing
    //--------------------------------------------------------------------------
    @Override
    public SedsAlarm toSedsAlarm(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newAlarm(
                AlarmType.fromName(parser().asString(value, "severity")),
                parser().asString(value, "status"),
                parser().asString(value, "message")
        );
    }

    @Override
    public SedsControl toSedsControl(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newControl(
                parser().asNumber(value, "lowLimit"),
                parser().asNumber(value, "highLimit")
        );
    }

    @Override
    public SedsDisplay toSedsDisplay(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newDisplay(
                parser().asNumber(value, "lowAlarm"),
                parser().asNumber(value, "highAlarm"),
                parser().asNumber(value, "lowDisplay"),
                parser().asNumber(value, "highDisplay"),
                parser().asNumber(value, "lowWarning"),
                parser().asNumber(value, "highWarning"),
                parser().asString(value, "description"),
                parser().asString(value, "units")
        );
    }

    @Override
    public SedsEnum toSedsEnum(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newEnum(
                parser().asString(value, "selected"),
                ArrayUtil.AsBoxedArray.typeString(parser().asArray(value, "elements"))
        );
    }

    @Override
    public SedsTime toSedsTime(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newTime(
                Timestamp.of(
                        parser().asLong(value, "unixSec"),
                        parser().asInteger(value, "nanoSec")
                ),
                parser().asInteger(value, "userTag")
        );
    }

    @Override
    public SedsScalar<Boolean> toSedsScalarBoolean(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newScalar(
                parser().asBoolean(value, "value"),
                toSedsAlarm(parser().asObject(value, "alarm")),
                toSedsControl(parser().asObject(value, "control")),
                toSedsDisplay(parser().asObject(value, "display")),
                toSedsTime(parser().asObject(value, "time"))
        );
    }

    @Override
    public SedsScalar<SedsEnum> toSedsScalarEnum(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newScalar(
                toSedsEnum(parser().asObject(value, "value")),
                toSedsAlarm(parser().asObject(value, "alarm")),
                toSedsControl(parser().asObject(value, "control")),
                toSedsDisplay(parser().asObject(value, "display")),
                toSedsTime(parser().asObject(value, "time"))
        );
    }

    @Override
    public SedsScalar<Integer> toSedsScalarInteger(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newScalar(
                parser().asInteger(value, "value"),
                toSedsAlarm(parser().asObject(value, "alarm")),
                toSedsControl(parser().asObject(value, "control")),
                toSedsDisplay(parser().asObject(value, "display")),
                toSedsTime(parser().asObject(value, "time"))
        );
    }

    @Override
    public SedsScalar<Number> toSedsScalarNumber(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newScalar(
                parser().asNumber(value, "value"),
                toSedsAlarm(parser().asObject(value, "alarm")),
                toSedsControl(parser().asObject(value, "control")),
                toSedsDisplay(parser().asObject(value, "display")),
                toSedsTime(parser().asObject(value, "time"))
        );
    }

    @Override
    public SedsScalar<String> toSedsScalarString(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newScalar(
                parser().asString(value, "value"),
                toSedsAlarm(parser().asObject(value, "alarm")),
                toSedsControl(parser().asObject(value, "control")),
                toSedsDisplay(parser().asObject(value, "display")),
                toSedsTime(parser().asObject(value, "time"))
        );
    }

    @Override
    public SedsScalarArray<Boolean> toSedsScalarArrayBoolean(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newScalarArray(
                ArrayUtil.AsBoxedArray.typeBoolean(parser().asArray(value, "valueArray")),
                toSedsAlarm(parser().asObject(value, "alarm")),
                toSedsControl(parser().asObject(value, "control")),
                toSedsDisplay(parser().asObject(value, "display")),
                toSedsTime(parser().asObject(value, "time"))
        );
    }

    @Override
    public SedsScalarArray<SedsEnum> toSedsScalarArrayEnum(JsonObject value) {
        if (value == null) {
            return null;
        }

        JsonArray arr = parser().asArray(value, "valueArray");
        SedsEnum[] data = new SedsEnum[arr.size()];
        for (int i = 0; i < data.length; ++i) {
            data[i] = toSedsEnum(arr.getJsonObject(i));
        }
        return factory.newScalarArray(
                data,
                toSedsAlarm(parser().asObject(value, "alarm")),
                toSedsControl(parser().asObject(value, "control")),
                toSedsDisplay(parser().asObject(value, "display")),
                toSedsTime(parser().asObject(value, "time"))
        );
    }

    @Override
    public SedsScalarArray<Integer> toSedsScalarArrayInteger(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newScalarArray(
                ArrayUtil.AsBoxedArray.typeInteger(parser().asArray(value, "valueArray")),
                toSedsAlarm(parser().asObject(value, "alarm")),
                toSedsControl(parser().asObject(value, "control")),
                toSedsDisplay(parser().asObject(value, "display")),
                toSedsTime(parser().asObject(value, "time"))
        );
    }

    @Override
    public SedsScalarArray<Number> toSedsScalarArrayNumber(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newScalarArray(
                ArrayUtil.AsBoxedArray.typeNumber(parser().asArray(value, "valueArray")),
                toSedsAlarm(parser().asObject(value, "alarm")),
                toSedsControl(parser().asObject(value, "control")),
                toSedsDisplay(parser().asObject(value, "display")),
                toSedsTime(parser().asObject(value, "time"))
        );
    }

    @Override
    public SedsScalarArray<String> toSedsScalarArrayString(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newScalarArray(
                ArrayUtil.AsBoxedArray.typeString(parser().asArray(value, "valueArray")),
                toSedsAlarm(parser().asObject(value, "alarm")),
                toSedsControl(parser().asObject(value, "control")),
                toSedsDisplay(parser().asObject(value, "display")),
                toSedsTime(parser().asObject(value, "time"))
        );
    }

    @Override
    public SedsTable toSedsTable(JsonObject value) {
        if (value == null) {
            return null;
        }

        String[] columnTypes = ArrayUtil.AsBoxedArray.typeString(parser().asArray(value, "columnTypes"));
        JsonArray data = parser().asArray(value, "values");
        SedsScalarArray[] values = null;

        if (data != null) {
            values = new SedsScalarArray[data.size()];

            for (int i = 0; i < values.length; i++) {
                String type = columnTypes[i];

                switch (TypeUtil.scalarTypeOf(type)) {
                    case BOOLEAN:
                        values[i] = toSedsScalarArrayBoolean(data.getJsonObject(i));
                        break;
                    case ENUM:
                        values[i] = toSedsScalarArrayEnum(data.getJsonObject(i));
                        break;
                    case INTEGER:
                        values[i] = toSedsScalarArrayInteger(data.getJsonObject(i));
                        break;
                    case NUMBER:
                        values[i] = toSedsScalarArrayNumber(data.getJsonObject(i));
                        break;
                    case STRING:
                        values[i] = toSedsScalarArrayString(data.getJsonObject(i));
                        break;
                    case UNKNOWN:
                        values[i] = null;
                        break;
                    default:
                        values[i] = null;
                        break;

                }
            }
        }

        return factory.newTable(
                parser().asInteger(value, "numRows"),
                parser().asInteger(value, "numColumns"),
                ArrayUtil.AsBoxedArray.typeString(parser().asArray(value, "names")),
                values
        );
    }
    //--------------------------------------------------------------------------

    //Serialize
    //--------------------------------------------------------------------------    
    @Override
    public JsonObject fromSedsAlarm(SedsAlarm alarm) {
        if (alarm == null) {
            return null;
        }

        return raw.alarm(
                alarm.getSeverity().name(),
                alarm.getStatus(),
                alarm.getMessage()
        );
    }

    @Override
    public JsonObject fromSedsControl(SedsControl control) {
        if (control == null) {
            return null;
        }

        return raw.control(
                control.getLowLimit(),
                control.getHighLimit()
        );
    }

    @Override
    public JsonObject fromSedsDisplay(SedsDisplay display) {
        if (display == null) {
            return null;
        }

        return raw.display(
                display.getLowAlarm(),
                display.getHighAlarm(),
                display.getLowDisplay(),
                display.getHighDisplay(),
                display.getLowWarning(),
                display.getHighWarning(),
                display.getDescription(),
                display.getUnits()
        );
    }

    @Override
    public JsonObject fromSedsEnum(SedsEnum enumerated) {
        if (enumerated == null) {
            return null;
        }

        return raw.enumerated(
                enumerated.getSelected(),
                enumerated.getElements()
        );
    }

    @Override
    public JsonObject fromSedsTime(SedsTime time) {
        if (time == null) {
            return null;
        }

        return raw.time(
                time.getUnixSec(),
                time.getNanoSec(),
                time.getUserTag()
        );
    }

    @Override
    public JsonObject fromSedsScalarBoolean(SedsScalar<Boolean> value) {
        if (value == null) {
            return null;
        }

        return raw.scalar(
                value.getValue(),
                value.getAlarm(),
                value.getControl(),
                value.getDisplay(),
                value.getTime()
        );
    }

    @Override
    public JsonObject fromSedsScalarEnum(SedsScalar<SedsEnum> value) {
        if (value == null) {
            return null;
        }

        return raw.scalar(
                fromSedsEnum((SedsEnum) value.getValue()),
                value.getAlarm(),
                value.getControl(),
                value.getDisplay(),
                value.getTime()
        );
    }

    @Override
    public JsonObject fromSedsScalarInteger(SedsScalar<Integer> value) {
        if (value == null) {
            return null;
        }

        return raw.scalar(
                value.getValue(),
                value.getAlarm(),
                value.getControl(),
                value.getDisplay(),
                value.getTime()
        );
    }

    @Override
    public JsonObject fromSedsScalarNumber(SedsScalar<Number> value) {
        if (value == null) {
            return null;
        }

        return raw.scalar(
                value.getValue(),
                value.getAlarm(),
                value.getControl(),
                value.getDisplay(),
                value.getTime()
        );
    }

    @Override
    public JsonObject fromSedsScalarString(SedsScalar<String> value) {
        if (value == null) {
            return null;
        }

        return raw.scalar(
                value.getValue(),
                value.getAlarm(),
                value.getControl(),
                value.getDisplay(),
                value.getTime()
        );
    }

    @Override
    public JsonObject fromSedsScalarArrayBoolean(SedsScalarArray<Boolean> value) {
        if (value == null) {
            return null;
        }

        return raw.scalarArray(
                ArrayUtil.AsJsonArray.typeJson(((SedsScalarArray<Boolean>) value).getValueArray()),
                value.getAlarm(),
                value.getControl(),
                value.getDisplay(),
                value.getTime()
        );
    }

    @Override
    public JsonObject fromSedsScalarArrayEnum(SedsScalarArray<SedsEnum> value) {
        if (value == null) {
            return null;
        }

        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (SedsEnum e : (SedsEnum[]) value.getValueArray()) {
            JsonValue v = fromSedsEnum(e);

            if (v == null) {
                builder.addNull();
            } else {
                builder.add(v);
            }
        }

        return raw.scalarArray(
                builder.build(),
                value.getAlarm(),
                value.getControl(),
                value.getDisplay(),
                value.getTime()
        );
    }

    @Override
    public JsonObject fromSedsScalarArrayInteger(SedsScalarArray<Integer> value) {
        if (value == null) {
            return null;
        }

        return raw.scalarArray(
                ArrayUtil.AsJsonArray.typeJson(((SedsScalarArray<Integer>) value).getValueArray()),
                value.getAlarm(),
                value.getControl(),
                value.getDisplay(),
                value.getTime()
        );
    }

    @Override
    public JsonObject fromSedsScalarArrayNumber(SedsScalarArray<Number> value) {
        if (value == null) {
            return null;
        }

        return raw.scalarArray(
                ArrayUtil.AsJsonArray.typeJson(((SedsScalarArray<Number>) value).getValueArray()),
                value.getAlarm(),
                value.getControl(),
                value.getDisplay(),
                value.getTime()
        );
    }

    @Override
    public JsonObject fromSedsScalarArrayString(SedsScalarArray<String> value) {
        if (value == null) {
            return null;
        }

        return raw.scalarArray(
                ArrayUtil.AsJsonArray.typeJson(((SedsScalarArray<String>) value).getValueArray()),
                value.getAlarm(),
                value.getControl(),
                value.getDisplay(),
                value.getTime()
        );
    }

    @Override
    public JsonObject fromSedsTable(SedsTable value) {
        if (value == null) {
            return null;
        }

        JsonArray array = null;

        if (value.getValues() != null) {
            JsonArrayBuilder builder = Json.createArrayBuilder();

            for (SedsScalarArray element : value.getValues()) {
                switch (element.getType()) {
                    case BOOLEAN:
                        builder.add(fromSedsScalarArrayBoolean(element));
                        break;
                    case ENUM:
                        builder.add(fromSedsScalarArrayEnum(element));
                        break;
                    case INTEGER:
                        builder.add(fromSedsScalarArrayInteger(element));
                        break;
                    case NUMBER:
                        builder.add(fromSedsScalarArrayNumber(element));
                        break;
                    case STRING:
                        builder.add(fromSedsScalarArrayString(element));
                        break;
                    case UNKNOWN:
                    default:
                        builder.addNull();
                        break;
                }
            }

            array = builder.build();
        }

        return raw.table(
                value.getNumRows(),
                value.getNumColumns(),
                value.getNames(),
                value.getColumnTypes(),
                array
        );
    }
    //--------------------------------------------------------------------------

    //Other
    //--------------------------------------------------------------------------
    @Override
    public JsonObject fromSedsType(SedsType value) {
        if (value instanceof SedsMetadata) {
            return fromSedsMetadata((SedsMetadata) value);
        }

        return (JsonObject) super.fromSedsType(value);
    }

    @Override
    public SedsType toSedsType(Object value) {
        throw new UnsupportedOperationException(
                "Not supported. Try an overloaded version of this method."
        );
    }

    /**
     * Maps the JSON data into SEDS data using the metadata to determine the
     * SEDS type.
     *
     * <p>
     * Returns null if metadata is null or the type field of the metadata is
     * null. Returns null if the JSON data is null.
     *
     * <p>
     * No validation (validation from JSON Schemas) is performed at this point.
     *
     * @param data JSON data that conforms to SEDS data
     * @param meta metadata sepcifying the type of the data
     * @return SEDS data based on the JSON data
     */
    public SedsType toSedsType(JsonObject data, SedsMetadata meta) {
        //Meta data
        //----------------------------------------------------------------------        
        if (meta == null) {
            return null;
        }

        Class type = TypeUtil.classOf(meta.getType());

        if (type == null) {
            return null;
        }
        //----------------------------------------------------------------------

        //Deserialize
        //----------------------------------------------------------------------        
        if (type.equals(SedsAlarm.class)) {
            return toSedsAlarm(data);
        } else if (type.equals(SedsDisplay.class)) {
            return toSedsDisplay(data);
        } else if (type.equals(SedsEnum.class)) {
            return toSedsEnum(data);
        } else if (type.equals(SedsMetadata.class)) {
            return toSedsMetadata(data);
        } else if (type.equals(SedsControl.class)) {
            return toSedsControl(data);
        } else if (type.equals(SedsScalar.class)) {
            switch (TypeUtil.scalarTypeOf(meta.getType())) {
                case BOOLEAN:
                    return toSedsScalarBoolean(data);
                case ENUM:
                    return toSedsScalarEnum(data);
                case INTEGER:
                    return toSedsScalarInteger(data);
                case NUMBER:
                    return toSedsScalarNumber(data);
                case STRING:
                    return toSedsScalarString(data);
                case UNKNOWN:
                    return null;
                default:
                    return null;
            }
        } else if (type.equals(SedsScalarArray.class)) {
            switch (TypeUtil.scalarTypeOf(meta.getType())) {
                case BOOLEAN:
                    return toSedsScalarArrayBoolean(data);
                case ENUM:
                    return toSedsScalarArrayEnum(data);
                case INTEGER:
                    return toSedsScalarArrayInteger(data);
                case NUMBER:
                    return toSedsScalarArrayNumber(data);
                case STRING:
                    return toSedsScalarArrayString(data);
                case UNKNOWN:
                    return null;
                default:
                    return null;
            }
        } else if (type.equals(SedsTable.class)) {
            return toSedsTable(data);
        } else if (type.equals(SedsTime.class)) {
            return toSedsTime(data);
        }
        //----------------------------------------------------------------------

        return null;
    }

    @Override
    public boolean isClientType(Object value) {
        return value instanceof JsonObject;
    }
    //--------------------------------------------------------------------------

    //Added Types
    //--------------------------------------------------------------------------
    /**
     * Converts the JSON object into a standard SEDS Metadata value.
     *
     * @param value JSON object to map
     * @return SEDS Metadata from the JSON data
     */
    public SedsMetadata toSedsMetadata(JsonObject value) {
        if (value == null) {
            return null;
        }

        return factory.newMetadata(
                parser().asString(value, "type"),
                parser().asString(value, "protocol"),
                parser().asString(value, "version")
        );
    }

    /**
     * Converts a standard SEDS Metadata value into a JSON object.
     *
     * @param meta SEDS Metadata to map
     * @return JSON object containing the data of the SEDS Metadata value
     */
    public JsonObject fromSedsMetadata(SedsMetadata meta) {
        if (meta == null) {
            return null;
        }

        return raw.metadata(
                meta.getType(),
                meta.getProtocol(),
                meta.getVersion()
        );
    }
    //--------------------------------------------------------------------------
}
