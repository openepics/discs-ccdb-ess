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
import javax.validation.constraints.Size;


/**
* A super-class used for most of the Configuration Database entities.
* Used as a {@link MappedSuperclass}.
*
* @author Miha Vitorovic
*/
@MappedSuperclass
public class ConfigurationEntity implements Serializable {
    private static final long serialVersionUID = -3251838692627723803L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected Long id;

    @Basic(optional = false)
    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifiedAt = new Date(0L);

    @Basic(optional = false)
    @Size(min = 1, max = 64)
    @Column(name = "modified_by")
    protected String modifiedBy;

    @Version
    protected Long version;

    /**
     * @return The database primary key of the configuration entity
     */
    public Long getId() {
        return id;
    }

    /**
     * @return The timestamp of the last modification of this database entity
     */
    public Date getModifiedAt() {
        return new Date(modifiedAt.getTime());
    }
    /**
     * The setter stores a new copy of the param.
     *
     * @param modifiedAt The timestamp of the last modification of this database entity
     */
    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = new Date(modifiedAt.getTime());
    }

    /**
     * @return The user performing the last modification of the database entity
     */
    public String getModifiedBy() {
        return modifiedBy;
    }
    /**
     * @param modifiedBy The user performing the last modification of the database entity
     */
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

        // return true for the same DB entity
        if (this.id != null) {
            return this.id.equals(other.id);
        }

        return this==object;
    }
}
