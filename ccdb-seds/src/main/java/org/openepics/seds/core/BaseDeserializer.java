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

import java.util.Map.Entry;
import javax.json.JsonObject;
import org.openepics.seds.api.SedsDeserializer;
import org.openepics.seds.api.SedsValidator;
import org.openepics.seds.api.datatypes.SedsMetadata;
import org.openepics.seds.api.datatypes.SedsType;
import org.openepics.seds.util.JsonUtil;
import static org.openepics.seds.util.JsonUtil.ValueParser.parser;
import org.openepics.seds.util.SedsException;
import static org.openepics.seds.util.SedsException.assertNotNull;
import org.openepics.seds.util.ValidationUtil;

/**
 * Parses a {@link javax.json.JsonObject} into a TYPE object.
 *
 * <p>
 * The utility class {@link JsonUtil} is capable of creating a
 * {@code BaseDeserializer}.
 *
 * <p>
 * <b>Null JSON object to deserialize:</b>
 * Each parse method returns null if no {@code JsonObject} is given, and will
 * put null data into the TYPE object if the correct data cannot be parsed. The
 * intent is to try to convert Json data into TYPE data as best as possible
 * without throwing an exception.
 *
 * <p>
 * <b>Metadata information:</b>
 * The primary parse method, {@link #deserializeSEDS(javax.json.JsonObject)}
 * parses {@link SedsMetadata} to determine the {@code SedsType} to parse the
 * {@code JsonObject} as. All other parse methods do NOT read the metadata.
 *
 * @author Aaron Barber
 */
class BaseDeserializer implements SedsDeserializer {

    //API
    //-------------------------------------------------------------------------- 
    private final SedsValidator validator;
    private final JsonMapper mapper;

    //Constructor
    //-------------------------------------------------------------------------- 
    BaseDeserializer(SedsValidator validator, JsonMapper mapper) {
        assertNotNull(validator, SedsValidator.class, "Validator for the Deserializer");

        this.validator = validator;
        this.mapper = mapper;
    }
    //-------------------------------------------------------------------------- 

    //Interface Implementation
    //-------------------------------------------------------------------------- 
    @Override
    public SedsType deserializeSEDS(JsonObject value) throws SedsException {
        if (value == null) {
            return null;
        }

        //Validation
        ValidationUtil.assertValid(
                validator,
                ValidationUtil.DataType.SEDS_TYPE,
                value
        );

        //Parse Data and Metadata
        //----------------------------------------------------------------------
        Entry<SedsMetadata, JsonObject> entry = separateMetadata(value);
        SedsMetadata meta = entry.getKey();
        JsonObject data = entry.getValue();
        //----------------------------------------------------------------------

        return mapper.toSedsType(data, meta);
    }

    @Override
    public SedsMetadata deserializeMeta(JsonObject value) throws SedsException {
        SedsMetadata meta = mapper.toSedsMetadata(
                ValidationUtil.assertValid(
                        validator,
                        ValidationUtil.DataType.SEDS_META,
                        value
                )
        );
        validator.validateMetadataValues(meta);
        return meta;
    }
    //-------------------------------------------------------------------------- 

    //Protocol
    //-------------------------------------------------------------------------- 
    @Override
    public Entry<SedsMetadata, JsonObject> separateMetadata(JsonObject value) throws SedsException {
        final JsonObject jsonData = parser().asObject(value, "data");
        final JsonObject jsonMeta = parser().asObject(value, "metadata");

        final SedsMetadata meta = deserializeMeta(jsonMeta);

        return new Entry<SedsMetadata, JsonObject>() {

            @Override
            public SedsMetadata getKey() {
                return meta;
            }

            @Override
            public JsonObject getValue() {
                return jsonData;
            }

            @Override
            public JsonObject setValue(JsonObject v) {
                throw new UnsupportedOperationException("This entry is immutable.");
            }

        };
    }
    //-------------------------------------------------------------------------- 

}
