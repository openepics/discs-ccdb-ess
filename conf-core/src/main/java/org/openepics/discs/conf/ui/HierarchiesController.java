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
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
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
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.DataTypeEJB;
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
import org.openepics.discs.conf.ent.PositionInformation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ent.values.DblValue;
import org.openepics.discs.conf.ent.values.StrValue;
import org.openepics.discs.conf.ent.values.Value;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.util.BlobStore;
import org.openepics.discs.conf.util.BuiltInDataType;
import org.openepics.discs.conf.util.Conversion;
import org.openepics.discs.conf.util.PropertyValueNotUniqueException;
import org.openepics.discs.conf.util.PropertyValueUIElement;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.util.names.Names;
import org.openepics.discs.conf.views.BuiltInProperty;
import org.openepics.discs.conf.views.BuiltInPropertyName;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.EntityAttributeViewKind;
import org.openepics.discs.conf.views.SlotBuiltInPropertyName;
import org.openepics.discs.conf.views.SlotRelationshipView;
import org.openepics.discs.conf.views.SlotView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultStreamedContent;
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
public class HierarchiesController implements Serializable {
    private static final long serialVersionUID = 2743408661782529373L;

    private static final Logger LOGGER = Logger.getLogger(HierarchiesController.class.getCanonicalName());
    private static final String MULTILINE_DELIMITER = "(\\r\\n)|\\r|\\n";

    @Inject private SlotsTreeBuilder slotsTreeBuilder;
    @Inject private transient SlotEJB slotEJB;
    @Inject private transient SlotPairEJB slotPairEJB;
    @Inject private transient InstallationEJB installationEJB;
    @Inject private transient DataTypeEJB dataTypeEJB;
    @Inject private transient SlotRelationEJB slotRelationEJB;
    @Inject private transient ComptypeEJB comptypeEJB;
    @Inject private transient PropertyEJB propertyEJB;
    @Inject private TagEJB tagEJB;
    @Inject private BlobStore blobStore;
    @Inject private Names names;

    private transient List<EntityAttributeView> attributes;
    private transient List<EntityAttributeView> filteredAttributes;
    private transient List<SelectItem> attributeKinds;
    private transient List<SlotRelationshipView> relationships;
    private transient List<SlotRelationshipView> filteredRelationships;
    private transient List<SelectItem> relationshipTypes;
    private List<Device> uninstalledDevices;
    private List<Device> filteredUninstalledDevices;
    private TreeNode rootNode;
    private TreeNode selectedNode;
    private InstallationRecord installationRecord;
    private Device deviceToInstall;
    private Slot selectedSlot;
    private DataType strDataType;
    private DataType dblDataType;

    // variables from the installation slot / containers editing merger.
    private transient Set<Long> collapsedNodes;
    private SlotView selectedSlotView;
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
    private BuiltInPropertyName builtInProperteryName;
    private String builtInPropertyDataType;
    private PropertyValueUIElement propertyValueUIElement;
    private boolean propertyNameChangeDisabled;
    private byte[] importData;
    private String importFileName;
    private String tag;
    private transient List<String> tagsForAutocomplete;

    private transient SlotRelationshipView selectedRelationship;
    private TreeNode selectedTreeNodeForRelationshipAdd;
    private String selectedRelationshipType;
    private List<String> relationshipTypesForDialog;
    private Map<String, SlotRelation> slotRelationBySlotRelationStringName;

