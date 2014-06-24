package org.openepics.discs.conf.util;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

public class NotAuthorizedException extends Exception {
    
    private static final long serialVersionUID = 451842050341747171L;
    private EntityTypeOperation operation;
    private EntityType entityType;
    
    public NotAuthorizedException(EntityTypeOperation operation, EntityType entityType) {
        this.operation = operation;
        this.entityType = entityType;
          
    }
    
    @Override
    public String getMessage() {
        return "You do not have permissions to perform operation " + operation.toString() + " on resouce " + entityType.toString();
    }

}
