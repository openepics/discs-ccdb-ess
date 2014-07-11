/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "comptype_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComptypeProperty.findAll", query = "SELECT c FROM ComptypeProperty c"),
    @NamedQuery(name = "ComptypeProperty.findByCtypePropId", query = "SELECT c FROM ComptypeProperty c WHERE c.ctypePropId = :ctypePropId"),
    @NamedQuery(name = "ComptypeProperty.findByType", query = "SELECT c FROM ComptypeProperty c WHERE c.type = :type"),
    @NamedQuery(name = "ComptypeProperty.findByInRepository", query = "SELECT c FROM ComptypeProperty c WHERE c.inRepository = :inRepository"),
    @NamedQuery(name = "ComptypeProperty.findByModifiedAt", query = "SELECT c FROM ComptypeProperty c WHERE c.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "ComptypeProperty.findByModifiedBy", query = "SELECT c FROM ComptypeProperty c WHERE c.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "ComptypeProperty.findByVersion", query = "SELECT c FROM ComptypeProperty c WHERE c.version = :version")})
public class ComptypeProperty implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ctype_prop_id")
    private Integer ctypePropId;

    @Size(max = 4)
    @Column(name = "type")
    private String type;

    @Column(name = "prop_value", columnDefinition="TEXT")
    private String propValue;
    @Basic(optional = false)

    @NotNull
    @Column(name = "in_repository")
    private boolean inRepository;

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

    @Version
    private Long version;

    @JoinColumn(name = "unit", referencedColumnName = "unit_id")
    @ManyToOne
    private Unit unit;

    @JoinColumn(name = "component_type", referencedColumnName = "component_type_id")
    @ManyToOne(optional = false)
    private ComponentType componentType;

    @JoinColumn(name = "property", referencedColumnName = "property_id")
    @ManyToOne(optional = false)
    private Property property;

    protected ComptypeProperty() {
    }

    public ComptypeProperty(boolean inRepository, String modifiedBy) {
        this.inRepository = inRepository;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public Integer getCtypePropId() {
        return ctypePropId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public boolean getInRepository() {
        return inRepository;
    }

    public void setInRepository(boolean inRepository) {
        this.inRepository = inRepository;
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

    protected Long getVersion() {
        return version;
    }

    protected void setVersion(Long version) {
        this.version = version;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ctypePropId != null ? ctypePropId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ComptypeProperty)) return false;

        ComptypeProperty other = (ComptypeProperty) object;
        if (this.ctypePropId == null && other.ctypePropId != null) return false;
        if (this.ctypePropId != null) return this.ctypePropId.equals(other.ctypePropId); // return true for same DB entity

        return this==object;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.ComptypeProperty[ ctypePropId=" + ctypePropId + " ]";
    }

}
