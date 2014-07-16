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
@Table(name = "slot_relation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotRelation.findAll", query = "SELECT s FROM SlotRelation s"),
    @NamedQuery(name = "SlotRelation.findBySlotRelationId", query = "SELECT s FROM SlotRelation s WHERE s.id = :id"),
    @NamedQuery(name = "SlotRelation.findByName", query = "SELECT s FROM SlotRelation s WHERE s.name = :name"),
    @NamedQuery(name = "SlotRelation.findByIname", query = "SELECT s FROM SlotRelation s WHERE s.iname = :iname"),
    @NamedQuery(name = "SlotRelation.findByDescription", query = "SELECT s FROM SlotRelation s WHERE s.description = :description"),
    @NamedQuery(name = "SlotRelation.findByModifiedAt", query = "SELECT s FROM SlotRelation s WHERE s.modifiedAt = :modifiedAt"),
    @NamedQuery(name = "SlotRelation.findByModifiedBy", query = "SELECT s FROM SlotRelation s WHERE s.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "SlotRelation.findByVersion", query = "SELECT s FROM SlotRelation s WHERE s.version = :version")})
public class SlotRelation extends ConfigurationEntity {
    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "name")
    private String name;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "iname")
    private String iname;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "slotRelation")
    private List<SlotPair> slotPairList;

    protected SlotRelation() {
    }

    public SlotRelation(String name, String iname, String modifiedBy) {
        this.name = name;
        this.iname = iname;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIname() {
        return iname;
    }

    public void setIname(String iname) {
        this.iname = iname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<SlotPair> getSlotPairList() {
        return slotPairList;
    }

    public void setSlotPairList(List<SlotPair> slotPairList) {
        this.slotPairList = slotPairList;
    }

    @Override
    public String toString() {
        return "SlotRelation[ slotRelationId=" + id + " ]";
    }

}
