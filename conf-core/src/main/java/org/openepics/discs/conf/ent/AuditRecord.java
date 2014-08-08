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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
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
    @NamedQuery(name = "AuditRecord.findByLogTime", query = "SELECT a FROM AuditRecord a WHERE a.logTime = :logTime"),
    @NamedQuery(name = "AuditRecord.findByUser", query = "SELECT a FROM AuditRecord a WHERE a.user = :user"),
    @NamedQuery(name = "AuditRecord.findByEntityType", query = "SELECT a FROM AuditRecord a WHERE a.entityType = :entityType"),
    @NamedQuery(name = "AuditRecord.findByEntityKey", query = "SELECT a FROM AuditRecord a WHERE a.entityKey = :entityKey")})
public class AuditRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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
    @Column(name = "ccdb_user")
    private String user;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private EntityType entityType;

    @Column(name = "entity_key")
    private String entityKey;

    @Column(name = "entity_id")
    private Long entityId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "entry", columnDefinition="TEXT")
    private String entry;

    @Version
    private Long version;

    protected AuditRecord() {
    }

    public AuditRecord(EntityTypeOperation oper, String user, String entry, Long entityId) {
        this.logTime = new Date();
        this.oper = oper;
        this.user = user;
        this.entry = entry;
        this.entityId = entityId;
    }

    public Long getId() {
        return id;
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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AuditRecord)) return false;

        AuditRecord other = (AuditRecord) object;
        if (this.id == null && other.id != null) return false;
        if (this.id != null) return this.id.equals(other.id);

        return this==object;
    }

    @Override
    public String toString() {
        return "AuditRecord[ auditRecordId=" + id + " ]";
    }

}
