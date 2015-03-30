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

import static org.openepics.seds.util.SedsException.assertNotNull;
import static org.openepics.seds.util.ValidationUtil.validate;

import javax.json.JsonObject;

import org.openepics.seds.api.SedsValidator;
import org.openepics.seds.api.datatypes.SedsMetadata;
import org.openepics.seds.util.SedsException;
import org.openepics.seds.util.ValueUtil;

import com.github.fge.jsonschema.core.report.ProcessingReport;

/**
 *
 * @author Aaron Barber
 */
class BaseValidator implements SedsValidator {

    BaseValidator() {

    }

    @Override
    public boolean isValidSEDS(JsonObject instance) throws SedsException {
        assertNotNull(instance, JsonObject.class, "Validating SEDS (meta and raw)");
        return validate(ValueUtil.SCHEMA_SEDS_TYPE, ValueUtil.SCHEMA_SEDS_TYPE_PATH, instance).isSuccess();
    }

    @Override
    public boolean isValidSEDSRaw(JsonObject instance) throws SedsException {
        assertNotNull(instance, JsonObject.class, "Validating SEDS (raw)");
        return validate(ValueUtil.SCHEMA_SEDS_RAW, ValueUtil.SCHEMA_SEDS_RAW_PATH, instance).isSuccess();
    }

    @Override
    public boolean isValidSEDSMeta(JsonObject instance) throws SedsException {
        assertNotNull(instance, JsonObject.class, "Validating SEDS (meta)");
        return validate(ValueUtil.SCHEMA_SEDS_META, ValueUtil.SCHEMA_SEDS_META_PATH, instance).isSuccess();
    }

    @Override
    public ProcessingReport validateSEDS(JsonObject instance) throws SedsException {
        assertNotNull(instance, JsonObject.class, "Validating SEDS (meta and raw)");
        return validate(ValueUtil.SCHEMA_SEDS_TYPE, ValueUtil.SCHEMA_SEDS_TYPE_PATH, instance);
    }

    @Override
    public ProcessingReport validateSEDSRaw(JsonObject instance) throws SedsException {
        assertNotNull(instance, JsonObject.class, "Validating SEDS (raw)");
        return validate(ValueUtil.SCHEMA_SEDS_RAW, ValueUtil.SCHEMA_SEDS_RAW_PATH, instance);
    }

    @Override
    public ProcessingReport validateSEDSMeta(JsonObject instance) throws SedsException {
        assertNotNull(instance, JsonObject.class, "Validating SEDS (meta)");
        return validate(ValueUtil.SCHEMA_SEDS_META, ValueUtil.SCHEMA_SEDS_META_PATH, instance);
    }

    @Override
    public void validateMetadataValues(SedsMetadata instance) throws SedsException {
        assertNotNull(instance, SedsMetadata.class, "Validating metadata");

        if (!ValueUtil.PROTOCOL.equals(instance.getProtocol())) {
            throw SedsException.buildIAE(
                    instance.getProtocol(),
                    ValueUtil.PROTOCOL,
                    "Invalid Metadata Protocol"
            );
        }
        if (!ValueUtil.VERSION.equals(instance.getVersion())) {
            throw SedsException.buildIAE(
                    instance.getVersion(),
                    ValueUtil.VERSION,
                    "Invalid Metadata Version"
            );
        }
        if (!ValueUtil.TYPENAMES.containsKey(instance.getType()) && !ValueUtil.TYPENAMES_GENERICS.containsKey(instance.getType())) {
            throw SedsException.buildIAE(
                    instance.getType(),
                    ValueUtil.TYPENAMES_LIST,
                    "Invalid Metadata Typename"
            );
        }
    }
}
