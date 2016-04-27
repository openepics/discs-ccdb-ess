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
package org.openepics.discs.ccdb.core.util;

import org.openepics.discs.ccdb.model.ComptypeArtifact;
import org.openepics.discs.ccdb.model.ComptypePropertyValue;
import org.openepics.discs.ccdb.model.DeviceArtifact;
import org.openepics.discs.ccdb.model.DevicePropertyValue;
import org.openepics.discs.ccdb.model.InstallationArtifact;
import org.openepics.discs.ccdb.model.SlotArtifact;
import org.openepics.discs.ccdb.model.SlotPropertyValue;

/**
 * Helper class to retrieve owner of the relationship
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 *
 */
public class ParentEntityResolver {
    private ParentEntityResolver() {}

    /**
     * Checks if input entity is a type of *ParameterValue or *Artifact and returns the entity
     * that is the owner of the relationship. If input is not one of those types, same entity is returned
     *
     * @param entity Input entity
     * @return Owner of the relationship for this entity
     */
    public static Object resolveParentEntity(Object entity) {
        final Object parentEntity;
        if (entity instanceof ComptypePropertyValue) {
            parentEntity = ((ComptypePropertyValue) entity).getComponentType();
        } else if (entity instanceof ComptypeArtifact) {
            parentEntity = ((ComptypeArtifact) entity).getComponentType();
        } else if (entity instanceof SlotPropertyValue) {
            parentEntity = ((SlotPropertyValue) entity).getSlot();
        } else if (entity instanceof SlotArtifact) {
            parentEntity = ((SlotArtifact) entity).getSlot();
        } else if (entity instanceof DevicePropertyValue) {
            parentEntity = ((DevicePropertyValue) entity).getDevice();
        } else if (entity instanceof DeviceArtifact) {
            parentEntity = ((DeviceArtifact) entity).getDevice();
        } else if (entity instanceof InstallationArtifact) {
            parentEntity = ((InstallationArtifact) entity).getInstallationRecord();
        } else {
            parentEntity = entity;
        }
        return parentEntity;
    }
}
