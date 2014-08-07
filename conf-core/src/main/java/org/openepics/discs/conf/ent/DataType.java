package org.openepics.discs.conf.ent;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
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
    @NamedQuery(name = "DataType.findByModifiedBy", query = "SELECT d FROM DataType d WHERE d.modifiedBy = :modifiedBy")})
public class DataType extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataType")
    private List<Property> propertyList;

    protected DataType() {
    }

    public DataType(String name, String description, boolean scalar, String definition, String modifiedBy) {
        this.name = name;
        this.description = description;
        this.scalar = scalar;
        this.definition = definition;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description;     }

    public boolean isScalar() { return scalar; }
    public void setScalar(boolean scalar) { this.scalar = scalar; }

    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }

    @XmlTransient
    public List<Property> getPropertyList() { return propertyList; }

    @Override
    public String toString() { return "DataType[ dataTypeId=" + id + " ]"; }
}
