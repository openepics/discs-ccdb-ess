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
@Table(name = "device_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DeviceProperty.findAll", query = "SELECT d FROM DeviceProperty d"),
    @NamedQuery(name = "DeviceProperty.findByDevPropId", query = "SELECT d FROM DeviceProperty d WHERE d.id = :id"),
    @NamedQuery(name = "DeviceProperty.findByInRepository", query = "SELECT d FROM DeviceProperty d WHERE d.inRepository = :inRepository"),
    @NamedQuery(name = "DeviceProperty.findByModifiedBy", query = "SELECT d FROM DeviceProperty d WHERE d.modifiedBy = :modifiedBy")})
public class DeviceProperty extends PropertyValue {
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "device")
    @ManyToOne(optional = false)
    private Device device;

    protected DeviceProperty() { }

    public DeviceProperty(boolean inRepository, String modifiedBy) {
        super(inRepository, modifiedBy);
    }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    @Override
    public String toString() {
        return "DeviceProperty[ devPropId=" + id + " ]";
    }
}
