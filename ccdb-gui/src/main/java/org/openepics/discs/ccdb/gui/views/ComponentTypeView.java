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
package org.openepics.discs.conf.views;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.openepics.discs.conf.ent.ComponentType;

/**
 * The Component view to show in the Device Type screen.
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class ComponentTypeView implements Serializable {
    private static final long serialVersionUID = 1L;

    private ComponentType componentType;
    private String usedBy;

    /**
     * <p>
     * If this constructor is missing, the JSF framework produces the <code>java.lang.InstantiationException</code>.
     * </p><p>
     * If this constructor is made private, it reveals this problem:
     * <tt>java.lang.IllegalAccessException: Class javax.faces.component.StateHolderSaver can not access a member of
     * class org.openepics.discs.conf.views.ComponentTypeView with modifiers "private"</tt>
     * </p>
     */
    public ComponentTypeView() {
        componentType = new ComponentType();
    }

    /** New ComponentTypeView based on {@link ComponentType}
     * @param componentType the {@link ComponentType}
     */
    public ComponentTypeView(ComponentType componentType) {
        this.componentType = componentType;
    }

    /** @return The name of the device type the user is adding or modifying. Used in the UI dialog. */
    @NotNull
    @Size(min = 1, max = 32, message = "Name can have at most 32 characters.")
    public String getName() {
        return componentType.getName();
    }

    /** @return The description of the device type the user is adding or modifying. Used in the UI dialog. */
    @Size(max = 255, message = "Description can have at most 255 characters.")
    public String getDescription() {
        return componentType.getDescription();
    }

    /** @return the usedBy */
    public String getUsedBy() {
        return usedBy;
    }

    /** @param usedBy the usedBy to set */
    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    /** @return the id */
    public Long getId() {
        return componentType.getId();
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    /** @param name The name of the device type the user is adding or modifying. Used in the UI dialog. */
    public void setName(String name) {
        componentType.setName(name);
    }

    /** @param description The description of the device type the user is adding or modifying. Used in the UI dialog. */
    public void setDescription(String description) {
        componentType.setDescription(description);
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }
}
