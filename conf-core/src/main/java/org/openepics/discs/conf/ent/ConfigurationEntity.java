package org.openepics.discs.conf.ent;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
* A super-class used for most of the Configuration Database entities.
* Used as a {@link MappedSuperclass}.
*
* @author Miha Vitorovic
*/
@MappedSuperclass
public class ConfigurationEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected Long id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifiedAt = new Date(0L);

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "modified_by")
    protected String modifiedBy;

    @Version
    protected Long version;

    public Long getId() {
        return id;
    }

    public Date getModifiedAt() {
        return new Date(modifiedAt.getTime());
    }
    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = new Date(modifiedAt.getTime());
    }

    public String getModifiedBy() {
        return modifiedBy;
    }
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object object) {
        if ((object == null) || (object.getClass() != this.getClass())) {
            return false;
        }

        ConfigurationEntity other = (ConfigurationEntity) object;
        if (this.id == null && other.id != null) {
            return false;
        }

        // return true for same DB entity
        if (this.id != null) {
            return this.id.equals(other.id);
        }

        return this==object;
    }
}
