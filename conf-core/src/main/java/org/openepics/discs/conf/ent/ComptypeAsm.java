package org.openepics.discs.conf.ent;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "comptype_asm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComptypeAsm.findAll", query = "SELECT c FROM ComptypeAsm c"),
    @NamedQuery(name = "ComptypeAsm.findByComptypeAsmId", query = "SELECT c FROM ComptypeAsm c WHERE c.id = :id"),
    @NamedQuery(name = "ComptypeAsm.findByModifiedBy", query = "SELECT c FROM ComptypeAsm c WHERE c.modifiedBy = :modifiedBy")
})
public class ComptypeAsm extends ConfigurationEntity {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "child_position")
    private String childPosition;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @JoinColumn(name = "child_type", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentType childType;

    @JoinColumn(name = "parent_type", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentType parentType;

    protected ComptypeAsm() {
    }

    public ComptypeAsm(String childPosition) {
        this.childPosition = childPosition;
    }

    public String getChildPosition() {
        return childPosition;
    }

    public void setChildPosition(String childPosition) {
        this.childPosition = childPosition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ComponentType getChildType() {
        return childType;
    }

    public void setChildType(ComponentType childType) {
        this.childType = childType;
    }

    public ComponentType getParentType() {
        return parentType;
    }

    public void setParentType(ComponentType parentType) {
        this.parentType = parentType;
    }

    @Override
    public String toString() {
        return "ComptypeAsm[ comptypeAsmId=" + id + " ]";
    }

}
