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
package org.openepics.discs.conf.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypeAsm;
import org.openepics.discs.conf.ent.ComptypePropertyValue;

/**
 * DAO Service for accesing Component Types ( {@link ComponentType} )
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */

@Stateless
public class ComptypeEJB extends DAO<ComponentType> {
    @Override
    protected void defineEntity() {
        defineEntityClass(ComponentType.class);

        defineParentChildInterface(ComptypePropertyValue.class,
                new ParentChildInterface<ComponentType, ComptypePropertyValue>() {
            @Override
            public List<ComptypePropertyValue> getChildCollection(ComponentType type) {
                return type.getComptypePropertyList();
            }
            @Override
            public ComponentType getParentFromChild(ComptypePropertyValue child) {
                return child.getComponentType();
            }
        });

        defineParentChildInterface(ComptypeArtifact.class, new ParentChildInterface<ComponentType, ComptypeArtifact>() {
            @Override
            public List<ComptypeArtifact> getChildCollection(ComponentType type) {
                return type.getComptypeArtifactList();
            }
            @Override
            public ComponentType getParentFromChild(ComptypeArtifact child) {
                return child.getComponentType();
            }
        });

        defineParentChildInterface(ComptypeAsm.class, new ParentChildInterface<ComponentType, ComptypeAsm>() {
            @Override
            public List<ComptypeAsm> getChildCollection(ComponentType type) {
                return type.getChildrenComptypeAsmList();
            }
            @Override
            public ComponentType getParentFromChild(ComptypeAsm child) {
                return child.getParentType();
            }
        });
    }

    /**
     * @return A list of all device types ordered by name.
     */
    public List<ComponentType> findComponentTypeOrderedByName() {
        return em.createNamedQuery("ComponentType.findAllOrdered", ComponentType.class).getResultList();
    }

    /**
     * @param componentType - the device type
     * @return A list of all property definitions for the selected device type.
     */
    public List<ComptypePropertyValue> findPropertyDefinitions(ComponentType componentType) {
        return em.createNamedQuery("ComptypePropertyValue.findPropertyDefs", ComptypePropertyValue.class)
               .setParameter("componentType", componentType).getResultList();
    }

}
