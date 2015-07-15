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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.dl.annotations.SignalsLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.ejb.SlotRelationEJB;
import org.openepics.discs.conf.ejb.TagEJB;
import org.openepics.discs.conf.ent.Artifact;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;
import org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.HierarchyBuilder;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.util.BlobStore;
import org.openepics.discs.conf.util.BuiltInDataType;
import org.openepics.discs.conf.util.Conversion;
import org.openepics.discs.conf.util.PropertyValueNotUniqueException;
import org.openepics.discs.conf.util.PropertyValueUIElement;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.util.names.Names;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.EntityAttributeViewKind;
import org.openepics.discs.conf.views.SlotRelationshipView;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
@Named
@ViewScoped
public class HierarchiesController extends AbstractExcelSingleFileImportUI implements Serializable {
    private static final long serialVersionUID = 2743408661782529373L;

    private static final Logger LOGGER = Logger.getLogger(HierarchiesController.class.getCanonicalName());
    private static final String MULTILINE_DELIMITER = "(\\r\\n)|\\r|\\n";

    @Inject private transient SlotEJB slotEJB;
    @Inject private transient SlotPairEJB slotPairEJB;
    @Inject private transient InstallationEJB installationEJB;
    @Inject private transient SlotRelationEJB slotRelationEJB;
    @Inject private transient ComptypeEJB comptypeEJB;
    @Inject private transient PropertyEJB propertyEJB;
    @Inject private TagEJB tagEJB;
    @Inject private BlobStore blobStore;
    @Inject private Names names;

    @Inject private transient DataLoaderHandler dataLoaderHandler;
    @Inject @SignalsLoader private transient DataLoader signalsDataLoader;

    private enum ActiveTab {
        INCLUDES, POWERS, CONTROLS
    }

    private transient List<EntityAttributeView> attributes;
    private transient List<EntityAttributeView> filteredAttributes;
    private transient List<SelectItem> attributeKinds;
    private transient List<SlotRelationshipView> relationships;
    private transient List<SlotRelationshipView> filteredRelationships;
    private transient List<SelectItem> relationshipTypes;
    private List<Device> uninstalledDevices;
    private List<Device> filteredUninstalledDevices;
    private List<InstallationRecord> installationRecords;
    /** <code>selectedInstallationRecord</code> is only initialized when there is only one node in the tree selected */
    private InstallationRecord selectedInstallationRecord;
    private Device deviceToInstall;

    // ---- variables for hierarchies and tabs --------
    private HierarchyBuilder hierarchyBuilder;
    private HierarchyBuilder powersHierarchyBuilder;
    private HierarchyBuilder controlsHierarchyBuilder;
    private TreeNode rootNode;
    private TreeNode powersRootNode;
    private TreeNode controlsRootNode;
    private List<TreeNode>selectedNodes;
    /** <code>selectedSlot</code> is only initialized when there is only one node in the tree selected */
    private Slot selectedSlot;
    /** <code>selectedSlotView</code> is only initialized when there is only one node in the tree selected */
    private SlotView selectedSlotView;
    private ActiveTab activeTab;

    // variables from the installation slot / containers editing merger.
    private String name;
    private String description;
    /** Used in "add child to parent" operations. This usually reflects the <code>selectedNode</code>. */
    private boolean isInstallationSlot;
    private ComponentType deviceType;
    private transient List<String> namesForAutoComplete;
    private boolean isNewInstallationSlot;

    // ------ variables for attribute manipulation ------
    private EntityAttributeView selectedAttribute;
    private String artifactDescription;
    private String artifactURI;
    private String artifactName;
    private boolean isArtifactInternal;
    private boolean isArtifactBeingModified;
    private Property property;
    private Value propertyValue;
    private transient List<String> enumSelections;
    private transient List<Property> filteredProperties;
    private PropertyValueUIElement propertyValueUIElement;
    private boolean propertyNameChangeDisabled;
    private String tag;
    private transient List<String> tagsForAutocomplete;

    private transient SlotRelationshipView selectedRelationship;
    private TreeNode selectedTreeNodeForRelationshipAdd;
    private String selectedRelationshipType;
    private List<String> relationshipTypesForDialog;
    private Map<String, SlotRelation> slotRelationBySlotRelationStringName;

