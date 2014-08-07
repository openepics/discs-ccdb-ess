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
@Table(name = "device_property_values")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DevicePropertyValue.findAll", query = "SELECT d FROM DeviceProperty d"),
    @NamedQuery(name = "DevicePropertyValue.findByDevPropId", query = "SELECT d FROM DeviceProperty d WHERE d.id = :id"),
    @NamedQuery(name = "DevicePropertyValue.findByInRepository", query = "SELECT d FROM DeviceProperty d WHERE d.inRepository = :inRepository"),
    @NamedQuery(name = "DevicePropertyValue.findByModifiedBy", query = "SELECT d FROM DeviceProperty d WHERE d.modifiedBy = :modifiedBy")})
public class DevicePropertyValue extends PropertyValue {
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "device")
    @ManyToOne(optional = false)
    private Device device;

    protected DevicePropertyValue() { }

    public DevicePropertyValue(boolean inRepository, String modifiedBy) {
        super(inRepository, modifiedBy);
    }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    @Override
    public String toString() { return "DeviceProperty[ devPropId=" + id + " ]"; }
}
