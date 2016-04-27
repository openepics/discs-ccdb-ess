/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.ccdb.gui.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.ccdb.core.ejb.AuthEJB;
import org.openepics.discs.ccdb.model.Privilege;
import org.openepics.discs.ccdb.model.Role;
import org.openepics.discs.ccdb.model.User;
import org.openepics.discs.ccdb.model.UserRole;

/**
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class AuthView implements Serializable {
    public static class Permission implements Serializable {
        public String role;
        public String oper;
        public String resource;
        
        public Permission() {
            
        }
        
        public Permission(String role, String oper, String resource) {
            this.role = role;
            this.oper = oper;
            this.resource = resource;
        }
        
        public static Permission toPermission(Privilege priv) {
            return new Permission(priv.getRole().getRoleId(), priv.getOper().toString(), priv.getResource().toString());            
        }

        public String getRole() {
            return role;
        }

        public String getOper() {
            return oper;
        }

        public String getResource() {
            return resource;
        }
        
        
    }
    
    @Inject private AuthEJB authEJB;
    private static final Logger logger = Logger.getLogger(AuthView.class.getName());

    private List<Role> roles;
    private List<Privilege> privileges;
    private List<Permission> permissions = new ArrayList<>();
    private List<User> users;
    private List<UserRole> userRoles;
    
    public AuthView() {
    }

    @PostConstruct
    public void init() {
        roles = authEJB.findAllRoles();
        users = authEJB.findAllUsers();
        userRoles = authEJB.findAllUserRoles();
        privileges = authEJB.findAllPrivileges();
        for (Privilege priv: privileges) {
            permissions.add(Permission.toPermission(priv));
        }
        permissions.add(new Permission("FE_AreaManager", "MANAGE", "FrontEnd"));
        permissions.add(new Permission("PS_Manager", "UPDATE", "PowerSupplies"));
        logger.log(Level.FINE, "Found number of slots: {0}", users.size());       
    }
    
    // --- getters/setters

    public List<Role> getRoles() {
        return roles;
    }

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

}
