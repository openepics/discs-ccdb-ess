/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2012.
 *
 * You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *       http://www.gnu.org/licenses/gpl.txt
 *
 * Contact Information:
 *   Facilitty for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 *
 */

package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotRelationName;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * Stores the state of the Configuration Tree view
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class ComponentTreeMBean implements Serializable {
    @EJB private SlotEJB slotBean;
    private TreeNode root;
    private TreeNode selectedNode;
    private Slot selectedComponent;

    /**
     *
     */
    public static class NodeData implements Serializable {
        @SuppressWarnings("unused")
        private char type; // l - logical component, r - relation, i - inverse relation
        private String name; // name of logical component or relationship
        private long id; // id of the logical component or relationship

        /**
         * Constructs new Node Data ..
         *
         * @param t
         * @param n
         * @param i
         */
        public NodeData(char t, String n, long i) {
            type = t;
            name = n;
            id = i;
        }

        /**
         * Getter for the name
         * @return the name
         */
        public String getName() {
            return name;
        }

    }

    /**
     *
     */
    public ComponentTreeMBean() {
    }

    /**
     *
     */
    @PostConstruct
    public void init() {
        root = new DefaultTreeNode("Root", null);
        List<Slot> rnodes = slotBean.getRootNodes(SlotRelationName.CONTAINS);

        for(Slot lc: rnodes) {
            TreeNode node0 = new DefaultTreeNode("component", new NodeData('l', lc.getName(), lc.getId()), root);
            this.expandNode(node0);
        }

    }

    /**
     * Gets the root node.
     *
     * @return the root node
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     * Gets the selected node
     *
     * @return the selected node
     */
    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    /**
     *
     * @param selectedNode
     */
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    private void expandNode(TreeNode node) {
        List<Slot> components;

        if (node.getChildCount() == 0) { // has not been expanded
            // ToDo: Improve - get related children using Id
            components = slotBean.relatedChildren(((NodeData) node.getData()).getName());
            for (Slot lc : components) {
                // node types ("component") are not useful now but may be needed in future.
                new DefaultTreeNode("component", new NodeData('l',lc.getName(), lc.getId()), node);
            }
        }
    }

    /**
     *
     * @param event
     */
    public void onNodeExpand(NodeExpandEvent event) {
        TreeNode node = event.getTreeNode();

        for (TreeNode tn : node.getChildren()) { // ToDo: inefficient. figure out how to not expand to the second level
            if (tn.getChildCount() == 0) { // not expanded
                expandNode(tn);
            }
        }
    }

    /**
     * Handles the node collapse UI event
     *
     * @param event event data
     */
    public void onNodeCollapse(NodeCollapseEvent event) {
        DefaultTreeNode tn = (DefaultTreeNode)event.getTreeNode();
        tn.setExpanded(false);
    }

    /**
    * Handles the node selection UI event
    *
    * @param event event data
    */
    public void onNodeSelect(NodeSelectEvent event) {
        NodeData ndata = (NodeData) selectedNode.getData();
        selectedComponent = slotBean.findById(ndata.id);
    }

    public void setSelectedComponent(Slot selectedComponent) {
        this.selectedComponent = selectedComponent;
    }

    /**
     * Getter for the selected component
     *
     * @return the selected component
     */
    public Slot getSelectedComponent() {
        return selectedComponent;
    }

}
