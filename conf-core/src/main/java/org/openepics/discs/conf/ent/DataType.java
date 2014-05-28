/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    @NamedQuery(name = "DataType.findByDataTypeId", query = "SELECT d FROM DataType d WHERE d.dataTypeId = :dataTypeId"),
    @NamedQuery(name = "DataType.findByDescription", query = "SELECT d FROM DataType d WHERE d.description = :description"),
    @NamedQuery(name = "DataType.findByScalar", query = "SELECT d FROM DataType d WHERE d.scalar = :scalar"),
    @NamedQuery(name = "DataType.findByModifiedAt", query = "SELECT d FROM DataType d WHERE d.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "DataType.findByModifiedBy", query = "SELECT d FROM DataType d WHERE d.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "DataType.findByVersion", query = "SELECT d FROM DataType d WHERE d.version = :version")})
public class DataType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "data_type_id")
    private String dataTypeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "scalar")
    private boolean scalar;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "modified_by")
    private String modifiedBy;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version")
    private int version;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataType")
    private List<Property> propertyList;

    public DataType() {
    }

    public DataType(String dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public DataType(String dataTypeId, String description, boolean scalar, Date modifiedAt, String modifiedBy, int version) {
        this.dataTypeId = dataTypeId;
        this.description = description;
        this.scalar = scalar;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public String getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(String dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getScalar() {
        return scalar;
    }

    public void setScalar(boolean scalar) {
        this.scalar = scalar;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @XmlTransient
    public List<Property> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dataTypeId != null ? dataTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataType)) {
            return false;
        }
        DataType other = (DataType) object;
        if ((this.dataTypeId == null && other.dataTypeId != null) || (this.dataTypeId != null && !this.dataTypeId.equals(other.dataTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.DataType[ dataTypeId=" + dataTypeId + " ]";
    }
    
}
