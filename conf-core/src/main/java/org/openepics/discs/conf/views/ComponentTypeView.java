package org.openepics.discs.conf.views;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.openepics.discs.conf.ent.ComponentType;

public class ComponentTypeView {
    private ComponentType componentType;
    private String usedBy;

    public ComponentTypeView(ComponentType componentType)
    {
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

    public ComponentType getComponentType()
    {
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
