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
package org.openepics.discs.conf.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.PostActivate;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import joptsimple.internal.Strings;

import org.openepics.discs.conf.dl.annotations.SignalsLoader;
import org.openepics.discs.conf.dl.annotations.SlotsLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.ui.export.ExportSimpleTableDialog;
import org.openepics.discs.conf.ui.export.SimpleTableExporter;
import org.openepics.discs.conf.ui.trees.BasicTreeNode;
import org.openepics.discs.conf.ui.trees.ConnectsTree;
import org.openepics.discs.conf.ui.trees.FilteredTreeNode;
import org.openepics.discs.conf.ui.trees.RootNodeWithChildren;
import org.openepics.discs.conf.ui.trees.SlotRelationshipTree;
import org.openepics.discs.conf.ui.trees.Tree;
import org.openepics.discs.conf.ui.util.ConnectsManager;
import org.openepics.discs.conf.ui.util.ExportSimpleSlotsTableDialog;
import org.openepics.discs.conf.ui.util.SlotRelationshipManager;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.ui.util.names.Names;
import org.openepics.discs.conf.util.AppProperties;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.SlotView;
import org.openepics.names.jaxb.DeviceNameElement;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TabChangeEvent;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Named
@ViewScoped
public class HierarchiesController extends AbstractExcelSingleFileImportUI implements SimpleTableExporter,
        SlotRelationshipManager, Serializable {
    private static final long       serialVersionUID = 2743408661782529373L;

    private static final Logger     LOGGER = Logger.getLogger(HierarchiesController.class.getCanonicalName());

    private static final String     CANNOT_PASTE_INTO_ROOT =
                                                "The following installation slots cannot be made hierarchy roots:";
    private static final String     CANNOT_PASTE_INTO_SLOT =
            "The following containers cannot become children of installation slot:";
    private static final String     CANNOT_PASTE_INTO_SELF =
            "The following containers cannot become children of themselves:";

    /** The device page part of the URL containing all the required parameters already. */
    private static final String     NAMING_DEVICE_PAGE = "devices.xhtml?i=2&deviceName=";

    private static final String     CABLEDB_DEVICE_PAGE = "cables.xhtml?cableNumber=";

    @Inject private SlotEJB slotEJB;
    @Inject private SlotPairEJB slotPairEJB;
    @Inject private InstallationEJB installationEJB;
    @Inject private ComptypeEJB comptypeEJB;
    @Inject private Names names;
    @Inject private ConnectsManager connectsManager;
    @Inject private InstallationController installationController;
    @Inject private RelationshipController relationshipController;
    @Inject private SlotAttributeController slotAttributeController;

    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject @SignalsLoader private DataLoader signalsDataLoader;
    @Inject @SlotsLoader private DataLoader slotsDataLoader;

    @Inject private AppProperties properties;

    private enum ActiveTab {
        INCLUDES, POWERS, CONTROLS, CONNECTS,
    }

    private enum ClipboardOperations {
        COPY, CUT
    }

    private enum NamingStatus {
        ACTIVE, OBSOLETE, DELETED, MISSING
    }

    private HashSet<Long> selectedNodeIds;
    private HashSet<Long> displayedAttributeNodeIds;
    private String requestedSlot;

    // ---- variables for hierarchies and tabs --------
    private Tree<SlotView> selectedTree;

    private transient SlotRelationshipTree containsTree;
    private transient SlotRelationshipTree powersTree;
    private transient SlotRelationshipTree controlsTree;
    private transient ConnectsTree connectsTree;

    /** <code>selectedSlot</code> is only initialized when there is only one node in the tree selected */
    private Slot selectedSlot;
    /** <code>selectedSlotView</code> is only initialized when there is only one node in the tree selected */
    private SlotView selectedSlotView;
    private ActiveTab activeTab;
    private transient List<FilteredTreeNode<SlotView>> clipboardSlots;
    private List<SlotView> pasteErrors;
    private ClipboardOperations clipboardOperation;
    private String pasteErrorReason;
    private transient List<FilteredTreeNode<SlotView>> nodesToDelete;
    private List<SlotView> slotsToDelete;
    private List<SlotView> filteredSlotsToDelete;
    private boolean detectNamingStatus;
    private boolean restrictToConventionNames;

    /* Thread local storage is used for preventing circular injection loop in case you get to this screen
     * requesting an installation slot gets selected (id or name parameter in the URL).
     */
    private static ThreadLocal<AtomicBoolean> ignoreUrlParams = new ThreadLocal<AtomicBoolean>() {
        private AtomicBoolean accessed;

        @Override
        protected AtomicBoolean initialValue() {
            accessed = new AtomicBoolean(false);
            return accessed;
        }
    };

    // variables from the installation slot / containers editing merger.
    private String name;
    private String description;
    /** Used in "add child to parent" operations. This usually reflects the <code>selectedNode</code>. */
    private boolean isInstallationSlot;
    private boolean hasDevice;
    private Long deviceType;
    private String parentName;
    private transient List<String> namesForAutoComplete;
    private boolean isNewSlot;
    private transient Map<String, DeviceNameElement> nameList;

    private String namingRedirectionUrl;
    private String cableRedirectionUrl;

    private SlotView linkSlot;

    private transient ExportSimpleSlotsTableDialog simpleTableExporterDialog;

    /** Java EE post construct life-cycle method. */
    @Override
    @PostConstruct
    public void init() {
        try {
            super.init();
            activeTab = ActiveTab.INCLUDES;

            initHierarchies();
            initNamingInformation();

            navigateToUrlSelectedSlot();

            simpleTableExporterDialog = new ExportSimpleSlotsTableDialog(this, slotEJB.getRootNode(), installationEJB);
        } catch (Exception e) {
            throw new UIException("Hierarchies display initialization fialed: " + e.getMessage(), e);
        }
    }

    @PostActivate
    public void postActivate() {
        initNamingInformation();
        initHierarchies();
        simpleTableExporterDialog = new ExportSimpleSlotsTableDialog(this, slotEJB.getRootNode(), installationEJB);
    }

    private void initNamingInformation() {
        final String namingStatus = properties.getProperty(AppProperties.NAMING_DETECT_STATUS);
        detectNamingStatus = "TRUE".equalsIgnoreCase(namingStatus);

        final String restrictNames = properties.getProperty(AppProperties.RESTRICT_TO_CONVENTION_NAMES);
        restrictToConventionNames = !detectNamingStatus && "TRUE".equalsIgnoreCase(restrictNames);

        nameList = detectNamingStatus ? names.getAllNames() : new HashMap<>();
        namesForAutoComplete = ImmutableList.copyOf(nameList.keySet());
        namingRedirectionUrl = null;

        if (!detectNamingStatus) {
            return;
        }

        final String namingUrl = properties.getProperty(AppProperties.NAMING_APPLICATION_URL);

        if (Strings.isNullOrEmpty(namingUrl)) {
            if (detectNamingStatus) {
                LOGGER.log(Level.WARNING, AppProperties.NAMING_APPLICATION_URL + " not defined.");
            }
        } else {
            final StringBuilder redirectionUrl = new StringBuilder(namingUrl);
            if (redirectionUrl.charAt(redirectionUrl.length() - 1) != '/') {
                redirectionUrl.append('/');
            }
            redirectionUrl.append(NAMING_DEVICE_PAGE);
            namingRedirectionUrl = redirectionUrl.toString();
            LOGGER.log(Level.FINE, "Naming url: " + namingRedirectionUrl);
        }
    }

    protected void saveSlotAndRefresh(final Slot slot) {
        slotEJB.save(slot);
        refreshSlot(slot);
    }

    protected void refreshSlot(final Slot slot) {
        final Slot freshSlot = slotEJB.refreshEntity(slot);
        if (selectedSlot != null) {
            selectedSlot = freshSlot;
        }
    }

    protected void refreshTrees(HashSet<Long> ids) {
    	containsTree.refreshIds(ids);
    	if (selectedTree == controlsTree) {
    		controlsTree.refreshIds(ids);
    	} else {
    		((RootNodeWithChildren)controlsTree.getRootNode()).reset();
    	}

    	if (selectedTree == powersTree) {
    		powersTree.refreshIds(ids);
    	} else {
    		((RootNodeWithChildren)powersTree.getRootNode()).reset();
    	}

    	if (selectedTree == connectsTree) {
    		connectsTree.refreshIds(ids);
    	} else {
    		((RootNodeWithChildren)connectsTree.getRootNode()).reset();
    	}
    }

    private void navigateToUrlSelectedSlot() {
        // navigate to slot based on ID or name
        if (ignoreUrlParams.get().get()) {
            return;
        }
        // next time you enter this method in the same thread, skip it
        ignoreUrlParams.get().set(true);
        final String slotIdStr = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().
                                                                                    getRequest()).getParameter("id");
        Slot slot = null;
        boolean slotRequested = false;

        if (slotIdStr != null) {
            try {
                slotRequested = true;
                requestedSlot = "id:" + slotIdStr;
                slot = slotEJB.findById(Long.parseLong(slotIdStr));
            } catch (NumberFormatException e) {
                slot = null;
            }
        } else {
            final String slotName = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().
                                                                                    getRequest()).getParameter("name");
            if (slotName != null) {
                slotRequested = true;
                requestedSlot = slotName;
                slot = getSlotFromName(slotName);
            }
        }

        if (slot != null) {
            selectNode(slot);
        } else if (slotRequested) {
            RequestContext.getCurrentInstance().update("cannotFindSlotForm:cannotFindSlot");
            RequestContext.getCurrentInstance().execute("PF('cannotFindSlot').show();");
        }
        // we're done processing URL params (if any), reset the value for the next time this bean will be used.
        ignoreUrlParams.get().set(false);
    }

    private NamingStatus getNamingStatus(final String name) {
        if (!detectNamingStatus) {
            return NamingStatus.ACTIVE;
        }

        final DeviceNameElement devName = nameList.get(name);
        if (devName == null) {
            return NamingStatus.MISSING;
        }
        switch (devName.getStatus()) {
            case "ACTIVE":
                return NamingStatus.ACTIVE;
            case "OBSOLETE":
                return NamingStatus.OBSOLETE;
            case "DELETED":
                return NamingStatus.DELETED;
            default:
                return NamingStatus.MISSING;
        }
    }

    public boolean linkToNaming(final SlotView slot) {
        return (namingRedirectionUrl != null) && detectNamingStatus && (slot != null) &&
                (NamingStatus.MISSING != getNamingStatus(slot.getName()));
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above : bean initialization section and global private utility methods.
     *
     * Below: Screen population methods. These methods prepare the data to be displayed on the main UI screen.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void updateDisplayedSlotInformation() {
        selectedSlotView = null;
        selectedSlot = null;
        if (Utility.isNullOrEmpty(selectedTree.getSelectedNodes())) {
            selectedNodeIds = null;
            displayedAttributeNodeIds = null;
            clearRelatedInformation();
        } else {
            initNodeIds();
            if (displayedAttributeNodeIds != null && !displayedAttributeNodeIds.isEmpty()) {
                // there are attributes displayed, remove the ones that are no longer selected
                removeUnselectedRelatedInformation();
            }
            // initialize the list of node IDs that have the related information displayed (if not already)
            if (displayedAttributeNodeIds == null) {
                displayedAttributeNodeIds = new HashSet<Long>();
            }
            // the related tables are ready for new items
            addRelatedInformationForNewSlots();

            if (selectedTree.getSelectedNodes().size() == 1) {
                selectSingleNode(selectedTree.getSelectedNodes().get(0));
            }
        }
    }

    /* Remove attributes, relationships and installation information for slots no longer selected */
    private void removeUnselectedRelatedInformation() {
        for (final Iterator<Long> iter = displayedAttributeNodeIds.iterator(); iter.hasNext(); ) {
            final Long id = iter.next();
            if (!selectedNodeIds.contains(id)) {
                final Slot unselectedSlot = slotEJB.findById(id);
                slotAttributeController.clearRelatedAttributeInformation();
                slotAttributeController.populateAttributesList();
                relationshipController.removeRelatedRelationships(unselectedSlot);
                installationController.removeRelatedInstallationRecord(unselectedSlot);
                iter.remove();
            }
        }
    }

    /* Add attributes, relationships and installation information for slots that are missing */
    private void addRelatedInformationForNewSlots() {
        for (final Long selectedId : selectedNodeIds) {
            if (!displayedAttributeNodeIds.contains(selectedId)) {
                // this slot doesn't have information in the related tables yet
                final Slot slotToAdd = slotEJB.findById(selectedId);
                slotAttributeController.clearRelatedAttributeInformation();
                slotAttributeController.populateAttributesList();
                relationshipController.initRelationshipList(slotToAdd, false);
                installationController.initInstallationRecordList(slotToAdd, false);
                displayedAttributeNodeIds.add(selectedId);
            }
        }
    }

    private void initNodeIds() {
        selectedNodeIds = new HashSet<Long>();
        for (final FilteredTreeNode<SlotView> node : selectedTree.getSelectedNodes()) {
            selectedNodeIds.add(node.getData().getId());
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Screen population methods. These methods prepare the data to be displayed on the main UI screen.
     *
     * Below: Callback methods called from the main UI screen. E.g.: methods that are called when user user selects
     *        a line in a table.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /* Clears all slot related information when user deselects the slots in the hierarchy. */
    private void clearRelatedInformation() {
        selectedSlotView = null;
        selectedSlot = null;
        selectedNodeIds = null;
        displayedAttributeNodeIds = null;
        slotAttributeController.clearRelatedAttributeInformation();
        installationController.clearInstallationInformation();
        relationshipController.clearRelationshipInformation();
    }

    private void selectSingleNode(final FilteredTreeNode<SlotView> selectedNode) {
        selectedSlotView = selectedNode.getData();
        selectedSlot = selectedSlotView.getSlot();
    }

    /** The function to select a different node in the TreeTable by clicking on the link in the relationship table.
     * @param slot the slot we want to switch to
     */
    public void selectNode(final Slot slot) {
        FilteredTreeNode<SlotView> node = containsTree.findNode(slot);

        // the final slot found
        containsTree.unselectAllTreeNodes();
        clearRelatedInformation();
        fakeUISelection(node);
    }


    private void fakeUISelection(final FilteredTreeNode<SlotView> node) {
        selectedTree.getSelectedNodes().add(node);
        node.setSelected(true);
        updateDisplayedSlotInformation();
    }

    /**
     * Called when a user selects a new node in one of the hierarchy trees. This event is also triggered once if a
     * range of nodes is selected using the Shift+Click action.
     *
     * @param event Event triggered on node selection action
     */
    public void onNodeSelect(NodeSelectEvent event) {
        updateDisplayedSlotInformation();
        if (activeTab == ActiveTab.INCLUDES) {
            removeTreeData();
        }
    }

    /**
     * Called when a user deselects a new node in one of the hierarchy trees.
     *
     * @param event Event triggered on node deselection action
     */
    public void onNodeUnselect(NodeUnselectEvent event) {
        // in the callback, the selectedNodes no longer contains the unselected node
        updateDisplayedSlotInformation();
        if (activeTab == ActiveTab.INCLUDES) {
            removeTreeData();
        }
    }
    /**
     * The event is triggered when the hierarchy tab is changed.
     * @param event the event
     */
    public void onTabChange(TabChangeEvent event) {
    	final List<FilteredTreeNode<SlotView>> masterNodes = !containsTree.getSelectedNodes().isEmpty()
                ? containsTree.getSelectedNodes() : Arrays.asList(containsTree.getRootNode());

    	ActiveTab newActiveTab = ActiveTab.valueOf(event.getTab().getId());

    	switch (newActiveTab) {
    	case INCLUDES:
    		selectedTree = containsTree;
    		break;
    	case POWERS:
    		((RootNodeWithChildren)powersTree.getRootNode()).initHierarchy(masterNodes);
    		selectedTree = powersTree;
    		break;
        case CONTROLS:
        	((RootNodeWithChildren)controlsTree.getRootNode()).initHierarchy(masterNodes);
        	selectedTree = controlsTree;
        	break;
        case CONNECTS:
        	((RootNodeWithChildren)connectsTree.getRootNode()).initHierarchy(masterNodes);
        	selectedTree = connectsTree;
        	break;
    	}

    	activeTab = newActiveTab;
    	updateDisplayedSlotInformation();
    }

    private void removeTreeData() {
        // remove other trees
    	((RootNodeWithChildren)powersTree.getRootNode()).reset();
    	((RootNodeWithChildren)controlsTree.getRootNode()).reset();
        ((RootNodeWithChildren)connectsTree.getRootNode()).reset();
    }

    @Override
    public void setDataLoader() {
        dataLoader = signalsDataLoader;
    }

    @Override
    public void doImport() {
        try (InputStream inputStream = new ByteArrayInputStream(importData)) {
            setLoaderResult(dataLoaderHandler.loadData(inputStream, dataLoader));
            initHierarchies();
            initNamingInformation();
            clearRelatedInformation();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the database id of an installation {@link Slot} based on its name.
     *
     * @param slotName the name of the installation slot to search for
     * @return the installation slot, <code>null</code> if such slot was not found or if it is not an installation slot.
     */
    private Slot getSlotFromName(final String slotName) {
        if (Strings.isNullOrEmpty(slotName)) {
            return null;
        }

        final Slot slot = slotEJB.findByName(slotName);

        return (slot == null || !slot.isHostingSlot()) ? null : slot;
    }

    /**
     * Calculates the CSS class name for the slot in question.
     * @param slot the {@link Slot} to calculate the CSS class name for
     * @return the name of the CSS class
     */
    public String calcNameClass(final SlotView slot) {
        Preconditions.checkNotNull(slot);
        if (!slot.isHostingSlot()) {
            return "nameContainer";
        }

        switch (getNamingStatus(slot.getName())) {
            case ACTIVE:
                return "nameActive";
            case MISSING:
                return "nameMissing";
            case OBSOLETE:
                return "nameObsolete";
            case DELETED:
                return "nameDeleted";
            default:
                return "nameMissing";
        }
    }

    public void prepareImportSignalPopup() {
        dataLoader = signalsDataLoader;
        prepareImportPopup();
    }

    public void prepareImportSlotPopup() {
        dataLoader = slotsDataLoader;
        prepareImportPopup();
    }

    public void expandTreeNodes() {
    	if (selectedTree.getSelectedNodes().isEmpty()) {
    		expandOrCollapseNode(selectedTree.getRootNode(), true);
    	} else {
    		for (final FilteredTreeNode<SlotView> node: selectedTree.getSelectedNodes()) {
    			expandOrCollapseNode(node, true);
    		}
    	}
    }

    public void collapseTreeNodes() {
    	if (selectedTree.getSelectedNodes().isEmpty()) {
    		expandOrCollapseNode(selectedTree.getRootNode(), false);
    	} else {
    		for (final FilteredTreeNode<SlotView> node: selectedTree.getSelectedNodes()) {
    			expandOrCollapseNode(node, false);
    		}
    	}
    }

    private void expandOrCollapseNode(final FilteredTreeNode<SlotView> parent, final boolean expand) {
    	parent.setExpanded(expand);
        for (final  FilteredTreeNode<SlotView> node : parent.getFilteredChildren()) {
            expandOrCollapseNode(node, expand);
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Callback methods called from the main UI screen. E.g.: methods that are called when user user selects
     *        a line in a table.
     *
     * Below: Methods for manipulation, populating and editing the hierarchy tree of slots and containers.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /** Prepares back-end data used for container deletion */
    public void prepareDeletePopup() {
        Preconditions.checkNotNull(selectedTree.getSelectedNodes());

        nodesToDelete = Lists.newArrayList();
        for (final FilteredTreeNode<SlotView> nodeToDelete : selectedTree.getSelectedNodes()) {
            addSlotToDeleteWithChildren(nodeToDelete);
        }
        slotsToDelete = nodesToDelete.stream().map(FilteredTreeNode::getData).collect(Collectors.toList());
    }

    private void addSlotToDeleteWithChildren(final FilteredTreeNode<SlotView> nodeToDelete) {
        if (!nodesToDelete.contains(nodeToDelete)) {
            nodesToDelete.add(nodeToDelete);
        }
        // make sure that the tree children are properly initialized.

        for (final FilteredTreeNode<SlotView> child : nodeToDelete.getBufferedAllChildren()) {
            addSlotToDeleteWithChildren(child);
        }
    }

    /** Deletes selected container */
    public void onSlotsDelete() {
        Preconditions.checkNotNull(nodesToDelete);
        Preconditions.checkState(!nodesToDelete.isEmpty());

        final int numSlotsToDelete = nodesToDelete.size();
        while (!nodesToDelete.isEmpty()) {
            removeDeletedFromClipboard();
            deleteWithChildren(nodesToDelete.get(0));
        }
        UiUtility.showMessage(FacesMessage.SEVERITY_INFO, "Slots deleted", Integer.toString(numSlotsToDelete)
                                            + " slots have been successfully deleted");
        selectedTree.getSelectedNodes().clear();
        nodesToDelete = null;
        clearRelatedInformation();
    }

    private void removeDeletedFromClipboard() {
        if (!Utility.isNullOrEmpty(clipboardSlots)) {
            for (final FilteredTreeNode<SlotView> deleteCandidate : nodesToDelete) {
                clipboardSlots.remove(deleteCandidate);
            }
        }
    }

    private void deleteWithChildren(final FilteredTreeNode<SlotView> node) {
        while (!node.getBufferedAllChildren().isEmpty()) {
            deleteWithChildren(node.getBufferedAllChildren().get(0));
        }
        final FilteredTreeNode<SlotView> parentTreeNode = (FilteredTreeNode<SlotView>)node.getParent();
        final SlotView slotViewToDelete = node.getData();
        final Slot slotToDelete = slotViewToDelete.getSlot();
        // delete uninstalls device as well
        slotEJB.delete(slotToDelete);
        // update UI data as well
        parentTreeNode.refreshCache();
        nodesToDelete.remove(node);
    }

    private void initHierarchies() {
    	SlotView rootView = new SlotView(slotEJB.getRootNode(), null, 1, slotEJB);

    	containsTree = new SlotRelationshipTree(SlotRelationName.CONTAINS, slotEJB, installationEJB);
    	containsTree.setRootNode(new FilteredTreeNode<SlotView>(rootView, null, containsTree));

    	controlsTree = new SlotRelationshipTree(SlotRelationName.CONTROLS, slotEJB, installationEJB);
    	controlsTree.setRootNode(new RootNodeWithChildren(rootView, controlsTree));

    	powersTree = new SlotRelationshipTree(SlotRelationName.POWERS, slotEJB, installationEJB);
    	powersTree.setRootNode(new RootNodeWithChildren(rootView, powersTree));

    	connectsTree = new ConnectsTree(slotEJB, connectsManager);
    	connectsTree.setRootNode(new RootNodeWithChildren(rootView, connectsTree));

    	selectedTree = containsTree;
    }

    /**
     * @return cableDBStatus
     */
    public boolean getCableDBStatus() {
        return connectsManager.getCableDBStatus();
    }

    /** The action event to be called when the user presses the "move up" action button. This action moves the current
     * container/installation slot up one space, if that is possible.
     */
    public void moveSlotUp() {
        Preconditions.checkState(isSingleNodeSelected());
        final FilteredTreeNode<SlotView> currentNode = selectedTree.getSelectedNodes().get(0);
        final FilteredTreeNode<SlotView> parent = (FilteredTreeNode<SlotView>)currentNode.getParent();
        slotPairEJB.moveUp(parent.getData().getSlot(), currentNode.getData().getSlot());
        parent.refreshCache();
    }

    /** The action event to be called when the user presses the "move down" action button. This action moves the current
     * container/installation slot down one space, if that is possible.
     */
    public void moveSlotDown() {
        Preconditions.checkState(isSingleNodeSelected());
        final FilteredTreeNode<SlotView> currentNode = selectedTree.getSelectedNodes().get(0);
        final FilteredTreeNode<SlotView> parent = (FilteredTreeNode<SlotView>)currentNode.getParent();
        slotPairEJB.moveDown(parent.getData().getSlot(), currentNode.getData().getSlot());
        parent.refreshCache();
    }

    /** Prepares fields that are used in pop up for editing an existing container */
    public void prepareEditPopup() {
        Preconditions.checkState(isSingleNodeSelected());
        isNewSlot = false;
        isInstallationSlot = selectedSlotView.isHostingSlot();
        name = selectedSlotView.getName();
        description = selectedSlotView.getDescription();
        deviceType = selectedSlotView.getSlot().getComponentType().getId();
        parentName = selectedSlotView.getParentNode().getParentNode() == null ? "" : selectedSlotView.getParentNode().getName();
        hasDevice = selectedSlotView.getInstalledDevice() != null;
    }

    /** Prepares fields that are used in pop up for adding a new container */
    public void prepareContainerAddPopup() {
        isNewSlot = true;
        isInstallationSlot = false;
        initAddInputFields();
    }

    /** Prepares fields that are used in pop up for adding a new installation slot */
    public void prepareInstallationSlotPopup() {
        isInstallationSlot = true;
        isNewSlot = true;
        initAddInputFields();
    }

    private void initAddInputFields() {
        name = null;
        description = null;
        deviceType = null;
        parentName = (selectedSlot == null) ? "" : selectedSlot.getName();
        hasDevice = false;
    }

    /** Called to save modified installation slot / container information */
    public void onSlotModify() {
        Slot modifiedSlot = selectedSlotView.getSlot();
        modifiedSlot.setName(name);
        modifiedSlot.setDescription(description);
        if (modifiedSlot.isHostingSlot() && installationEJB.getActiveInstallationRecordForSlot(modifiedSlot) == null) {
            // changeSlotType only saves if the actual device type changes, otherwise it returns the slot unmodified
            modifiedSlot = slotEJB.changeSlotType(modifiedSlot, comptypeEJB.findById(deviceType));
        }
        slotEJB.save(modifiedSlot);
        selectedSlotView.setSlot(modifiedSlot);
        UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS, "Slot has been modified.");
        clearRelatedInformation();
        updateDisplayedSlotInformation();
    }

    /** Called to add a new installation slot / container to the database */
    public void onSlotAdd() {
        Preconditions.checkState(selectedTree.getSelectedNodes().size() <= 1);
        final Slot newSlot = new Slot(name, isInstallationSlot);
        newSlot.setDescription(description);
        if (isInstallationSlot) {
            newSlot.setComponentType(comptypeEJB.findById(deviceType));
        } else {
            newSlot.setComponentType(comptypeEJB.findByName(SlotEJB.GRP_COMPONENT_TYPE));
        }
        final FilteredTreeNode<SlotView> parentNode = selectedTree.getSelectedNodes().size() == 1
                                            ? selectedTree.getSelectedNodes().get(0) : selectedTree.getRootNode();
        final Slot parentSlot = parentNode.getData().getSlot();
        slotEJB.addSlotToParentWithPropertyDefs(newSlot, parentSlot, false);

        // first update the back-end data
        parentNode.refreshCache();
        parentNode.setExpanded(true);
        UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                                                                                "Slot has been successfully created");
    }

    /**
     * Helper method for auto complete when entering a name for new installation {@link Slot}.
     *
     * @param query Text that was entered so far
     * @return {@link List} of strings with suggestions
     */
    public List<String> nameAutocompleteText(String query) {
        final List<String> resultList = new ArrayList<>();
        final String queryUpperCase = query.toUpperCase();
        for (String element : namesForAutoComplete) {
            if (element.toUpperCase().startsWith(queryUpperCase))
                resultList.add(element);
        }
        return resultList;
    }

    /**
     * This method places the currently selected tree nodes into the clipboard and marks for moving.
     */
    public void cutTreeNodes() {
        Preconditions.checkState(isIncludesActive());
        Preconditions.checkState(!selectedTree.getSelectedNodes().isEmpty());

        clipboardOperation = ClipboardOperations.CUT;
        putSelectedNodesOntoClipboard();
    }

    /**
     * This method places the currently selected tree nodes into the clipboard and marks for copying.
     */
    public void copyTreeNodes() {
        Preconditions.checkState(isIncludesActive());
        Preconditions.checkState(!selectedTree.getSelectedNodes().isEmpty());

        clipboardOperation = ClipboardOperations.COPY;
        putSelectedNodesOntoClipboard();
    }

    public ClipboardOperations getCliboardOperation() {
        return clipboardOperation;
    }

    /**
     * This method tells whether a parent of the {@link BasicTreeNode} is in the clipboard.
     * @param node the {@link BasicTreeNode} to check for
     * @return <code>true</code> if the node's parent is in the clipboard, <code>false</code> otherwise
     */
    public boolean isAncestorNodeInClipboard(final BasicTreeNode<SlotView> node) {
        if (Utility.isNullOrEmpty(clipboardSlots) || (node == null) || node.equals(selectedTree.getRootNode().getData())) {
            return false;
        }
        if (clipboardSlots.contains(node)) {
            return true;
        } else {
             return isAncestorNodeInClipboard(node.getParent());
        }
    }

    private void putSelectedNodesOntoClipboard() {
        clipboardSlots = new ArrayList<>();

        // 2. We put the selected nodes into the clipboard
        for (final FilteredTreeNode<SlotView> node : selectedTree.getSelectedNodes()) {
            clipboardSlots.add(node);
        }

        // 3. We remove all the nodes that have their parents in the clipboard
        for (final Iterator<FilteredTreeNode<SlotView>> nodesIterator = clipboardSlots.iterator(); nodesIterator.hasNext();) {
            final FilteredTreeNode<SlotView> removalCandidate = nodesIterator.next();
            if (isAncestorNodeInList(clipboardSlots, removalCandidate)) {
                nodesIterator.remove();
            }
        }
    }

    /**
     * @param possibleAscendant
     * @param candidate
     * @return <code>true</code> if the <code>candidate</code> is a descendant of <code>possibleAscendant</code>,
     * <code>false</code> otherwise
     */
    private boolean isNodeDescendant(final FilteredTreeNode<SlotView> possibleAscendant, final FilteredTreeNode<SlotView> candidate) {
        return isAncestorNodeInList(Arrays.asList(possibleAscendant), candidate);
    }

    private boolean isAncestorNodeInList(final List<FilteredTreeNode<SlotView>> candidates, final FilteredTreeNode<SlotView> node) {
        BasicTreeNode<SlotView> parentNode = node.getParent();
        while (parentNode != null) {
            if (candidates.contains(parentNode)) {
                return true;
            }
            parentNode = parentNode.getParent();
        }
        return false;
    }

    /**
     * The method checks whether the paste operation is legal. If not, it fills the <code>pasteErrors</code>
     * {@link List} and the <code>pasteErrorReason</code>. If the paste operation is legal the <code>pasteErrors</code>
     * {@link List} will remain empty.
     */
    public void checkPasteAction() {
        Preconditions.checkState(!isClipboardEmpty());
        Preconditions.checkState(selectedTree.getSelectedNodes().size() < 2);

        final boolean makeRoots = Utility.isNullOrEmpty(selectedTree.getSelectedNodes());
        final boolean isTargetInstallationslot = !makeRoots && selectedSlot.isHostingSlot();
        if (makeRoots) {
            pasteErrorReason = CANNOT_PASTE_INTO_ROOT;
        } else {
            pasteErrorReason = CANNOT_PASTE_INTO_SLOT;
        }

        pasteErrors = Lists.newArrayList();
        for (final FilteredTreeNode<SlotView> node : clipboardSlots) {
            if (makeRoots && node.getData().isHostingSlot()) {
                pasteErrors.add(node.getData());
            } else if (isTargetInstallationslot && !node.getData().isHostingSlot()) {
                pasteErrors.add(node.getData());
            }
        }

        if ((pasteErrors.size() == 0) && (selectedSlot != null)) {
            pasteErrorReason = CANNOT_PASTE_INTO_SELF;
            BasicTreeNode<SlotView> current = selectedTree.getSelectedNodes().get(0);
            while (current != null) {
                for (final FilteredTreeNode<SlotView> node : clipboardSlots) {
                    if (node.equals(current)) {
                        pasteErrors.add(node.getData());
                    }
                }
                current = current.getParent();
            }
        }
    }

    /**
     * This method is called when the user selects the "Paste" action. The method either moves the nodes that are
     * in the clipboard, or creates new copies.
     */
    public void pasteTreeNodes() {
        Preconditions.checkState(isIncludesActive());
        Preconditions.checkState(!isClipboardEmpty());
        Preconditions.checkState(selectedTree.getSelectedNodes().size() < 2);
        Preconditions.checkNotNull(pasteErrors);
        Preconditions.checkState(pasteErrors.isEmpty());

        if (clipboardOperation == ClipboardOperations.CUT) {
            moveSlotsToNewParent();
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                    "Slots were moved.");
        } else {
            final FilteredTreeNode<SlotView> parentNode = (!selectedTree.getSelectedNodes().isEmpty())
                                            ? selectedTree.getSelectedNodes().get(0)
                                            : selectedTree.getRootNode();
            copySlotsToParent(clipboardSlots.stream().map(FilteredTreeNode<SlotView>::getData).
                                                map(SlotView::getSlot).collect(Collectors.toList()), parentNode);
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                    "Slots were copied.");
        }
    }

    private void moveSlotsToNewParent() {
        final FilteredTreeNode<SlotView> newParent = Utility.isNullOrEmpty(selectedTree.getSelectedNodes()) ? selectedTree.getRootNode() : selectedTree.getSelectedNodes().get(0);

        // remove the nodes that do not get moved or are moved to their own descendant
        final List<FilteredTreeNode<SlotView>> candidates = clipboardSlots.stream().
                                        filter(e -> !(e.getParent().equals(newParent) || isNodeDescendant(e, newParent))).collect(Collectors.toList());
        final List<BasicTreeNode<SlotView>> oldParents = candidates.stream().map(FilteredTreeNode<SlotView>::getParent).collect(Collectors.toList());

        final List<SlotPair> moveCandidatesByRelationship = candidates.stream().
                                        map(FilteredTreeNode<SlotView>::getData).map(SlotView::getParentRelationship).collect(Collectors.toList());
        slotPairEJB.moveSlotsToNewParent(moveCandidatesByRelationship, newParent.getData().getSlot());

        clipboardSlots = null;
        // Refresh the information about the affected slots in all the hierarchy trees
        newParent.refreshCache();
        newParent.setExpanded(true);

        for (BasicTreeNode<SlotView> node : oldParents) {
        	((FilteredTreeNode<SlotView>)node).refreshCache();
        }
    }

    private void copySlotsToParent(final List<Slot> sourceSlots, final FilteredTreeNode<SlotView> parentNode) {
        final SlotView newParentSlotView = parentNode.getData();
        final Slot newParentSlot = newParentSlotView.getSlot();
        slotEJB.copySlotsToParent(sourceSlots, newParentSlot);

        parentNode.refreshCache();
        parentNode.setExpanded(true);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Methods for manipulation, populating and editing the hierarchy tree of slots and containers.
     *
     * Below: Input field validators regardless of the dialog they are used in.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * Throws a validation error exception if the installation slot name is not unique.
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException validation failed
     */
    public void validateInstallationSlot(FacesContext ctx, UIComponent component, Object value) {
        final String valueStr = value.toString();

        if (restrictToConventionNames && isInstallationSlot) {
            if (!nameList.containsKey(valueStr))
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        UiUtility.MESSAGE_SUMMARY_ERROR, "The installation slot name not found in the naming tool."));
        }
        if (isNewSlot) {
            // add dialog
            if (isInstallationSlot) {
                // check uniqueness across whole database
                if (!slotEJB.isInstallationSlotNameUnique(valueStr))
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            UiUtility.MESSAGE_SUMMARY_ERROR, "The installation slot name must be unique."));
            } else {
                // check uniqueness only for the parent
                final Slot slotParent = selectedSlotView != null ? selectedSlotView.getSlot() : slotEJB.getRootNode();
                if (!slotEJB.isContainerNameUnique(valueStr, slotParent, null))
                        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                UiUtility.MESSAGE_SUMMARY_ERROR, "Parent alread contains equally named child."));
            }
        } else {
            // edit dialog
            if (isInstallationSlot) {
                // check uniqueness across whole database
                if (!name.equals(valueStr) && !slotEJB.isInstallationSlotNameUnique(valueStr))
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            UiUtility.MESSAGE_SUMMARY_ERROR, "The installation slot name must be unique."));
            } else {
                // check uniqueness only for the parent
                if (!name.equals(valueStr)
                        && !slotEJB.isContainerNameUnique(valueStr, selectedSlotView.getParentNode().getSlot(),
                                selectedSlotView.getSlot()))
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            UiUtility.MESSAGE_SUMMARY_ERROR, "Parent alread contains equally named child."));
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Input field validators regardless of the dialog they are used in.
     *
     * Below: Getters and setter all logically grouped based on where they are used. All getters and setters are
     *        usually called from the UI dialogs.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Main screen
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** @return The slot (container or installation slot) that is currently selected in the tree. */
    public Slot getSelectedNodeSlot() {
        return selectedSlot;
    }

    /** @return the {@link SlotView} of the slot, if there is only one node selected in the tree.
     * <code>null</code> otherwise.
     */
    public SlotView getSingleSelectedSlotView() {
        return selectedSlotView;
    }

    protected void expandFirstSelectedNode() {
        selectedTree.getSelectedNodes().get(0).setExpanded(true);
    }

    public Tree<SlotView> getContainsTree() {
        return containsTree;
    }

    public Tree<SlotView> getControlsTree() {
        return controlsTree;
    }

    public Tree<SlotView> getPowersTree() {
        return powersTree;
    }

    public Tree<SlotView> getConnectsTree() {
        return connectsTree;
    }

    /** @return <code>true</code> if the UI is currently showing the <code>INCLUDES</code> hierarchy */
    public boolean isIncludesActive() {
        return activeTab == ActiveTab.INCLUDES;
    }

    /** @return <code>true</code> if the currently shown hierarchy tree has no nodes under the current filter,
     * <code>false</code> otherwise. */
    public boolean isDisplayedTreeEmpty() {
        return (activeTab == ActiveTab.INCLUDES
                    && (containsTree == null || containsTree.getRootNode().getFilteredChildren().isEmpty()))
                || (activeTab == ActiveTab.CONTROLS
                        && (controlsTree == null || controlsTree.getRootNode().getFilteredChildren().isEmpty()))
                || (activeTab == ActiveTab.POWERS
                        && (powersTree == null || powersTree.getRootNode().getFilteredChildren().isEmpty()))
                || (activeTab == ActiveTab.CONNECTS
                        && (connectsTree == null || connectsTree.getRootNode().getFilteredChildren().isEmpty()));
    }

    public boolean isSingleNodeSelected() {
        return (selectedTree.getSelectedNodes() != null) && (selectedTree.getSelectedNodes().size() == 1);
    }

    public boolean isMultipleNodesSelected() {
        return (selectedTree.getSelectedNodes() != null) && (selectedTree.getSelectedNodes().size() > 1);
    }

    public boolean isClipboardEmpty() {
        return Utility.isNullOrEmpty(clipboardSlots);
    }

    public List<SlotView> getSlotsToDelete() {
        return slotsToDelete;
    }
    public void setSlotsToDelete(List<SlotView> slotsToDelete) {
        this.slotsToDelete = slotsToDelete;
    }

    public List<SlotView> getFilteredSlotsToDelete() {
        return filteredSlotsToDelete;
    }
    public void setFilteredSlotsToDelete(List<SlotView> filteredSlotsToDelete) {
        this.filteredSlotsToDelete = filteredSlotsToDelete;
    }

    public int getNumberOfSlotsToDelete() {
        return nodesToDelete != null ? nodesToDelete.size() : 0;
    }

    public String getPasteErrorReason() {
        return pasteErrorReason;
    }

    public List<SlotView> getPasteErrors() {
        return pasteErrors;
    }

    public List<SlotView> getClipboardSlots() {
        return clipboardSlots == null ? null : clipboardSlots.stream().map(FilteredTreeNode<SlotView>::getData).
                                                                            collect(Collectors.toList());
    }

    public String getRequestedSlot() {
        return requestedSlot;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Slot dialog
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** @return the name */
    @NotNull
    @Size(min = 1, max = 128, message = "Name can have at most 128 characters.")
    public String getName() {
        return name;
    }
    /** @param name the name to set */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the description */
    @NotNull
    @Size(min = 1, max = 255, message = "Description can have at most 255 characters.")
    public String getDescription() {
        return description;
    }
    /** @param description the description to set */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the isInstallationSlot */
    public boolean isInstallationSlot() {
        return isInstallationSlot;
    }

    /** @return the deviceType */
    public Long getDeviceType() {
        return deviceType;
    }
    /** @param deviceType the deviceType to set */
    public void setDeviceType(Long deviceType) {
        this.deviceType = deviceType;
    }

    /** @return the isNewSlot */
    public boolean isNewSlot() {
        return isNewSlot;
    }

    /** @return the parent slot name */
    public String getParentName() {
        return parentName;
    }

    /** @return hasDevice */
    public boolean getHasDevice() {
        return hasDevice;
    }

    /** @return the namingRedirectionUrl */
    public String getNamingRedirectionUrl() {
        return namingRedirectionUrl;
    }


    /** @return the linkSlot */
    public SlotView getLinkSlot() {
        return linkSlot;
    }

    /** @param linkSlot the linkSlot to set */
    public void setLinkSlot(SlotView linkSlot) {
        this.linkSlot = linkSlot;
    }

    /** @return the cableRedirectionUrl */
    public String getCableRedirectionUrl() {
        if (cableRedirectionUrl == null) {
            final String cableRedirectionUrl = properties.getProperty(AppProperties.CABLEDB_APPLICATION_URL);

            if (!Strings.isNullOrEmpty(cableRedirectionUrl)) {
                final StringBuilder redirectionUrl = new StringBuilder(cableRedirectionUrl);
                if (redirectionUrl.charAt(redirectionUrl.length() - 1) != '/') {
                    redirectionUrl.append('/');
                }
                redirectionUrl.append(CABLEDB_DEVICE_PAGE);
                this.cableRedirectionUrl = redirectionUrl.toString();
            }
        }
        return cableRedirectionUrl;
    }

    /** @return the namesForAutoComplete */
    public List<String> getNamesForAutoComplete() {
        return namesForAutoComplete;
    }

    /** @return the restrictToConventionNames */
    public boolean isRestrictToConventionNames() {
        return restrictToConventionNames;
    }

    protected Slot getSelectedEntity() {
        if (selectedSlot != null) {
            Slot slot = slotEJB.refreshEntity(selectedSlot);
            return slot;
        }
        throw new IllegalArgumentException("No slot selected");
    }

    protected List<Slot> getSelectedSlots() {
        if (selectedTree.getSelectedNodes().isEmpty())
            return Collections.emptyList();
        else
            return selectedTree.getSelectedNodes().stream().map(e -> e.getData().getSlot()).collect(Collectors.toList());
    }

    @Override
    public ExportSimpleTableDialog getSimpleTableDialog() {
        return simpleTableExporterDialog;
    }

    public boolean isContainsEmpy() {
        return containsTree.getRootNode().getBufferedAllChildren().isEmpty();
    }

    @Override
    public SlotRelationshipTree getContainsRelationshipTree() {
        return containsTree;
    }
}
