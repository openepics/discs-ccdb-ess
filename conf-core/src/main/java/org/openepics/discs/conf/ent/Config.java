/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "config")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Config.findAll", query = "SELECT c FROM Config c"),
    @NamedQuery(name = "Config.findByName", query = "SELECT c FROM Config c WHERE c.name = :name"),
    @NamedQuery(name = "Config.findByPropValue", query = "SELECT c FROM Config c WHERE c.propValue = :propValue")})
public class Config implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
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

    public Config(String name) {
        this.name = name;
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

    protected Long getVersion() {
        return version;
    }

    protected void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Config)) return false;

        Config other = (Config) object;
        if (this.name == null && other.name != null) return false;
        if (this.name != null) return this.name.equals(other.name); // return true for same DB entity

        return this==object;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.Config[ name=" + name + " ]";
    }

}
