package org.openepics.discs.conf.views;

import org.openepics.discs.conf.ejb.ComptypeEJB;
import org.openepics.discs.conf.ent.ComponentType;

public class ComponentTypeView {
    private final Long id;
    private final String name;
    private final String description;
    private String usedBy;

    public ComponentTypeView()
    {
        id = null;
        name = null;
        description = null;
    }

    public ComponentTypeView(ComponentType componentType)
    {
        id = componentType.getId();
        name = componentType.getName();
        description = componentType.getDescription();
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the usedBy
     */
    public String getUsedBy() {
        return usedBy;
    }

    /**
     * @param usedBy the usedBy to set
     */
    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }


    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    public ComponentType findComponentType(ComptypeEJB ejb)
    {
        return ejb.findById(id);
    }
}
