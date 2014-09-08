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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
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
        rootNode = getRootTreeNode();
    }

    private TreeNode getRootTreeNode() {
        final List<Slot> containers = new ArrayList<>();

        for (Slot slot : slotEJB.findAll()) {
            if (!slot.getIsHostingSlot()) {
                containers.add(slot);
            }
        }

        return containerTreeBuilder.newContainerTree(containers, collapsedNodes);
    }

    public TreeNode getRootNode() { return rootNode; }

    public TreeNode getSelectedNode() { return selectedNode; }
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

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
    public void setSelectedContainerToModify(ContainerView selectedContainer) {
        this.selectedContainer = selectedContainer;
        prepareModifyPopup();
    }

    public ContainerView getSelectedContainer() { return selectedContainer; }
    public void setSelectedContainer(ContainerView selectedContainer) { this.selectedContainer = selectedContainer; }

    public void prepareModifyPopup() {
        name = selectedContainer.getName();
        description = selectedContainer.getDescription();
        parentContainer = selectedContainer.getParentNode();
        RequestContext.getCurrentInstance().update("modifyContainerForm:modifyContainer");
    }

    public void addContainer() {
        final Slot newContainer = new Slot(name, false);
        newContainer.setDescription(description);
        if (selectedNode != null) {
            newContainer.setComponentType(comptypeEJB.findByName("_GRP"));
        } else {
            newContainer.setComponentType(comptypeEJB.findByName("_ROOT"));
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

    public void modifyContainer() {
        final Slot slotToModify = selectedContainer.getSlot();
        slotToModify.setName(name);
        slotToModify.setDescription(description);
        slotEJB.save(slotToModify);

        init();
    }

    public void onDelete() {
        slotEJB.delete(selectedContainer.getSlot());
        init();
    }

    public void onNodeCollapse(NodeCollapseEvent event) {
        if (event != null && event.getTreeNode() != null) {
            if (collapsedNodes == null) {
                collapsedNodes = new HashSet<>();
            }
            collapsedNodes.add(((ContainerView)event.getTreeNode().getData()).getId());
            event.getTreeNode().setExpanded(false);
        }
    }

    public void onNodeExpand(NodeExpandEvent event) {
        if (event != null && event.getTreeNode() != null) {
            if (collapsedNodes != null) {
                collapsedNodes.remove(((ContainerView)event.getTreeNode().getData()).getId());
            }
        }
    }

    public void recirectToAttributes(Long id) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("container-attributes-manager.xhtml?id=" + id);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ContainerView getParentContainer() { return parentContainer; }
    public void setParentContainer(ContainerView parentContainer) { this.parentContainer = parentContainer; }


}
