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
import javax.persistence.Lob;
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
@Table(name = "comptype_log_rec")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComptypeLogRec.findAll", query = "SELECT c FROM ComptypeLogRec c"),
    @NamedQuery(name = "ComptypeLogRec.findByCtypeLogRecId", query = "SELECT c FROM ComptypeLogRec c WHERE c.ctypeLogRecId = :ctypeLogRecId"),
    @NamedQuery(name = "ComptypeLogRec.findByComponentType", query = "SELECT c FROM ComptypeLogRec c WHERE c.componentType = :componentType"),
    @NamedQuery(name = "ComptypeLogRec.findByLogTime", query = "SELECT c FROM ComptypeLogRec c WHERE c.logTime = :logTime"),
    @NamedQuery(name = "ComptypeLogRec.findByUser", query = "SELECT c FROM ComptypeLogRec c WHERE c.user = :user")})
public class ComptypeLogRec implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ctype_log_rec_id")
    private Integer ctypeLogRecId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "component_type")
    private String componentType;
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

    public ComptypeLogRec() {
    }

    public ComptypeLogRec(Integer ctypeLogRecId) {
        this.ctypeLogRecId = ctypeLogRecId;
    }

    public ComptypeLogRec(Integer ctypeLogRecId, String componentType, Date logTime, String user, String entry) {
        this.ctypeLogRecId = ctypeLogRecId;
        this.componentType = componentType;
        this.logTime = logTime;
        this.user = user;
        this.entry = entry;
    }

    public Integer getCtypeLogRecId() {
        return ctypeLogRecId;
    }

    public void setCtypeLogRecId(Integer ctypeLogRecId) {
        this.ctypeLogRecId = ctypeLogRecId;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ctypeLogRecId != null ? ctypeLogRecId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComptypeLogRec)) {
            return false;
        }
        ComptypeLogRec other = (ComptypeLogRec) object;
        if ((this.ctypeLogRecId == null && other.ctypeLogRecId != null) || (this.ctypeLogRecId != null && !this.ctypeLogRecId.equals(other.ctypeLogRecId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.ComptypeLogRec[ ctypeLogRecId=" + ctypeLogRecId + " ]";
    }
    
}
