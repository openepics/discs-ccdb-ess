package org.openepics.discs.conf.ent;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "comptype_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComptypeProperty.findAll", query = "SELECT c FROM ComptypeProperty c"),
    @NamedQuery(name = "ComptypeProperty.findByCtypePropId", query = "SELECT c FROM ComptypeProperty c WHERE c.id = :id"),
    @NamedQuery(name = "ComptypeProperty.findByType", query = "SELECT c FROM ComptypeProperty c WHERE c.type = :type"),
    @NamedQuery(name = "ComptypeProperty.findByInRepository", query = "SELECT c FROM ComptypeProperty c WHERE c.inRepository = :inRepository"),
    @NamedQuery(name = "ComptypeProperty.findByModifiedAt", query = "SELECT c FROM ComptypeProperty c WHERE c.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "ComptypeProperty.findByModifiedBy", query = "SELECT c FROM ComptypeProperty c WHERE c.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "ComptypeProperty.findByVersion", query = "SELECT c FROM ComptypeProperty c WHERE c.version = :version")})
public class ComptypeProperty extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

    @Size(max = 4)
    @Column(name = "type")
    private String type;

    @Column(name = "prop_value", columnDefinition="TEXT")
    private String propValue;

    @Basic(optional = false)
    @NotNull
    @Column(name = "in_repository")
    private boolean inRepository;

    @JoinColumn(name = "unit", referencedColumnName = "unit_id")
    @ManyToOne
    private Unit unit;

    @JoinColumn(name = "component_type", referencedColumnName = "component_type_id")
    @ManyToOne(optional = false)
    private ComponentType componentType;

    @JoinColumn(name = "property", referencedColumnName = "property_id")
    @ManyToOne(optional = false)
    private Property property;

    protected ComptypeProperty() {
    }

    public ComptypeProperty(boolean inRepository, String modifiedBy) {
        this.inRepository = inRepository;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public boolean getInRepository() {
        return inRepository;
    }

    public void setInRepository(boolean inRepository) {
        this.inRepository = inRepository;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "ComptypeProperty[ ctypePropId=" + id + " ]";
    }

}
