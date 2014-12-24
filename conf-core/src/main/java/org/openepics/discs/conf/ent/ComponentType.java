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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A {@link ComponentType} entity defines the type (set of properties) for a device and/or a slot.
 *
 * @author vuppala
 */
@Entity
@Table(name = "component_type", indexes = { @Index(columnList = "super_component_type") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentType.findAll", query = "SELECT c FROM ComponentType c"),
    @NamedQuery(name = "ComponentType.findUserTypesOnly", query = "SELECT c FROM ComponentType c "
            + "WHERE c.name <> :internalType1 AND c.name <> :internalType2 ORDER BY c.name"),
    @NamedQuery(name = "ComponentType.findByName", query = "SELECT c FROM ComponentType c WHERE c.name = :name"),
    @NamedQuery(name = "ComponentType.findAllOrdered", query = "SELECT c FROM ComponentType c ORDER BY c.name"),
    @NamedQuery(name = "ComponentType.findByModifiedBy", query = "SELECT c FROM ComponentType c "
            + "WHERE c.modifiedBy = :modifiedBy")
})
public class ComponentType extends ConfigurationEntity {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "name", unique = true)
    private String name;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<ComptypePropertyValue> comptypePropertyList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "childType")
    private List<ComptypeAsm> childrenTypes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentType")
    private List<ComptypeAsm> parentTypes;

    @OneToMany(mappedBy = "superComponentType")
    private List<ComponentType> componentTypeList;

    @JoinColumn(name = "super_component_type")
    @ManyToOne
    private ComponentType superComponentType;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<ComptypeArtifact> comptypeArtifactList = new ArrayList<>();

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "comptype_tag",
               joinColumns = { @JoinColumn(name = "comptype_id") },
               inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<Tag> tags = new HashSet<>();

    public ComponentType() {}

    public ComponentType(String name) {
        this.name = name;
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

    @XmlTransient
    @JsonIgnore
    public List<ComptypePropertyValue> getComptypePropertyList() {
        return comptypePropertyList;
    }

    @XmlTransient
    @JsonIgnore
    public List<ComptypeAsm> getChildrenComptypeAsmList() {
        return childrenTypes;
    }

    @XmlTransient
    @JsonIgnore
    public List<ComptypeAsm> getParentComptypeAsmList() {
        return parentTypes;
    }

    @XmlTransient
    @JsonIgnore
    public List<ComponentType> getComponentTypeList() {
        return componentTypeList;
    }

    public ComponentType getSuperComponentType() {
        return superComponentType;
    }
    public void setSuperComponentType(ComponentType superComponentType) {
        this.superComponentType = superComponentType;
    }

    @XmlTransient
    @JsonIgnore
    public List<ComptypeArtifact> getComptypeArtifactList() {
        return comptypeArtifactList;
    }

    @XmlTransient
    @JsonIgnore
    public Set<Tag> getTags() {
        return tags;
    }
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "ComponentType[ componentTypeId=" + id + " ]";
    }
}
