/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "device_log_rec")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DeviceLogRec.findAll", query = "SELECT d FROM DeviceLogRec d"),
    @NamedQuery(name = "DeviceLogRec.findByDevLogRecId", query = "SELECT d FROM DeviceLogRec d WHERE d.devLogRecId = :devLogRecId"),
    @NamedQuery(name = "DeviceLogRec.findByLogTime", query = "SELECT d FROM DeviceLogRec d WHERE d.logTime = :logTime"),
    @NamedQuery(name = "DeviceLogRec.findByUser", query = "SELECT d FROM DeviceLogRec d WHERE d.user = :user")})
public class DeviceLogRec implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "dev_log_rec_id")
    private Integer devLogRecId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "log_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logTime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "user")
    private String user;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "entry")
    private String entry;
    @JoinColumn(name = "device", referencedColumnName = "device_id")
    @ManyToOne(optional = false)
    private Device device;

    public DeviceLogRec() {
    }

    public DeviceLogRec(Integer devLogRecId) {
        this.devLogRecId = devLogRecId;
    }

    public DeviceLogRec(Integer devLogRecId, Date logTime, String user, String entry) {
        this.devLogRecId = devLogRecId;
        this.logTime = logTime;
        this.user = user;
        this.entry = entry;
    }

    public Integer getDevLogRecId() {
        return devLogRecId;
    }

    public void setDevLogRecId(Integer devLogRecId) {
        this.devLogRecId = devLogRecId;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (devLogRecId != null ? devLogRecId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DeviceLogRec)) {
            return false;
        }
        DeviceLogRec other = (DeviceLogRec) object;
        if ((this.devLogRecId == null && other.devLogRecId != null) || (this.devLogRecId != null && !this.devLogRecId.equals(other.devLogRecId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.DeviceLogRec[ devLogRecId=" + devLogRecId + " ]";
    }
    
}
