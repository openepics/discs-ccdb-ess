/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.security;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Privilege;

import com.google.common.base.Preconditions;


/**
 * Implementation of simple security policy (checking for entity-type access only) using the DB {@link Privilege} table 
 * and the Java EE security module, as was in Configuration Module v. 1.0.
 * 
 * Stateful EJB, caches all permissions from database on first access. 
 * 
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Stateful
@SessionScoped
@Named("securityPolicy")
@Alternative
public class EntityTypeDBTableSecurityPolicy implements SecurityPolicy, Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(EntityTypeDBTableSecurityPolicy.class.getCanonicalName());
    
    @PersistenceContext private EntityManager em;
    @Inject private HttpServletRequest servletRequest;
 
    /**
     * Contains cached permissions
     */
    private Map<EntityType, Set<EntityTypeOperation> > cachedPermissions;

    
    @Override
    public void login(String userName, String password) {        
        try {
            if (servletRequest.getUserPrincipal() == null) {
                servletRequest.login(userName, password);                
                logger.log(Level.INFO, "Login successful for " + userName);                
            }
        } catch (Exception e) {
            throw new SecurityException("Login Failed !", e);
        }
    }

    @Override
    public void logout() {
        try {
            servletRequest.logout();
            servletRequest.getSession().invalidate();            
        } catch (Exception e) {
            throw new SecurityException("Error while logging out!", e);
        }
    }

    @Override
    public String getUserId() {        
        return servletRequest.getUserPrincipal()!=null ? servletRequest.getUserPrincipal().getName() : null;
    }
    
    @Override
    public void checkAuth(Object entity, EntityTypeOperation operationType) {
        final EntityType entityType = EntityTypeResolver.resolveEntityType(entity);
        
        logger.log(Level.INFO, "Check auth with " + entityType.toString() + " and " + operationType.toString());
 
        if (!hasPermission(entityType , operationType)) {
            throw SecurityException.generateExceptionMessage(entity, entityType, operationType);
        }
    }
   
  
    @Override
    public boolean getUIHint(String param) {
        return hasAnyModifyPermission( EntityType.valueOf(param) );
    }

   
    /** 
     * Will allow UI element to be shown for given entity type
     * 
     * @param entityType
     * @return
     */
    private boolean hasAnyModifyPermission(EntityType entityType) {
        return hasPermission(entityType, EntityTypeOperation.CREATE) ||
                hasPermission(entityType, EntityTypeOperation.DELETE) ||
                hasPermission(entityType, EntityTypeOperation.UPDATE) ||
                hasPermission(entityType, EntityTypeOperation.RENAME);  
    }
     
    
    /** 
     * Checks if the user has access to the given entityType using operation operationType
     * 
     * @param entityType
     * @param operationType
     * @return true if permission exists
     */
    private boolean hasPermission(EntityType entityType, EntityTypeOperation operationType) {
        
        final String principal = getUserId();
        
        // Handle the non-logged case 
        if (principal == null) {
            return false;
        }
        
        if (cachedPermissions == null)
            populateCachedPermissions();       
        
        final Set<EntityTypeOperation> entityTypeOperations = cachedPermissions.get(entityType);
        
        if (entityTypeOperations == null) {
            return false;
        } else {
            return entityTypeOperations.contains(operationType);          
        }
    }
    
 
    /**
     * Populates the map of cached privileges from the database
     */
    private void populateCachedPermissions() {
        Preconditions.checkArgument(cachedPermissions==null, 
                "EntityTypeDBTableSecurityPolicy.populateCachedPermissions called when cached data was already available");
        
        cachedPermissions = new EnumMap<EntityType, Set<EntityTypeOperation>>(EntityType.class);

        final String principal = getUserId();
        // The following should not happen for logged in user
        if (principal == null || principal.isEmpty()) {
            throw new SecurityException("Identity could not be established. Is user logged in");
        }

        final List<Privilege> privs = em.createQuery(
                "SELECT p FROM UserRole ur JOIN ur.role r JOIN r.privilegeList p " +
                "WHERE ur.ccdb_user.userId = :user", Privilege.class).
                setParameter("user", principal).getResultList();
        logger.finer("found privileges: " + privs.size());

        for (Privilege p : privs) {
            final EntityType entityType = p.getResource();
            
            Set<EntityTypeOperation> operationTypeSet = cachedPermissions.get(entityType);
            if (operationTypeSet == null) {
                operationTypeSet = EnumSet.noneOf(EntityTypeOperation.class);
                cachedPermissions.put(entityType, operationTypeSet);
            }
            
            operationTypeSet.add(p.getOper());            
        }   
    }
}
