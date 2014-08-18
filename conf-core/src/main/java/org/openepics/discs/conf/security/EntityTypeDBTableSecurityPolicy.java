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

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Privilege;
import org.openepics.discs.conf.ui.LoginManager;

import com.google.common.base.Preconditions;


/**
 * Implementation of simple security policy (checking for entity-type access only) using the DB {@link Privilege} table
 * Stateful EJB, caches all permissions from database on first access. 
 * 
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Stateful
@Alternative
public class EntityTypeDBTableSecurityPolicy implements SecurityPolicy {
    private static final Logger logger = Logger.getLogger(EntityTypeDBTableSecurityPolicy.class.getCanonicalName());
    
    @PersistenceContext private EntityManager em;
    @Inject private LoginManager loginManager;
    
 
    /**
     * Contains cached permissions
     */
    private Map<EntityType, Set<EntityTypeOperation> > cachedPermissions;

    
    @Override
    public void checkAuth(Object entity, EntityType entityType, EntityTypeOperation operationType) {
        if (!hasPermission(entityType, operationType)) {
            throw SecurityException.generateExceptionMessage(entity, entityType, operationType);
        }
    }
   
  
    @Override
    public boolean getUIHint(String param) {
        if (SecurityPolicy.MANAGE_COMPONENT_TYPES.equals(param)) {
            return hasPermission(EntityType.COMPONENT_TYPE, EntityTypeOperation.CREATE) ||
                    hasPermission(EntityType.COMPONENT_TYPE, EntityTypeOperation.DELETE) ||
                    hasPermission(EntityType.COMPONENT_TYPE, EntityTypeOperation.UPDATE) ||
                    hasPermission(EntityType.COMPONENT_TYPE, EntityTypeOperation.RENAME);
        } else if (SecurityPolicy.MANAGE_PROPERTIES.equals(param)) {
            return hasPermission(EntityType.PROPERTY, EntityTypeOperation.CREATE) ||
                    hasPermission(EntityType.PROPERTY, EntityTypeOperation.DELETE) ||
                    hasPermission(EntityType.PROPERTY, EntityTypeOperation.UPDATE) ||
                    hasPermission(EntityType.PROPERTY, EntityTypeOperation.RENAME);
        } else if (SecurityPolicy.MANAGE_DEVICES.equals(param)) {
            return hasPermission(EntityType.DEVICE, EntityTypeOperation.CREATE) ||
                    hasPermission(EntityType.DEVICE, EntityTypeOperation.DELETE) ||
                    hasPermission(EntityType.DEVICE, EntityTypeOperation.UPDATE);
        } else if (SecurityPolicy.MANAGE_LAYOUT_SLOTS.equals(param)) {
            return hasPermission(EntityType.SLOT, EntityTypeOperation.CREATE) ||
                    hasPermission(EntityType.SLOT, EntityTypeOperation.DELETE) ||
                    hasPermission(EntityType.SLOT, EntityTypeOperation.UPDATE) ||
                    hasPermission(EntityType.SLOT, EntityTypeOperation.RENAME);
        } else if (SecurityPolicy.MANAGE_UNITS.equals(param)) {
            return hasPermission(EntityType.UNIT, EntityTypeOperation.CREATE) ||
                    hasPermission(EntityType.UNIT, EntityTypeOperation.DELETE) ||
                    hasPermission(EntityType.UNIT, EntityTypeOperation.UPDATE) ||
                    hasPermission(EntityType.UNIT, EntityTypeOperation.RENAME);
        }
        
        return false;
    }

    
    /** 
     * Checks if the user has access to the given entityType using operation operationType
     * 
     * @param entityType
     * @param operationType
     * @return true if permission exists
     */
    private boolean hasPermission(EntityType entityType, EntityTypeOperation operationType) {
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

        final String principal = loginManager.getUserid();
        
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
