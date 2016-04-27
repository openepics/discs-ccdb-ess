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
import org.openepics.seds.api.SedsFactory;
import org.openepics.seds.api.SedsSerializer;
import org.openepics.seds.api.SedsValidator;
import org.openepics.seds.api.datatypes.SedsMetadata;
import org.openepics.seds.api.datatypes.SedsType;
import static org.openepics.seds.util.JsonUtil.ValueBuilder.builder;
import org.openepics.seds.util.SedsException;
import org.openepics.seds.util.ValidationUtil;

/**
 * Builds a {@link javax.json.JsonObject} from a TYPE object.
 *
 * <p>
 * The utility class {@link  is capable of creating a
 * {@code BaseSerializer}.
 *
 * <p>
 * <b>Null parameter value to serialize:</b>
 * Each build method returns null if no {@code JsonObject} is given. The intent
 * is to try to convert TYPE data into Json data as best as possible without
 * throwing an exception.
 *
 * <p>
 * <b>Null value for required field:</b>
 * Exceptions are thrown when <b>required</b> fields have null data.
 *
 * <p>
 * <b>Null value for non-required field:</b>
 * Additionally, the builder will not include name-value pairs with a null
 * value. Note that arrays <b>DO</b> still include elements that are null (this
 * ensures fields associated with certain indices or the array length will still
 * be correct).
 *
 * <p>
 * <b>Metadata information:</b>
 * The primary build method, null {@link #serializeSEDS(org.openepics.seds.types.SedsType)}, also attaches
 * {@link SedsMetadata} to the {@code JsonObject}. All other build methods do
 * NOT include the metadata.
 *
 * @author Aaron Barber
 */
class BaseSerializer implements SedsSerializer {

    private final SedsFactory factory;
    private final SedsValidator validator;
    private final JsonMapper mapper;

    //Building
    //--------------------------------------------------------------------------
    BaseSerializer(SedsFactory factory, SedsValidator validator, JsonMapper mapper) {
        this.factory = factory;
        this.validator = validator;
        this.mapper = mapper;
    }
    //--------------------------------------------------------------------------

    //Interface Implementation
    //--------------------------------------------------------------------------
    @Override
    public JsonObject serializeSEDS(SedsType value) throws SedsException {
        if (value == null) {
            return null;
        }

        JsonObject data = serializeData(value);
        JsonObject meta = serializeMeta(value);

        return ValidationUtil.assertValid(
                validator,
                ValidationUtil.DataType.SEDS_TYPE,
                appendMetadata(data, meta)
        );
    }

    @Override
    public JsonObject serializeMeta(SedsType value) throws SedsException {
        return ValidationUtil.assertValid(
                validator,
                ValidationUtil.DataType.SEDS_META,
                mapper.fromSedsType(factory.newMetadata(value))
        );
    }

    @Override
    public JsonObject serializeData(SedsType value) throws SedsException {
        return ValidationUtil.assertValid(
                validator,
                ValidationUtil.DataType.SEDS_RAW,
                mapper.fromSedsType(value)
        );
    }
    //--------------------------------------------------------------------------    

    //Protocol
    //--------------------------------------------------------------------------        
    private JsonObject appendMetadata(JsonObject data, JsonObject metadata) {
        return builder()
                .put("metadata", metadata)
                .put("data", data)
                .build();
    }
    //--------------------------------------------------------------------------    
}
