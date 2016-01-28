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
 * @param <D> type of the data it contains
 */
public abstract class BasicTreeNode<D> implements TreeNode {
	public static final String DEFAULT_TYPE = "default";

	private String type;
	private BasicTreeNode<D> parent;
	private D data;
	private boolean expanded;
    private boolean selected;
    private boolean selectable = true;
    private int rowKey;

	public BasicTreeNode(D data, BasicTreeNode<D> parent) {
        this.type = DEFAULT_TYPE;
        this.data = data;
        this.parent = parent;
    }

	public abstract List<? extends BasicTreeNode<D>> getAllChildren();
	public abstract List<? extends BasicTreeNode<D>> getFilteredChildren();

	@Override @Deprecated
	public List<TreeNode> getChildren() {
		return (List<TreeNode>)(List<?>)getFilteredChildren();
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
	public D getData() {
		return data;
	}

	@Override
	public BasicTreeNode<D> getParent() {
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
		this.expanded = expanded;
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

    @Override
	public int getChildCount() {
		return getFilteredChildren().size();
	}

	@Override
	public boolean isLeaf() {
		return getChildCount() == 0;
	}

	@Override
    public String getRowKey() {
		if (parent == null) return "root";
		else if (parent.parent == null) return String.valueOf(rowKey);
		else return parent.getRowKey() + "_" + String.valueOf(rowKey);
    }

	protected void updateRowKeys() {
		int i = 0;
		for (BasicTreeNode<D> node : getFilteredChildren()) {
			node.setRowKey(i);
			++i;
		}
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

		BasicTreeNode<D> other = (BasicTreeNode<D>) obj;
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
}
