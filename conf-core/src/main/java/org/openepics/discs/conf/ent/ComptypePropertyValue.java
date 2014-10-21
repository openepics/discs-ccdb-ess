package org.openepics.discs.conf.ent;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * {@link PropertyValue} attached to a {@link ComponentType} entity
 *
 * @author vuppala
 */
@Entity
@Table(name = "comptype_property_value", indexes = { @Index(columnList = "component_type, prop_value") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComptypePropertyValue.findAll", query = "SELECT c FROM ComptypePropertyValue c"),
    @NamedQuery(name = "ComptypePropertyValue.findPropertyDefs", query = "SELECT c FROM ComptypePropertyValue c "
            + "WHERE c.componentType = :componentType AND c.isPropertyDefinition = TRUE"),
    @NamedQuery(name = "ComptypePropertyValue.findByInRepository", query = "SELECT c FROM ComptypePropertyValue c "
            + "WHERE c.inRepository = :inRepository"),
    @NamedQuery(name = "ComptypePropertyValue.findByModifiedBy", query = "SELECT c FROM ComptypePropertyValue c "
            + "WHERE c.modifiedBy = :modifiedBy")
})
public class ComptypePropertyValue extends PropertyValue {
    @JoinColumn(name = "component_type")
    @ManyToOne(optional = false)
    private ComponentType componentType;

    @Basic(optional = false)
    @NotNull
    @Column(name = "is_property_definition")
    private boolean isPropertyDefinition;

    public ComptypePropertyValue() { }

    public ComptypePropertyValue(boolean inRepository) {
        super(inRepository);
    }

    public ComponentType getComponentType() {
        return componentType;
    }
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public boolean isPropertyDefinition() {
        return isPropertyDefinition;
    }

    public void setPropertyDefinition(boolean isPropertyDefinition) {
        this.isPropertyDefinition = isPropertyDefinition;
    }

    @Override
    public String toString() {
        return "ComptypeProperty[ ctypePropId=" + id + " ]";
    }
}
