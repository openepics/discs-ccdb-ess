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
        int hash = 0;
        hash += (configId != null ? configId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Config)) return false;

        Config other = (Config) object;
        if (this.configId == null && other.configId != null) return false;
        if (this.configId != null) return this.configId.equals(other.configId); // return true for same DB entity

        return this==object;
    }

    @Override
    public String toString() {
        return "Config[ configId=" + configId + " ]";
    }

}