    /** Java EE post construct life-cycle method. */
    @Override
    @PostConstruct
    public void init() {
        try {
            super.init();
            activeTab = ActiveTab.INCLUDES;
            initIncludeHierarchy();
            initPowersHierarchy();
            initControlsHierarchy();
            fillNamesAutocomplete();
            attributeKinds = Utility.buildAttributeKinds();
            relationshipTypes = buildRelationshipTypeList();

            final String slotId = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().
                    getRequest()).getParameter("id");
            if (slotId != null) {
                selectNode(Long.parseLong(slotId));
            }
        } catch(Exception e) {
            throw new UIException("Hierarchies display initialization fialed: " + e.getMessage(), e);
        }
    }

    private List<SelectItem> buildRelationshipTypeList() {
        Builder<SelectItem> immutableListBuilder = ImmutableList.builder();
        immutableListBuilder.add(new SelectItem("", "Select one"));

        final List<SlotRelation> slotRelations = slotRelationEJB.findAll();
        slotRelationBySlotRelationStringName = new HashMap<>();
        for (final SlotRelation slotRelation : slotRelations) {
            immutableListBuilder.add(new SelectItem(slotRelation.getNameAsString(), slotRelation.getNameAsString()));
            immutableListBuilder.add(new SelectItem(slotRelation.getIname(), slotRelation.getIname()));
            slotRelationBySlotRelationStringName.put(slotRelation.getNameAsString(), slotRelation);
            slotRelationBySlotRelationStringName.put(slotRelation.getIname(), slotRelation);
        }

        relationshipTypesForDialog = ImmutableList.copyOf(slotRelationBySlotRelationStringName.keySet().iterator());
        return immutableListBuilder.build();
    }

    private void fillNamesAutocomplete() {
        namesForAutoComplete = ImmutableList.copyOf(names.getAllNames());
    }

    private void saveSlotAndRefresh(final Slot slot) {
        slotEJB.save(slot);
        refreshSlot(slot);
    }

    private void refreshSlot(final Slot slot) {
        final Slot freshSlot = slotEJB.findById(slot.getId());
        if (selectedSlot != null) {
            selectedSlot = freshSlot;
        }
        if (selectedSlotView != null) {
            selectedSlotView.setSlot(freshSlot);
        }
        updateTreesWithFreshSlot(freshSlot);
    }

    private void updateTreesWithFreshSlot(final Slot freshSlot) {
        updateTreeWithFreshSlot(rootNode, freshSlot);
        updateTreeWithFreshSlot(controlsRootNode, freshSlot);
        updateTreeWithFreshSlot(powersRootNode, freshSlot);
    }

    private void updateTreeWithFreshSlot(final TreeNode node, final Slot freshSlot) {
        final SlotView nodeSlotView = (SlotView) node.getData();
        if (freshSlot.getId().equals(nodeSlotView.getId())) {
            nodeSlotView.setSlot(freshSlot);
        } else {
            for (final TreeNode nodeChild : node.getChildren()) {
                updateTreeWithFreshSlot(nodeChild, freshSlot);
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above : bean initialization section and global private utility methods.
     *
     * Below: Screen population methods. These methods prepare the data to be displayed on the main UI screen.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void initAttributeList(final Slot slot) {
        attributes = new ArrayList<>();
        addPropertyValues(slot);
        addArtifacts(slot);
        addTags(slot);
    }

    private void refreshAttributeList(final Slot slot, final SlotPropertyValue propertyValue) {
        // Use iterator. If the property value is found, then update it.
        // If not, add new property value to the already existing ones. Append to the end of the ones for the same slot.
        boolean encounteredParentSiblings = false;
        ListIterator<EntityAttributeView> attributesIter = attributes.listIterator();
        while (attributesIter.hasNext()) {
            final EntityAttributeView tableAttribute = attributesIter.next();
            if (tableAttribute.getParent().equals(slot.getName())) {
                encounteredParentSiblings = true;
                // the entity's real sibling

                if (tableAttribute.getEntity().equals(propertyValue)) {
                    // found the existing artifact, update it and exit!
                    attributesIter.set(new EntityAttributeView(propertyValue, slot.isHostingSlot()
                            ? EntityAttributeViewKind.INSTALL_SLOT_PROPERTY
                                    : EntityAttributeViewKind.CONTAINER_SLOT_PROPERTY,
                            slot.getName()));
                    return;
                }

                if (tableAttribute.getKind() == EntityAttributeViewKind.DEVICE_TYPE_ARTIFACT
                        || tableAttribute.getKind() == EntityAttributeViewKind.INSTALL_SLOT_ARTIFACT
                        || tableAttribute.getKind() == EntityAttributeViewKind.DEVICE_ARTIFACT
                        || tableAttribute.getKind() == EntityAttributeViewKind.CONTAINER_SLOT_ARTIFACT
                        || tableAttribute.getKind() == EntityAttributeViewKind.ARTIFACT) {
                    // we just encountered our sibling ARTIFACT. Insert before that.
                    attributesIter.previous();
                    break;
                }
            } else if (encounteredParentSiblings) {
                // we just moved past all our siblings. Move one back and break;
                attributesIter.previous();
                break;
            }
        }
        // the insertion pointer is at the right spot. This is either the last property value for this parent,
        //   the last attribute for this parent (no artifacts and tags), or the very last attribute in the entire table
        attributesIter.add(new EntityAttributeView(propertyValue, slot.isHostingSlot()
                ? EntityAttributeViewKind.INSTALL_SLOT_PROPERTY : EntityAttributeViewKind.CONTAINER_SLOT_PROPERTY,
                slot.getName()));
    }

    private void refreshAttributeList(final Slot slot, final Tag tag) {
        // Use iterator. Add new Tag to the already existing ones. Append to the end of the ones for the same slot.
        boolean encounteredParentSiblings = false;
        ListIterator<EntityAttributeView> attributesIter = attributes.listIterator();
        while (attributesIter.hasNext()) {
            final EntityAttributeView tableAttribute = attributesIter.next();
            if (tableAttribute.getParent().equals(slot.getName())) {
                encounteredParentSiblings = true;
            } else if (encounteredParentSiblings) {
                // we just moved past all our siblings. Move one back and break.
                attributesIter.previous();
                break;
            }
        }
        // the insertion pointer is at the right spot. This is either the last attribute for this parent,
        //     or the very last attribute in the entire table
        attributesIter.add(new EntityAttributeView(tag,  slot.isHostingSlot()
                ? EntityAttributeViewKind.INSTALL_SLOT_TAG : EntityAttributeViewKind.CONTAINER_SLOT_TAG,
                slot.getName()));
    }

    private void refreshAttributeList(final Slot slot, final SlotArtifact artifact) {
        // Use iterator. If the artifact is found, then update it.
        // If not, add new artifact to the already existing ones. Append to the end of the ones for the same slot.
        boolean encounteredParentSiblings = false;
        ListIterator<EntityAttributeView> attributesIter = attributes.listIterator();
        while (attributesIter.hasNext()) {
            final EntityAttributeView tableAttribute = attributesIter.next();
            if (tableAttribute.getParent().equals(slot.getName())) {
                encounteredParentSiblings = true;
                // the entity's real sibling

                if (tableAttribute.getEntity().equals(artifact)) {
                    // found the existing artifact, update it and exit!
                    attributesIter.set(new EntityAttributeView(artifact, slot.isHostingSlot()
                            ? EntityAttributeViewKind.INSTALL_SLOT_ARTIFACT
                                    : EntityAttributeViewKind.CONTAINER_SLOT_ARTIFACT,
                            slot.getName()));
                    return;
                }

                if (tableAttribute.getKind() == EntityAttributeViewKind.DEVICE_TYPE_TAG
                        || tableAttribute.getKind() == EntityAttributeViewKind.INSTALL_SLOT_TAG
                        || tableAttribute.getKind() == EntityAttributeViewKind.DEVICE_TAG
                        || tableAttribute.getKind() == EntityAttributeViewKind.CONTAINER_SLOT_TAG
                        || tableAttribute.getKind() == EntityAttributeViewKind.TAG) {
                    // we just encountered our sibling TAG. Insert before that.
                    attributesIter.previous();
                    break;
                }
            } else if (encounteredParentSiblings) {
                // we just moved past all our siblings. Move one back and break;
                attributesIter.previous();
                break;
            }
        }
        // the insertion pointer is at the right spot. This is either the last artifact for this parent,
        //     the last attribute for this parent (no tags), or the very last attribute in the entire table
        attributesIter.add(new EntityAttributeView(artifact, slot.isHostingSlot()
                ? EntityAttributeViewKind.INSTALL_SLOT_ARTIFACT : EntityAttributeViewKind.CONTAINER_SLOT_ARTIFACT,
                slot.getName()));
    }

    private void removeRelatedAttributes(Slot slot) {
        final ListIterator<EntityAttributeView> slotAttributes = attributes.listIterator();
        while (slotAttributes.hasNext()) {
            final EntityAttributeView attribute = slotAttributes.next();
            if (slot.getName().equals(attribute.getParent())) {
                slotAttributes.remove();
            }
        }
    }

    private void addPropertyValues(final Slot slot) {
        final boolean isHostingSlot = slot.isHostingSlot();
        final Device installedDevice = getInstalledDevice();

        for (final ComptypePropertyValue value : slot.getComponentType().getComptypePropertyList()) {
            if (!value.isPropertyDefinition()) {
                attributes.add(new EntityAttributeView(value, EntityAttributeViewKind.DEVICE_TYPE_PROPERTY,
                                                            slot.getName()));
            }
        }

        for (final SlotPropertyValue value : slot.getSlotPropertyList()) {
            attributes.add(new EntityAttributeView(value, isHostingSlot
                                                            ? EntityAttributeViewKind.INSTALL_SLOT_PROPERTY
                                                            : EntityAttributeViewKind.CONTAINER_SLOT_PROPERTY,
                                                            slot.getName()));
        }

        if (installedDevice != null) {
            for (final DevicePropertyValue devicePropertyValue : installedDevice.getDevicePropertyList()) {
                attributes.add(new EntityAttributeView(devicePropertyValue,
                                                            EntityAttributeViewKind.DEVICE_PROPERTY,
                                                            slot.getName()));
            }
        }
    }

    private void addArtifacts(final Slot slot) {
        final boolean isHostingSlot = slot.isHostingSlot();
        final Device installedDevice = getInstalledDevice();

        for (final ComptypeArtifact artifact : slot.getComponentType().getComptypeArtifactList()) {
            attributes.add(new EntityAttributeView(artifact, EntityAttributeViewKind.DEVICE_TYPE_ARTIFACT,
                                                            slot.getName()));
        }

        for (final SlotArtifact artifact : slot.getSlotArtifactList()) {
            attributes.add(new EntityAttributeView(artifact, isHostingSlot
                                                            ? EntityAttributeViewKind.INSTALL_SLOT_ARTIFACT
                                                            : EntityAttributeViewKind.CONTAINER_SLOT_ARTIFACT,
                                                            slot.getName()));
        }

        if (installedDevice != null) {
            for (final DeviceArtifact deviceArtifact : installedDevice.getDeviceArtifactList()) {
                attributes.add(new EntityAttributeView(deviceArtifact,
                                                            EntityAttributeViewKind.DEVICE_ARTIFACT,
                                                            slot.getName()));
            }
        }
    }

    private void addTags(final Slot slot) {
        final boolean isHostingSlot = slot.isHostingSlot();
        final Device installedDevice = getInstalledDevice();

        for (final Tag tagInstance : slot.getComponentType().getTags()) {
            attributes.add(new EntityAttributeView(tagInstance, EntityAttributeViewKind.DEVICE_TYPE_TAG,
                                                            slot.getName()));
        }

        for (final Tag tagInstance : slot.getTags()) {
            attributes.add(new EntityAttributeView(tagInstance, isHostingSlot
                                                            ? EntityAttributeViewKind.INSTALL_SLOT_TAG
                                                            : EntityAttributeViewKind.CONTAINER_SLOT_TAG,
                                                            slot.getName()));
        }

        if (installedDevice != null) {
            for (final Tag tagInstance : installedDevice.getTags()) {
                attributes.add(new EntityAttributeView(tagInstance, EntityAttributeViewKind.DEVICE_TAG,
                                                            slot.getName()));
            }
        }
    }

    private void initRelationshipList(final Slot slot) {
        relationships = Lists.newArrayList();
        addToRelationshipList(slot);
    }

    private void addToRelationshipList(final Slot slot) {
        final Slot rootSlot = slotEJB.getRootNode();
        final List<SlotPair> slotPairs = slotPairEJB.getSlotRelations(slot);

        for (final SlotPair slotPair : slotPairs) {
            if (!slotPair.getParentSlot().equals(rootSlot)) {
                relationships.add(new SlotRelationshipView(slotPair, slot));
            }
        }
    }

    private void initInstallationRecordList(Slot slot) {
        installationRecords = Lists.newArrayList();
        addToInstalaltionRecordList(slot);
    }

    private void addToInstalaltionRecordList(Slot slot) {
        if (slot.isHostingSlot()) {
            final InstallationRecord record = installationEJB.getActiveInstallationRecordForSlot(slot);
            if (record != null) {
                installationRecords.add(record);
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Screen population methods. These methods prepare the data to be displayed on the main UI screen.
     *
     * Below: Callback methods called from the main UI screen. E.g.: methods that are called when user user selects
     *        a line in a table.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /** Clears all slot related information when user deselects the slot in the hierarchy. */
    private void clearAttributeList() {
        attributes = null;
        filteredAttributes = null;
        relationships = null;
        filteredRelationships = null;
        installationRecords = null;
        deselectSingleNode();
    }

    private void selectSingleNode(final TreeNode selectedNode) {
        selectedSlotView = (SlotView) selectedNode.getData();
        selectedSlot = selectedSlotView.getSlot();
        selectedInstallationRecord = installationEJB.getActiveInstallationRecordForSlot(selectedSlot);
    }

    private void deselectSingleNode() {
        selectedSlotView = null;
        selectedSlot = null;
        selectedInstallationRecord = null;
    }

    /** The function to select a different node in the TreeTable by clicking on the link in the relationship table.
     * @param id the ID of the slot we want to switch to.
     */
    public void selectNode(Long id) {
        // TODO much more difficult, since not all nodes are already loaded at this point.
        Preconditions.checkNotNull(id);

        final TreeNode nodeToSelect = findNode(id, rootNode);
        if (nodeToSelect != null) {
            // TODO deselect all nodes in the tree
            nodeToSelect.setSelected(true);
            //setSelectedNode(nodeToSelect);
            initSelectedItemLists(selectedSlotView.getSlot());
        }
    }

    /* The recursive function to search for the node in the "depth first" order.
     *
     * @param id the database ID of the {@link Slot} we're searching for
     * @param parent the node we're searching at the moment
     * @return The TreeNode containing the {@link Slot} we're looking for or <code>null</code>, if it dies not exist.
     */
    private TreeNode findNode(Long id, TreeNode parent) {
        if (id.equals(((SlotView)parent.getData()).getId())) {
            return parent;
        }
        for (final TreeNode child : parent.getChildren()) {
            final TreeNode foundNode = findNode(id, child);
            if (foundNode != null) {
                return foundNode;
            }
        }
        return null;
    }

    /** Prepares the attribute and relationship lists for display when user selects the slot in the hierarchy. */
    private void initSelectedItemLists(final Slot slot) {
        initAttributeList(slot);
        initRelationshipList(slot);
        initInstallationRecordList(slot);
        selectedAttribute = null;
    }

    /**
     * Called when a user deselects a new node in one of the hierarchy trees.
     *
     * @param event Event triggered on node selection action
     */
    public void onNodeSelect(NodeSelectEvent event) {
        final SlotView addedSlotView = (SlotView) event.getTreeNode().getData();
        if (selectedNodes != null && selectedNodes.size() == 1) {
            initSelectedItemLists(addedSlotView.getSlot());
            selectSingleNode(event.getTreeNode());
        } else {
            final Slot slot = addedSlotView.getSlot();
            addPropertyValues(slot);
            addArtifacts(slot);
            addTags(slot);
            addToRelationshipList(slot);
            addToInstalaltionRecordList(slot);
        }
    }

    /**
     * Called when a user deselects a new node in one of the hierarchy trees.
     *
     * @param event Event triggered on node selection action
     */
    public void onNodeUnselect(NodeUnselectEvent event) {
        // in the callback, the selectedNodes no longer contains the unselected node
        if (selectedNodes == null || selectedNodes.isEmpty()) {
            clearAttributeList();
            return;
        }

        removeRelatedAttributes(((SlotView) event.getTreeNode().getData()).getSlot());
        if (selectedNodes.size() == 1) {
            selectSingleNode(selectedNodes.get(0));
        }
    }

    /**
     * Builds the part of the tree under the expanded node if that is necessary.
     *
     * @param event Event triggered on node expand action
     */
    public void onContainsExpand(NodeExpandEvent event) {
        final TreeNode expandedNode = event.getTreeNode();
        if (expandedNode != null) {
            hierarchyBuilder.expandNode(expandedNode);
        }
    }

    /**
     * Builds the part of the tree under the expanded node if that is necessary.
     *
     * @param event Event triggered on node expand action
     */
    public void onPowersExpand(NodeExpandEvent event) {
        final TreeNode expandedNode = event.getTreeNode();
        if (expandedNode != null) {
            powersHierarchyBuilder.expandNode(expandedNode);
        }
    }

    /**
     * Builds the part of the tree under the expanded node if that is necessary.
     *
     * @param event Event triggered on node expand action
     */
    public void onControlsExpand(NodeExpandEvent event) {
        final TreeNode expandedNode = event.getTreeNode();
        if (expandedNode != null) {
            controlsHierarchyBuilder.expandNode(expandedNode);
        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeTab = ActiveTab.valueOf(event.getTab().getId());
        clearAttributeList();
    }

    @Override
    public void setDataLoader() {
        dataLoader = signalsDataLoader;
    }

    @Override
    public void doImport() {
        try (InputStream inputStream = new ByteArrayInputStream(importData)) {
            setLoaderResult(dataLoaderHandler.loadData(inputStream, signalsDataLoader));
            if (selectedSlot != null) {
                refreshSlot(selectedSlot);
                //initAttributeList(); // TODO deselect everything in all trees.
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        Preconditions.checkNotNull(selectedSlotView);
    }

    /** Deletes selected container */
    public void onSlotsDelete() {
        // check all selected nodes and delete them if they have no children.
        Preconditions.checkState(isNodesDeletable());
        final int numSlotstoDelete = selectedNodes.size();
        for (final TreeNode nodeToRemove : selectedNodes) {
            final TreeNode parentTreeNode = nodeToRemove.getParent();
            final Slot slotToDelete = ((SlotView) nodeToRemove.getData()).getSlot();
            slotEJB.delete(slotToDelete);
            // node removed. Refresh the parent
            final SlotView parentSlotView = (SlotView) parentTreeNode.getData();
            parentSlotView.setSlot(slotEJB.findById(parentSlotView.getSlot().getId()));
            // update UI data as well
            parentTreeNode.getChildren().remove(nodeToRemove);
            if (parentTreeNode.getChildCount() == 0) {
                parentSlotView.setDeletable(true);
            }
        }
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Slots deleted", Integer.toString(numSlotstoDelete)
                                            + " slots have been successfully deleted");
        selectedNodes = null;
        clearAttributeList();
    }

    public boolean isNodesDeletable() {
        if (selectedNodes == null) {
            return false;
        }
        for (final TreeNode node : selectedNodes) {
            final SlotView nodeSlot = (SlotView) node.getData();
            if ((node.getChildCount() > 0) || (nodeSlot.getInstalledDevice() != null)) {
                return false;
            }
        }
        return true;
    }

    private void initIncludeHierarchy() {
        hierarchyBuilder = new HierarchyBuilder(3, installationEJB);
        rootNode = new DefaultTreeNode(new SlotView(slotEJB.getRootNode(), null, 1), null);

        hierarchyBuilder.rebuildSubTree(rootNode);
    }

    private void initPowersHierarchy() {
        powersHierarchyBuilder = new HierarchyBuilder(3, installationEJB);
        powersHierarchyBuilder.setRelationship(SlotRelationName.POWERS);

        powersRootNode = new DefaultTreeNode(new SlotView(slotEJB.getRootNode(), null, 1), null);
        powersHierarchyBuilder.rebuildSubTree(powersRootNode);
    }

    private void initControlsHierarchy() {
        controlsHierarchyBuilder = new HierarchyBuilder(3, installationEJB);
        controlsHierarchyBuilder.setRelationship(SlotRelationName.CONTROLS);

        controlsRootNode = new DefaultTreeNode(new SlotView(slotEJB.getRootNode(), null, 1), null);
        controlsHierarchyBuilder.rebuildSubTree(controlsRootNode);
    }


    /** The action event to be called when the user presses the "move up" action button. This action moves the current
     * container/installation slot up one space, if that is possible.
     */
    public void moveSlotUp() {
        Preconditions.checkState(isSingleNodeSelected());
        final TreeNode currentNode = selectedNodes.get(0);
        final TreeNode parent = currentNode.getParent();

        final ListIterator<TreeNode> listIterator = parent.getChildren().listIterator();
        while (listIterator.hasNext()) {
            final TreeNode element = listIterator.next();
            if (element.equals(currentNode) && listIterator.hasPrevious()) {
                final SlotView movedSlotView = (SlotView) currentNode.getData();
                final SlotView currentNodesParentSlotView = (SlotView) parent.getData();
                listIterator.remove();
                final SlotView affectedNode = (SlotView) listIterator.previous().getData();
                affectedNode.setLast(movedSlotView.isLast());
                affectedNode.setFirst(false);
                movedSlotView.setLast(false);
                movedSlotView.setFirst(!listIterator.hasPrevious());
                listIterator.add(currentNode);
                slotPairEJB.moveUp(currentNodesParentSlotView.getSlot(), movedSlotView.getSlot());
                break;
            }
        }
    }

    /** The action event to be called when the user presses the "move down" action button. This action moves the current
     * container/installation slot down one space, if that is possible.
     */
    public void moveSlotDown() {
        Preconditions.checkState(isSingleNodeSelected());
        final TreeNode currentNode = selectedNodes.get(0);
        final TreeNode parent = currentNode.getParent();

        final ListIterator<TreeNode> listIterator = parent.getChildren().listIterator();
        while (listIterator.hasNext()) {
            final TreeNode element = listIterator.next();
            if (element.equals(currentNode) && listIterator.hasNext()) {
                final SlotView movedSlotView = (SlotView) currentNode.getData();
                final SlotView currentNodesParentSlotView = (SlotView) parent.getData();
                listIterator.remove();
                final SlotView affectedNode = (SlotView) listIterator.next().getData();
                affectedNode.setFirst(movedSlotView.isFirst());
                affectedNode.setLast(false);
                movedSlotView.setFirst(false);
                movedSlotView.setLast(!listIterator.hasNext());
                listIterator.add(currentNode);
                slotPairEJB.moveDown(currentNodesParentSlotView.getSlot(), movedSlotView.getSlot());
                break;
            }
        }
    }

    /** Prepares fields that are used in pop up for editing an existing container */
    public void prepareEditPopup() {
        Preconditions.checkState(isSingleNodeSelected());
        isNewInstallationSlot = false;
        isInstallationSlot = selectedSlotView.isHostingSlot();
        name = selectedSlotView.getName();
        description = selectedSlotView.getDescription();
        deviceType = selectedSlotView.getDeviceType();
    }

    /** Prepares fields that are used in pop up for adding a new container */
    public void prepareContainerAddPopup() {
        isInstallationSlot = false;
        initAddInputFields();
    }

    /** Prepares fields that are used in pop up for adding a new installation slot */
    public void prepareInstallationSlotPopup() {
        isInstallationSlot = true;
        isNewInstallationSlot = true;
        initAddInputFields();
    }

    private void initAddInputFields() {
        name = null;
        description = null;
        deviceType = null;
    }

    /** Called to save modified installation slot / container information */
    public void onSlotModify() {
        final Slot modifiedSlot = selectedSlotView.getSlot();
        modifiedSlot.setName(name);
        modifiedSlot.setDescription(description);
        if (modifiedSlot.isHostingSlot() && installationRecords == null) {
            modifiedSlot.setComponentType(deviceType);
        }
        slotEJB.save(modifiedSlot);
        selectedSlotView.setSlot(slotEJB.findById(modifiedSlot.getId()));
    }

    /** Called to add a new installation slot / container to the database */
    public void onSlotAdd() {
        Preconditions.checkState(selectedNodes == null || selectedNodes.size() == 1);
        final Slot newSlot = new Slot(name, isInstallationSlot);
        newSlot.setDescription(description);
        if (isInstallationSlot) {
            newSlot.setComponentType(deviceType);
        } else {
            newSlot.setComponentType(comptypeEJB.findByName(SlotEJB.GRP_COMPONENT_TYPE));
        }
        final TreeNode parentNode = selectedNodes != null ? selectedNodes.get(0) : rootNode;
        final Slot parentSlot = selectedNodes != null ? ((SlotView) parentNode.getData()).getSlot() : null;
        slotEJB.addSlotToParentWithPropertyDefs(newSlot, parentSlot, false);

        // first update the back-end data
        final SlotView slotViewToUpdate = (SlotView) parentNode.getData();
        slotViewToUpdate.setSlot(slotEJB.findById(slotViewToUpdate.getSlot().getId()));
        if (selectedSlotView != null) {
            selectedSlotView = slotViewToUpdate;
        }

        // now update the front-end data as well.
        if (slotViewToUpdate.isInitialzed()) {
            final List<SlotPair> containsRelation = slotPairEJB.findSlotPairsByParentChildRelation(newSlot.getName(),
                    slotViewToUpdate.getName(), SlotRelationName.CONTAINS);
            final SlotPair newRelation = containsRelation.get(0);
            final SlotView slotViewToAdd = new SlotView(newSlot, slotViewToUpdate, newRelation.getSlotOrder());
            hierarchyBuilder.addChildToParent(parentNode, slotViewToAdd);
        } else {
            hierarchyBuilder.rebuildSubTree(parentNode);
        }
        slotViewToUpdate.setDeletable(false);
        parentNode.setExpanded(true);
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Slot created", "Slot has been successfully created");
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

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Methods for manipulation, populating and editing the hierarchy tree of slots and containers.
     *
     * Below: Methods for device instance and attribute table manipulation.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * This method creates a new installation record for the device selected in the installation dialog. Before that
     * it checks whether a device slot and device are both selected, and that both are also uninstalled at the moment.
     * The checks are performed in the EJB.
     * This method creates a new installation record containing:
     * <ul>
     * <li>record number</li>
     * <li>install date</li>
     * <li>device</li>
     * <li>installation slot</li>
     * </ul>
     * The uninstall date is left empty (NULL).
     */
    public void installDevice() {
        final Date today = new Date();
        final InstallationRecord newRecord = new InstallationRecord(Long.toString(today.getTime()), today);
        newRecord.setDevice(deviceToInstall);
        newRecord.setSlot(selectedSlot);
        installationEJB.add(newRecord);

        deviceToInstall = null;
        addToInstalaltionRecordList(selectedSlot);
    }

    /** This method is called when a user presses the "Uninstall" button in the hierarchies view. */
    public void uninstallDevice() {
        Preconditions.checkNotNull(selectedInstallationRecord);
        selectedInstallationRecord.setUninstallDate(new Date());
        installationEJB.save(selectedInstallationRecord);
        // the device is not installed any more. Clear the installation state information.
        selectedInstallationRecord = null;
    }

    /** @return <code>true</code> if the attribute "Delete" button can be enabled, <code>false</code> otherwise */
    public boolean canDeleteAttribute() {
        if (selectedAttribute == null) {
            return false;
        }
        switch (selectedAttribute.getKind()) {
            case ARTIFACT:
            case CONTAINER_SLOT_ARTIFACT:
            case CONTAINER_SLOT_TAG:
            case CONTAINER_SLOT_PROPERTY:
            case INSTALL_SLOT_ARTIFACT:
            case INSTALL_SLOT_TAG:
                return true;
            default:
                return false;
        }
    }

    /** The handler called from the "Delete confirmation" dialog. This actually deletes an attribute */
    public void deleteAttribute() {
        Preconditions.checkNotNull(selectedAttribute);
        final Slot slot = slotEJB.findByName(selectedAttribute.getParent());
        switch (selectedAttribute.getKind()) {
            case INSTALL_SLOT_ARTIFACT:
            case CONTAINER_SLOT_ARTIFACT:
            case CONTAINER_SLOT_PROPERTY:
                slotEJB.deleteChild(selectedAttribute.getEntity());
                refreshSlot(slot);
                break;
            case INSTALL_SLOT_TAG:
            case CONTAINER_SLOT_TAG:
                slot.getTags().remove(selectedAttribute.getEntity());
                saveSlotAndRefresh(slot);
                break;
            default:
                throw new RuntimeException("Trying to delete an attribute that cannot be removed on home screen.");
        }
        attributes.remove(selectedAttribute);
        selectedAttribute = null;
    }

    /** @return <code>true</code> if the attribute "Edit" button can be enables, <code>false</code> otherwise */
    public boolean canEditAttribute() {
        if (selectedAttribute == null) {
            return false;
        }
        switch (selectedAttribute.getKind()) {
            case CONTAINER_SLOT_ARTIFACT:
            case CONTAINER_SLOT_PROPERTY:
            case INSTALL_SLOT_ARTIFACT:
            case INSTALL_SLOT_PROPERTY:
                return true;
            default:
                return false;
        }
    }

    /** Prepares the information for the "Edit" attribute dialog. */
    public void prepareModifyAttributePopup() {
        Preconditions.checkNotNull(selectedAttribute);
        if (selectedAttribute.getEntity() instanceof SlotPropertyValue) {
            prepareModifyPropertyValuePopup();
        } else if (selectedAttribute.getEntity() instanceof SlotArtifact) {
            prepareModifyArtifactPopup();
        } else {
            throw new UnhandledCaseException();
        }
    }

    /** Prepares a list of a devices that can still be installed into the selected installation slot */
    public void prepareUninstalledDevices() {
        uninstalledDevices = (selectedSlot == null) || !selectedSlot.isHostingSlot() ? null
                : installationEJB.getUninstalledDevices(selectedSlot.getComponentType());
        filteredUninstalledDevices = null;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Methods for device instance and attribute table manipulation.
     *
     * Below: Methods for adding and modifying container and installation slot property values.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /** Prepares data for addition of {@link PropertyValue}. Only valid for containers. */
    public void prepareForPropertyValueAdd() {
        propertyNameChangeDisabled = false;
        property = null;
        propertyValue = null;
        enumSelections = null;
        selectedAttribute = null;
        propertyValueUIElement = PropertyValueUIElement.NONE;
        filterProperties();
    }

    /** Adds new {@link PropertyValue} to container {@link Slot} */
    public void addNewPropertyValue() {
        try {
            final SlotPropertyValue slotValueInstance = new SlotPropertyValue(false);
            selectedAttribute = null;
            slotValueInstance.setProperty(property);
            slotValueInstance.setPropValue(propertyValue);
            slotValueInstance.setPropertiesParent(selectedSlot);

            slotEJB.addChild(slotValueInstance);

            refreshSlot(selectedSlot);
            refreshAllPropertyValues();

            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "New property has been created");
        } catch (EJBException e) {
            if (Utility.causedBySpecifiedExceptionClass(e, PropertyValueNotUniqueException.class)) {
                FacesContext.getCurrentInstance().addMessage("uniqueMessage",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                "Value is not unique."));
                FacesContext.getCurrentInstance().validationFailed();
            } else {
                throw e;
            }
        }
    }

    private void refreshAllPropertyValues() {
        final List<SlotPropertyValue> propertyValues = selectedSlot.getSlotPropertyList();
        for (SlotPropertyValue propertyValue : propertyValues) {
            refreshAttributeList(selectedSlot, propertyValue);
        }
    }

    /** The handler called to save a new value of the {@link SlotPropertyValue} after modification */
    public void modifyPropertyValue() {
        final SlotPropertyValue selectedPropertyValue = (SlotPropertyValue) selectedAttribute.getEntity();
        selectedPropertyValue.setProperty(property);
        selectedPropertyValue.setPropValue(propertyValue);

        try {
            slotEJB.saveChild(selectedPropertyValue);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                                                                        "Property value has been modified");
            final Slot parentSlot = selectedPropertyValue.getSlot();
            refreshSlot(parentSlot);
            final SlotPropertyValue freshPropertyValue = slotEJB.refreshPropertyValue(selectedPropertyValue);
            refreshAttributeList(parentSlot, freshPropertyValue);
        } catch (EJBException e) {
            if (Utility.causedBySpecifiedExceptionClass(e, PropertyValueNotUniqueException.class)) {
                FacesContext.getCurrentInstance().addMessage("uniqueMessage",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                "Value is not unique."));
                FacesContext.getCurrentInstance().validationFailed();
            } else {
                throw e;
            }
        }
    }

    private void prepareModifyPropertyValuePopup() {
        final SlotPropertyValue selectedPropertyValue = (SlotPropertyValue) selectedAttribute.getEntity();
        property = selectedPropertyValue.getProperty();
        propertyValue = selectedPropertyValue.getPropValue();
        propertyNameChangeDisabled = selectedSlot.isHostingSlot();
        propertyValueUIElement = Conversion.getUIElementFromProperty(property);

        if (Conversion.getBuiltInDataType(property.getDataType()) == BuiltInDataType.USER_DEFINED_ENUM) {
            // if it is an enumeration, get the list of its options from the data type definition field
            enumSelections = Conversion.prepareEnumSelections(property.getDataType());
        } else {
            enumSelections = null;
        }

        if (!propertyNameChangeDisabled) {
            filterProperties();
        }

        RequestContext.getCurrentInstance().update("modifyPropertyValueForm:modifyPropertyValue");
        RequestContext.getCurrentInstance().execute("PF('modifyPropertyValue').show();");
    }

    private void filterProperties() {
        final List<Property> propertyCandidates = propertyEJB.findAllOrderedByName();

        // remove all properties that are already defined.
        for (final SlotPropertyValue slotPropertyValue : selectedSlot.getSlotPropertyList()) {
            if (!slotPropertyValue.getProperty().equals(property)) {
                propertyCandidates.remove(slotPropertyValue.getProperty());
            }
        }

        filteredProperties = propertyCandidates;
    }

    /** Called when the user selects a new {@link Property} in the dialog drop-down control.
     * @param event {@link javax.faces.event.ValueChangeEvent}
     */
    public void propertyChangeListener(ValueChangeEvent event) {
        // get the newly selected property
        if (event.getNewValue() instanceof Property) {
            final Property newProperty = (Property) event.getNewValue();
            propertyValueUIElement = Conversion.getUIElementFromProperty(newProperty);
            propertyValue = null;
            if (Conversion.getBuiltInDataType(newProperty.getDataType()) == BuiltInDataType.USER_DEFINED_ENUM) {
                // if it is an enumeration, get the list of its options from the data type definition field
                enumSelections = Conversion.prepareEnumSelections(newProperty.getDataType());
            } else {
                enumSelections = null;
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Methods for adding and modifying container and installation slot property values.
     *
     * Below: Methods for adding and deleting tags.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /** Prepares the UI data for addition of {@link Tag} */
    public void prepareForTagAdd() {
        fillTagsAutocomplete();
        tag = null;
    }

    /** Adds new {@link Tag} to parent {@link ConfigurationEntity} */
    public void addNewTag() {
        selectedAttribute = null;
        final String normalizedTag = tag.trim();
        Tag existingTag = tagEJB.findById(normalizedTag);
        if (existingTag == null) {
            existingTag = new Tag(normalizedTag);
        }
        selectedSlot.getTags().add(existingTag);
        saveSlotAndRefresh(selectedSlot);
        refreshAttributeList(selectedSlot, existingTag);
        fillTagsAutocomplete();

        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Tag added", tag);
    }

    private void fillTagsAutocomplete() {
        tagsForAutocomplete = ImmutableList.copyOf(Lists.transform(tagEJB.findAllSorted(), new Function<Tag, String>() {

            @Override
            public String apply(Tag input) {
                return input.getName();
            }
        }));
    }

    /** Used by the {@link Tag} input value control to display the list of auto-complete suggestions. The list contains
     * the tags already stored in the database.
     * @param query The text the user typed so far.
     * @return The list of auto-complete suggestions.
     */
    public List<String> tagAutocompleteText(String query) {
        final List<String> resultList = new ArrayList<String>();
        final String queryUpperCase = query.toUpperCase();
        for (final String element : tagsForAutocomplete) {
            if (element.toUpperCase().startsWith(queryUpperCase))
                resultList.add(element);
        }
        return resultList;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Methods for adding and deleting tags.
     *
     * Below: Methods for adding, deleting and modifying artifacts.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /** Prepares the UI data for addition of {@link Artifact} */
    public void prepareForArtifactAdd() {
        artifactName = null;
        artifactDescription = null;
        isArtifactInternal = false;
        artifactURI = null;
        isArtifactBeingModified = false;
        importData = null;
        importFileName = null;
    }

    /**
     * Adds new {@link PropertyValue} to parent {@link ConfigurationEntity}
     * defined in {@link AbstractAttributesController#setArtifactParent(Artifact)}
     *
     * @throws IOException thrown if file in the artifact could not be stored on the file system
     */
    public void addNewArtifact() throws IOException {
        Preconditions.checkNotNull(selectedSlot);
        selectedAttribute = null;
        if (importData != null) {
            artifactURI = blobStore.storeFile(new ByteArrayInputStream(importData));
        }

        final SlotArtifact slotArtifact = new SlotArtifact(importData != null ? importFileName : artifactName,
                isArtifactInternal, artifactDescription, artifactURI);
        slotArtifact.setSlot(selectedSlot);

        slotEJB.addChild(slotArtifact);
        refreshSlot(selectedSlot);
        refreshAllArtifacts();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                                                                    "New artifact has been created");
    }

    private void refreshAllArtifacts() {
        final List<SlotArtifact> slotArtifacts = selectedSlot.getSlotArtifactList();
        for (final SlotArtifact artifact : slotArtifacts) {
            refreshAttributeList(selectedSlot, artifact);
        }
    }

    private void prepareModifyArtifactPopup() {
        final SlotArtifact selectedArtifact = (SlotArtifact) selectedAttribute.getEntity();
        if (selectedArtifact.isInternal()) {
            importFileName = selectedArtifact.getName();
            artifactName = null;
        } else {
            artifactName = selectedArtifact.getName();
            importFileName = null;
        }
        importData = null;
        artifactDescription = selectedArtifact.getDescription();
        isArtifactInternal = selectedArtifact.isInternal();
        artifactURI = selectedArtifact.getUri();
        isArtifactBeingModified = true;

        RequestContext.getCurrentInstance().update("modifyArtifactForm:modifyArtifact");
        RequestContext.getCurrentInstance().execute("PF('modifyArtifact').show();");
    }

    /** Modifies the selected artifact properties */
    public void modifyArtifact() {
        final SlotArtifact selectedArtifact = (SlotArtifact) selectedAttribute.getEntity();
        selectedArtifact.setDescription(artifactDescription);
        selectedArtifact.setUri(artifactURI);
        if (!selectedArtifact.isInternal()) {
            selectedArtifact.setName(artifactName);
        }

        slotEJB.saveChild(selectedArtifact);
        final Slot parentSlot = selectedArtifact.getSlot();
        refreshSlot(parentSlot);
        final SlotArtifact freshArtifact = slotEJB.refreshArtifact(selectedArtifact);
        refreshAttributeList(parentSlot, freshArtifact);
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                                                                    "Artifact has been modified");
    }

    /**
     * Uploads file to be saved in the {@link Artifact}
     * @param event the {@link FileUploadEvent}
     */
    @Override
    public void handleImportFileUpload(FileUploadEvent event) {
        if (event.getComponent().getClientId().equals("importSignalsForm:singleFileDLUploadCtl")) {
            // this handler is shared between AbstractExcelSingleFileImportUI and Artifact loading
            super.handleImportFileUpload(event);
        } else {
            try (InputStream inputStream = event.getFile().getInputstream()) {
                importData = ByteStreams.toByteArray(inputStream);
                importFileName = FilenameUtils.getName(event.getFile().getFileName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /** If user changes the type of the artifact, any previously uploaded file gets deleted */
    public void artifactTypeChanged() {
        importData = null;
        importFileName = null;
    }

    /**
     * Finds artifact file that was uploaded on the file system and returns it to be downloaded
     *
     * @return Artifact file to be downloaded
     * @throws FileNotFoundException Thrown if file was not found on file system
     */
    public StreamedContent getDownloadFile() throws FileNotFoundException {
        final Artifact selectedArtifact = (Artifact) selectedAttribute.getEntity();
        final String filePath = blobStore.getBlobStoreRoot() + File.separator + selectedArtifact.getUri();
        final String contentType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(filePath);

        return new DefaultStreamedContent(new FileInputStream(filePath), contentType, selectedArtifact.getName());
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Methods for adding, deleting and modifying artifacts.
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
        if (isInstallationSlot && isNewInstallationSlot && !slotEJB.isInstallationSlotNameUnique(value.toString())) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "The installation slot name must be unique."));
        }
    }

    /** The validator for the UI input field when UI control accepts a double precision number, and integer number or a
     * string for input.
     * Called when saving {@link PropertyValue}
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException {@link javax.faces.validator.ValidatorException}
     */
    public void inputValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Utility.MESSAGE_SUMMARY_ERROR, "No value to parse."));
        }

        if (property == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Utility.MESSAGE_SUMMARY_ERROR, "You must select a property first."));
        }

        final DataType dataType = property != null ? property.getDataType() : selectedAttribute.getType();
        validateSingleLine(value.toString(), dataType);
    }

    private void validateSingleLine(final String strValue, final DataType dataType) {
        switch (Conversion.getBuiltInDataType(dataType)) {
            case DOUBLE:
                try {
                    Double.parseDouble(strValue.trim());
                } catch (NumberFormatException e) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            Utility.MESSAGE_SUMMARY_ERROR, "Not a double value."));
                }
                break;
            case INTEGER:
                try {
                    Integer.parseInt(strValue.trim());
                } catch (NumberFormatException e) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            Utility.MESSAGE_SUMMARY_ERROR, "Not an integer number."));
                }
                break;
            case STRING:
                break;
            case TIMESTAMP:
                try {
                    Conversion.toTimestamp(strValue);
                } catch (RuntimeException e) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            Utility.MESSAGE_SUMMARY_ERROR, e.getMessage()), e);
                }
                break;
            default:
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        Utility.MESSAGE_SUMMARY_ERROR, "Incorrect property data type."));
        }
    }

    /** The validator for the UI input area when the UI control accepts a matrix of double precision numbers or a list
     * of values for input.
     * Called when saving {@link PropertyValue}
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException {@link javax.faces.validator.ValidatorException}
     */
    public void areaValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Utility.MESSAGE_SUMMARY_ERROR, "No value to parse."));
        }
        if (property == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    Utility.MESSAGE_SUMMARY_ERROR, "You must select a property first."));
        }

        final DataType dataType = property != null ? property.getDataType() : selectedAttribute.getType();
        validateMultiLine(value.toString(), dataType);
    }

    private void validateMultiLine(final String strValue, final DataType dataType) {
        switch (Conversion.getBuiltInDataType(dataType)) {
            case DBL_TABLE:
                validateTable(strValue);
                break;
            case DBL_VECTOR:
                validateDblVector(strValue);
                break;
            case INT_VECTOR:
                validateIntVector(strValue);
                break;
            case STRING_LIST:
                break;
            default:
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        Utility.MESSAGE_SUMMARY_ERROR, "Incorrect property data type."));
        }
    }

    private void validateTable(final String value) throws ValidatorException {
        try (final Scanner lineScanner = new Scanner(value)) {
            lineScanner.useDelimiter(Pattern.compile(MULTILINE_DELIMITER));

            int lineLength = -1;
            while (lineScanner.hasNext()) {
                // replace unicode no-break spaces with normal ones
                final String line = lineScanner.next().replaceAll("\u00A0", " ");

                try (Scanner valueScanner = new Scanner(line)) {
                    valueScanner.useDelimiter(",\\s*");
                    int currentLineLength = 0;
                    while (valueScanner.hasNext()) {
                        final String dblValue = valueScanner.next().trim();
                        currentLineLength++;
                        try {
                            Double.valueOf(dblValue);
                        } catch (NumberFormatException e) {
                            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    Utility.MESSAGE_SUMMARY_ERROR, "Incorrect value: " + dblValue));
                        }
                    }
                    if (lineLength < 0) {
                        lineLength = currentLineLength;
                    } else if (currentLineLength != lineLength) {
                        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                Utility.MESSAGE_SUMMARY_ERROR, "All rows must contain the same number of elements."));
                    }
                }
            }
        }
    }

    private void validateIntVector(final String value) throws ValidatorException {
        try (final Scanner scanner = new Scanner(value)) {
            scanner.useDelimiter(Pattern.compile(MULTILINE_DELIMITER));

            while (scanner.hasNext()) {
                String intValue = "<error>";
                try {
                    // replace unicode no-break spaces with normal ones
                    intValue = scanner.next().replaceAll("\\u00A0", " ").trim();
                    Integer.parseInt(intValue);
                } catch (NumberFormatException e) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            Utility.MESSAGE_SUMMARY_ERROR, "Incorrect value: " + intValue));
                }
            }
        }
    }

    private void validateDblVector(final String value) throws ValidatorException {
        try (final Scanner scanner = new Scanner(value)) {
            scanner.useDelimiter(Pattern.compile(MULTILINE_DELIMITER));

            while (scanner.hasNext()) {
                String dblValue = "<error>";
                try {
                    // replace unicode no-break spaces with normal ones
                    dblValue = scanner.next().replaceAll("\\u00A0", " ").trim();
                    Double.parseDouble(dblValue);
                } catch (NumberFormatException e) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            Utility.MESSAGE_SUMMARY_ERROR, "Incorrect value: " + dblValue));
                }
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Input field validators regardless of the dialog they are used in.
     *
     * Below: Relationships related methods
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * This method rebuilds the tree so that all displayed slots are refreshed. This is important for refreshing the
     * relationship status of all displayed slots. Otherwise the information can get out of synch with the database.
     */
    public void onRelationshipPopupClose() {
    }

    /**
     * Called when button to delete relationship is clicked
     */
    public void onRelationshipDelete() {
        if (canRelationshipBeDeleted()) {
            slotPairEJB.delete(selectedRelationship.getSlotPair());
            selectedRelationship = null;
        } else {
            RequestContext.getCurrentInstance().execute("PF('cantDeleteRelation').show();");
        }
        prepareAddRelationshipPopup();
    }

    private boolean canRelationshipBeDeleted() {
        return !(selectedRelationship.getSlotPair().getSlotRelation().getName() == SlotRelationName.CONTAINS
                && !slotPairEJB.slotHasMoreThanOneContainsRelation(selectedRelationship.getSlotPair().getChildSlot()));
    }

    /**
     * Prepares data for adding new relationship
     */
    public void prepareAddRelationshipPopup() {
        Preconditions.checkNotNull(selectedSlot);
        //initRelationshipList();
        // hide the current main selection, since the same data can be used to add new relationships.
        // Will be restored when the user finishes relationship manipulation.
        if (selectedTreeNodeForRelationshipAdd != null) {
            selectedTreeNodeForRelationshipAdd.setSelected(false);
        }
        selectedTreeNodeForRelationshipAdd = null;
        selectedRelationship = null;
        selectedRelationshipType = SlotRelationName.CONTAINS.toString();
    }

    /**
     * Called when slot to be in relationship selected from tree of installation slots is changed.
     * This method is needed to modify relationship types drop down menu so that if user selects
     * container slot the only relationship that can be created is "contained in".
     */
    public void slotForRelationshipChanged() {
        if (((SlotView)selectedTreeNodeForRelationshipAdd.getData()).isHostingSlot()) {
            relationshipTypesForDialog = ImmutableList.copyOf(slotRelationBySlotRelationStringName.keySet().iterator());
            if (selectedRelationshipType == null) {
                selectedRelationshipType = SlotRelationName.CONTAINS.toString();
            }
        } else {
            relationshipTypesForDialog = ImmutableList.of(SlotRelationName.CONTAINS.toString(),
                                                            SlotRelationName.CONTAINS.inverseName());
            selectedRelationshipType = SlotRelationName.CONTAINS.toString();
        }
    }

    /**
     * Called when user clicks add button to add new relationship. Relationship is added if this does not
     * cause a loop on CONTAINS relationships
     */
    public void onRelationshipAdd() {
        final SlotRelation slotRelation = slotRelationBySlotRelationStringName.get(selectedRelationshipType);
        final Slot parentSlot;
        final Slot childSlot;
        if (slotRelation.getNameAsString().equals(selectedRelationshipType)) {
            childSlot = ((SlotView) selectedTreeNodeForRelationshipAdd.getData()).getSlot();
            parentSlot = selectedSlot;
        } else {
            childSlot = selectedSlot;
            parentSlot = ((SlotView) selectedTreeNodeForRelationshipAdd.getData()).getSlot();
        }

        if (childSlot.equals(parentSlot)) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "The installation slot cannot be in relationship with itself.");
            onRelationshipPopupClose();
            return;
        }

        if (slotPairEJB.findSlotPairsByParentChildRelation(childSlot.getName(), parentSlot.getName(), slotRelation.getName()).isEmpty()) {
            final SlotPair newSlotPair = new SlotPair(childSlot, parentSlot, slotRelation);
            if (!slotPairEJB.slotPairCreatesLoop(newSlotPair, childSlot)) {
                slotPairEJB.add(newSlotPair);
            } else {
                RequestContext.getCurrentInstance().execute("PF('slotPairLoopNotification').show();");
            }
            prepareAddRelationshipPopup();
        } else {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "This relationship already exists.");
        }
        onRelationshipPopupClose();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Relationships related methods
     *
     * Below: Getters and setter all logically grouped based on where they are used. All getters and setters are
     *        usually called from the UI dialogs.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Main screen
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return The list of attributes (property values, artifacts and tags) for at all levels:
     * <ul>
     * <li>device type properties</li>
     * <li>container or installation slot properties</li>
     * </ul>
     */
    public List<EntityAttributeView> getAttributes() {
        return attributes;
    }
    public void setAttrbutes(List<EntityAttributeView> attributes) {
        this.attributes = attributes;
    }

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

    /** @return The list of relationships for the currently selected slot. */
    public List<SlotRelationshipView> getRelationships() {
        return relationships;
    }
    public void setRelationships(List<SlotRelationshipView> relationships) {
        this.relationships = relationships;
    }

    /** @return The root node of the <code>CONTAINS</code> hierarchy tree. */
    public TreeNode getRootNode() {
        return rootNode;
    }

    /** @return The root node of the <code>POWERS</code> hierarchy tree. */
    public TreeNode getPowersRoot() {
        return powersRootNode;
    }

    /** @return The root node of the <code>CONTROLS</code> hierarchy tree. */
    public TreeNode getControlsRoot() {
        return controlsRootNode;
    }

    public void setSelectedIncludesNodes(TreeNode[] nodes) {
        if (activeTab == ActiveTab.INCLUDES) {
            selectedNodes = nodes == null ? null : Lists.newArrayList(nodes);
        }
    }

    public TreeNode[] getSelectedIncludesNodes() {
        return ((selectedNodes == null) || (activeTab != ActiveTab.INCLUDES))
                ? null : selectedNodes.toArray(new TreeNode[] {});
    }

    public void setSelectedControlsNodes(TreeNode[] nodes) {
        if (activeTab == ActiveTab.CONTROLS) {
            selectedNodes = nodes == null ? null : Lists.newArrayList(nodes);
        }
    }

    public TreeNode[] getSelectedControlsNodes() {
        return ((selectedNodes == null) || (activeTab != ActiveTab.CONTROLS))
                ? null : selectedNodes.toArray(new TreeNode[] {});
    }

    public void setSelectedPowersNodes(TreeNode[] nodes) {
        if (activeTab == ActiveTab.POWERS) {
            selectedNodes = nodes == null ? null : Lists.newArrayList(nodes);
        }
    }

    public TreeNode[] getSelectedPowersNodes() {
        return ((selectedNodes == null) || (activeTab != ActiveTab.POWERS))
                ? null : selectedNodes.toArray(new TreeNode[] {});
    }


    /** @return <code>true</code> if the UI is currently showing the <code>INCLUDES</code> hierarchy */
    public boolean isIncludesActive() {
        return activeTab == ActiveTab.INCLUDES;
    }

    public boolean isSingleNodeSelected() {
        return (selectedNodes != null) && selectedNodes.size() == 1;
    }

    public boolean isMultipleNodesSelected() {
        return (selectedNodes != null) && selectedNodes.size() > 1;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Device instance installation
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return The device that is installed in the currently selected slot, <code>null</code> if no device is installed
     * or this is a <i>container</i> or no node is selected or there are multiple nodes selected.
     */
    public Device getInstalledDevice() {
        return selectedInstallationRecord == null ? null : selectedInstallationRecord.getDevice();
    }

    /**
     * @return The latest installation records associated with the selected installation slots, <code>null</code> if no
     * slots are selected.
     */
    public List<InstallationRecord> getInstallationRecords() {
        return installationRecords;
    }

    /** @return the installation record of the single selected installation {@link Slot}, <code>null</code> if
     * nothing is installed, nothing is selected or multiple slots are selected.
     */
    public InstallationRecord getSelectedInstallationRecord() {
        return selectedInstallationRecord;
    }

    /**
     * @return Based on the device type of the currently selected slot, this returns a list of all appropriate device
     * instances that are not installed.
     */
    public List<Device> getUninstalledDevices() {
        return uninstalledDevices;
    }
    public void setUninstalledDevices(List<Device> uninstalledDevices) {
        this.uninstalledDevices = uninstalledDevices;
    }

    /** @return the filteredUninstalledDevices */
    public List<Device> getFilteredUninstalledDevices() {
        return filteredUninstalledDevices;
    }

    /** @param filteredUninstalledDevices the filteredUninstalledDevices to set */
    public void setFilteredUninstalledDevices(List<Device> filteredUninstalledDevices) {
        this.filteredUninstalledDevices = filteredUninstalledDevices;
    }

    /** @return the deviceToInstall */
    public Device getDeviceToInstall() {
        return deviceToInstall;
    }
    /** @param deviceToInstall the deviceToInstall to set */
    public void setDeviceToInstall(Device deviceToInstall) {
        this.deviceToInstall = deviceToInstall;
    }

    /**
     * @return The path to the installation slot the user is currently installing a device into. Used in the
     * "Install device" dialog.
     */
    public String getInstallationSlotPath() {
        final String slotPath = Utility.buildSlotPath(selectedSlot).toString();
        return slotPath.substring(1, slotPath.length() - 1);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Attributes table
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** @return the filteredAttributes */
    public List<EntityAttributeView> getFilteredAttributes() {
        return filteredAttributes;
    }
    /** @param filteredAttributes the filteredAttributes to set */
    public void setFilteredAttributes(List<EntityAttributeView> filteredAttributes) {
        this.filteredAttributes = filteredAttributes;
    }

    public List<SelectItem> getAttributeKinds() {
        return attributeKinds;
    }

    /** @return the selectedAttribute */
    public EntityAttributeView getSelectedAttribute() {
        return selectedAttribute;
    }
    /** @param selectedAttribute the selectedAttribute to set */
    public void setSelectedAttribute(EntityAttributeView selectedAttribute) {
        this.selectedAttribute = selectedAttribute;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Relationships table
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** @return the {@link List} of relationship types to display in the filter drop down selection. */
    public List<SelectItem> getRelationshipTypes() {
        return relationshipTypes;
    }

    /** @return the filteredRelationships */
    public List<SlotRelationshipView> getFilteredRelationships() {
        return filteredRelationships;
    }
    /** @param filteredRelationships the filteredRelationships to set */
    public void setFilteredRelationships(List<SlotRelationshipView> filteredRelationships) {
        this.filteredRelationships = filteredRelationships;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Slot dialog
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** @return the name */
    public String getName() {
        return name;
    }
    /** @param name the name to set */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the description */
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
    public ComponentType getDeviceType() {
        return deviceType;
    }
    /** @param deviceType the deviceType to set */
    public void setDeviceType(ComponentType deviceType) {
        this.deviceType = deviceType;
    }

    /** @return the isNewInstallationSlot */
    public boolean isNewInstallationSlot() {
        return isNewInstallationSlot;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Property values
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** @return the propertyValueUIElement */
    public PropertyValueUIElement getPropertyValueUIElement() {
        return propertyValueUIElement;
    }

    /** @return the propertyValue */
    public String getPropertyValue() {
        return Conversion.valueToString(propertyValue);
    }
    /** @param propertyValue the propertyValue to set */
    public void setPropertyValue(String propertyValue) {
        final DataType dataType = selectedAttribute != null ? selectedAttribute.getType() : property.getDataType();
        this.propertyValue = Conversion.stringToValue(propertyValue, dataType);
    }

    /** @return the enumSelections */
    public List<String> getEnumSelections() {
        return enumSelections;
    }

    public boolean isPropertyNameChangeDisabled() {
        return propertyNameChangeDisabled;
    }

    /** @return the filteredProperties */
    public List<Property> getFilteredProperties() {
        return filteredProperties;
    }

    /** Called by the UI input control to set the value.
     * @param property The property
     */
    public void setProperty(Property property) {
        this.property = property;
    }
    /** @return The property associated with the property value */
    public Property getProperty() {
        return property;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Artifact dialog
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** @return the isArtifactInternal */
    public boolean isArtifactInternal() {
        return isArtifactInternal;
    }
    /** @param isArtifactInternal the isArtifactInternal to set */
    public void setArtifactInternal(boolean isArtifactInternal) {
        this.isArtifactInternal = isArtifactInternal;
    }

    /** @return the isArtifactBeingModified */
    public boolean isArtifactBeingModified() {
        return isArtifactBeingModified;
    }

    /** @return the artifactName */
    public String getArtifactName() {
        return artifactName;
    }
    /** @param artifactName the artifactName to set */
    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    /** @return the artifactDescription */
    public String getArtifactDescription() {
        return artifactDescription;
    }
    /** @param artifactDescription the artifactDescription to set */
    public void setArtifactDescription(String artifactDescription) {
        this.artifactDescription = artifactDescription;
    }

    /** @return the artifactURI */
    public String getArtifactURI() {
        return artifactURI;
    }
    /** @param artifactURI the artifactURI to set */
    public void setArtifactURI(String artifactURI) {
        this.artifactURI = artifactURI;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Artifact dialog
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** @return The value of the tag */
    public String getTag() {
        return tag;
    }
    /** Called by the UI input control to set the value.
     * @param tag The value of the tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Relationship manipulation
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** @return the selectedRelationship */
    public SlotRelationshipView getSelectedRelationship() {
        return selectedRelationship;
    }
    /** @param selectedRelationship the selectedRelationship to set */
    public void setSelectedRelationship(SlotRelationshipView selectedRelationship) {
        this.selectedRelationship = selectedRelationship;
    }

    /** @return the selectedRelationshipType */
    public String getSelectedRelationshipType() {
        return selectedRelationshipType;
    }
    /** @param selectedRelationshipType the selectedRelationshipType to set */
    public void setSelectedRelationshipType(String selectedRelationshipType) {
        this.selectedRelationshipType = selectedRelationshipType;
    }

    /** @return the relationshipTypesForDialog */
    public List<String> getRelationshipTypesForDialog() {
        return relationshipTypesForDialog;
    }
    /** @param relationshipTypesForDialog the relationshipTypesForDialog to set */
    public void setRelationshipTypesForDialog(List<String> relationshipTypesForDialog) {
        this.relationshipTypesForDialog = relationshipTypesForDialog;
    }

    /** @return the selectedTreeNodeForRelationshipAdd */
    public TreeNode getSelectedTreeNodeForRelationshipAdd() {
        return selectedTreeNodeForRelationshipAdd;
    }
    /** @param selectedTreeNodeForRelationshipAdd the selectedTreeNodeForRelationshipAdd to set */
    public void setSelectedTreeNodeForRelationshipAdd(TreeNode selectedTreeNodeForRelationshipAdd) {
        this.selectedTreeNodeForRelationshipAdd = selectedTreeNodeForRelationshipAdd;
    }
}
