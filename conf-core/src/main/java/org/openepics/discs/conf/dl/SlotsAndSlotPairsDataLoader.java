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

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;


/**
 * Implementation of data loader for slots and slot pairs.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Stateless
public class SlotsAndSlotPairsDataLoader implements Serializable {
    private static final long serialVersionUID = 6772985623823412436L;

    @Resource private EJBContext context;
    @Inject @SlotPairDataLoaderQualifier transient private DataLoader slotPairDataLoader;
    @Inject @SlotsDataLoaderQualifier transient private DataLoader slotsDataLoader;

    /**
     * Saves data read from two input files to the database
     *
     * @param slotsFileInputRows {@link List} of all rows containing data from Slots input file
     * @param slotPairsFileInputRows {@link List} of all rows containing data from Slot relationships input file
     * @param slotsFileName the name of the file containing data from Slots input file
     * @param slotPairsFileName the name of the file containing data from Slot relationships input file
     *
     * @return {@link DataLoaderResult} describing the outcome of the data loading
     */
    @SuppressWarnings("unchecked")
    public DataLoaderResult loadDataToDatabase(final List<Pair<Integer, List<String>>> slotsFileInputRows,
            final List<Pair<Integer, List<String>>> slotPairsFileInputRows, final String slotsFileName,
            final String slotPairsFileName) {
        final DataLoaderResult slotsLoaderResult;

        if (slotsFileInputRows != null) {
            slotsLoaderResult = slotsDataLoader.loadDataToDatabase(slotsFileInputRows, null);
        } else {
            slotsLoaderResult = new DataLoaderResult();
        }

        final DataLoaderResult slotPairsLoaderResult;
        final Set<Slot> newSlotPairChildren;

        if (!slotsLoaderResult.isError() && slotPairsFileInputRows != null) {
            slotPairsLoaderResult = slotPairDataLoader.loadDataToDatabase(slotPairsFileInputRows, slotsLoaderResult.getContextualData());
            newSlotPairChildren = (Set<Slot>) slotPairsLoaderResult.getContextualData().get(DataLoaderResult.CTX_NEW_SLOT_PAIR_CHILDREN);
        } else {
            slotPairsLoaderResult = new DataLoaderResult();
            newSlotPairChildren = new HashSet<>();
        }

        final DataLoaderResult loaderResult = mergeDataLoaderResults(slotsLoaderResult, slotPairsLoaderResult, slotsFileName, slotPairsFileName);

        if (!loaderResult.isError()) {
            checkForRelationConsistency((List<Slot>) slotsLoaderResult.getContextualData().get(DataLoaderResult.CTX_NEW_SLOTS), newSlotPairChildren, loaderResult);
        }

        if (loaderResult.isError()) {
            context.setRollbackOnly();
        }
        return loaderResult;
    }

    private void checkForRelationConsistency(final List<Slot> newSlots, final Set<Slot> newSlotPairChildren,
            DataLoaderResult loaderResult) {
        for (Slot newSlot : newSlots) {
            if (!newSlotPairChildren.contains(newSlot) && !newSlot.getComponentType().getName().equals(SlotEJB.ROOT_COMPONENT_TYPE)) {
                loaderResult.addOrphanSlotMessage(newSlot.getName());
            }
        }
    }

    private DataLoaderResult mergeDataLoaderResults(final DataLoaderResult slotsLoaderResult, final DataLoaderResult slotPairsLoaderResult, final String slotsFileName, final String slotPairsFileName) {
        final DataLoaderResult mergedLoaderResult = new DataLoaderResult();
        if (slotsLoaderResult.isError()) {
            mergedLoaderResult.setFileName(slotsFileName);
            mergedLoaderResult.copyDataLoaderResult(slotsLoaderResult);
        }

        if (slotPairsLoaderResult.isError()) {
            mergedLoaderResult.setFileName(slotPairsFileName);
            mergedLoaderResult.copyDataLoaderResult(slotPairsLoaderResult);
        }
        return mergedLoaderResult;
    }

}
