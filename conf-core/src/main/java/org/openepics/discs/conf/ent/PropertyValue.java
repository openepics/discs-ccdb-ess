package org.openepics.discs.conf.ent;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.openepics.discs.conf.ent.values.Value;

/**
 * An abstract property for a Configuration Db entity
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class PropertyValue extends ConfigurationEntity {
    @Column(name = "prop_value", columnDefinition = "TEXT")
    private Value propValue;

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

    public Value getPropValue() {
        return propValue;
    }
    public void setPropValue(Value propValue) {
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
