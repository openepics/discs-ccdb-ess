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
package org.openepics.discs.ccdb.gui.ui.trees;

import java.util.List;

/**
 * This type of 'tree node' asks Tree what are its children.
 * Extrinsic info about children let's us use the same nodes implementation for quite different trees.
 *
 * @author ilist
 *
 * @param <D> type of data
 */
public abstract class TreeNodeWithTree<D> extends BasicTreeNode<D> {
	private Tree<D> tree;

	/**
	 * Besides data and parent, construction is also given the tree.
	 *
	 * @param data data
	 * @param parent parent node
	 * @param tree the tree
	 */
	public TreeNodeWithTree(D data, BasicTreeNode<D> parent, Tree<D> tree) {
		super(data, parent);
		this.tree = tree;
	}

	/**
	 * Returns the children it gets from the parent tree. No caching.
	 *
	 * @return the children
	 */
	@Override
	public List<? extends BasicTreeNode<D>> getAllChildren() {
		return tree.getAllChildren(this);
	}

	/**
	 * Returns the tree.
	 * @return the tree.
	 */
	protected Tree<D> getTree() {
		return tree;
	}
}
