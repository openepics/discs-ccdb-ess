/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ent;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @NamedQuery(name = "Privilege.findByPrivilegeId", query = "SELECT p FROM Privilege p WHERE p.privilegeId = :privilegeId"),
    @NamedQuery(name = "Privilege.findByResource", query = "SELECT p FROM Privilege p WHERE p.resource = :resource"),
    @NamedQuery(name = "Privilege.findByOper", query = "SELECT p FROM Privilege p WHERE p.oper = :oper")})
public class Privilege implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "privilege_id")
    private Integer privilegeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "resource")
    private String resource;
    @Basic(optional = false)
    @NotNull
    @Column(name = "oper")
    private char oper;
    @JoinColumn(name = "role", referencedColumnName = "role_id")
    @ManyToOne(optional = false)
    private Role role;

    public Privilege() {
    }

    public Privilege(Integer privilegeId) {
        this.privilegeId = privilegeId;
    }

    public Privilege(Integer privilegeId, String resource, char oper) {
        this.privilegeId = privilegeId;
        this.resource = resource;
        this.oper = oper;
    }

    public Integer getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(Integer privilegeId) {
        this.privilegeId = privilegeId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public char getOper() {
        return oper;
    }

    public void setOper(char oper) {
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
        int hash = 0;
        hash += (privilegeId != null ? privilegeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Privilege)) {
            return false;
        }
        Privilege other = (Privilege) object;
        if ((this.privilegeId == null && other.privilegeId != null) || (this.privilegeId != null && !this.privilegeId.equals(other.privilegeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.Privilege[ privilegeId=" + privilegeId + " ]";
    }
    
}
