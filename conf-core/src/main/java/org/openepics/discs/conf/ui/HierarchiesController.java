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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import joptsimple.internal.Strings;

import org.openepics.discs.conf.dl.annotations.SignalsLoader;
import org.openepics.discs.conf.dl.annotations.SlotsLoader;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.ejb.SlotRelationEJB;
import org.openepics.discs.conf.ejb.TagEJB;
import org.openepics.discs.conf.ent.Artifact;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.PropertyValueUniqueness;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.conf.ui.common.ConnectsHierarchyBuilder;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.EntityHierarchyBuilder;
import org.openepics.discs.conf.ui.common.HierarchyBuilder;
import org.openepics.discs.conf.ui.common.TreeFilterContains;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.util.AppProperties;
import org.openepics.discs.conf.util.BlobStore;
import org.openepics.discs.conf.util.ConnectsManager;
import org.openepics.discs.conf.util.PropertyValueNotUniqueException;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.util.names.Names;
import org.openepics.discs.conf.views.EntityAttrArtifactView;
import org.openepics.discs.conf.views.EntityAttrPropertyValueView;
import org.openepics.discs.conf.views.EntityAttrTagView;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.EntityAttributeViewKind;
import org.openepics.discs.conf.views.InstallationView;
import org.openepics.discs.conf.views.SlotRelationshipView;
import org.openepics.discs.conf.views.SlotView;
import org.openepics.names.jaxb.DeviceNameElement;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
@Named
@ViewScoped
public class HierarchiesController extends AbstractExcelSingleFileImportUI implements Serializable {
    private static final long       serialVersionUID = 2743408661782529373L;

    private static final String     CANNOT_PASTE_INTO_ROOT =
                                                "The following installation slots cannot be made hierarchy roots:";
    private static final String     CANNOT_PASTE_INTO_SLOT =
            "The following containers cannot become children of installation slot:";
    private static final String     CANNOT_PASTE_INTO_SELF =
            "The following containers cannot become children of themselves:";
    private static final int        PRELOAD_LIMIT = 3;
    /** The device page part of the URL containing all the required parameters already. */
    private static final String     NAMING_DEVICE_PAGE = "devices.xhtml?i=2&deviceName=";

    private static final String CABLEDB_DEVICE_PAGE = "index.xhtml?cableNumber=";

    @Inject private transient SlotEJB slotEJB;
    @Inject private transient SlotPairEJB slotPairEJB;
    @Inject private transient InstallationEJB installationEJB;
    @Inject private transient SlotRelationEJB slotRelationEJB;
    @Inject private transient ComptypeEJB comptypeEJB;
    @Inject private transient PropertyEJB propertyEJB;
    @Inject private transient TagEJB tagEJB;
    @Inject private transient BlobStore blobStore;
    @Inject private Names names;
    @Inject private transient ConnectsManager connectsManager;

    @Inject private transient DataLoaderHandler dataLoaderHandler;
    @Inject @SignalsLoader private transient DataLoader signalsDataLoader;
    @Inject @SlotsLoader private transient DataLoader slotsDataLoader;

    @Inject private transient AppProperties properties;

    private enum ActiveTab {
        INCLUDES, POWERS, CONTROLS, CONNECTS,
    }

    private enum ClipboardOperations {
        COPY, CUT
    }

    private enum NamingStatus {
        ACTIVE, OBSOLETE, DELETED, MISSING
    }

    private transient List<EntityAttributeView<Slot>> attributes;
    private transient List<EntityAttributeView<Slot>> filteredAttributes;
    private EntityAttributeView<Slot> dialogAttribute;
    private transient List<SelectItem> attributeKinds;
    private transient List<SlotRelationshipView> relationships;
    private transient List<SlotRelationshipView> filteredRelationships;
    private transient List<SelectItem> relationshipTypes;
    private transient HashSet<Long> selectedNodeIds;
    private transient HashSet<Long> displayedAttributeNodeIds;
    private transient List<Device> uninstalledDevices;
    private transient List<Device> filteredUninstalledDevices;
    private transient List<InstallationView> installationRecords;
    private transient List<InstallationView> filteredInstallationRecords;
    private transient List<InstallationView> selectedInstallationViews;
    private Device deviceToInstall;
    private String requestedSlot;

    // ---- variables for hierarchies and tabs --------
    private transient EntityHierarchyBuilder hierarchyBuilder;
    private transient EntityHierarchyBuilder powersHierarchyBuilder;
    private transient EntityHierarchyBuilder controlsHierarchyBuilder;
    private transient ConnectsHierarchyBuilder connectsHierarchyBuilder;
    private TreeNode rootNode;
    private TreeNode powersRootNode;
    private TreeNode controlsRootNode;
    private TreeNode connectsRootNode;
    private List<TreeNode> powersChildren;
    private List<TreeNode> controlsChildren;
    private List<TreeNode> connectsChildren;

    private transient List<TreeNode> selectedNodes;
    private transient List<TreeNode> savedIncludesSelectedNodes;
    /** <code>selectedSlot</code> is only initialized when there is only one node in the tree selected */
    private Slot selectedSlot;
    /** <code>selectedSlotView</code> is only initialized when there is only one node in the tree selected */
    private transient SlotView selectedSlotView;
    private ActiveTab activeTab;
    private transient List<SlotView> clipboardSlots;
    private transient List<SlotView> pasteErrors;
    private ClipboardOperations clipboardOperation;
    private String pasteErrorReason;
    private transient List<TreeNode> nodesToDelete;
    private transient List<SlotView> slotsToDelete;
    private transient List<SlotView> filteredSlotsToDelete;
    private boolean detectNamingStatus;
    private boolean restrictToConventionNames;

    // variables from the installation slot / containers editing merger.
    private String name;
    private String description;
    /** Used in "add child to parent" operations. This usually reflects the <code>selectedNode</code>. */
    private boolean isInstallationSlot;
    private boolean hasDevice;
    private Long deviceType;
    private String parentName;
    private transient List<String> namesForAutoComplete;
    private boolean isNewInstallationSlot;
    private transient Map<String, DeviceNameElement> nameList;

    // ------ variables for attribute manipulation ------
    private transient List<EntityAttributeView<Slot>> selectedAttributes;
    private transient EntityAttributeView<Slot> downloadAttribute;
    private transient List<Property> filteredProperties;
    private transient List<String> tagsForAutocomplete;

    private transient List<SlotRelationshipView> selectedRelationships;

    private SlotRelationshipView editedRelationshipView;
    private List<String> relationshipTypesForDialog;
    private Map<String, SlotRelation> slotRelationBySlotRelationStringName;


    private String filterContainsTree;
    private String filterControlsTree;
    private String filterPowersTree;
    private String filterConnectsTree;

    private String namingRedirectionUrl;
    private String cableRedirectionUrl;

    private SlotView linkSlot;

    /** Java EE post construct life-cycle method. */
    @Override
    @PostConstruct
    public void init() {
        try {
            super.init();
            activeTab = ActiveTab.INCLUDES;

            initHierarchies();
            initNamingInformation();
            attributeKinds = Utility.buildAttributeKinds();
            relationshipTypes = buildRelationshipTypeList();

            navigateToUrlSelectedSlot();
        } catch (Exception e) {
            throw new UIException("Hierarchies display initialization fialed: " + e.getMessage(), e);
        }
    }

    private List<SelectItem> buildRelationshipTypeList() {
        Builder<SelectItem> immutableListBuilder = ImmutableList.builder();
        immutableListBuilder.add(new SelectItem("", "Select one"));

        final List<SlotRelation> slotRelations = slotRelationEJB.findAll();
        slotRelations.sort(new Comparator<SlotRelation>() {
            @Override
            public int compare(SlotRelation o1, SlotRelation o2) {
                return o1.getNameAsString().compareTo(o2.getNameAsString());
            }});
        slotRelationBySlotRelationStringName = new LinkedHashMap<>();
        for (final SlotRelation slotRelation : slotRelations) {
            immutableListBuilder.add(new SelectItem(slotRelation.getNameAsString(), slotRelation.getNameAsString()));
            immutableListBuilder.add(new SelectItem(slotRelation.getIname(), slotRelation.getIname()));
            slotRelationBySlotRelationStringName.put(slotRelation.getNameAsString(), slotRelation);
            slotRelationBySlotRelationStringName.put(slotRelation.getIname(), slotRelation);
        }
        if (getCableDBStatus()) {
            immutableListBuilder.add(new SelectItem(connectsManager.getRelationshipName(),
                    connectsManager.getRelationshipName()));
        }

        relationshipTypesForDialog = ImmutableList.copyOf(slotRelationBySlotRelationStringName.keySet().iterator());
        return immutableListBuilder.build();
    }

    private void initNamingInformation() {
        final String namingStatus = properties.getProperty(AppProperties.NAMING_DETECT_STATUS);
        detectNamingStatus = namingStatus == null ? false : "TRUE".equals(namingStatus.toUpperCase());

        final String restrictNames = properties.getProperty(AppProperties.RESTRICT_TO_CONVENTION_NAMES);
        restrictToConventionNames = restrictNames == null || !detectNamingStatus
                                            ? false
                                            : "TRUE".equals(restrictNames.toUpperCase());

        nameList = detectNamingStatus ? names.getAllNames() : new HashMap<>();
        namesForAutoComplete = ImmutableList.copyOf(nameList.keySet());

        final String namingUrl = properties.getProperty(AppProperties.NAMING_APPLICATION_URL);

        if (Strings.isNullOrEmpty(namingUrl)) {
            namingRedirectionUrl = null;
        } else {
            final StringBuilder redirectionUrl = new StringBuilder(namingUrl);
            if (redirectionUrl.charAt(redirectionUrl.length() - 1) != '/') {
                redirectionUrl.append('/');
            }
            redirectionUrl.append(NAMING_DEVICE_PAGE);
            namingRedirectionUrl = redirectionUrl.toString();
            System.out.println("Naming url: " + namingRedirectionUrl);
        }
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
    }

    private void updateTreesWithFreshSlot(final Slot freshSlot, boolean rebuildAffectedSlots) {
        updateTreeWithFreshSlot(rootNode, freshSlot, rebuildAffectedSlots);
        updateTreeWithFreshSlot(controlsRootNode, freshSlot, rebuildAffectedSlots);
        updateTreeWithFreshSlot(powersRootNode, freshSlot, rebuildAffectedSlots);
    }

