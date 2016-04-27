/*
 * Copyright (c) 2016 European Spallation Source
 * Copyright (c) 2016 Cosylab d.d.
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
package org.openepics.discs.conf.ui.util;

import java.util.HashMap;
import java.util.Map;

import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.export.ExportTable;
import org.openepics.discs.conf.ui.export.ExportSimpleTableDialog;
import org.openepics.discs.conf.ui.trees.BasicTreeNode;
import org.openepics.discs.conf.ui.trees.FilteredTreeNode;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.views.SlotView;

public class ExportSimpleSlotsTableDialog extends ExportSimpleTableDialog {
    private ExportTable exportTable;
    private final SlotRelationshipManager containsTreeManager;
    private final Slot hierarchyRootSlot;
    private final InstallationEJB installationEJB;

    public ExportSimpleSlotsTableDialog(final SlotRelationshipManager containsTreeManager, final Slot hierarchyRootSlot,
            final InstallationEJB installationEJB) {
        this.containsTreeManager = containsTreeManager;
        this.hierarchyRootSlot = hierarchyRootSlot;
        this.installationEJB = installationEJB;
    }

    @Override
    protected String getTableName() {
        return "Slots";
    }

    @Override
    protected String getFileName() {
        return "ccdb_slots";
    }

    @Override
    protected void addHeaderRow(ExportTable exportTable) {
        exportTable.addHeaderRow("Operation", "Entity Type", "Device Type", "Entity Name", "Entity Description",
                "Entity Parent", "Property Name", "Property Value", "Relationship Type", "Relationship Entity Name",
                "Installation");
    }

    @Override
    protected void addData(ExportTable exportTable) {
        this.exportTable = exportTable;
        final Map<Slot, FilteredTreeNode<SlotView>> exportedSlots = new HashMap<>();
        dfsAddNode(containsTreeManager.getContainsRelationshipTree().getRootNode(), exportedSlots);

        for (final FilteredTreeNode<SlotView> relCandidate : exportedSlots.values()) {
            exportAllRelationships(relCandidate, exportedSlots);
        }
    }

    private void dfsAddNode(final FilteredTreeNode<SlotView> nodeToProcess,
            final Map<Slot, FilteredTreeNode<SlotView>> exportedSlots) {
        final Slot slotToExport = nodeToProcess.getData().getSlot();
        if (exportedSlots.containsKey(slotToExport)) return;
        if (!slotToExport.equals(hierarchyRootSlot)) {
            // don't export implicit root node
            exportedSlots.put(slotToExport, nodeToProcess);
            exportAllSlotData(nodeToProcess);
        }
        // Recursively export all children
        for (final FilteredTreeNode<SlotView> child : nodeToProcess.getFilteredChildren()) {
            dfsAddNode(child, exportedSlots);
        }

    }

    private void exportAllSlotData(final FilteredTreeNode<SlotView> nodeToExport) {
        final Slot slotToExport = nodeToExport.getData().getSlot();
        exportTable.addDataRow(DataLoader.CMD_UPDATE_ENTITY,
                slotToExport.isHostingSlot() ? DataLoader.ENTITY_TYPE_SLOT : DataLoader.ENTITY_TYPE_CONTAINER,
                slotToExport.isHostingSlot() ? slotToExport.getComponentType().getName() : null,
                slotToExport.getName(), slotToExport.getDescription(), assembleParentPath(nodeToExport));
        exportAllProperties(nodeToExport);
        exportInstallation(nodeToExport);
    }

    private String assembleParentPath(final FilteredTreeNode<SlotView> nodeToExport) {
        final StringBuilder parentPath = new StringBuilder();
        BasicTreeNode<SlotView> parentNode = nodeToExport.getParent();
        while ((parentNode != null) && !hierarchyRootSlot.equals(parentNode.getData().getSlot())) {
            if (parentPath.length() > 0) parentPath.insert(0, DataLoader.PATH_SEPARATOR_PATTERN);
            parentPath.insert(0, parentNode.getData().getName());
            parentNode = parentNode.getParent();
        }

        return parentPath.length() != 0 ? parentPath.toString() : null;
    }

    private void exportAllProperties(final FilteredTreeNode<SlotView> nodeToExport) {
        final Slot slotToExport = nodeToExport.getData().getSlot();
        for (final SlotPropertyValue pv : slotToExport.getSlotPropertyList()) {
            exportTable.addDataRow(DataLoader.CMD_UPDATE_PROPERTY, null, null, slotToExport.getName(), null,
                    assembleParentPath(nodeToExport),
                    pv.getProperty().getName(), pv.getPropValue());
        }
    }

    private void exportAllRelationships(final FilteredTreeNode<SlotView> nodeToExport,
            final Map<Slot, FilteredTreeNode<SlotView>> exportedSlots) {
        final Slot slotToExport = nodeToExport.getData().getSlot();
        final Slot parentSlot = nodeToExport.getParent().getData().getSlot();
        // export relationships to children
        for (final SlotPair rel : slotToExport.getPairsInWhichThisSlotIsAParentList()) {
            final Slot relChild = rel.getChildSlot();
            final FilteredTreeNode<SlotView> relationshipTargetNode = exportedSlots.get(relChild);
            if (relationshipTargetNode != null) {
                exportTable.addDataRow(DataLoader.CMD_UPDATE_RELATIONSHIP, null, null, slotToExport.getName(), null,
                        assembleParentPath(nodeToExport),
                        null, null,
                        getRelationString(rel.getSlotRelation().getName(), false),
                        assembleParentPath(relationshipTargetNode) + ">>" + relChild.getName());
            }
        }
        // export relationships to parent omitting the relationship to direct hierarchy parent
        for (final SlotPair rel : slotToExport.getPairsInWhichThisSlotIsAChildList()) {
            final Slot relParent = rel.getParentSlot();
            final FilteredTreeNode<SlotView> relationshipTargetNode = exportedSlots.get(relParent);
            if ((relationshipTargetNode != null) && (rel.getSlotRelation().getName() != SlotRelationName.CONTAINS)
                    && !parentSlot.equals(relParent)) {
                exportTable.addDataRow(DataLoader.CMD_UPDATE_RELATIONSHIP, null, null, slotToExport.getName(), null,
                        assembleParentPath(nodeToExport),
                        null, null,
                        getRelationString(rel.getSlotRelation().getName(), true),
                        assembleParentPath(relationshipTargetNode) + ">>" + relParent.getName());
            }
        }
    }

    private String getRelationString(final SlotRelationName relation, final boolean inverse) {
        switch (relation) {
            case CONTAINS:
                return inverse ? SlotRelationName.CONTAINS.inverseName().toUpperCase()
                        : SlotRelationName.CONTAINS.toString().toUpperCase();
            case CONTROLS:
                return inverse ? SlotRelationName.CONTROLS.inverseName().toUpperCase()
                        : SlotRelationName.CONTROLS.toString().toUpperCase();
            case POWERS:
                return inverse ? SlotRelationName.POWERS.inverseName().toUpperCase()
                        : SlotRelationName.POWERS.toString().toUpperCase();
            default:
                throw new UnhandledCaseException();
        }
    }

    private void exportInstallation(final FilteredTreeNode<SlotView> nodeToExport) {
        final Slot slotToExport = nodeToExport.getData().getSlot();
        final InstallationRecord record = installationEJB.getActiveInstallationRecordForSlot(slotToExport);
        if (record != null) {
            exportTable.addDataRow(DataLoader.CMD_INSTALL, null, null, slotToExport.getName(), null,
                    assembleParentPath(nodeToExport),   // entity definition
                    null, null,                         // property definition
                    null, null,                         // relationship definition
                    record.getDevice().getSerialNumber());
        }
    }

    @Override
    protected String getExcelTemplatePath() {
        return "/resources/templates/ccdb_slots.xlsx";
    }

    @Override
    protected int getExcelDataStartRow() {
        return 10;
    }
}
