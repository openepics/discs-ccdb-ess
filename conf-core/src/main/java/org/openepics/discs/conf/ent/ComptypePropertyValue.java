package org.openepics.discs.conf.ent;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "comptype_property_values")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComptypePropertyValue.findAll", query = "SELECT c FROM ComptypeProperty c"),
    @NamedQuery(name = "ComptypePropertyValue.findByCtypePropId", query = "SELECT c FROM ComptypeProperty c WHERE c.id = :id"),
    @NamedQuery(name = "ComptypePropertyValue.findByInRepository", query = "SELECT c FROM ComptypeProperty c WHERE c.inRepository = :inRepository"),
    @NamedQuery(name = "ComptypePropertyValue.findByModifiedBy", query = "SELECT c FROM ComptypeProperty c WHERE c.modifiedBy = :modifiedBy")})
public class ComptypePropertyValue extends PropertyValue {
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "component_type")
    @ManyToOne(optional = false)
    private ComponentType componentType;

    protected ComptypePropertyValue() { }

    public ComptypePropertyValue(boolean inRepository, String modifiedBy) {
        super(inRepository, modifiedBy);
    }

    public ComponentType getComponentType() { return componentType; }
    public void setComponentType(ComponentType componentType) { this.componentType = componentType; }

    @Override
    public String toString() { return "ComptypeProperty[ ctypePropId=" + id + " ]"; }
}
