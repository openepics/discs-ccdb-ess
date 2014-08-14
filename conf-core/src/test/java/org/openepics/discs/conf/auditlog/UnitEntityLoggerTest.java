package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Unit;

public class UnitEntityLoggerTest {

    private final Unit unit = new Unit("Ampre", "Current", "A", "BlahBlha", "Miki");
    private final UnitEntityLogger entLogger = new UnitEntityLogger();

    @Before
    public void setUp() {
        unit.setModifiedAt(new Date());
    }

    @Test
    public void testGetType() {
        assertTrue(Unit.class.equals(entLogger.getType()));
    }

    @Test
    public void testSerializeEntity() {
        System.out.println(entLogger.auditEntries(unit, EntityTypeOperation.CREATE, "admin").get(0).getEntry());
    }

}
