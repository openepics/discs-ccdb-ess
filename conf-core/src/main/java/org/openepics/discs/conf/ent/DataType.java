package org.openepics.discs.conf.ent;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Data Type used in various {@link Property} entities
 *
 * @author vuppala
 */
@Entity
@Table(name = "data_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DataType.findAll", query = "SELECT d FROM DataType d"),
    @NamedQuery(name = "DataType.findByName", query = "SELECT d FROM DataType d WHERE d.name = :name"),
    @NamedQuery(name = "DataType.findByDataTypeId", query = "SELECT d FROM DataType d WHERE d.id = :id"),
    @NamedQuery(name = "DataType.findByModifiedBy", query = "SELECT d FROM DataType d WHERE d.modifiedBy = :modifiedBy")
})
public class DataType extends ConfigurationEntity {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name", unique = true)
    private String name;

    @Basic(optional = false)
    @NotNull
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Column(name = "scalar")
    private boolean scalar;

    @Column(name = "definition", columnDefinition="TEXT")
    private String definition;

    protected DataType() {
    }

    public DataType(String name, String description, boolean scalar, String definition) {
        this.name = name;
        this.description = description;
        this.scalar = scalar;
        this.definition = definition;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isScalar() {
        return scalar;
    }
    public void setScalar(boolean scalar) {
        this.scalar = scalar;
    }

    public String getDefinition() {
        return definition;
    }
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "DataType[ dataTypeId=" + id + " ]";
    }
}
