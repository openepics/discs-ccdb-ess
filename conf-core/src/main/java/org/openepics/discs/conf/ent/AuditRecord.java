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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "audit_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AuditRecord.findAll", query = "SELECT a FROM AuditRecord a"),
    @NamedQuery(name = "AuditRecord.findByAuditRecordId", query = "SELECT a FROM AuditRecord a WHERE a.auditRecordId = :auditRecordId"),
    @NamedQuery(name = "AuditRecord.findByLogTime", query = "SELECT a FROM AuditRecord a WHERE a.logTime = :logTime"),
    @NamedQuery(name = "AuditRecord.findByOper", query = "SELECT a FROM AuditRecord a WHERE a.oper = :oper"),
    @NamedQuery(name = "AuditRecord.findByUser", query = "SELECT a FROM AuditRecord a WHERE a.user = :user"),
    @NamedQuery(name = "AuditRecord.findByEntityType", query = "SELECT a FROM AuditRecord a WHERE a.entityType = :entityType"),
    @NamedQuery(name = "AuditRecord.findByEntityKey", query = "SELECT a FROM AuditRecord a WHERE a.entityKey = :entityKey")})
public class AuditRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "audit_record_id")
    private Integer auditRecordId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "log_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logTime;
    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "oper")
    private EntityTypeOperation oper;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "\"user\"")
    private String user;
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private EntityType entityType;
    @Size(max = 64)
    @Column(name = "entity_key")
    private String entityKey;
    @Basic(optional = false)
    @NotNull
    @Column(name = "entry", columnDefinition="TEXT")
    private String entry;

    public AuditRecord() {
    }

    public AuditRecord(Integer auditRecordId) {
        this.auditRecordId = auditRecordId;
    }

    public AuditRecord(Integer auditRecordId, Date logTime, EntityTypeOperation oper, String user, String entry) {
        this.auditRecordId = auditRecordId;
        this.logTime = logTime;
        this.oper = oper;
        this.user = user;
        this.entry = entry;
    }

    public Integer getAuditRecordId() {
        return auditRecordId;
    }

    public void setAuditRecordId(Integer auditRecordId) {
        this.auditRecordId = auditRecordId;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public EntityTypeOperation getOper() {
        return oper;
    }

    public void setOper(EntityTypeOperation oper) {
        this.oper = oper;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
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
        hash += (auditRecordId != null ? auditRecordId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AuditRecord)) {
            return false;
        }
        AuditRecord other = (AuditRecord) object;
        if ((this.auditRecordId == null && other.auditRecordId != null) || (this.auditRecordId != null && !this.auditRecordId.equals(other.auditRecordId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.AuditRecord[ auditRecordId=" + auditRecordId + " ]";
    }
    
}
