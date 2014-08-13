package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

public class DataTypeEntityLoggerTest {

    private DataTypeEntityLogger entLogger = new DataTypeEntityLogger();

    @Test
    public void testGetType() {
        assertTrue(DataType.class.equals(entLogger.getType()));
    }

    @Test
    public void testSerializeEntity() {
        DataType dt = new DataType("Float", "Float", true, "Well.. a scalar float", "Iznogud");

        System.out.println(entLogger.auditEntry(dt, EntityTypeOperation.CREATE, "admin").getEntry());
    }

}
