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
package org.openepics.seds.api;

import java.io.File;
import javax.json.JsonObject;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.openepics.seds.api.datatypes.SedsMetadata;
import org.openepics.seds.core.Seds;
import org.openepics.seds.util.FileUtil;
import org.openepics.seds.util.SedsException;
import org.openepics.seds.util.ValueUtil;

/**
 *
 * @author Aaron Barber
 */
public class SedsValidatorTest {

    /**
     * Test of isValidSEDS method, of class SedsValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testIsValidSEDS() throws Exception {
        System.out.println("isValidSEDS");

        File[] valids = FileUtil.get(
                FileUtil.API,
                "validator_seds_type_valid_",
                new String[]{"1"}
        );
        File[] invalids = FileUtil.get(
                FileUtil.API,
                "validator_seds_type_invalid_",
                new String[]{"1"}
        );

        for (File valid : valids) {
            JsonObject data = Seds.newReader().read(valid);
            assertEquals(true, Seds.newValidator().isValidSEDS(data));
        }

        for (File invalid : invalids) {
            JsonObject data = Seds.newReader().read(invalid);
            assertEquals(false, Seds.newValidator().isValidSEDS(data));
        }
    }

    /**
     * Test of isValidSEDSRaw method, of class SedsValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testIsValidSEDSRaw() throws Exception {
        System.out.println("isValidSEDSRaw");

        File[] valids = FileUtil.get(
                FileUtil.API,
                "validator_seds_raw_valid_",
                new String[]{"1"}
        );
        File[] invalids = FileUtil.get(
                FileUtil.API,
                "validator_seds_raw_invalid_",
                new String[]{"1"}
        );

        for (File valid : valids) {
            JsonObject data = Seds.newReader().read(valid);
            assertEquals(true, Seds.newValidator().isValidSEDSRaw(data));
        }

        for (File invalid : invalids) {
            JsonObject data = Seds.newReader().read(invalid);
            assertEquals(false, Seds.newValidator().isValidSEDSRaw(data));
        }
    }

    /**
     * Test of isValidSEDSMeta method, of class SedsValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testIsValidSEDSMeta() throws Exception {
        System.out.println("isValidSEDSMeta");

        File[] valids = FileUtil.get(
                FileUtil.API,
                "validator_seds_meta_valid_",
                new String[]{"1"}
        );
        File[] invalids = FileUtil.get(
                FileUtil.API,
                "validator_seds_meta_invalid_",
                new String[]{"1"}
        );

        for (File valid : valids) {
            JsonObject data = Seds.newReader().read(valid);
            assertEquals(true, Seds.newValidator().isValidSEDSMeta(data));
        }

        for (File invalid : invalids) {
            JsonObject data = Seds.newReader().read(invalid);
            assertEquals(false, Seds.newValidator().isValidSEDSMeta(data));
        }
    }

    /**
     * Test of validateSEDS method, of class SedsValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidateSEDS() throws Exception {
        System.out.println("validateSEDS");

        File[] valids = FileUtil.get(
                FileUtil.API,
                "validator_seds_type_valid_",
                new String[]{"1"}
        );
        File[] invalids = FileUtil.get(
                FileUtil.API,
                "validator_seds_type_invalid_",
                new String[]{"1"}
        );

        for (File valid : valids) {
            JsonObject data = Seds.newReader().read(valid);
            assertEquals(true, Seds.newValidator().validateSEDS(data).isSuccess());
        }

        for (File invalid : invalids) {
            JsonObject data = Seds.newReader().read(invalid);
            assertEquals(false, Seds.newValidator().validateSEDS(data).isSuccess());
        }
    }

    /**
     * Test of validateSEDSRaw method, of class SedsValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidateSEDSRaw() throws Exception {
        System.out.println("validateSEDSRaw");

        File[] valids = FileUtil.get(
                FileUtil.API,
                "validator_seds_raw_valid_",
                new String[]{"1"}
        );
        File[] invalids = FileUtil.get(
                FileUtil.API,
                "validator_seds_raw_invalid_",
                new String[]{"1"}
        );

        for (File valid : valids) {
            JsonObject data = Seds.newReader().read(valid);
            assertEquals(true, Seds.newValidator().validateSEDSRaw(data).isSuccess());
        }

        for (File invalid : invalids) {
            JsonObject data = Seds.newReader().read(invalid);
            assertEquals(false, Seds.newValidator().validateSEDSRaw(data).isSuccess());
        }
    }

    /**
     * Test of validateSEDSMeta method, of class SedsValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidateSEDSMeta() throws Exception {
        System.out.println("validateSEDSMeta");

        File[] valids = FileUtil.get(
                FileUtil.API,
                "validator_seds_meta_valid_",
                new String[]{"1"}
        );
        File[] invalids = FileUtil.get(
                FileUtil.API,
                "validator_seds_meta_invalid_",
                new String[]{"1"}
        );

        for (File valid : valids) {
            JsonObject data = Seds.newReader().read(valid);
            assertEquals(true, Seds.newValidator().validateSEDSMeta(data).isSuccess());
        }

        for (File invalid : invalids) {
            JsonObject data = Seds.newReader().read(invalid);
            assertEquals(false, Seds.newValidator().validateSEDSMeta(data).isSuccess());
        }
    }

    /**
     * Test of validateMetadataValues method, of class SedsValidator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidateMetadataValues() throws Exception {
        System.out.println("validateMetadataValues");
        SedsFactory f = Seds.newFactory();

        SedsMetadata[] valid = new SedsMetadata[]{
            f.newMetadata("SedsAlarm", ValueUtil.PROTOCOL, ValueUtil.VERSION),
            f.newMetadata("SedsScalar", ValueUtil.PROTOCOL, ValueUtil.VERSION),
            f.newMetadata("SedsScalar_Boolean", ValueUtil.PROTOCOL, ValueUtil.VERSION),
            f.newMetadata("SedsScalarArray", ValueUtil.PROTOCOL, ValueUtil.VERSION),
            f.newMetadata("SedsScalarArray_Boolean", ValueUtil.PROTOCOL, ValueUtil.VERSION)
        };

        SedsMetadata[] invalid = new SedsMetadata[]{
            f.newMetadata(null, ValueUtil.PROTOCOL, ValueUtil.VERSION),
            f.newMetadata("Alarm", ValueUtil.PROTOCOL, ValueUtil.VERSION),
            f.newMetadata("SEDSAlarm", ValueUtil.PROTOCOL, ValueUtil.VERSION),
            f.newMetadata("SedsScalar_Double", ValueUtil.PROTOCOL, ValueUtil.VERSION),
            f.newMetadata("SedsScalar_Null", ValueUtil.PROTOCOL, ValueUtil.VERSION),
            f.newMetadata("SedsScalarArray_Double", ValueUtil.PROTOCOL, ValueUtil.VERSION),
            f.newMetadata("SedsScalarArray_Null", ValueUtil.PROTOCOL, ValueUtil.VERSION),
            f.newMetadata("SedsAlarm", ValueUtil.PROTOCOL, "0.0.0"),
            f.newMetadata("SedsAlarm", "SEDSv0", ValueUtil.VERSION)
        };

        for (SedsMetadata meta : valid) {
            boolean failed = false;
            try {
                Seds.newValidator().validateMetadataValues(meta);
            } catch (SedsException | IllegalArgumentException e) {
                failed = true;
            }

            assertEquals(false, failed);
        }

        for (SedsMetadata meta : invalid) {
            boolean failed = false;
            try {
                Seds.newValidator().validateMetadataValues(meta);
            } catch (SedsException | IllegalArgumentException e) {
                failed = true;
            }

            assertEquals(true, failed);
        }
    }

}
