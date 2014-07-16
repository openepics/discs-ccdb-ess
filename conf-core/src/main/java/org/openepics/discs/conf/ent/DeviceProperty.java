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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "device_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DeviceProperty.findAll", query = "SELECT d FROM DeviceProperty d"),
    @NamedQuery(name = "DeviceProperty.findByDevPropId", query = "SELECT d FROM DeviceProperty d WHERE d.id = :id"),
    @NamedQuery(name = "DeviceProperty.findByInRepository", query = "SELECT d FROM DeviceProperty d WHERE d.inRepository = :inRepository"),
    @NamedQuery(name = "DeviceProperty.findByModifiedAt", query = "SELECT d FROM DeviceProperty d WHERE d.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "DeviceProperty.findByModifiedBy", query = "SELECT d FROM DeviceProperty d WHERE d.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "DeviceProperty.findByVersion", query = "SELECT d FROM DeviceProperty d WHERE d.version = :version")})
public class DeviceProperty extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "prop_value", columnDefinition="TEXT")
    private String propValue;

    @Basic(optional = false)
    @NotNull
    @Column(name = "in_repository")
    private boolean inRepository;

    @JoinColumn(name = "property", referencedColumnName = "property_id")
    @ManyToOne(optional = false)
    private Property property;

    @JoinColumn(name = "unit", referencedColumnName = "unit_id")
    @ManyToOne
    private Unit unit;

    @JoinColumn(name = "device", referencedColumnName = "device_id")
    @ManyToOne(optional = false)
    private Device device;

    protected DeviceProperty() {
    }

    public DeviceProperty(boolean inRepository, String modifiedBy) {
        this.inRepository = inRepository;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
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

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "DeviceProperty[ devPropId=" + id + " ]";
    }

}
