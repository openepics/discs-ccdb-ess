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
package org.openepics.discs.conf.ui.common;

import org.primefaces.model.TreeNode;

public interface HierarchyBuilder {

    /** This method is called when the user expands the tree node in the UI. If the tree node is still not initialized
     * (its subtree only contains the stub to show expansion mark), then the subtree is initialized, and the node is
     * also marked initialized.
     * @param node the {@link TreeNode} to add children to.
     */
    public abstract void expandNode(TreeNode node);

}
