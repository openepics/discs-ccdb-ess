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
package org.openepics.discs.conf.dl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.dl.common.ErrorMessage;
import org.openepics.discs.conf.dl.common.ValidationMessage;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.util.TestUtility;

/**
 * Integration tests for {@link SlotsDataLoader} and {@link SlotPairDataLoader}
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@RunWith(Arquillian.class)
@UsingDataSet(value= {"unit.xml", "property.xml", "basic_component_types.xml", "component_type.xml", "slot_relation.xml", "basic_slot.xml"})
@ApplyScriptAfter(value= "truncate_database.sql")
public class SlotsAndSlotPairsDataLoaderIT {

    @Inject private SlotsAndSlotPairsDataLoaderHelper dataLoaderHelper;
    @Inject private SlotEJB slotEJB;
    @Inject private SlotPairEJB slotPairEJB;
    @Inject private TestUtility testUtility;

    final static private String HDR_RELATION = "RELATION";

    @Deployment
    public static WebArchive createDeployment() {
        return TestUtility.createWebArchive();
    }

    @Before
    public void setUpBeforeTest() {
        testUtility.loginForTests();
    }

    @Test
    @Transactional(TransactionMode.DISABLED)
    public void slotPairsImportMiscellaneousErrorFailureTest() {
        final String slotsImportFileName = "slots-test.xlsx";
        final String slotPairsImportFileName = "slot-pairs-misc-failure-test.xlsx";
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        expectedValidationMessages.add(new ValidationMessage(slotPairsImportFileName));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.UNKNOWN_SLOT_RELATION_TYPE, 3, HDR_RELATION));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.INSTALL_CANT_CONTAIN_CONTAINER, 5, null));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.POWER_RELATIONSHIP_RESTRICTIONS, 6, null));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.POWER_RELATIONSHIP_RESTRICTIONS, 7, null));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.POWER_RELATIONSHIP_RESTRICTIONS, 8, null));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.CONTROL_RELATIONSHIP_RESTRICTIONS, 10, null));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.CONTROL_RELATIONSHIP_RESTRICTIONS, 12, null));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.CONTROL_RELATIONSHIP_RESTRICTIONS, 13, null));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.SAME_CHILD_AND_PARENT, 14, null));

        final DataLoaderResult loaderResult = dataLoaderHelper.importSlotsAndSlotPairs(slotsImportFileName, slotPairsImportFileName);

        Assert.assertEquals(expectedValidationMessages, loaderResult.getMessages());
        Assert.assertEquals(SlotsAndSlotPairsDataLoaderHelper.NUM_OF_SLOTS_IF_FAILURE, slotEJB.findAll().size());
        Assert.assertEquals(SlotsAndSlotPairsDataLoaderHelper.NUM_OF_SLOT_PAIRS_IF_FAILURE, slotPairEJB.findAll().size());
    }

    @Test
    @Transactional(TransactionMode.DISABLED)
    public void slotsAndSlotPairsImportTest() {
        final String slotsImportFileName = "slots-test.xlsx";
        final String slotPairsImportFileName = "slot-pairs-test.xlsx";
        final DataLoaderResult loaderResult = dataLoaderHelper.importSlotsAndSlotPairs(slotsImportFileName, slotPairsImportFileName);

        Assert.assertFalse(loaderResult.isError());
        Assert.assertEquals(SlotsAndSlotPairsDataLoaderHelper.NUM_OF_SLOTS_IF_SUCCESS, slotEJB.findAll().size());
        Assert.assertEquals(SlotsAndSlotPairsDataLoaderHelper.NUM_OF_SLOT_PAIRS_IF_SUCCESS, slotPairEJB.findAll().size());
    }


}