    /** Java EE post construct life-cycle method. */
    @PostConstruct
    public void init() {
        try {
            updateRootNode();
            fillNamesAutocomplete();
            strDataType = dataTypeEJB.findByName(BuiltInDataType.STR_NAME);
            dblDataType = dataTypeEJB.findByName(BuiltInDataType.DBL_NAME);
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

    private void saveSlotAndRefresh() {
        slotEJB.save(selectedSlot);
        selectedSlot = slotEJB.findById(selectedSlot.getId());
        selectedSlotView.setSlot(selectedSlot);
    }

    private void refreshSlot() {
        selectedSlot = slotEJB.findById(selectedSlot.getId());
        selectedSlotView.setSlot(selectedSlot);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above : bean initialization section and global private utility methods.
     *
     * Below: Screen population methods. These methods prepare the data to be displayed on the main UI screen.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void initAttributeList() {
        final List<EntityAttributeView> attributesList = new ArrayList<>();

        if (selectedNode != null) {
            addBuiltInProperties(attributesList);
            addPropertyValues(attributesList);
            addArtifacts(attributesList);
            addTags(attributesList);
        }
        this.attributes = attributesList;
    }

    private void addBuiltInProperties(List<EntityAttributeView> attributesList) {
        final boolean isHostingSlot = selectedSlot.isHostingSlot();

        attributesList.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_DESCRIPTION,
                                        selectedSlot.getDescription(), strDataType)));
        if (isHostingSlot) {
            attributesList.add(new EntityAttributeView(
                                        new BuiltInProperty(SlotBuiltInPropertyName.BIP_BEAMLINE_POS,
                                                selectedSlot.getBeamlinePosition(), dblDataType)));
            final PositionInformation slotPosition = selectedSlot.getPositionInformation();
            attributesList.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_GLOBAL_X,
                                                                slotPosition.getGlobalX(), dblDataType)));
            attributesList.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_GLOBAL_Y,
                                                                slotPosition.getGlobalY(), dblDataType)));
            attributesList.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_GLOBAL_Z,
                                                                slotPosition.getGlobalZ(), dblDataType)));
            attributesList.add(new EntityAttributeView(
                                        new BuiltInProperty(SlotBuiltInPropertyName.BIP_GLOBAL_PITCH,
                                                slotPosition.getGlobalPitch(), dblDataType)));
            attributesList.add(new EntityAttributeView(
                                        new BuiltInProperty(SlotBuiltInPropertyName.BIP_GLOBAL_ROLL,
                                                slotPosition.getGlobalRoll(), dblDataType)));
            attributesList.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_GLOBAL_YAW,
                                                                slotPosition.getGlobalYaw(), dblDataType)));
        }
    }

    private void addPropertyValues(List<EntityAttributeView> attributesList) {
        final boolean isHostingSlot = selectedSlot.isHostingSlot();
        final Device installedDevice = getInstalledDevice();

        for (final ComptypePropertyValue value : selectedSlot.getComponentType().getComptypePropertyList()) {
            if (!value.isPropertyDefinition()) {
                attributesList.add(new EntityAttributeView(value, EntityAttributeViewKind.DEVICE_TYPE_PROPERTY));
            }
        }

        for (final SlotPropertyValue value : selectedSlot.getSlotPropertyList()) {
            attributesList.add(new EntityAttributeView(value, isHostingSlot
                                                            ? EntityAttributeViewKind.INSTALL_SLOT_PROPERTY
                                                            : EntityAttributeViewKind.CONTAINER_SLOT_PROPERTY));
        }

        if (installedDevice != null) {
            for (final DevicePropertyValue devicePropertyValue : installedDevice.getDevicePropertyList()) {
                attributesList.add(new EntityAttributeView(devicePropertyValue,
                                                            EntityAttributeViewKind.DEVICE_PROPERTY));
            }
        }
    }

    private void addArtifacts(List<EntityAttributeView> attributesList) {
        final boolean isHostingSlot = selectedSlot.isHostingSlot();
        final Device installedDevice = getInstalledDevice();

        for (final ComptypeArtifact artifact : selectedSlot.getComponentType().getComptypeArtifactList()) {
            attributesList.add(new EntityAttributeView(artifact, EntityAttributeViewKind.DEVICE_TYPE_ARTIFACT));
        }

        for (final SlotArtifact artifact : selectedSlot.getSlotArtifactList()) {
            attributesList.add(new EntityAttributeView(artifact, isHostingSlot
                                                            ? EntityAttributeViewKind.INSTALL_SLOT_ARTIFACT
                                                            : EntityAttributeViewKind.CONTAINER_SLOT_ARTIFACT));
        }

        if (installedDevice != null) {
            for (final DeviceArtifact deviceArtifact : installedDevice.getDeviceArtifactList()) {
                attributesList.add(new EntityAttributeView(deviceArtifact,
                                                            EntityAttributeViewKind.DEVICE_ARTIFACT));
            }
        }
    }

    private void addTags(List<EntityAttributeView> attributesList) {
        final boolean isHostingSlot = selectedSlot.isHostingSlot();
        final Device installedDevice = getInstalledDevice();

        for (final Tag tagInstance : selectedSlot.getComponentType().getTags()) {
            attributesList.add(new EntityAttributeView(tagInstance, EntityAttributeViewKind.DEVICE_TYPE_TAG));
        }

        for (final Tag tagInstance : selectedSlot.getTags()) {
            attributesList.add(new EntityAttributeView(tagInstance, isHostingSlot
                                                            ? EntityAttributeViewKind.INSTALL_SLOT_TAG
                                                            : EntityAttributeViewKind.CONTAINER_SLOT_TAG));
        }

        if (installedDevice != null) {
            for (final Tag tagInstance : installedDevice.getTags()) {
                attributesList.add(new EntityAttributeView(tagInstance, EntityAttributeViewKind.DEVICE_TAG));
            }
        }
    }

    private void initRelationshipList() {
        relationships = Lists.newArrayList();

        if (selectedNode != null) {
            final Slot rootSlot = slotEJB.getRootNode();
            final List<SlotPair> slotPairs = slotPairEJB.getSlotRelations(selectedSlot);

            for (final SlotPair slotPair : slotPairs) {
                if (!slotPair.getParentSlot().equals(rootSlot)) {
                    relationships.add(new SlotRelationshipView(slotPair, selectedSlot));
                }
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Screen population methods. These methods prepare the data to be displayed on the main UI screen.
     *
     * Below: Callback methods called from the main UI screen. E.g.: methods that are called when user user selects
     *        a line in a table.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /** Clears the attribute list for display when user deselects the slot in the hierarchy. */
    public void clearAttributeList() {
        attributes = null;
        filteredAttributes = null;
        relationships = null;
        filteredRelationships = null;
        installationRecord = null;
        selectedSlotView = null;
        selectedSlot = null;
        selectedNode = null;
    }

    /** With this method PrimeFaces sets the currently selected node. The method has two side effects:
     * <ul>
     * <li>it also sets the container or installation slot associated with the currently selected tree node</li>
     * <li>it also searches id there is an installation record associated with the currently selected tree node</li>
     * </ul>
     * @param selectedNode The PrimeFaces tree node to that is selected.
     */
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
        selectedSlotView = selectedNode == null ? null : (SlotView)selectedNode.getData();
        selectedSlot = selectedNode == null ? null : this.selectedSlotView.getSlot();
        installationRecord = selectedNode == null || !selectedSlot.isHostingSlot() ? null
                                                    : installationEJB.getActiveInstallationRecordForSlot(selectedSlot);
    }

    /** The function to select a different node in the TreeTable by clicking on the link in the relationship table.
     * @param id the ID of the slot we want to switch to.
     */
    public void selectNode(Long id) {
        Preconditions.checkNotNull(id);

        final TreeNode nodeToSelect = findNode(id, rootNode);
        if (nodeToSelect != null) {
            if (selectedNode != null) {
                selectedNode.setSelected(false);
            }
            nodeToSelect.setSelected(true);
            setSelectedNode(nodeToSelect);
            initSelectedItemLists();
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
    public void initSelectedItemLists() {
        initAttributeList();
        initRelationshipList();
        installationRecord = selectedNode == null || !selectedSlot.isHostingSlot() ? null
                : installationEJB.getActiveInstallationRecordForSlot(selectedSlot);
        selectedAttribute = null;
    }

    /**
     * Adds collapsed node to the set of collapsed nodes which is used to preserve the state of tree
     * throughout the nodes manipulation.
     *
     * @param event Event triggered on node collapse action
     */
    public void onNodeCollapse(NodeCollapseEvent event) {
        if (event != null && event.getTreeNode() != null) {
            if (collapsedNodes == null) {
                collapsedNodes = new HashSet<>();
            }
            collapsedNodes.add(((SlotView)event.getTreeNode().getData()).getId());
            event.getTreeNode().setExpanded(false);
        }
    }

    /**
     * Removes expanded node from list of collapsed nodes which is used to preserve the state of tree
     * throughout the nodes manipulation.
     *
     * @param event Event triggered on node expand action
     */
    public void onNodeExpand(NodeExpandEvent event) {
        if (event != null && event.getTreeNode() != null && collapsedNodes != null) {
            collapsedNodes.remove(((SlotView)event.getTreeNode().getData()).getId());
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
        Preconditions.checkNotNull(selectedNode);
        selectedSlotView = (SlotView) selectedNode.getData();
    }

    /** Deletes selected container */
    public void onSlotDelete() {
        if (!selectedSlotView.getIsHostingSlot()
                    || installationEJB.getActiveInstallationRecordForSlot(selectedSlotView.getSlot()) == null) {
            slotEJB.delete(selectedSlotView.getSlot());
            selectedSlotView = null;
            selectedNode = null;
            clearAttributeList();
            updateRootNode();
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Slot deleted", "Slot has been successfully deleted");
        } else {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_DELETE_FAIL,
                                "Installation slot could not be deleted because it has a device installed on it.");
        }
    }

    private void updateRootNode() {
        if (selectedSlotView != null) {
            selectedSlot = selectedSlotView.getSlot();
        } else {
            selectedSlot = null;
        }

        rootNode = slotsTreeBuilder.newSlotsTree(slotEJB.findAll(), selectedSlot, collapsedNodes, true);
        selectedNode = slotsTreeBuilder.getInitiallySelectedTreeNode();
        selectedSlotView = slotsTreeBuilder.getInitiallySelectedSlotView();
    }

    /** The action event to be called when the user presses the "move up" action button. This action moves the current
     * container/installation slot up one space, if that is possible.
     */
    public void moveSlotUp() {
        final TreeNode currentNode = selectedNode;
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
        final TreeNode currentNode = selectedNode;
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
        Preconditions.checkNotNull(selectedNode);
        isNewInstallationSlot = false;
        selectedSlotView = (SlotView) selectedNode.getData();
        isInstallationSlot = selectedSlotView.getIsHostingSlot();
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
        if (modifiedSlot.isHostingSlot() && installationRecord == null) {
            modifiedSlot.setComponentType(deviceType);
        }
        slotEJB.save(modifiedSlot);
        selectedSlotView.setSlot(slotEJB.findById(modifiedSlot.getId()));
        initAttributeList();
    }

    /** Called to add a new installation slot / container to the database */
    public void onSlotAdd() {
        final Slot newSlot = new Slot(name, isInstallationSlot);
        newSlot.setDescription(description);
        if (isInstallationSlot) {
            newSlot.setComponentType(deviceType);
        } else {
            newSlot.setComponentType(comptypeEJB.findByName(SlotEJB.GRP_COMPONENT_TYPE));
        }
        final Slot parentSlot = selectedNode != null ? ((SlotView) selectedNode.getData()).getSlot() : null;
        slotEJB.addSlotToParentWithPropertyDefs(newSlot, parentSlot, false);

        updateRootNode();
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
        installationRecord = installationEJB.getActiveInstallationRecordForSlot(selectedSlot);
    }

    /** This method is called when a user presses the "Uninstall" button in the hierarchies view.
     * @param device the device
     */
    public void uninstallDevice(Device device) {
        Preconditions.checkNotNull(device);
        final InstallationRecord deviceInstallationRecord =
                                                    installationEJB.getActiveInstallationRecordForDevice(device);
        if (deviceInstallationRecord == null) {
            LOGGER.log(Level.WARNING, "The device appears installed, but no active installation record for "
                    + "it could be retrieved. Device db ID: " + device.getId()
                    + ", serial number: " + device.getSerialNumber());
            throw new RuntimeException("No active installation record for the device exists.");
        }
        deviceInstallationRecord.setUninstallDate(new Date());
        installationEJB.save(deviceInstallationRecord);
        // the device is not installed any more. Clear the installation state information.
        this.installationRecord = null;
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
        Preconditions.checkNotNull(selectedSlot);
        switch (selectedAttribute.getKind()) {
            case INSTALL_SLOT_ARTIFACT:
            case CONTAINER_SLOT_ARTIFACT:
            case CONTAINER_SLOT_PROPERTY:
                slotEJB.deleteChild(selectedAttribute.getEntity());
                refreshSlot();
                break;
            case INSTALL_SLOT_TAG:
            case CONTAINER_SLOT_TAG:
                selectedSlot.getTags().remove(selectedAttribute.getEntity());
                saveSlotAndRefresh();
                break;
            default:
                throw new RuntimeException("Trying to delete an attribute that cannot be removed on home screen.");
        }
        selectedAttribute = null;
        initAttributeList();
    }

    /** @return <code>true</code> if the attribute "Edit" button can be enables, <code>false</code> otherwise */
    public boolean canEditAttribute() {
        if (selectedAttribute == null) {
            return false;
        }
        switch (selectedAttribute.getKind()) {
            case BUILT_IN_PROPERTY:
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
        } else if (selectedAttribute.getEntity() instanceof BuiltInProperty) {
            prepareModifyBuiltInPropertyPopup();
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
            slotValueInstance.setProperty(property);
            slotValueInstance.setPropValue(propertyValue);
            slotValueInstance.setPropertiesParent(selectedSlot);

            slotEJB.addChild(slotValueInstance);

            refreshSlot();
            selectedAttribute = null;
            initAttributeList();

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

    /** The handler called to save a new value of the {@link SlotPropertyValue} after modification */
    public void modifyPropertyValue() {
        final SlotPropertyValue selectedPropertyValue = (SlotPropertyValue) selectedAttribute.getEntity();
        selectedPropertyValue.setProperty(property);
        selectedPropertyValue.setPropValue(propertyValue);

        try {
            slotEJB.saveChild(selectedPropertyValue);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                                                                        "Property value has been modified");
            refreshSlot();
            initAttributeList();
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
        builtInProperteryName = null;
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
        final String normalizedTag = tag.trim();
        Tag existingTag = tagEJB.findById(normalizedTag);
        if (existingTag == null) {
            existingTag = new Tag(normalizedTag);
        }
        selectedSlot.getTags().add(existingTag);
        saveSlotAndRefresh();
        initAttributeList();
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
        if (importData != null) {
            artifactURI = blobStore.storeFile(new ByteArrayInputStream(importData));
        }

        final SlotArtifact slotArtifact = new SlotArtifact(importData != null ? importFileName : artifactName,
                isArtifactInternal, artifactDescription, artifactURI);
        slotArtifact.setSlot(selectedSlot);

        slotEJB.addChild(slotArtifact);
        refreshSlot();
        selectedAttribute = null;
        initAttributeList();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                                                                    "New artifact has been created");
    }

    private void prepareModifyArtifactPopup() {
        builtInProperteryName = null;
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
        refreshSlot();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                                                                    "Artifact has been modified");
        initAttributeList();
    }

    /**
     * Uploads file to be saved in the {@link Artifact}
     * @param event the {@link FileUploadEvent}
     */
    public void handleImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.importFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException(e);
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
     * Below: Methods for modifying built-in properties.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void prepareModifyBuiltInPropertyPopup() {
        property = null;
        enumSelections = null;
        final BuiltInProperty builtInProperty = (BuiltInProperty) selectedAttribute.getEntity();
        builtInProperteryName = builtInProperty.getName();
        builtInPropertyDataType = builtInProperty.getDataType().getName();

        final BuiltInDataType propertyDataType = Conversion.getBuiltInDataType(builtInProperty.getDataType());
        propertyValueUIElement = Conversion.getUIElementFromBuiltInDataType(propertyDataType);

        propertyValue = builtInProperty.getValue();
        propertyNameChangeDisabled = true;

        RequestContext.getCurrentInstance().update("modifyBuiltInPropertyForm:modifyBuiltInProperty");
        RequestContext.getCurrentInstance().execute("PF('modifyBuiltInProperty').show();");
    }

    /** The handler called to save the new value of the built-in property */
    public void modifyBuiltInProperty() {
        Preconditions.checkNotNull(selectedAttribute);
        Preconditions.checkNotNull(selectedSlot);

        final BuiltInProperty builtInProperty = (BuiltInProperty) selectedAttribute.getEntity();
        final SlotBuiltInPropertyName builtInPropertyName = (SlotBuiltInPropertyName)builtInProperty.getName();

        final String userValueStr = propertyValue == null ? null
                        : (propertyValue instanceof StrValue ? ((StrValue)propertyValue).getStrValue() : null);
        final Double userValueDbl = propertyValue == null ? null
                        : (propertyValue instanceof DblValue ? ((DblValue)propertyValue).getDblValue() : null);
        switch (builtInPropertyName) {
            case BIP_DESCRIPTION:
                if ((userValueStr == null) || !userValueStr.equals(selectedSlot.getDescription())) {
                    selectedSlot.setDescription(userValueStr);
                    saveSlotAndRefresh();
                }
                break;
            case BIP_BEAMLINE_POS:
                if ((userValueDbl == null) || !userValueDbl.equals(selectedSlot.getBeamlinePosition())) {
                    selectedSlot.setBeamlinePosition(userValueDbl);
                    saveSlotAndRefresh();
                }
                break;
            case BIP_GLOBAL_X:
                if ((userValueDbl == null) || !userValueDbl.equals(selectedSlot.getPositionInformation().getGlobalX())) {
                    selectedSlot.getPositionInformation().setGlobalX(userValueDbl);
                    saveSlotAndRefresh();
                }
                break;
            case BIP_GLOBAL_Y:
                if ((userValueDbl == null) || !userValueDbl.equals(selectedSlot.getPositionInformation().getGlobalY())) {
                    selectedSlot.getPositionInformation().setGlobalY(userValueDbl);
                    saveSlotAndRefresh();
                }
                break;
            case BIP_GLOBAL_Z:
                if ((userValueDbl != null) && !userValueDbl.equals(selectedSlot.getPositionInformation().getGlobalZ())) {
                    selectedSlot.getPositionInformation().setGlobalZ(userValueDbl);
                    saveSlotAndRefresh();
                }
                break;
            case BIP_GLOBAL_PITCH:
                if ((userValueDbl != null) && !userValueDbl.equals(selectedSlot.getPositionInformation().getGlobalPitch())) {
                    selectedSlot.getPositionInformation().setGlobalPitch(userValueDbl);
                    saveSlotAndRefresh();
                }
                break;
            case BIP_GLOBAL_ROLL:
                if ((userValueDbl != null) && !userValueDbl.equals(selectedSlot.getPositionInformation().getGlobalRoll())) {
                    selectedSlot.getPositionInformation().setGlobalRoll(userValueDbl);
                    saveSlotAndRefresh();
                }
                break;
            case BIP_GLOBAL_YAW:
                if ((userValueDbl != null) && !userValueDbl.equals(selectedSlot.getPositionInformation().getGlobalYaw())) {
                    selectedSlot.getPositionInformation().setGlobalYaw(userValueDbl);
                    saveSlotAndRefresh();
                }
                break;
            default:
                throw new UnhandledCaseException();
        }
        initAttributeList();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Methods for modifying built-in properties.
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
        if (isInstallationSlot && !slotEJB.isInstallationSlotNameUnique(value.toString())) {
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

        if (property == null && builtInProperteryName == null) {
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
        if (property == null && builtInProperteryName == null) {
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
     * Prepares a list of relationships to display to the user.
     */
    public void prepareRelationshipsPopup() {
        Preconditions.checkNotNull(selectedSlot);
        selectedRelationship = null;
        initRelationshipList();
    }

    /**
     * This method rebuilds the tree so that all displayed slots are refreshed. This is important for refreshing the
     * relationship status of all displayed slots. Otherwise the information can get out of synch with the database.
     */
    public void onRelationshipPopupClose() {
        // restore the selection state in the main hierarchy tree
        if (selectedNode != null) {
            selectedNode.setSelected(true);
        }
        updateRootNode();
    }

    /**
     * Called when button to delete relationship is clicked
     */
    public void onRelationshipDelete() {
        if (canRelationshipBeDeleted()) {
            slotPairEJB.delete(selectedRelationship.getSlotPair());
        } else {
            RequestContext.getCurrentInstance().execute("PF('cantDeleteRelation').show();");
        }
        prepareRelationshipsPopup();
    }

    private boolean canRelationshipBeDeleted() {
        return !(selectedRelationship.getSlotPair().getSlotRelation().getName() == SlotRelationName.CONTAINS
                && !slotPairEJB.slotHasMoreThanOneContainsRelation(selectedRelationship.getSlotPair().getChildSlot()));
    }

    /**
     * Prepares data for adding new relationship
     */
    public void prepareAddRelationshipPopup() {
        // hide the current main selection, since the same data can be used to add new relationships.
        // Will be restored when the user finishes relationship manipulation.
        if (selectedNode != null) {
            selectedNode.setSelected(false);
        }
        if (selectedTreeNodeForRelationshipAdd != null) {
            selectedTreeNodeForRelationshipAdd.setSelected(false);
        }
        selectedTreeNodeForRelationshipAdd = null;
        selectedRelationshipType = SlotRelationName.CONTAINS.toString();
    }

    /**
     * Called when slot to be in relationship selected from tree of installation slots is changed.
     * This method is needed to modify relationship types drop down menu so that if user selects
     * container slot the only relationship that can be created is "contained in".
     */
    public void slotForRelationshipChanged() {
        if (((SlotView)selectedTreeNodeForRelationshipAdd.getData()).getIsHostingSlot()) {
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
            return;
        }

        if (slotPairEJB.findSlotPairsByParentChildRelation(childSlot.getName(), parentSlot.getName(), slotRelation.getName()).isEmpty()) {
            final SlotPair newSlotPair = new SlotPair(childSlot, parentSlot, slotRelation);
            if (!slotPairEJB.slotPairCreatesLoop(newSlotPair, childSlot)) {
                slotPairEJB.add(newSlotPair);
            } else {
                RequestContext.getCurrentInstance().execute("PF('slotPairLoopNotification').show();");
            }
            prepareRelationshipsPopup();
        } else {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                    "This relationship already exists.");
        }
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

    /** @return The list of relationships for the currently selected slot. */
    public List<SlotRelationshipView> getRelationships() {
        return relationships;
    }
    public void setRelationships(List<SlotRelationshipView> relationships) {
        this.relationships = relationships;
    }

    /** @return The root node of (and consequently the entire) hierarchy tree. */
    public TreeNode getRootNode() {
        return rootNode;
    }

    /** @return Getter for the currently selected node in a tree (required by PrimeFaces). */
    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Device instance installation
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return The device that is installed in the currently selected slot, <code>null</code> if no device is installed
     * or this is a <i>container</i> or no node is selected.
     */
    public Device getInstalledDevice() {
        return installationRecord == null ? null : installationRecord.getDevice();
    }

    /**
     * @return The latest installation record associated with the selected installation slot, <code>null</code> if a
     * container is selected.
     */
    public InstallationRecord getInstallationRecord() {
        return installationRecord;
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
    // Built-in and normal property values
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** @return the builtInProperteryName */
    public BuiltInPropertyName getBuiltInProperteryName() {
        return builtInProperteryName;
    }

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

    /** @return the builtInPropertyDataType */
    public String getBuiltInPropertyDataType() {
        return builtInPropertyDataType;
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

    /** @return the importFileName */
    public String getImportFileName() {
        return importFileName;
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
