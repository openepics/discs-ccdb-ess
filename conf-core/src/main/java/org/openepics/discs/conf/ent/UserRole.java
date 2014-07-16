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
@Table(name = "user_role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserRole.findAll", query = "SELECT u FROM UserRole u"),
    @NamedQuery(name = "UserRole.findByUserRoleId", query = "SELECT u FROM UserRole u WHERE u.userRoleId = :userRoleId"),
    @NamedQuery(name = "UserRole.findByCanDelegate", query = "SELECT u FROM UserRole u WHERE u.canDelegate = :canDelegate"),
    @NamedQuery(name = "UserRole.findByIsRoleManager", query = "SELECT u FROM UserRole u WHERE u.isRoleManager = :isRoleManager"),
    @NamedQuery(name = "UserRole.findByStartTime", query = "SELECT u FROM UserRole u WHERE u.startTime = :startTime"),
    @NamedQuery(name = "UserRole.findByEndTime", query = "SELECT u FROM UserRole u WHERE u.endTime = :endTime"),
    @NamedQuery(name = "UserRole.findByComment", query = "SELECT u FROM UserRole u WHERE u.comment = :comment")})
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Integer userRoleId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "canDelegate")
    private boolean canDelegate;

    @Basic(optional = false)
    @NotNull
    @Column(name = "isRoleManager")
    private boolean isRoleManager;

    @Basic(optional = false)
    @NotNull
    @Column(name = "startTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Basic(optional = false)
    @NotNull
    @Column(name = "endTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Size(max = 255)
    @Column(name = "comment")
    private String comment;

    @Version
    private Long version;

    @JoinColumn(name = "role", referencedColumnName = "role_id")
    @ManyToOne(optional = false)
    private Role role;

    @JoinColumn(name = "ccdb_user", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private User ccdb_user;

    protected UserRole() {
    }

    public UserRole(boolean canDelegate, boolean isRoleManager, Date startTime, Date endTime) {
        this.canDelegate = canDelegate;
        this.isRoleManager = isRoleManager;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getUserRoleId() {
        return userRoleId;
    }

    public boolean getCanDelegate() {
        return canDelegate;
    }

    public void setCanDelegate(boolean canDelegate) {
        this.canDelegate = canDelegate;
    }

    public boolean getIsRoleManager() {
        return isRoleManager;
    }

    public void setIsRoleManager(boolean isRoleManager) {
        this.isRoleManager = isRoleManager;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User getUser() {
        return ccdb_user;
    }

    public void setUser(User user) {
        this.ccdb_user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userRoleId != null ? userRoleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserRole)) return false;

        UserRole other = (UserRole) object;
        if (this.userRoleId == null && other.userRoleId != null)  return false;
        if (this.userRoleId != null) return this.userRoleId.equals(other.userRoleId); // return true for same DB entity

        return this==object;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.UserRole[ userRoleId=" + userRoleId + " ]";
    }

}
