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
@Table(name = "slot_log_rec")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotLogRec.findAll", query = "SELECT s FROM SlotLogRec s"),
    @NamedQuery(name = "SlotLogRec.findBySlotLogRecId", query = "SELECT s FROM SlotLogRec s WHERE s.slotLogRecId = :slotLogRecId"),
    @NamedQuery(name = "SlotLogRec.findByLogTime", query = "SELECT s FROM SlotLogRec s WHERE s.logTime = :logTime"),
    @NamedQuery(name = "SlotLogRec.findByUser", query = "SELECT s FROM SlotLogRec s WHERE s.user = :user")})
public class SlotLogRec implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "slot_log_rec_id")
    private Integer slotLogRecId;
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
    @JoinColumn(name = "slot", referencedColumnName = "slot_id")
    @ManyToOne(optional = false)
    private Slot slot;

    public SlotLogRec() {
    }

    public SlotLogRec(Integer slotLogRecId) {
        this.slotLogRecId = slotLogRecId;
    }

    public SlotLogRec(Integer slotLogRecId, Date logTime, String user, String entry) {
        this.slotLogRecId = slotLogRecId;
        this.logTime = logTime;
        this.user = user;
        this.entry = entry;
    }

    public Integer getSlotLogRecId() {
        return slotLogRecId;
    }

    public void setSlotLogRecId(Integer slotLogRecId) {
        this.slotLogRecId = slotLogRecId;
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

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (slotLogRecId != null ? slotLogRecId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SlotLogRec)) {
            return false;
        }
        SlotLogRec other = (SlotLogRec) object;
        if ((this.slotLogRecId == null && other.slotLogRecId != null) || (this.slotLogRecId != null && !this.slotLogRecId.equals(other.slotLogRecId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.SlotLogRec[ slotLogRecId=" + slotLogRecId + " ]";
    }
    
}
