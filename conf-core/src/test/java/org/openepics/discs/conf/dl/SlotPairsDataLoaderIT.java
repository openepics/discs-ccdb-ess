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
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.util.TestUtility;

/**
 * Integration tests for {@link SlotPairDataLoader}
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@RunWith(Arquillian.class)
@UsingDataSet(value= {"unit.xml", "property.xml", "basic_component_types.xml", "component_type.xml", "basic_slot.xml"})
@ApplyScriptAfter(value= "truncate_database.sql")
public class SlotPairsDataLoaderIT {

    @Inject private SlotsAndSlotPairsDataLoaderHelper dataLoaderHelper;
    @Inject private SlotPairEJB slotPairEJB;
    @Inject private TestUtility testUtility;

    final static private String HDR_RELATION = "RELATION";
    final static private String HDR_PARENT = "PARENT";
    final static private String HDR_CHILD = "CHILD";

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
    public void slotsImportRequiredFieldsFailureTest() {
        final String slotPairsImportFileName = "slot-pairs-required-fields-failure-test.xlsx";
        final List<ValidationMessage> expectedValidationMessages = new ArrayList<>();
        expectedValidationMessages.add(new ValidationMessage(slotPairsImportFileName));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 3, HDR_RELATION));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 4, HDR_PARENT));
        expectedValidationMessages.add(new ValidationMessage(ErrorMessage.REQUIRED_FIELD_MISSING, 5, HDR_CHILD));

        final DataLoaderResult loaderResult = dataLoaderHelper.importSlotsAndSlotPairs(null, slotPairsImportFileName);

        Assert.assertEquals(expectedValidationMessages, loaderResult.getMessages());
        Assert.assertEquals(SlotsAndSlotPairsDataLoaderHelper.NUM_OF_SLOT_PAIRS_IF_FAILURE, slotPairEJB.findAll().size());
    }

}
