package org.openepics.discs.conf.ent;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "privilege")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Privilege.findAll", query = "SELECT p FROM Privilege p"),
    @NamedQuery(name = "Privilege.findByPrivilegeId", query = "SELECT p FROM Privilege p "
            + "WHERE p.privilegeId = :privilegeId")
})
public class Privilege implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "privilege_id")
    private Integer privilegeId;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "resource")
    private EntityType resource;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "oper")
    private EntityTypeOperation oper;

    @JoinColumn(name = "role", referencedColumnName = "role_id")
    @ManyToOne(optional = false)
    private Role role;

    protected Privilege() {
    }

    public Privilege(EntityType resource, EntityTypeOperation oper) {
        this.resource = resource;
        this.oper = oper;
    }

    public Integer getPrivilegeId() {
        return privilegeId;
    }

    public EntityType getResource() {
        return resource;
    }

    public void setResource(EntityType resource) {
        this.resource = resource;
    }

    public EntityTypeOperation getOper() {
        return oper;
    }

    public void setOper(EntityTypeOperation oper) {
        this.oper = oper;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        return privilegeId != null ? privilegeId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Privilege)) {
            return false;
        }

        Privilege other = (Privilege) object;
        if (this.privilegeId == null && other.privilegeId != null) {
            return false;
        }

        // return true for same DB entity
        if (this.privilegeId != null) {
            return this.privilegeId.equals(other.privilegeId);
        }

        return this==object;
    }

    @Override
    public String toString() {
        return "Privilege[ privilegeId=" + privilegeId + " ]";
    }

}
