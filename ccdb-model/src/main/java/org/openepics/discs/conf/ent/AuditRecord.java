/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ent;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

// TODO check
// import org.openepics.discs.conf.util.Conversion;

/**
 * {@link AuditRecord} entity stores audit logs for changes on entity types
 *
 * @author vuppala
 */
@Entity
@Table(name = "audit_record", indexes = { @Index(columnList = "entity_id, entity_type") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AuditRecord.findAll", query = "SELECT a FROM AuditRecord a ORDER BY a.id DESC"),
    @NamedQuery(name = "AuditRecord.findByEntityIdAndType", query = "SELECT a FROM AuditRecord a "
            + "WHERE a.entityId = :entityId AND a.entityType = :entityType ORDER BY a.logTime DESC")
})
public class AuditRecord implements EntityWithId, Serializable {
    private static final long serialVersionUID = 5144501646584495649L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    /**
     * Constructs a new audit record.
     *
     * @param oper the type of database operation (see {@link EntityTypeOperation})
     * @param entry the JSON description of the audit record entry
     * @param entityId the database primary key of the entity the audit log entry is created for
     */
    public AuditRecord(EntityTypeOperation oper, String entry, Long entityId) {
        this.oper = oper;
        this.entry = entry;
        this.entityId = entityId;
    }

    @Override
    public Long getId() {
        return id;
    }

    /** @return a copy of the log entry timestamp */
    public Date getLogTime() {
        return new Date(logTime.getTime());
    }

    public void setLogTime(Date logTime) {
        this.logTime = new Date(logTime.getTime());
    }

    /** @return a string representation of the audit log entry timestamp */
    public String getLogTimeFormatted() {
        // TODO check
        final SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return timestampFormatter.format(logTime);
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
        if (!(object instanceof AuditRecord)) {
            return false;
        }

        AuditRecord other = (AuditRecord) object;
        if ((this.id == null) && (other.id != null)) {
            return false;
        }
        if (this.id != null) {
            return this.id.equals(other.id);
        }

        return this==object;
    }

    @Override
    public String toString() {
        return "AuditRecord[ auditRecordId=" + id + " ]";
    }

}
