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
package org.openepics.discs.conf.ui.trees;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.primefaces.model.TreeNode;

/**
 * This is a basic extension of primefaces' TreeNode, not doing any additional logic.
 * <p>
 * Be careful with PF implementation. Rowkeys are used to find the nodes in tree.
 * So rowkeys need to be updated every time children change.
 *
 * @author ilist
 *
 * @param <T> type of the data it contains
 */
public abstract class BasicTreeNode<T> implements TreeNode {
    public static final String DEFAULT_TYPE = "default";
    public static final int LOAD_AFTER_LEVEL = 4;

    private String type;
    private BasicTreeNode<T> parent;
    private T data;
    private boolean expanded;
    private boolean selected;
    private boolean selectable = true;
    private int rowKey;
    private int level;

    public BasicTreeNode(T data, BasicTreeNode<T> parent) {
        this.type = DEFAULT_TYPE;
        this.data = data;
        this.parent = parent;
        this.level = parent == null ? 0 : parent.level + 1;

        // expanded also takes care of loading, so we want to have root node expanded
        if (parent == null) {
            expanded = true;
        }
    }

    public abstract List<? extends BasicTreeNode<T>> getAllChildren();
    public abstract List<? extends BasicTreeNode<T>> getFilteredChildren();


    /**
     * Method returns the children only when the node is expanded or deep enough.
     *
     * @deprecated This method is deprecated to prevent incidental use. Instead use getAllChildren or getFilteredChildren.
     * @return The displayed children.
     */
    @Override @Deprecated
    public List<TreeNode> getChildren() {
        if (expanded || level >= LOAD_AFTER_LEVEL) {
            return (List<TreeNode>)(List<?>)getFilteredChildren();
        } else {
            return Arrays.asList();
        }
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public BasicTreeNode<T> getParent() {
        return parent;
    }

    @Override
    public void setParent(TreeNode parent) {
        //nothing
    }

    @Override
    public void clearParent() {
        //nothing
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void setExpanded(boolean expanded) {
        // the actual PrimeFaces implicit root must never be collapsed
        if (parent != null) {
            this.expanded = expanded;
        }
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public void setSelected(boolean value) {
        this.selected = value;
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * Method returns the number proper number of children only when the node is expanded or deep enough.
     *
     * @deprecated This method is deprecated to prevent incidental use. Instead use {@link #getAllChildren()} or
     * {@link #getFilteredChildren()}.
     * @return Number of displayed children
     */
    @Override
    @Deprecated
    public int getChildCount() {
        if (expanded || (level >= LOAD_AFTER_LEVEL) || (parent == null)) {
            return getFilteredChildren().size();
        } else {
            return 0;
        }
    }

    /**
     * This method properly returns if this node is a leaf. In the process it loads one level of the tree.
     * Primefaces uses this method to display 'the carrot' icon.
     */
    @Override
    public boolean isLeaf() {
        return getFilteredChildren().isEmpty();
    }

    @Override
    public String getRowKey() {
        if (parent == null) return "root";
        else if (parent.parent == null) return String.valueOf(rowKey);
        else return parent.getRowKey() + "_" + String.valueOf(rowKey);
    }

    @Override
    public void setRowKey(String rowKey) {
        //nothing
    }

    protected void setRowKey(int rowKey) {
        this.rowKey = rowKey;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        // data already takes care of hashing the entire path
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + rowKey;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;

        BasicTreeNode<T> other = (BasicTreeNode<T>) obj;
        return Objects.equals(data, other.data);
    }

    @Override
    public String toString() {
        if(data != null)
            return data.toString();
        else
            return super.toString();
    }

    @Override
    public boolean isPartialSelected() {
        return false;
    }

    @Override
    public void setPartialSelected(boolean value) {
        //nothing
    }

    /**
     * Returns the level in the tree this node is on, i.e (0 is root, 1 root's children)
     * @return the level
     */
    public int getLevel() {
        return level;
    }
}
