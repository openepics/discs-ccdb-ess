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
package org.openepics.discs.conf.ejb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.hamcrest.core.IsInstanceOf;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.util.TestUtility;

/**
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@RunWith(Arquillian.class)
public class AuditRecordEJBIT {
    @Inject private AuditRecordEJB auditRecordService;
    @Inject private TestUtility testUtility;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Deployment()
    public static WebArchive createDeployment() {
        return TestUtility.createWebArchive();
    }

    @Before
    public void setUp() throws Exception {
        testUtility.loginForTests();
    }

    @Test
    @UsingDataSet(value= {"audit_record.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindByEntityIdAndType() {
        final AuditRecord anAuditRecord = auditRecordService.findAll().get(0);

        final List<AuditRecord> records = auditRecordService.findByEntityIdAndType(anAuditRecord.getEntityId(), anAuditRecord.getEntityType());
        assertNotNull(records);
        assertNotEquals(records.size(), 0);
    }

    @Test
    @UsingDataSet(value= {"audit_record.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindById() {
        final AuditRecord anAuditRecord = auditRecordService.findAll().get(0);

        final AuditRecord newAuditRecord = auditRecordService.findById(anAuditRecord.getId());
        assertNotNull(newAuditRecord);
        assertEquals(newAuditRecord, anAuditRecord);
    }

    @Test
    @UsingDataSet(value= {"audit_record.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindByName() {
        // Expected cause that will be unwrapped
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(
                                          UnsupportedOperationException.class) );

        auditRecordService.findByName("BlahBlah");
    }

    @Test
    @UsingDataSet(value= {"audit_record.xml"})
    @ApplyScriptBefore(value= {"update_sequences.sql"})
    public void testFindAll() {
        final List<AuditRecord> allAuditRecords = auditRecordService.findAll();
        assertNotNull(allAuditRecords);
        assertNotEquals(allAuditRecords.size(), 0);
    }
}
