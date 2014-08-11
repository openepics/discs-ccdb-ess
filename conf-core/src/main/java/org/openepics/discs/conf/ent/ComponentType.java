package org.openepics.discs.conf.ent;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "component_type")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "ComponentType.findAll", query = "SELECT c FROM ComponentType c"),
        @NamedQuery(name = "ComponentType.findByComponentTypeId", query = "SELECT c FROM ComponentType c WHERE c.id = :id"),
        @NamedQuery(name = "ComponentType.findByName", query = "SELECT c FROM ComponentType c WHERE c.name = :name"),
        @NamedQuery(name = "ComponentType.findByModifiedBy", query = "SELECT c FROM ComponentType c WHERE c.modifiedBy = :modifiedBy") })
public class ComponentType extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "name", unique = true)
    private String name;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<ComptypePropertyValue> comptypePropertyList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<Slot> slotList;

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
    private List<Device> deviceList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<ComptypeArtifact> comptypeArtifactList;

    @ManyToMany
    @JoinTable(name = "comptype_tags",
        joinColumns = { @JoinColumn(name = "comptype_id") }, inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<Tag> tags;

    public ComponentType() {
        this.modifiedAt = new Date();
    }

    public ComponentType(String name, String modifiedBy) {
        this.name = name;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @XmlTransient
    @JsonIgnore
    public List<ComptypePropertyValue> getComptypePropertyList() { return comptypePropertyList; }

    @XmlTransient
    @JsonIgnore
    public List<Slot> getSlotList() { return slotList; }

    @XmlTransient
    @JsonIgnore
    public List<ComptypeAsm> getComptypeAsmList() { return childrenTypes; }
    public void setComptypeAsmList(List<ComptypeAsm> comptypeAsmList) { this.childrenTypes = comptypeAsmList; }

    @XmlTransient
    @JsonIgnore
    public List<ComptypeAsm> getComptypeAsmList1() { return parentTypes; }
    public void setComptypeAsmList1(List<ComptypeAsm> comptypeAsmList1) { this.parentTypes = comptypeAsmList1; }

    @XmlTransient
    @JsonIgnore
    public List<ComponentType> getComponentTypeList() { return componentTypeList; }

    public ComponentType getSuperComponentType() { return superComponentType; }
    public void setSuperComponentType(ComponentType superComponentType) { this.superComponentType = superComponentType; }

    @XmlTransient
    @JsonIgnore
    public List<Device> getDeviceList() { return deviceList; }

    @XmlTransient
    @JsonIgnore
    public List<ComptypeArtifact> getComptypeArtifactList() { return comptypeArtifactList; }
    public void setComptypeArtifactList(List<ComptypeArtifact> comptypeArtifactList) { this.comptypeArtifactList = comptypeArtifactList; }

    @XmlTransient
    @JsonIgnore
    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    @Override
    public String toString() { return "ComponentType[ componentTypeId=" + id + " ]"; }

}
