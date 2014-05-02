/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author vuppala
 */
@Embeddable
public class DevicePropertyPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "device")
    private int device;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "property")
    private String property;

    public DevicePropertyPK() {
    }

    public DevicePropertyPK(int device, String property) {
        this.device = device;
        this.property = property;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) device;
        hash += (property != null ? property.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DevicePropertyPK)) {
            return false;
        }
        DevicePropertyPK other = (DevicePropertyPK) object;
        if (this.device != other.device) {
            return false;
        }
        if ((this.property == null && other.property != null) || (this.property != null && !this.property.equals(other.property))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.DevicePropertyPK[ device=" + device + ", property=" + property + " ]";
    }
    
}
