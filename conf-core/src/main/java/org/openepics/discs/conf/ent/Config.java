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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A support entity used to store key-value type information in the database.
 *
 * @author vuppala
 */
@Entity
@Table(name = "config")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Config.findAll", query = "SELECT c FROM Config c"),
    @NamedQuery(name = "Config.findByName", query = "SELECT c FROM Config c WHERE c.name = :name"),
    @NamedQuery(name = "Config.findByPropValue", query = "SELECT c FROM Config c WHERE c.propValue = :propValue")
})
public class Config implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "config_id")
    private Integer configId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;

    @Size(max = 128)
    @Column(name = "prop_value")
    private String propValue;

    @Version
    private Long version;

    public Config() {
    }

    public Config(String name, String propValue) {
        this.name = name;
        this.propValue = propValue;
    }

    public int getCofingId() {
        return configId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    @Override
    public int hashCode() {
        return configId != null ? configId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Config)) {
            return false;
        }

        Config other = (Config) object;
        if (this.configId == null && other.configId != null) {
            return false;
        }
        // return true for same DB entity
        if (this.configId != null) {
            return this.configId.equals(other.configId);
        }

        return this==object;
    }

    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Config[ configId=" + configId + " ]";
    }
}