    private void updateTreeWithFreshSlot(final TreeNode node, final Slot freshSlot, boolean rebuildAffectedSlots) {
        final SlotView nodeSlotView = (SlotView) node.getData();
        if (freshSlot.getId().equals(nodeSlotView.getId())) {
            nodeSlotView.setSlot(freshSlot);
            if (rebuildAffectedSlots) {
                hierarchyBuilder.rebuildSubTree(node);
            }
        } else {
            for (final TreeNode nodeChild : node.getChildren()) {
                updateTreeWithFreshSlot(nodeChild, freshSlot, rebuildAffectedSlots);
            }
        }
    }

    private void unselectAllTreeNodes() {
        if (selectedNodes != null) {
            for (final TreeNode node : selectedNodes) {
                node.setSelected(false);
            }
        }
    }

    private void navigateToUrlSelectedSlot() {
        // navigate to slot based on ID or name
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
        if (namingRedirectionUrl == null) return false;
        if (!detectNamingStatus) return false;
        if (slot == null) return false;
        return NamingStatus.ACTIVE.equals(getNamingStatus(slot.getName()));
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above : bean initialization section and global private utility methods.
     *
     * Below: Screen population methods. These methods prepare the data to be displayed on the main UI screen.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void initAttributeList(final Slot slot, final boolean forceInit) {
        if (forceInit || attributes == null) {
            attributes = Lists.newArrayList();
        }
        addPropertyValues(slot);
        addArtifacts(slot);
        addTags(slot);
    }

    private void refreshAttributeList(final Slot slot, final SlotPropertyValue propertyValue) {
        // Use iterator. If the property value is found, then update it.
        // If not, add new property value to the already existing ones. Append to the end of the ones for the same slot.
        boolean encounteredParentSiblings = false;
        ListIterator<EntityAttributeView<Slot>> attributesIter = attributes.listIterator();
        while (attributesIter.hasNext()) {
            final EntityAttributeView<Slot> tableAttribute = attributesIter.next();
            if (tableAttribute.getParent().equals(slot.getName())) {
                encounteredParentSiblings = true;
                // the entity's real sibling

                if (tableAttribute.getEntity().equals(propertyValue)) {
                    // found the existing artifact, update it and exit!
                    attributesIter.set(new EntityAttrPropertyValueView<Slot>(propertyValue, slot));
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
        attributesIter.add(new EntityAttrPropertyValueView<Slot>(propertyValue, slot));
    }

    private void refreshAttributeList(final Slot slot, final Tag tag) {
        // Use iterator. Add new Tag to the already existing ones. Append to the end of the ones for the same slot.
        boolean encounteredParentSiblings = false;
        ListIterator<EntityAttributeView<Slot>> attributesIter = attributes.listIterator();
        while (attributesIter.hasNext()) {
            final EntityAttributeView<Slot> tableAttribute = attributesIter.next();
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
        attributesIter.add(new EntityAttrTagView<Slot>(tag, slot));
    }

    private void refreshAttributeList(final Slot slot, final SlotArtifact artifact) {
        // Use iterator. If the artifact is found, then update it.
        // If not, add new artifact to the already existing ones. Append to the end of the ones for the same slot.
        boolean encounteredParentSiblings = false;
        ListIterator<EntityAttributeView<Slot>> attributesIter = attributes.listIterator();
        while (attributesIter.hasNext()) {
            final EntityAttributeView<Slot> tableAttribute = attributesIter.next();
            if (tableAttribute.getParent().equals(slot.getName())) {
                encounteredParentSiblings = true;
                // the entity's real sibling

                if (tableAttribute.getEntity().equals(artifact)) {
                    // found the existing artifact, update it and exit!
                    attributesIter.set(new EntityAttrArtifactView<Slot>(artifact, slot));
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
        attributesIter.add(new EntityAttrArtifactView<Slot>(artifact, slot));
    }

    private void removeRelatedAttributes(Slot slot) {
        final ListIterator<EntityAttributeView<Slot>> slotAttributes = attributes.listIterator();
        while (slotAttributes.hasNext()) {
            final EntityAttributeView<Slot> attribute = slotAttributes.next();
            if (slot.getName().equals(attribute.getParent())) {
                slotAttributes.remove();
            }
        }
    }

    private void addPropertyValues(final Slot slot) {
        final InstallationRecord activeInstallationRecord = installationEJB.getActiveInstallationRecordForSlot(slot);

        for (final ComptypePropertyValue value : slot.getComponentType().getComptypePropertyList()) {
            if (!value.isPropertyDefinition()) {
                attributes.add(new EntityAttrPropertyValueView<Slot>(value, slot, slot.getComponentType()));
            }
        }

        for (final SlotPropertyValue value : slot.getSlotPropertyList()) {
            attributes.add(new EntityAttrPropertyValueView<Slot>(value, slot));
        }

        if (activeInstallationRecord != null) {
            for (final DevicePropertyValue devicePropertyValue : activeInstallationRecord.getDevice().
                                                                                        getDevicePropertyList()) {
                attributes.add(new EntityAttrPropertyValueView<Slot>(devicePropertyValue,
                                                            EntityAttributeViewKind.DEVICE_PROPERTY,
                                                            slot,
                                                            activeInstallationRecord.getDevice()));
            }
        }
    }

    private void addArtifacts(final Slot slot) {
        final InstallationRecord activeInstallationRecord = installationEJB.getActiveInstallationRecordForSlot(slot);

        for (final ComptypeArtifact artifact : slot.getComponentType().getComptypeArtifactList()) {
            attributes.add(new EntityAttrArtifactView<Slot>(artifact, slot, slot.getComponentType()));
        }

        for (final SlotArtifact artifact : slot.getSlotArtifactList()) {
            attributes.add(new EntityAttrArtifactView<Slot>(artifact, slot));
        }

        if (activeInstallationRecord != null) {
            for (final DeviceArtifact deviceArtifact : activeInstallationRecord.getDevice().getDeviceArtifactList()) {
                attributes.add(new EntityAttrArtifactView<Slot>(deviceArtifact, slot, activeInstallationRecord.getDevice()));
            }
        }
    }

    private void addTags(final Slot slot) {
        final InstallationRecord activeInstallationRecord = installationEJB.getActiveInstallationRecordForSlot(slot);

        for (final Tag tagInstance : slot.getComponentType().getTags()) {
            attributes.add(new EntityAttrTagView<Slot>(tagInstance, slot, slot.getComponentType()));
        }

        for (final Tag tagInstance : slot.getTags()) {
            attributes.add(new EntityAttrTagView<Slot>(tagInstance, slot));
        }

        if (activeInstallationRecord != null) {
            for (final Tag tagInstance : activeInstallationRecord.getDevice().getTags()) {
                attributes.add(new EntityAttrTagView<Slot>(tagInstance, slot, activeInstallationRecord.getDevice()));
            }
        }
    }

    private void initRelationshipList(final Slot slot, final boolean forceInit) {
        if (forceInit || relationships == null) {
            relationships = Lists.newArrayList();
        }
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

        final List<Slot> connectedSlots = connectsManager.getSlotConnects(slot);
        for (final Slot targetSlot : connectedSlots) {
            relationships.add(new SlotRelationshipView(slot.getId()+"c"+targetSlot.getId(), slot, targetSlot,
                    connectsManager.getRelationshipName()));
        }
    }

    private void removeRelatedRelationships(final Slot slot) {
        final ListIterator<SlotRelationshipView> relationsList = relationships.listIterator();
        while (relationsList.hasNext()) {
            final SlotRelationshipView relationshipView = relationsList.next();
            if (slot.getName().equals(relationshipView.getSourceSlotName())) {
                relationsList.remove();
            }
        }
    }

    private void initInstallationRecordList(final Slot slot, final boolean forceInit) {
        if (forceInit || installationRecords == null) {
            installationRecords = Lists.newArrayList();
        }
        addToInstallationRecordList(slot);
    }

    private void addToInstallationRecordList(final Slot slot) {
        if (slot.isHostingSlot()) {
            final InstallationRecord record = installationEJB.getActiveInstallationRecordForSlot(slot);
            installationRecords.add(new InstallationView(slot, record));
        }
    }

    private void removeRelatedInstallationRecord(final Slot slot) {
        final ListIterator<InstallationView> recordsIterator = installationRecords.listIterator();
        while (recordsIterator.hasNext()) {
            final InstallationView record = recordsIterator.next();
            if (slot.equals(record.getSlot())) {
                recordsIterator.remove();
                break;
            }
        }
    }

    private void updateDisplayedSlotInformation() {
        selectedSlotView = null;
        selectedSlot = null;
        if (Utility.isNullOrEmpty(selectedNodes)) {
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

            if (selectedNodes.size() == 1) {
                selectSingleNode(selectedNodes.get(0));
            }
        }
    }

    /** Remove attributes, relationships and installation information for slots no longer selected */
    private void removeUnselectedRelatedInformation() {
        for (final Iterator<Long> iter = displayedAttributeNodeIds.iterator(); iter.hasNext(); ) {
            final Long id = iter.next();
            if (!selectedNodeIds.contains(id)) {
                final Slot unselectedSlot = slotEJB.findById(id);
                removeRelatedAttributes(unselectedSlot);
                removeRelatedRelationships(unselectedSlot);
                removeRelatedInstallationRecord(unselectedSlot);
                iter.remove();
                if (selectedAttributes != null) {
                    Iterator<EntityAttributeView<Slot>> i = selectedAttributes.iterator();
                    while (i.hasNext()) {
                        EntityAttributeView<Slot> selectedAttribute = i.next();
                        if (selectedAttribute.getParent().equals(unselectedSlot.getName())) i.remove();
                    }
                }
                if (selectedRelationships!= null) {
                    Iterator<SlotRelationshipView> i = selectedRelationships.iterator();
                    while (i.hasNext()) {
                        SlotRelationshipView selectedRelationship = i.next();
                        if (selectedRelationship.getSourceSlotName().equals(unselectedSlot.getName())) i.remove();
                    }
                }
                if (selectedInstallationViews!= null) {
                    Iterator<InstallationView> i = selectedInstallationViews.iterator();
                    while (i.hasNext()) {
                        InstallationView selectedInstallationView = i.next();
                        if (selectedInstallationView.getSlot().getId().equals(id)) i.remove();
                    }
                }
            }
        }
    }

    /** Add attributes, relationships and installation information for slots that are missing */
    private void addRelatedInformationForNewSlots() {
        for (final Long selectedId : selectedNodeIds) {
            if (!displayedAttributeNodeIds.contains(selectedId)) {
                // this slot doesn't have information in the related tables yet
                final Slot slotToAdd = slotEJB.findById(selectedId);
                initAttributeList(slotToAdd, false);
                initRelationshipList(slotToAdd, false);
                initInstallationRecordList(slotToAdd, false);
                displayedAttributeNodeIds.add(selectedId);
            }
        }
    }

    private void initNodeIds() {
        selectedNodeIds = new HashSet<Long>();
        for (final TreeNode node : selectedNodes) {
            selectedNodeIds.add(((SlotView) node.getData()).getId());
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Screen population methods. These methods prepare the data to be displayed on the main UI screen.
     *
     * Below: Callback methods called from the main UI screen. E.g.: methods that are called when user user selects
     *        a line in a table.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /** Clears all slot related information when user deselects the slots in the hierarchy. */
    private void clearRelatedInformation() {
        attributes = null;
        filteredAttributes = null;
        relationships = null;
        filteredRelationships = null;
        installationRecords = null;
        selectedSlotView = null;
        selectedSlot = null;
        selectedAttributes = null;
        selectedRelationships = null;
        selectedInstallationViews = null;
        selectedNodeIds = null;
        displayedAttributeNodeIds = null;
    }

    private void selectSingleNode(final TreeNode selectedNode) {
        selectedSlotView = (SlotView) selectedNode.getData();
        selectedSlot = selectedSlotView.getSlot();
    }

    /** The function to select a different node in the TreeTable by clicking on the link in the relationship table.
     * @param slot the slot we want to switch to
     */
    public void selectNode(final Slot slot) {
        TreeNode node = findNode(slot);


     // the final slot found
        unselectAllTreeNodes();
        clearRelatedInformation();
        fakeUISelection(node);

    }

    public TreeNode findNode(final Slot slot) {
        Preconditions.checkNotNull(slot);

        TreeNode node = rootNode;
        final List<Slot> pathToRoot = getPathToRoot(slot);
        final ListIterator<Slot> pathIterator = pathToRoot.listIterator(pathToRoot.size());
        // we're not interested in the root node. Skip it.
        pathIterator.previous();
        while (pathIterator.hasPrevious()) {
            final Slot soughtSlot = pathIterator.previous();
            boolean soughtChildFound = false;
            for (TreeNode child : node.getChildren()) {
                final SlotView slotView = (SlotView) child.getData();
                if (slotView.getSlot().equals(soughtSlot)) {
                    // the sought TreeNode found. Process it.
                    soughtChildFound = true;
                    node = child;
                    hierarchyBuilder.expandNode(node);
                    if (!node.isLeaf()) {
                        node.setExpanded(true);
                    }
                    break;
                }
            }
            if (!soughtChildFound) {
                // the tree does not contain a slot in the path
                throw new IllegalStateException("Slot " + ((SlotView)node.getData()).getName() +
                        " does not CONTAINS slot " + soughtSlot.getName());
            }
        }
        return node;

    }

    private void fakeUISelection(final TreeNode node) {
        selectedNodes = Lists.newArrayList();
        selectedNodes.add(node);
        node.setSelected(true);
        updateDisplayedSlotInformation();
    }

    /** The method generates the paćth from the requested node to the root of the contains hierarchy. If an element has
     * multiple parents, this method always chooses the first parent it encounters.
     * @param slotOnPath the slot to find the path for
     * @return the path from requested node (first element) to the root of the hierarchy (last element).
     */
    private List<Slot> getPathToRoot(Slot slot) {
        final List<Slot> path = Lists.newArrayList();
        final Slot rootSlot = slotEJB.getRootNode();
        Slot slotOnPath = slot;

        path.add(slotOnPath);

        while (!rootSlot.equals(slotOnPath)) {
            final List<SlotPair> parents = slotOnPath.getPairsInWhichThisSlotIsAChildList();
            boolean containsParentFound = false;
            for (final SlotPair pair : parents) {
                if (pair.getSlotRelation().getName() == SlotRelationName.CONTAINS) {
                    containsParentFound = true;
                    slotOnPath = pair.getParentSlot();
                    path.add(slotOnPath);
                    break;
                }
            }
            if (!containsParentFound) {
                throw new IllegalStateException("Slot " + slotOnPath.getName() + " does not have a CONTAINS parent.");
            }
        }
        return path;
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

    /**
     * Builds the part of the tree under the expanded node if that is necessary.
     *
     * @param event Event triggered on node expand action
     */
    public void onConnectsExpand(NodeExpandEvent event) {
        final TreeNode expandedNode = event.getTreeNode();
        if (expandedNode != null) {
            connectsHierarchyBuilder.expandNode(expandedNode);
        }
    }

    /**
     * The event is triggered when the hierarchy tab is changed.
     * @param event the event
     */
    public void onTabChange(TabChangeEvent event) {
        ActiveTab newActiveTab = ActiveTab.valueOf(event.getTab().getId());


        if (activeTab == ActiveTab.INCLUDES) {
            savedIncludesSelectedNodes = selectedNodes;
        }

        final List<TreeNode> masterNodes = savedIncludesSelectedNodes != null && savedIncludesSelectedNodes.size() > 0
                ? savedIncludesSelectedNodes : Arrays.asList(rootNode);

        if (activeTab == ActiveTab.INCLUDES) {
            // we need to clear the filter temporarily
            hierarchyBuilder.setFilterValue(null);
            hierarchyBuilder.applyFilter(rootNode, new ArrayList<>(rootNode.getChildren()));
            // we need to expand the nodes virtually for the following searches
            for (TreeNode n : masterNodes) {
                expandOrCollapseNode(n, true, hierarchyBuilder, false);
            }
        }

        switch (newActiveTab) {
            case POWERS:
                powersHierarchyBuilder.setFilterValue(null);
                filterPowersTree = null;
                powersChildren = powersHierarchyBuilder.initHierarchy(masterNodes, powersRootNode);
            break;
            case CONTROLS:
                controlsHierarchyBuilder.setFilterValue(null);
                filterControlsTree = null;
                controlsChildren = controlsHierarchyBuilder.initHierarchy(masterNodes, controlsRootNode); break;
            case CONNECTS:
                connectsHierarchyBuilder.setFilterValue(null);
                filterConnectsTree = null;
                connectsChildren = connectsHierarchyBuilder.initHierarchy(masterNodes, connectsRootNode); break;
            default:break;
        }

        activeTab = newActiveTab;
        unselectAllTreeNodes();
        selectedNodes = null;
        clearRelatedInformation();

        if (newActiveTab == ActiveTab.INCLUDES) {
            hierarchyBuilder.setFilterValue(filterContainsTree);
            hierarchyBuilder.applyFilter(rootNode, new ArrayList<>(rootNode.getChildren()));

            selectedNodes = new ArrayList<TreeNode>();
            reselectNodes(rootNode, savedIncludesSelectedNodes);
            savedIncludesSelectedNodes = null;

            onNodeSelect(null);
        }
    }

    /**
     * On each filter event new TreeNodes are created. This functions takes care to select
     * old nodes from selectedNodes in the new tree with root root.
     */
    private void reselectNodes(TreeNode root, List<TreeNode> selectedNodes) {
        if (selectedNodes == null) return;
        for (TreeNode node : selectedNodes) {
            TreeNode newNode = findNode(root, node);
            if (newNode != null) {
                newNode.setSelected(true);
                this.selectedNodes.add(newNode);
            }

        }
    }

    /**
     * Finds a new version of old "node" in the new tree with "root".
     * @param root
     * @param node
     * @return
     */
    private TreeNode findNode(TreeNode root, TreeNode node) {
        if (node.getParent() == null) {
            return root;
        }

        final TreeNode searchNode = findNode(root, node.getParent());
        if (searchNode == null) return null;

        Long searchId = ((SlotView)node.getData()).getId();
        for (TreeNode child : searchNode.getChildren()) {
            Long childId = ((SlotView)child.getData()).getId();
            if (childId.equals(searchId)) {
                return child;
            }
        }
        return null;
    }

    private void removeTreeData()
    {
        // remove other trees
        ((SlotView)controlsRootNode.getData()).setInitialzed(false);
        controlsRootNode.getChildren().clear();
        ((SlotView)powersRootNode.getData()).setInitialzed(false);
        powersRootNode.getChildren().clear();
        ((SlotView)connectsRootNode.getData()).setInitialzed(false);
        connectsRootNode.getChildren().clear();
    }

    @Override
    public void setDataLoader() {
        dataLoader = signalsDataLoader;
    }

    @Override
    public void doImport() {
        try (InputStream inputStream = new ByteArrayInputStream(importData)) {
            setLoaderResult(dataLoaderHandler.loadData(inputStream, dataLoader));
            selectedNodes = null;
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

    public void expandTreeNodes()
    {
        final HierarchyBuilder hb;
        final TreeNode root;
        switch (activeTab) {
            case CONTROLS:
                hb = controlsHierarchyBuilder;
                root = controlsRootNode;
                break;
            case POWERS:
                hb = powersHierarchyBuilder;
                root = powersRootNode;
                break;
            case CONNECTS:
                hb = connectsHierarchyBuilder;
                root = connectsRootNode;
                break;
            case INCLUDES:
            default:
                hb = hierarchyBuilder;
                root = rootNode;
                break;
        }

        if (selectedNodes == null) {
            expandOrCollapseNode(root, true, hb, true);
        } else {
            for (final TreeNode node : selectedNodes) {
                expandOrCollapseNode(node, true, hb, true);
            }
        }
    }

    public void collapseTreeNodes()
    {
        final TreeNode root;
        switch (activeTab) {
            case CONTROLS:
                root = controlsRootNode;
                break;
            case POWERS:
                root = powersRootNode;
                break;
            case CONNECTS:
                root = connectsRootNode;
                break;
            case INCLUDES:
            default:
                root = rootNode;
                break;
        }

        if (selectedNodes == null) {
            expandOrCollapseNode(root, false, null, true);
        } else {
            for (final TreeNode node : selectedNodes) {
                expandOrCollapseNode(node, false, null, true);
            }
        }
    }

    private void expandOrCollapseNode(final TreeNode root, final boolean expand, final HierarchyBuilder hb,
            final boolean show)
    {
        if (expand) {
            if (!((SlotView)root.getData()).isInitialzed()) {
                hb.expandNode(root);
                root.setExpanded(show);
            } else {
                if (show) {
                    root.setExpanded(true);
                }
            }
        } else {
            if (show) {
                root.setExpanded(false);
            }
        }
        for (final TreeNode node : root.getChildren()) {
            expandOrCollapseNode(node, expand, hb, show);
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
        Preconditions.checkNotNull(selectedNodes);

        nodesToDelete = Lists.newArrayList();
        for (final TreeNode nodeToDelete : selectedNodes) {
            addSlotToDeleteWithChildren(nodeToDelete);
        }
        slotsToDelete = nodesToDelete.stream().map(node -> (SlotView) node.getData())
                            .collect(Collectors.toList());
    }

    private void addSlotToDeleteWithChildren(final TreeNode nodeToDelete) {
        if (!nodesToDelete.contains(nodeToDelete)) {
            nodesToDelete.add(nodeToDelete);
        }
        // make sure that the tree children are properly initialized.
        if (!((SlotView)nodeToDelete.getData()).isInitialzed()) {
            hierarchyBuilder.expandNode(nodeToDelete);
        }
        for (final TreeNode child : nodeToDelete.getChildren()) {
            addSlotToDeleteWithChildren(child);
        }
    }

    /** Deletes selected container */
    public void onSlotsDelete() {
        Preconditions.checkNotNull(nodesToDelete);
        Preconditions.checkState(!nodesToDelete.isEmpty());

        final int numSlotsToDelete = nodesToDelete.size();
        final List<TreeNode> parentRefreshList = Lists.newArrayList();
        while (!nodesToDelete.isEmpty()) {
            removeDeletedFromClipboard();
            deleteWithChildren(nodesToDelete.get(0), parentRefreshList);
        }
        for (final TreeNode refreshNode : parentRefreshList) {
            updateTreesWithFreshSlot(((SlotView) refreshNode.getData()).getSlot(), true);
        }
        Utility.showMessage(FacesMessage.SEVERITY_INFO, "Slots deleted", Integer.toString(numSlotsToDelete)
                                            + " slots have been successfully deleted");
        selectedNodes = null;
        nodesToDelete = null;
        clearRelatedInformation();
    }

    private void removeDeletedFromClipboard() {
        if (!Utility.isNullOrEmpty(clipboardSlots)) {
            for (final TreeNode deleteCandidate : nodesToDelete) {
                clipboardSlots.remove(deleteCandidate.getData());
            }
        }
    }

    private void deleteWithChildren(final TreeNode node, final List<TreeNode> parentRefreshList) {
        while (!node.getChildren().isEmpty()) {
            deleteWithChildren(node.getChildren().get(0), parentRefreshList);
        }
        final TreeNode parentTreeNode = node.getParent();
        final SlotView slotViewToDelete = (SlotView) node.getData();
        final Slot slotToDelete = slotViewToDelete.getSlot();
        // uninstall device if one is installed
        final Device deviceToUninstall = slotViewToDelete.getInstalledDevice();
        if (deviceToUninstall != null) {
            final InstallationRecord deviceRecord = installationEJB.getActiveInstallationRecordForDevice(deviceToUninstall);
            deviceRecord.setUninstallDate(new Date());
            installationEJB.save(deviceRecord);
        }
        slotEJB.delete(slotToDelete);
        // update UI data as well
        parentTreeNode.getChildren().remove(node);
        if (parentTreeNode.getChildCount() == 0) {
            ((SlotView) parentTreeNode.getData()).setDeletable(true);
        }
        nodesToDelete.remove(node);
        // the parent needs to be refreshed
        if (!parentRefreshList.contains(parentTreeNode)) {
            parentRefreshList.add(parentTreeNode);
        }
        // maybe this node had some children, and it was added previously
        parentRefreshList.remove(node);
    }

    public boolean isNodesDeletable() {
        if (Utility.isNullOrEmpty(selectedNodes)) {
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

    private void initHierarchies() {
        hierarchyBuilder = new EntityHierarchyBuilder(PRELOAD_LIMIT, installationEJB, slotEJB);
        hierarchyBuilder.setFilterMethod(new TreeFilterContains());
        rootNode = new DefaultTreeNode(new SlotView(slotEJB.getRootNode(), null, 1, slotEJB), null);
        hierarchyBuilder.rebuildSubTree(rootNode);

        // for POWERS and CONTROLS hierarchies, the trees will be rebuild dynamically based on user selection
        powersHierarchyBuilder = new EntityHierarchyBuilder(PRELOAD_LIMIT, installationEJB, slotEJB);
        powersHierarchyBuilder.setRelationship(SlotRelationName.POWERS);
        powersHierarchyBuilder.setFilterMethod(new TreeFilterContains());
        // initializing root node prevents NPE in initial page display
        powersRootNode = new DefaultTreeNode(new SlotView(slotEJB.getRootNode(), null, 1, slotEJB), null);

        controlsHierarchyBuilder = new EntityHierarchyBuilder(PRELOAD_LIMIT, installationEJB, slotEJB);
        controlsHierarchyBuilder.setRelationship(SlotRelationName.CONTROLS);
        controlsHierarchyBuilder.setFilterMethod(new TreeFilterContains());
        // initializing root node prevents NPE in initial page display
        controlsRootNode = new DefaultTreeNode(new SlotView(slotEJB.getRootNode(), null, 1, slotEJB), null);

        connectsHierarchyBuilder = new ConnectsHierarchyBuilder(connectsManager, slotEJB);
        connectsHierarchyBuilder.setFilterMethod(new TreeFilterContains());
        // initializing root node prevents NPE in initial page display
        connectsRootNode = new DefaultTreeNode(new SlotView(slotEJB.getRootNode(), null, 1, slotEJB), null);
    }

    /**
     * @return cableDBStatus
     */
    public boolean getCableDBStatus()
    {
        return connectsManager.getCableDBStatus();
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
        deviceType = selectedSlotView.getSlot().getComponentType().getId();
        parentName = selectedSlotView.getParentNode().getParentNode() == null ? "" : selectedSlotView.getParentNode().getName();
        hasDevice = installationEJB.getActiveInstallationRecordForSlot(selectedSlotView.getSlot()) != null;
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
        parentName = getSelectedNodeSlot() == null ? "" : getSelectedNodeSlot().getName();
        hasDevice = false;
    }

    /** Called to save modified installation slot / container information */
    public void onSlotModify() {
        final Slot modifiedSlot = selectedSlotView.getSlot();
        modifiedSlot.setName(name);
        modifiedSlot.setDescription(description);
        if (modifiedSlot.isHostingSlot() && installationEJB.getActiveInstallationRecordForSlot(modifiedSlot) == null) {
            modifiedSlot.setComponentType(comptypeEJB.findById(deviceType));
        }
        slotEJB.save(modifiedSlot);
        selectedSlotView.setSlot(modifiedSlot);
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS, "Slot has been modified.");
        clearRelatedInformation();
        updateDisplayedSlotInformation();
    }

    /** Called to add a new installation slot / container to the database */
    public void onSlotAdd() {
        Preconditions.checkState(selectedNodes == null || selectedNodes.size() == 1);
        final Slot newSlot = new Slot(name, isInstallationSlot);
        newSlot.setDescription(description);
        if (isInstallationSlot) {
            newSlot.setComponentType(comptypeEJB.findById(deviceType));
        } else {
            newSlot.setComponentType(comptypeEJB.findByName(SlotEJB.GRP_COMPONENT_TYPE));
        }
        final TreeNode parentNode = selectedNodes != null ? selectedNodes.get(0) : rootNode;
        final Slot parentSlot = selectedNodes != null ? ((SlotView) parentNode.getData()).getSlot() : null;
        slotEJB.addSlotToParentWithPropertyDefs(newSlot, parentSlot, false);

        // first update the back-end data
        final SlotView slotViewToUpdate = (SlotView) parentNode.getData();
        if (selectedSlotView != null) {
            selectedSlotView = slotViewToUpdate;
        }

        // now update the front-end data as well.
        if (slotViewToUpdate.isInitialzed()) {
            final List<SlotPair> containsRelation = slotPairEJB.findSlotPairsByParentChildRelation(newSlot.getName(),
                    slotViewToUpdate.getName(), SlotRelationName.CONTAINS);
            final SlotPair newRelation = containsRelation.get(0);
            final SlotView slotViewToAdd = new SlotView(newSlot, slotViewToUpdate, newRelation.getSlotOrder(), slotEJB);
            hierarchyBuilder.addChildToParent(parentNode, slotViewToAdd);
        } else {
            hierarchyBuilder.rebuildSubTree(parentNode);
        }
        slotViewToUpdate.setDeletable(false);
        parentNode.setExpanded(true);
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS, "Slot has been successfully created");
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
        Preconditions.checkState(!selectedNodes.isEmpty());

        clipboardOperation = ClipboardOperations.CUT;
        putSelectedNodesOntoClipboard();
    }

    /**
     * This method places the currently selected tree nodes into the clipboard and marks for copying.
     */
    public void copyTreeNodes() {
        Preconditions.checkState(isIncludesActive());
        Preconditions.checkState(!selectedNodes.isEmpty());

        clipboardOperation = ClipboardOperations.COPY;
        putSelectedNodesOntoClipboard();
    }

    public ClipboardOperations getCliboardOperation() {
        return clipboardOperation;
    }

    /**
     * This method tells whether a parent of the {@link TreeNode} is in the clipboard.
     * @param node the {@link TreeNode} to check for
     * @return <code>true</code> if the node's parent is in the clipboard, <code>false</code> otherwise
     */
    public boolean isAncestorNodeInClipboard(final TreeNode node) {
        if (Utility.isNullOrEmpty(clipboardSlots) || (node == null) || node.equals(rootNode)) {
            return false;
        }
        if (clipboardSlots.contains(node.getData())) {
            return true;
        } else {
            return isAncestorNodeInClipboard(node.getParent());
        }
    }

    private void putSelectedNodesOntoClipboard() {
        clipboardSlots = new ArrayList<>();

        // 2. We put the selected nodes into the clipboard
        for (final TreeNode node : selectedNodes) {
            clipboardSlots.add((SlotView)node.getData());
        }

        // 3. We remove all the nodes that have their parents in the clipboard
        for (final Iterator<SlotView> nodesIterator = clipboardSlots.iterator(); nodesIterator.hasNext();) {
            final SlotView removalCandidate = nodesIterator.next();
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
    private boolean isNodeDescendant(final SlotView possibleAscendant, final SlotView candidate) {
        return isAncestorNodeInList(Arrays.asList(possibleAscendant), candidate);
    }

    private boolean isAncestorNodeInList(final List<SlotView> candidates, final SlotView node) {
        SlotView parentNode = node.getParentNode();
        while (parentNode != null) {
            if (candidates.contains(parentNode)) {
                return true;
            }
            parentNode = parentNode.getParentNode();
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
        Preconditions.checkState(selectedNodes == null || selectedNodes.size() < 2);

        final boolean makeRoots = Utility.isNullOrEmpty(selectedNodes);
        final boolean isTargetInstallationslot = !makeRoots && selectedSlot.isHostingSlot();
        if (makeRoots) {
            pasteErrorReason = CANNOT_PASTE_INTO_ROOT;
        } else {
            pasteErrorReason = CANNOT_PASTE_INTO_SLOT;
        }

        pasteErrors = Lists.newArrayList();
        for (final SlotView slotView : clipboardSlots) {
            if (makeRoots && slotView.isHostingSlot()) {
                pasteErrors.add(slotView);
            } else if (isTargetInstallationslot && !slotView.isHostingSlot()) {
                pasteErrors.add(slotView);
            }
        }

        if (pasteErrors.size() == 0 && selectedSlot != null) {
            pasteErrorReason = CANNOT_PASTE_INTO_SELF;
            TreeNode current = selectedNodes.get(0);
            while (current != null) {
                for (final SlotView slotView : clipboardSlots) {
                    if (slotView.equals(current.getData())) {
                        pasteErrors.add(slotView);
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
        Preconditions.checkState(selectedNodes == null || selectedNodes.size() < 2);
        Preconditions.checkNotNull(pasteErrors);
        Preconditions.checkState(pasteErrors.isEmpty());

        if (clipboardOperation == ClipboardOperations.CUT) {
            moveSlotsToNewParent();
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Slots were moved.");
        } else {
            final TreeNode parentNode = (selectedNodes != null) && (!selectedNodes.isEmpty())
                                            ? selectedNodes.get(0)
                                            : rootNode;
            copySlotsToParent(clipboardSlots.stream().map(SlotView::getSlot).collect(Collectors.toList()), parentNode);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Slots were copied.");
        }
        filterContainsTree();
    }

    private void moveSlotsToNewParent() {
        final TreeNode newParent = Utility.isNullOrEmpty(selectedNodes) ? rootNode : selectedNodes.get(0);
        final SlotView parentSlotView = (SlotView) newParent.getData();

        // remove the nodes that do not get moved or are moved to their own descendant
        final List<SlotPair> moveCandidatesByRelationship = clipboardSlots.stream().
                                        filter(e -> !(e.getParentNode().equals(parentSlotView)
                                                || isNodeDescendant(e, parentSlotView))).
                                        map(SlotView::getParentRelationship).collect(Collectors.toList());
        slotPairEJB.moveSlotsToNewParent(moveCandidatesByRelationship, parentSlotView.getSlot());

        clipboardSlots = null;
        // Refresh the information about the affected slots in all the hierarchy trees
        if (!newParent.equals(rootNode)) {
            newParent.setExpanded(true);
        }
    }

    private void copySlotsToParent(final List<Slot> sourceSlots, final TreeNode parentNode) {
        for (final Slot sourceSlot : sourceSlots) {
            // initialize subtree if required

            final Slot newCopy = createSlotCopy(sourceSlot, parentNode);

            addAttributesToNewCopy(slotEJB.findById(newCopy.getId()), sourceSlot);

            List<Slot> children = sourceSlot.getPairsInWhichThisSlotIsAParentList().stream()
                .filter(sp -> sp.getSlotRelation().getName().equals(SlotRelationName.CONTAINS))
                .map(SlotPair::getChildSlot).collect(Collectors.toList());
            copySlotsToParent(children, findNodeForSlot(slotEJB.findById(newCopy.getId()), parentNode));
        }
    }

    /*
     * Finds a TreeNode for a given slot among the parent's children. If no such TreeNode can be found, the methods
     * returns 'null'.
     */
    private @Nullable TreeNode findNodeForSlot(final Slot slot, final TreeNode parentNode) {
        for (final TreeNode child : parentNode.getChildren()) {
            if (((SlotView)child.getData()).getSlot().equals(slot)) {
                return child;
            }
        }
        return null;
    }

    /**
     * This method transfers from the copy source all the attributes that can be copied:
     * <ul>
     * <li>non-unique <b>DEFINED</b> property values</li>
     * <li>tags</li>
     * <li>URL artifacts</li>
     * </ul>
     * It does not copy the attachments, since this may be too heavy.
     *
     * @param newCopy the slot that was just created
     * @param copySource the slot that is the source of the data
     */
    private void addAttributesToNewCopy(final Slot newCopy, final Slot copySource) {
        if (newCopy.isHostingSlot()) {
            // installation slots already have the property value instances, we just need to copy the actual values
            transferValuesFromSource(newCopy, copySource);
        } else {
            // containers can have "free floating" property values. We need to copy them to the newly created containers
            copyValuesFromSource(newCopy, copySource);
        }

        copyArtifactsFromSource(slotEJB.findById(newCopy.getId()), copySource);

        final Slot tagCopy = slotEJB.findById(newCopy.getId());
        tagCopy.getTags().addAll(copySource.getTags());

        slotEJB.save(tagCopy);
    }

    private void transferValuesFromSource(final Slot newCopy, final Slot copySource) {
        for (final SlotPropertyValue pv : newCopy.getSlotPropertyList()) {
            if (pv.getProperty().getValueUniqueness() == PropertyValueUniqueness.NONE) {
                final SlotPropertyValue parentPv = getPropertyValue(copySource, pv.getProperty().getName());
                if (parentPv != null) {
                    pv.setPropValue(parentPv.getPropValue());
                }
            }
        }
    }

    private SlotPropertyValue getPropertyValue(final Slot slot, final String pvName) {
        for (final SlotPropertyValue pv : slot.getSlotPropertyList()) {
            if (pv.getProperty().getName().equals(pvName)) {
                return pv;
            }
        }
        return null;
    }

    private void copyValuesFromSource(final Slot newCopy, final Slot copySource) {
        for (final SlotPropertyValue pv : copySource.getSlotPropertyList()) {
            final SlotPropertyValue targetPv = new SlotPropertyValue(false);
            targetPv.setProperty(pv.getProperty());
            targetPv.setUnit(pv.getUnit());
            if (pv.getProperty().getValueUniqueness() == PropertyValueUniqueness.NONE) {
                targetPv.setPropValue(pv.getPropValue());
            }
            targetPv.setSlot(newCopy);
            slotEJB.addChild(targetPv);
            newCopy.getSlotPropertyList().add(targetPv);
        }
    }

    private void copyArtifactsFromSource(final Slot newCopy, final Slot copySource) {
        for (final SlotArtifact artifact : copySource.getSlotArtifactList()) {
            String uri = artifact.getUri();
            if (artifact.isInternal()) {
                try {
                    uri = blobStore.storeFile(blobStore.retreiveFile(uri));
                } catch (IOException e) {
                    throw new PersistenceException(e);
                }
            }

            final SlotArtifact newArtifact = new SlotArtifact(artifact.getName(), artifact.isInternal(), artifact.getDescription(),
                                                                    artifact.getUri());
            newArtifact.setSlot(newCopy);
            slotEJB.addChild(newArtifact);
        }
    }

    private Slot createSlotCopy(final Slot source, final TreeNode parentNode) {
        final String newName = findNewSlotCopyName(source.getName());
        final Slot newSlot = new Slot(newName, source.isHostingSlot());
        newSlot.setDescription(source.getDescription());
        newSlot.setComponentType(source.getComponentType());
        final Slot parentSlot = ((SlotView) parentNode.getData()).getSlot();
        slotEJB.addSlotToParentWithPropertyDefs(newSlot, parentSlot, false);

        // first update the back-end data
        final SlotView slotViewToUpdate = (SlotView) parentNode.getData();
        if (selectedSlotView != null) {
            selectedSlotView = slotViewToUpdate;
        }

        // now update the front-end data as well.
        if (slotViewToUpdate.isInitialzed()) {
            final List<SlotPair> containsRelation = slotPairEJB.findSlotPairsByParentChildRelation(newSlot.getName(),
                    slotViewToUpdate.getName(), SlotRelationName.CONTAINS);
            final SlotPair newRelation = containsRelation.get(0);
            final SlotView slotViewToAdd = new SlotView(newSlot, slotViewToUpdate, newRelation.getSlotOrder(), slotEJB);
            hierarchyBuilder.addChildToParent(parentNode, slotViewToAdd);
        } else {
            hierarchyBuilder.rebuildSubTree(parentNode);
        }
        slotViewToUpdate.setDeletable(false);
        parentNode.setExpanded(true);
        return newSlot;
    }

    private String findNewSlotCopyName(String name) {
        int slotIndex = 1;
        String returnName = "";
        while (returnName.isEmpty()) {
            String candidateName = name + "_" + slotIndex;
            if (slotEJB.findByName(candidateName) == null) {
                return candidateName;
            }
            ++slotIndex;
        }
        return returnName;
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
        Preconditions.checkNotNull(selectedInstallationViews);
        final Date today = new Date();
        final InstallationRecord newRecord = new InstallationRecord(Long.toString(today.getTime()), today);
        final InstallationView installationView = selectedInstallationViews.get(0);

        newRecord.setDevice(deviceToInstall);
        newRecord.setSlot(installationView.getSlot());
        installationEJB.add(newRecord);

        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                "Device installed.");

        installationView.setSlot(slotEJB.findById(installationView.getSlot().getId()));
        installationView.setInstallationRecord(newRecord);

        final Slot installationSlot = installationView.getSlot();
        removeRelatedAttributes(installationSlot);
        initAttributeList(installationSlot, false);

        deviceToInstall = null;
    }

    /** This method is called when a user presses the "Uninstall" button in the hierarchies view. */
    public void uninstallDevice() {
        for (InstallationView selectedInstallationView : selectedInstallationViews) {
            final InstallationRecord selectedInstallationRecord = selectedInstallationView.getInstallationRecord();
            Preconditions.checkNotNull(selectedInstallationRecord);
            selectedInstallationRecord.setUninstallDate(new Date());
            installationEJB.save(selectedInstallationRecord);
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    "Device uninstalled.");
            // signal that nothing is installed
            selectedInstallationView.setInstallationRecord(null);

            final Slot installationSlot = selectedInstallationView.getSlot();
            removeRelatedAttributes(installationSlot);
            initAttributeList(installationSlot, false);
        }
    }

    public boolean canInstall()
    {
        return selectedInstallationViews != null && selectedInstallationViews.size() == 1 && selectedInstallationViews.get(0).getInstallationRecord() == null;
    }

    public boolean canUninstall()
    {
        if (selectedInstallationViews == null || selectedInstallationViews.size() == 0) return false;
        for (InstallationView selectedInstallationView : selectedInstallationViews)
            if (selectedInstallationView.getInstallationRecord() == null) return false;
        return true;
    }

    /** @return <code>true</code> if the attribute "Delete" button can be enabled, <code>false</code> otherwise */
    public boolean canDeleteAttributes() {
        if (selectedAttributes == null || selectedAttributes.size() == 0) {
            return false;
        }
        for (EntityAttributeView<Slot> selectedAttribute : selectedAttributes)
            switch (selectedAttribute.getKind()) {
                case ARTIFACT:
                case CONTAINER_SLOT_ARTIFACT:
                case CONTAINER_SLOT_TAG:
                case CONTAINER_SLOT_PROPERTY:
                case INSTALL_SLOT_ARTIFACT:
                case INSTALL_SLOT_TAG:
                    continue;
                case INSTALL_SLOT_PROPERTY:
                    if (selectedAttribute.getValue() != null) {
                        continue;
                    } else {
                        return false;
                    }
                default:
                    return false;
            }
        return true;
    }

    /** The handler called from the "Delete confirmation" dialog. This actually deletes an attribute */
    public void deleteAttributes() {
        Preconditions.checkNotNull(selectedAttributes);
        int props = 0;
        for (EntityAttributeView<Slot> selectedAttribute : selectedAttributes) {
            final Slot slot = slotEJB.findByName(selectedAttribute.getParent());
            switch (selectedAttribute.getKind()) {
                case INSTALL_SLOT_ARTIFACT:
                case CONTAINER_SLOT_ARTIFACT:
                case CONTAINER_SLOT_PROPERTY:
                    slotEJB.deleteChild(selectedAttribute.getEntity());
                    refreshSlot(slot);
                    props++;
                    break;
                case INSTALL_SLOT_TAG:
                case CONTAINER_SLOT_TAG:
                    slot.getTags().remove(selectedAttribute.getEntity());
                    saveSlotAndRefresh(slot);
                    props++;
                    break;
                case INSTALL_SLOT_PROPERTY:
                    SlotPropertyValue prop = ((SlotPropertyValue)selectedAttribute.getEntity());
                    prop.setPropValue(null);
                    slotEJB.saveChild(prop);
                    refreshSlot(slot);
                    props++;
                    final SlotPropertyValue freshPropertyValue = slotEJB.refreshPropertyValue(prop);
                    refreshAttributeList(slot, freshPropertyValue);
                    break;
                default:
                    throw new RuntimeException("Trying to delete an attribute that cannot be removed on home screen.");
            }
            attributes.remove(selectedAttribute);
        }
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                "Deleted " + props + " properties.");
        selectedAttributes = null;
    }

    /** @return <code>true</code> if the attribute "Edit" button can be enables, <code>false</code> otherwise */
    public boolean canEditAttribute() {
        if (selectedAttributes == null || selectedAttributes.size() != 1) {
            return false;
        }
        switch (selectedAttributes.get(0).getKind()) {
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
        Preconditions.checkNotNull(selectedAttributes);
        Preconditions.checkState(selectedAttributes.size() == 1);

        final EntityAttributeView<Slot> selectedAttrView = selectedAttributes.get(0);

        if (selectedAttrView instanceof EntityAttrPropertyValueView<?>) {
            final EntityAttrPropertyValueView<Slot> propertyValueView = new EntityAttrPropertyValueView<Slot>(
                    slotEJB.refreshPropertyValue((PropertyValue)selectedAttrView.getEntity()), selectedAttrView.getParentEntity());
            final boolean propertyNameChangeDisabled = propertyValueView.getParentEntity().isHostingSlot();
            propertyValueView.setPropertyNameChangeDisabled(propertyNameChangeDisabled);
            if (!propertyNameChangeDisabled) {
                filterProperties();
            }
            dialogAttribute = propertyValueView;

            RequestContext.getCurrentInstance().update("modifyPropertyValueForm:modifyPropertyValue");
            RequestContext.getCurrentInstance().execute("PF('modifyPropertyValue').show();");
        }

        if (selectedAttrView instanceof EntityAttrArtifactView<?>) {
            final EntityAttrArtifactView<Slot> view = new EntityAttrArtifactView<Slot>(
                    slotEJB.refreshArtifact((Artifact)selectedAttrView.getEntity()), selectedAttrView.getParentEntity());
            dialogAttribute = view;

            RequestContext.getCurrentInstance().update("modifyArtifactForm:modifyArtifact");
            RequestContext.getCurrentInstance().execute("PF('modifyArtifact').show();");
        }
    }

    /** Prepares a list of a devices that can still be installed into the selected installation slot */
    public void prepareUninstalledDevices() {
        final Slot slotToFill = selectedInstallationViews.get(0).getSlot();
        uninstalledDevices = (slotToFill == null) || !slotToFill.isHostingSlot() ? null
                : installationEJB.getUninstalledDevices(slotToFill.getComponentType());
        filteredUninstalledDevices = null;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Methods for device instance and attribute table manipulation.
     *
     * Below: Methods for adding and modifying container and installation slot property values.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /** Prepares data for addition of {@link PropertyValue}. Only valid for containers. */
    public void prepareForPropertyValueAdd() {
        selectedAttributes = null;
        final SlotPropertyValue slotValueInstance = new SlotPropertyValue(false);
        slotValueInstance.setPropertiesParent(selectedSlot);
        dialogAttribute = new EntityAttrPropertyValueView<Slot>(slotValueInstance, getSelectedEntity());
        filterProperties();
    }

    private void refreshAllPropertyValues() {
        final List<SlotPropertyValue> propertyValues = selectedSlot.getSlotPropertyList();
        for (final SlotPropertyValue propValue : propertyValues) {
            refreshAttributeList(selectedSlot, propValue);
        }
    }

    /** The handler called to save or add a new value of the {@link SlotPropertyValue} after modification */
    public void modifyPropertyValue() {
        try {
            final EntityAttrPropertyValueView<Slot> view = getDialogAttrPropertyValue();
            final PropertyValue slotValueInstance = view.getEntity();
            if (view.isBeingAdded()) {
                slotEJB.addChild(slotValueInstance);
                refreshSlot(view.getParentEntity());
                refreshAllPropertyValues();
                Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                        "New property has been created");
            } else {
                slotEJB.saveChild(slotValueInstance);
                refreshSlot(view.getParentEntity());
                final SlotPropertyValue freshPropertyValue = slotEJB.refreshPropertyValue((SlotPropertyValue)slotValueInstance);
                refreshAttributeList(view.getParentEntity(), freshPropertyValue);;
                Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                        "Property value has been modified");
            }
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

    private void filterProperties() {
        final List<Property> propertyCandidates = propertyEJB.findAllOrderedByName();

        final Property dialogProperty = getDialogAttrPropertyValue() != null ? getDialogAttrPropertyValue().getProperty() : null;

        // remove all properties that are already defined.
        for (final SlotPropertyValue slotPropertyValue : selectedSlot.getSlotPropertyList()) {
            if (!slotPropertyValue.getProperty().equals(dialogProperty)) {
                propertyCandidates.remove(slotPropertyValue.getProperty());
            }
        }

        filteredProperties = propertyCandidates;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Methods for adding and modifying container and installation slot property values.
     *
     * Below: Methods for adding and deleting tags.
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /** Prepares the UI data for addition of {@link Tag} */
    public void prepareForTagAdd() {
        fillTagsAutocomplete();
        dialogAttribute = new EntityAttrTagView<Slot>(getSelectedEntity());
    }

    /** Adds new {@link Tag} to parent {@link ConfigurationEntity} */
    public void addNewTag() {
        try {
            final EntityAttrTagView<Slot> tagView = getDialogAttrTag();
            Tag tag = tagEJB.findById(tagView.getTag());
            if (tag == null) {
                tag = tagView.getEntity();
            }

            Slot ent = tagView.getParentEntity();
            final Set<Tag> existingTags = ent.getTags();
            if (!existingTags.contains(tag)) {
                existingTags.add(tag);
                saveSlotAndRefresh(ent);
                refreshAttributeList(ent, tag);
                Utility.showMessage(FacesMessage.SEVERITY_INFO, "Tag added", tag.getName());
            }
        } finally {
            fillTagsAutocomplete();
            selectedAttributes = null;
            dialogAttribute = null;
        }
    }

    private void fillTagsAutocomplete() {
        tagsForAutocomplete = ImmutableList.copyOf(Lists.transform(tagEJB.findAllSorted(), Tag::getName));
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
        final Artifact artifact = new SlotArtifact();
        final Slot selectedEntity = getSelectedEntity();
        artifact.setArtifactsParent(selectedEntity);
        dialogAttribute = new EntityAttrArtifactView<Slot>(artifact, selectedEntity);
    }

    private void refreshAllArtifacts() {
        final List<SlotArtifact> slotArtifacts = selectedSlot.getSlotArtifactList();
        for (final SlotArtifact artifact : slotArtifacts) {
            refreshAttributeList(selectedSlot, artifact);
        }
    }

    /** Modifies the selected artifact properties
     * @throws IOException if attachment file operation has failed. */
    public void modifyArtifact() throws IOException {
        try {
            final EntityAttrArtifactView<Slot> artifactView = getDialogAttrArtifact();

            if (artifactView.isArtifactInternal()) {
                final byte[] importData = artifactView.getImportData();
                if (importData != null) {
                    if (artifactView.isArtifactBeingModified()) {
                        blobStore.deleteFile(artifactView.getArtifactURI());
                    }
                    artifactView.setArtifactURI(blobStore.storeFile(new ByteArrayInputStream(importData)));
                }
            }

            final Artifact artifactInstance = artifactView.getEntity();
            final Slot parentSlot = artifactView.getParentEntity();

            if (artifactView.isArtifactBeingModified()) {
                slotEJB.saveChild(artifactInstance);
                final SlotArtifact freshArtifact = slotEJB.refreshArtifact((SlotArtifact)artifactInstance);
                refreshAttributeList(parentSlot, freshArtifact);
            } else {
                slotEJB.addChild(artifactInstance);
                refreshAllArtifacts();
            }
            Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                    artifactView.isArtifactBeingModified() ? "Artifact has been modified" : "New artifact has been created");

            refreshSlot(parentSlot);
        } finally {
            dialogAttribute = null;
        }
    }

    /**
     * Finds artifact file that was uploaded on the file system and returns it to be downloaded
     *
     * @return Artifact file to be downloaded
     * @throws FileNotFoundException Thrown if file was not found on file system
     */
    public StreamedContent getDownloadFile() throws FileNotFoundException {
        final Artifact downloadArtifact = (Artifact) downloadAttribute.getEntity();
        final String filePath = blobStore.getBlobStoreRoot() + File.separator + downloadArtifact.getUri();
        final String contentType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(filePath);

        return new DefaultStreamedContent(new FileInputStream(filePath), contentType, downloadArtifact.getName());
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
        final String valueStr = value.toString();

        if (restrictToConventionNames && isInstallationSlot) {
            if (!nameList.containsKey(valueStr))
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        Utility.MESSAGE_SUMMARY_ERROR, "The installation slot name not found in the naming tool."));
        }
        if (isNewInstallationSlot) {
            if (!slotEJB.isInstallationSlotNameUnique(valueStr))
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        Utility.MESSAGE_SUMMARY_ERROR, "The installation slot name must be unique."));
        } else if (isInstallationSlot)
            if (!name.equals(valueStr) && !slotEJB.isInstallationSlotNameUnique(valueStr)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        Utility.MESSAGE_SUMMARY_ERROR, "The installation slot name must be unique."));
        }
    }

      /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Above: Input field validators regardless of the dialog they are used in.
     *
     * Below: Relationships related methods
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * This method gets called when the relationship processing is complete or dialog is simply closed. It properly
     * refreshes the selected node state so that the hierarchy behaves consistently.
     * The currently selected slot is also refreshed, so that new realtionship data is displayed.
     */
    public void onRelationshipPopupClose() {
        // clear the previous dialog selection for the next use
        if (editedRelationshipView != null) {
            if (editedRelationshipView.getTargetNode() != null) {
                editedRelationshipView.getTargetNode().setSelected(false);
            }

            editedRelationshipView = null;
        }

     // restore the current main selection. The relationship manipulation is done.
        hierarchyBuilder.setFilterValue(filterContainsTree);
        hierarchyBuilder.applyFilter(rootNode, new ArrayList<>(rootNode.getChildren()));
        List<TreeNode> selected = selectedNodes;
        selectedNodes = new ArrayList<TreeNode>();
        reselectNodes(rootNode, selected);


        refreshSlot(selectedSlot);
    }

    /** Called when button to delete relationship is clicked */
    public void onRelationshipDelete() {
        if (canRelationshipBeDeleted()) {
            for (SlotRelationshipView selectedRelationship : selectedRelationships) {
                final SlotPair slotPairToBeRemoved = selectedRelationship.getSlotPair();
                final boolean isContainsRemoved =
                        (slotPairToBeRemoved.getSlotRelation().getName() == SlotRelationName.CONTAINS);
                final Long parentSlotId = slotPairToBeRemoved.getParentSlot().getId();
                final Long childSlotId = slotPairToBeRemoved.getChildSlot().getId();
                relationships.remove(selectedRelationship);
                slotPairEJB.delete(slotPairToBeRemoved);
                Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                        "Relationship deleted.");
                selectedRelationship = null;
                updateTreesWithFreshSlot(slotEJB.findById(childSlotId), isContainsRemoved);
                updateTreesWithFreshSlot(slotEJB.findById(parentSlotId), isContainsRemoved);
            }
        } else {
            RequestContext.getCurrentInstance().execute("PF('cantDeleteRelation').show();");
        }
    }

    public boolean canRelationshipBeEdited() {
        if (selectedRelationships == null || selectedRelationships.size() != 1) return false;
        if (selectedRelationships.get(0).getSlotPair() == null) return false;
        if (selectedRelationships.get(0).getSlotPair().getSlotRelation().getName().equals(SlotRelationName.CONTAINS)) {
            return false;
        }
        return true;
    }

    private boolean canRelationshipBeDeleted() {
        if (selectedRelationships == null || selectedRelationships.size() == 0) return false;
        for (SlotRelationshipView selectedRelationship : selectedRelationships) {
            if (selectedRelationship.getSlotPair() == null) return false;
            if (selectedRelationship.getSlotPair().getSlotRelation().getName().equals(SlotRelationName.CONTAINS)
                && !slotPairEJB.slotHasMoreThanOneContainsRelation(selectedRelationship.getSlotPair().getChildSlot())) return false;
        }
        return true;
    }

    private void prepareTreeForRelationshipsPopup() {
        // hide the current main selection, since the same data can be used to add new relationships.
        // Will be restored when the user finishes relationship manipulation.
        for (TreeNode node : selectedNodes) node.setSelected(false);
        hierarchyBuilder.setFilterValue(null);
        hierarchyBuilder.applyFilter(rootNode, new ArrayList<>(rootNode.getChildren()));
    }

    /** Prepares data for editing new relationship */
    public void prepareEditRelationshipPopup() {
        Preconditions.checkState((selectedRelationships != null) && (selectedRelationships.size() == 1));
        prepareTreeForRelationshipsPopup();

        // setups the dialog
        SlotRelationshipView v = selectedRelationships.get(0);
        editedRelationshipView = new SlotRelationshipView(v.getSlotPair(), v.getSourceSlot());

        TreeNode node = findNode(editedRelationshipView.getTargetSlot());
        node.setSelected(true);
        editedRelationshipView.setTargetNode(node);

        // modify relationship types drop down menu
        relationshipTypesForDialog = slotRelationBySlotRelationStringName.entrySet().stream()
                .filter(e -> !e.getValue().getName().equals(SlotRelationName.CONTAINS)).map(Entry::getKey).collect(Collectors.toList());
    }

    /** Prepares data for adding new relationship */
    public void prepareAddRelationshipPopup() {
        Preconditions.checkNotNull(selectedSlot);
        Preconditions.checkState((selectedNodes != null) && (selectedNodes.size() == 1));

        prepareTreeForRelationshipsPopup();

        // clear the previous dialog selection in case the dialog was already used before
        editedRelationshipView = new SlotRelationshipView(null, selectedSlot);
        editedRelationshipView.setRelationshipName(SlotRelationName.CONTAINS.toString());

        // modify relationship types drop down menu
        if (selectedSlot.isHostingSlot()) {
            relationshipTypesForDialog = ImmutableList.copyOf(slotRelationBySlotRelationStringName.keySet().iterator());

        } else {
            relationshipTypesForDialog = ImmutableList.of(SlotRelationName.CONTAINS.toString(),
                    SlotRelationName.CONTAINS.inverseName());
        }
    }

    /**
     * Called when user clicks add button to add new relationship. Relationship is added if this does not
     * cause a loop on CONTAINS relationships
     */
    public void onRelationshipAdd() {
        try {
            final SlotRelation slotRelation = slotRelationBySlotRelationStringName.get(editedRelationshipView.getRelationshipName());
            final Slot parentSlot;
            final Slot childSlot;
            if (slotRelation.getNameAsString().equals(editedRelationshipView.getRelationshipName())) {
                childSlot = ((SlotView) editedRelationshipView.getTargetNode().getData()).getSlot();
                parentSlot = editedRelationshipView.getSourceSlot();
            } else {
                childSlot = editedRelationshipView.getSourceSlot();
                parentSlot = ((SlotView) editedRelationshipView.getTargetNode().getData()).getSlot();
            }

            if (childSlot.equals(parentSlot)) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                        "The installation slot cannot be in relationship with itself.");
                return;
            }

            final SlotPair newSlotPair;
            final boolean isContainsRemoved = editedRelationshipView.getSlotPair() != null && editedRelationshipView.getSlotPair().getSlotRelation().equals(SlotRelationName.CONTAINS);
            if (editedRelationshipView.getSlotPair() != null) {
                newSlotPair = editedRelationshipView.getSlotPair();
                if (newSlotPair.getChildSlot().equals(childSlot) &&
                        newSlotPair.getParentSlot().equals(parentSlot) &&
                        newSlotPair.getSlotRelation().equals(slotRelation)) {
                    // nothing to do, relationship not modified
                    return;
                }
            } else {
                newSlotPair = new SlotPair();
            }

            if (!slotPairEJB.findSlotPairsByParentChildRelation(childSlot.getName(), parentSlot.getName(),
                    slotRelation.getName()).isEmpty()) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                        "This relationship already exists.");  // TODO why is this message not show?!
                return;
            }

            newSlotPair.setChildSlot(childSlot);
            newSlotPair.setParentSlot(parentSlot);
            newSlotPair.setSlotRelation(slotRelation);

            if (slotPairEJB.slotPairCreatesLoop(newSlotPair, childSlot)) {
                RequestContext.getCurrentInstance().execute("PF('slotPairLoopNotification').show();");
                return;
            }

            if (editedRelationshipView.getSlotPair() == null) {
                slotPairEJB.add(newSlotPair);
                relationships.add(new SlotRelationshipView(slotPairEJB.findById(newSlotPair.getId()), selectedSlot));
                Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                        "Relationship added.");
            } else {
                slotPairEJB.save(newSlotPair);
                relationships.remove(selectedRelationships.get(0));
                relationships.add(new SlotRelationshipView(slotPairEJB.findById(newSlotPair.getId()), selectedRelationships.get(0).getSourceSlot()));
                Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                        "Relationship modified.");
                selectedRelationships = null;
            }

            final boolean isContainsAdded = (slotRelation.getName() == SlotRelationName.CONTAINS);
            updateTreesWithFreshSlot(slotEJB.findById(childSlot.getId()), isContainsAdded || isContainsRemoved);
            updateTreesWithFreshSlot(slotEJB.findById(parentSlot.getId()), isContainsAdded || isContainsRemoved);

            if (isContainsAdded && parentSlot == selectedSlot) {
                selectedNodes.get(0).setExpanded(true);
            }
        } finally {
            onRelationshipPopupClose();
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
    public List<EntityAttributeView<Slot>> getAttributes() {
        return attributes;
    }
    public void setAttrbutes(List<EntityAttributeView<Slot>> attributes) {
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

    /** @return The root node of the <code>CONNECTS</code> hierarchy tree. */
    public TreeNode getConnectsRoot() {
        return connectsRootNode;
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

    public void setSelectedConnectsNodes(TreeNode[] nodes) {
        if (activeTab == ActiveTab.CONNECTS) {
            selectedNodes = nodes == null ? null : Lists.newArrayList(nodes);
        }
    }

    public TreeNode[] getSelectedConnectsNodes() {
        return ((selectedNodes == null) || (activeTab != ActiveTab.CONNECTS))
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
        return clipboardSlots;
    }

    public String getRequestedSlot() {
        return requestedSlot;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Device instance installation
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return The latest installation records associated with the selected installation slots, <code>null</code> if no
     * slots are selected.
     */
    public List<InstallationView> getInstallationRecords() {
        return installationRecords;
    }

    /** @return the selectedInstallationView */
    public List<InstallationView> getSelectedInstallationViews() {
        return selectedInstallationViews;
    }

    /** @param selectedInstallationViews the selectedInstallationViews to set */
    public void setSelectedInstallationViews(List<InstallationView> selectedInstallationViews) {
        this.selectedInstallationViews = selectedInstallationViews;
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

    /** @return installation records for filtering */
    public List<InstallationView> getFilteredInstallationRecords() {
        return filteredInstallationRecords;
    }
    /** @param filteredInstallationRecords installation records for filtering */
    public void setFilteredInstallationRecords(List<InstallationView> filteredInstallationRecords) {
        this.filteredInstallationRecords = filteredInstallationRecords;
    }

    /**
     * @return The path to the installation slot the user is currently installing a device into. Used in the
     * "Install device" dialog.
     */
    public String getInstallationSlotPath() {
        final String slotPath = Utility.buildSlotPath(selectedInstallationViews.get(0).getSlot()).toString();
        return slotPath.substring(1, slotPath.length() - 1);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Attributes table
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** @return the filteredAttributes */
    public List<EntityAttributeView<Slot>> getFilteredAttributes() {
        return filteredAttributes;
    }
    /** @param filteredAttributes the filteredAttributes to set */
    public void setFilteredAttributes(List<EntityAttributeView<Slot>> filteredAttributes) {
        this.filteredAttributes = filteredAttributes;
    }

    public List<SelectItem> getAttributeKinds() {
        return attributeKinds;
    }

    /** @return the selectedAttributes */
    public List<EntityAttributeView<Slot>> getSelectedAttributes() {
        return selectedAttributes;
    }
    /** @param selectedAttributes the selectedAttributes to set */
    public void setSelectedAttributes(List<EntityAttributeView<Slot>> selectedAttributes) {
        this.selectedAttributes = selectedAttributes;
    }

    /** @return the downloadAttribute */
    public EntityAttributeView<Slot> getDownloadAttribute() {
        return downloadAttribute;
    }
    /** @param downloadAttribute the downloadAttribute to set */
    public void setDownloadAttribute(EntityAttributeView<Slot> downloadAttribute) {
        this.downloadAttribute = downloadAttribute;
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

    /** @return the isNewInstallationSlot */
    public boolean isNewInstallationSlot() {
        return isNewInstallationSlot;
    }

    /** @return the parent slot name */
    public String getParentName() {
        return parentName;
    }

    /** @return hasDevice */
    public boolean getHasDevice() {
        return hasDevice;
    }

    /** @return the filteredProperties */
    public List<Property> getFilteredProperties() {
        return filteredProperties;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Relationship manipulation
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** @return the selectedRelationships */
    public List<SlotRelationshipView> getSelectedRelationships() {
        return selectedRelationships;
    }
    /** @param selectedRelationships the selectedRelationships to set */
    public void setSelectedRelationships(List<SlotRelationshipView> selectedRelationships) {
        this.selectedRelationships = selectedRelationships;
    }

    /** @return the relationshipTypesForDialog */
    public List<String> getRelationshipTypesForDialog() {
        return relationshipTypesForDialog;
    }
    /** @param relationshipTypesForDialog the relationshipTypesForDialog to set */
    public void setRelationshipTypesForDialog(List<String> relationshipTypesForDialog) {
        this.relationshipTypesForDialog = relationshipTypesForDialog;
    }

    /**
     * @return the filterContainsTree
     */
    public String getFilterContainsTree() {
        return filterContainsTree;
    }

    /**
     * @param filterContainsTree the filterContainsTree to set
     */
    public void setFilterContainsTree(String filterContainsTree) {
        this.filterContainsTree = filterContainsTree;
    }

    /**
     * @return the namingRedirectionUrl
     */
    public String getNamingRedirectionUrl() {
        return namingRedirectionUrl;
    }

    /**
     * @return the filterControlsTree
     */
    public String getFilterControlsTree() {
        return filterControlsTree;
    }

    /**
     * @param filterControlsTree the filterControlsTree to set
     */
    public void setFilterControlsTree(String filterControlsTree) {
        this.filterControlsTree = filterControlsTree;
    }

    /**
     * @return the filterPowersTree
     */
    public String getFilterPowersTree() {
        return filterPowersTree;
    }

    /**
     * @param filterPowersTree the filterPowersTree to set
     */
    public void setFilterPowersTree(String filterPowersTree) {
        this.filterPowersTree = filterPowersTree;
    }

    /**
     * @return the filterConnectsTree
     */
    public String getFilterConnectsTree() {
        return filterConnectsTree;
    }

    /**
     * @param filterConnectsTree the filterConnectsTree to set
     */
    public void setFilterConnectsTree(String filterConnectsTree) {
        this.filterConnectsTree = filterConnectsTree;
    }

    public void filterContainsTree() {
        hierarchyBuilder.setFilterValue(filterContainsTree);
        hierarchyBuilder.applyFilter(rootNode, new ArrayList<>(rootNode.getChildren()));
        unselectAllTreeNodes();
        selectedNodes = null;
    }

    public void filterControlsTree() {
        controlsHierarchyBuilder.setFilterValue(filterControlsTree);
        controlsHierarchyBuilder.applyFilter(controlsRootNode, controlsChildren);
        unselectAllTreeNodes();
        selectedNodes = null;
    }

    public void filterPowersTree() {
        powersHierarchyBuilder.setFilterValue(filterPowersTree);
        powersHierarchyBuilder.applyFilter(powersRootNode,powersChildren);
        unselectAllTreeNodes();
        selectedNodes = null;
    }

    public void filterConnectsTree() {
        connectsHierarchyBuilder.setFilterValue(filterConnectsTree);
        connectsHierarchyBuilder.applyFilter(connectsRootNode, connectsChildren);
        unselectAllTreeNodes();
        selectedNodes = null;
    }

    /**
     * @return the linkSlot
     */
    public SlotView getLinkSlot() {
        return linkSlot;
    }

    /**
     * @param linkSlot the linkSlot to set
     */
    public void setLinkSlot(SlotView linkSlot) {
        this.linkSlot = linkSlot;
    }

    /**
     * @return the cableRedirectionUrl
     */
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

    /**
     * @return the editedRelationshipView
     */
    public SlotRelationshipView getEditedRelationshipView() {
        return editedRelationshipView;
    }

    /**
     * @return the namesForAutoComplete
     */
    public List<String> getNamesForAutoComplete() {
        return namesForAutoComplete;
    }

    /**
     * @return the restrictToConventionNames
     */
    public boolean isRestrictToConventionNames() {
        return restrictToConventionNames;
    }

    /** @return the dialogAttribute */
    public EntityAttrTagView<Slot> getDialogAttrTag() {
        if (dialogAttribute instanceof EntityAttrTagView<?>) {
            return (EntityAttrTagView<Slot>)dialogAttribute;
        }
        return null;
    }

    /** @return the dialogAttribute */
    public EntityAttrPropertyValueView<Slot> getDialogAttrPropertyValue() {
        if (dialogAttribute instanceof EntityAttrPropertyValueView<?>) {
            return (EntityAttrPropertyValueView<Slot>)dialogAttribute;
        }
        return null;
    }

    /** @return the dialogAttribute */
    public EntityAttrArtifactView<Slot> getDialogAttrArtifact() {
        if (dialogAttribute instanceof EntityAttrArtifactView<?>) {
            return (EntityAttrArtifactView<Slot>)dialogAttribute;
        }
        return null;
    }

    protected Slot getSelectedEntity() {
        if (selectedSlot != null) {
            Slot slot = slotEJB.findById(selectedSlot.getId());
            return slot;
        }
        throw new IllegalArgumentException("No slot selected");
    }

    public void resetFields() {
        dialogAttribute = null;
    }
}
