/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ejb.SlotPairEJB;
import org.openepics.discs.conf.ejb.SlotRelationEJB;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.openepics.discs.conf.views.ContainerView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.TreeNode;

/**
 * Controller for manipulation of container {@link Slot}s
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Named
@ViewScoped
public class ContainerController implements Serializable {

    @Inject private ContainerTreeBuilder containerTreeBuilder;
    @Inject private SlotEJB slotEJB;
    @Inject private SlotPairEJB slotPairEJB;
    @Inject private SlotRelationEJB slotRelationEJB;
    @Inject private ComptypeEJB comptypeEJB;

    private TreeNode rootNode;
    private TreeNode selectedNode;

    private ContainerView selectedContainer;

    private String name;
    private String description;
    private ContainerView parentContainer;

    private Set<Long> collapsedNodes;

    @PostConstruct
    public void init() {
        rootNode = containerTreeBuilder.newContainerTree(slotEJB.findByIsHostingSlot(false), collapsedNodes);
    }

    /**
     * Returns root node of a tree of containers
     *
     * @return root {@link TreeNode} of tree of containers
     */
    public TreeNode getRootNode() { return rootNode; }

    public TreeNode getSelectedNode() { return selectedNode; }
    public void setSelectedNode(TreeNode selectedNode) { this.selectedNode = selectedNode; }

    /**
     * Prepares fields that are used in pop up for adding new container
     */
    public void prepareAddPopup() {
        name = null;
        description = null;
        if (selectedNode != null) {
            parentContainer = (ContainerView) selectedNode.getData();
        } else {
            parentContainer = null;
        }
        RequestContext.getCurrentInstance().update("addContainerForm:addContainer");
    }

    public ContainerView getSelectedContainerToModify() { return selectedContainer; }

    /**
     * Sets selected {@link ContainerView} and prepares fields that are used in pop up for container modification
     *
     * @param selectedContainer selected {@link ContainerView} node
     */
    public void setSelectedContainerToModify(ContainerView selectedContainer) {
        this.selectedContainer = selectedContainer;
        prepareModifyPopup();
    }

    public ContainerView getSelectedContainer() { return selectedContainer; }
    public void setSelectedContainer(ContainerView selectedContainer) { this.selectedContainer = selectedContainer; }

    private void prepareModifyPopup() {
        name = selectedContainer.getName();
        description = selectedContainer.getDescription();
        parentContainer = selectedContainer.getParentNode();
        RequestContext.getCurrentInstance().update("modifyContainerForm:modifyContainer");
    }

    /**
     * From fields populated in pop up creates new container and saves it.
     */
    public void addContainer() {
        final Slot newContainer = new Slot(name, false);
        newContainer.setDescription(description);
        if (selectedNode != null) {
            newContainer.setComponentType(comptypeEJB.findByName(SlotEJB.GRP_COMPONENT_TYPE));
        } else {
            newContainer.setComponentType(comptypeEJB.findByName(SlotEJB.ROOT_COMPONENT_TYPE));
        }
        slotEJB.add(newContainer);

        if (selectedNode != null) {
            slotPairEJB.add(new SlotPair(newContainer, ((ContainerView) selectedNode.getData()).getSlot(), slotRelationEJB.findBySlotRelationName(SlotRelationName.CONTAINS)));
        }

        List<ComptypePropertyValue> propertyDefinitions = comptypeEJB.findPropertyDefinitions(newContainer.getComponentType());
        for (ComptypePropertyValue propertyDefinition : propertyDefinitions) {
            final SlotPropertyValue slotPropertyValue = new SlotPropertyValue(false);
            slotPropertyValue.setProperty(propertyDefinition.getProperty());
            slotPropertyValue.setSlot(newContainer);
            slotEJB.addChild(slotPropertyValue);
        }

        init();
    }

    /**
     * Saves modifications to the container
     */
    public void modifyContainer() {
        final Slot slotToModify = selectedContainer.getSlot();
        slotToModify.setName(name);
        slotToModify.setDescription(description);
        slotEJB.save(slotToModify);

        init();
    }

    /**
     * Deletes selected container
     */
    public void onDelete() {
        slotEJB.delete(selectedContainer.getSlot());
        init();
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
            collapsedNodes.add(((ContainerView)event.getTreeNode().getData()).getId());
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
        if (event != null && event.getTreeNode() != null) {
            if (collapsedNodes != null) {
                collapsedNodes.remove(((ContainerView)event.getTreeNode().getData()).getId());
            }
        }
    }

    /**
     * Returns relative path for redirection to attribute manipulation view for id of selected container
     *
     * @param id of selected container
     * @return relative path to which vew should be redirected
     */
    public String recirectToAttributes(Long id) { return "container-attributes-manager.xhtml?faces-redirect=true&id=" + id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ContainerView getParentContainer() { return parentContainer; }
    public void setParentContainer(ContainerView parentContainer) { this.parentContainer = parentContainer; }


}
