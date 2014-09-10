package org.openepics.discs.conf.ent;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "property_value")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class PropertyValue extends ConfigurationEntity {
    @Column(name = "prop_value", columnDefinition = "TEXT")
    private String propValue;

    @Basic(optional = false)
    @NotNull
    @Column(name = "in_repository")
    private boolean inRepository;

    @JoinColumn(name = "property")
    @ManyToOne(optional = false)
    private Property property;

    @JoinColumn(name = "unit")
    @ManyToOne
    private Unit unit;

    protected PropertyValue() { }

    public PropertyValue(boolean inRepository) {
        this.inRepository = inRepository;
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

    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }

    public Unit getUnit() {
        return unit;
    }
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
