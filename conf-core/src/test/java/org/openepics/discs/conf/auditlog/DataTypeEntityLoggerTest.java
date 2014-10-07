package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

public class DataTypeEntityLoggerTest {

    private final DataTypeEntityLogger entLogger = new DataTypeEntityLogger();

    @Test
    public void testGetType() {
        assertTrue(DataType.class.equals(entLogger.getType()));
    }

    @Test
    public void testSerializeEntity() {
        final DataType dt = new DataType("Float", "Float", true, "Well.. a scalar float");
        final String RESULT = "{\"description\":\"Float\",\"scalar\":true,\"definition\":\"Well.. a scalar float\"}";

        assertEquals(RESULT, entLogger.auditEntries(dt, EntityTypeOperation.CREATE).get(0).getEntry());
    }

}
