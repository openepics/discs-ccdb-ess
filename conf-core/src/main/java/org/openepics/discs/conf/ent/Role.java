package org.openepics.discs.conf.ent;

import java.io.Serializable;
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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Role.findAll", query = "SELECT r FROM Role r"),
    @NamedQuery(name = "Role.findByRoleId", query = "SELECT r FROM Role r WHERE r.roleId = :roleId")
})
public class Role implements Serializable {
    @Id
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "role_id")
    private String roleId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;

    @Version
    private Long version;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<UserRole> userRoleList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<Privilege> privilegeList;

    protected Role() {
    }

    public Role(String roleId) {
        this.roleId = roleId;
    }

    public Role(String roleId, String description) {
        this.roleId = roleId;
        this.description = description;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserRole> getUserRoleList() {
        return userRoleList;
    }

    public void setUserRoleList(List<UserRole> userRoleList) {
        this.userRoleList = userRoleList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Privilege> getPrivilegeList() {
        return privilegeList;
    }

    public void setPrivilegeList(List<Privilege> privilegeList) {
        this.privilegeList = privilegeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roleId != null ? roleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Role)) return false;

        Role other = (Role) object;
        if (this.roleId == null && other.roleId != null) return false;
        if (this.roleId != null) return this.roleId.equals(other.roleId); // return true for same DB entity

        return this==object;
    }

    @Override
    public String toString() {
        return "Role[ roleId=" + roleId + " ]";
    }

}
