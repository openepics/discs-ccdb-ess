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
package org.openepics.discs.ccdb.core.ejb;

import java.util.List;

/**
 * Classes implementing this interface are used to map between parent adnd child entities, where the relationship
 * exists.
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 * @param <T> the parent class
 * @param <S> the child class
 */
public interface ParentChildInterface<T, S> {
    /**
     * This method resolves the collection ( {@link List} ) of children from the parent entity.
     *
     * @param parent the parent entity
     * @return the {@link List} of children of the parent entity
     */
    public List<S> getChildCollection(T parent);

    /**
     * This method resolves the parent, given a child entity
     *
     * @param child the child entity
     * @return the parent entity
     */
    public T getParentFromChild(S child);
}
