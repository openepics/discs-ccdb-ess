package org.openepics.discs.conf.security;

import java.util.HashMap;
import java.util.Map;

import org.openepics.discs.conf.ent.EntityType;

/**
 * 
 * Helper class used to resolve the {@link EntityType} of configuration entities, relevant to audit logging and security
 * 
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
public class EntityTypeResolver {
    static Map<String, EntityType> entityTypes = new HashMap<> ();
    
    static {
        entityTypes.put(org.openepics.discs.conf.ent.Device.class.getCanonicalName(), EntityType.DEVICE);
        entityTypes.put(org.openepics.discs.conf.ent.DevicePropertyValue.class.getCanonicalName(), EntityType.DEVICE);
        entityTypes.put(org.openepics.discs.conf.ent.DeviceArtifact.class.getCanonicalName(), EntityType.DEVICE);
                               
        entityTypes.put(org.openepics.discs.conf.ent.Slot.class.getCanonicalName(), EntityType.SLOT);
        entityTypes.put(org.openepics.discs.conf.ent.SlotPropertyValue.class.getCanonicalName(), EntityType.SLOT);
        entityTypes.put(org.openepics.discs.conf.ent.SlotArtifact.class.getCanonicalName(), EntityType.SLOT);
        entityTypes.put(org.openepics.discs.conf.ent.SlotPair.class.getCanonicalName(), EntityType.SLOT);       
        
        entityTypes.put(org.openepics.discs.conf.ent.ComponentType.class.getCanonicalName(), EntityType.COMPONENT_TYPE);
        entityTypes.put(org.openepics.discs.conf.ent.ComptypePropertyValue.class.getCanonicalName(), EntityType.COMPONENT_TYPE);
        entityTypes.put(org.openepics.discs.conf.ent.ComptypeArtifact.class.getCanonicalName(), EntityType.COMPONENT_TYPE);
        entityTypes.put(org.openepics.discs.conf.ent.ComptypeAsm.class.getCanonicalName(), EntityType.COMPONENT_TYPE);
                       
        
        entityTypes.put(org.openepics.discs.conf.ent.InstallationRecord.class.getCanonicalName(), EntityType.INSTALLATION_RECORD);
        entityTypes.put(org.openepics.discs.conf.ent.InstallationArtifact.class.getCanonicalName(), EntityType.INSTALLATION_RECORD);
        
        entityTypes.put(org.openepics.discs.conf.ent.AlignmentRecord.class.getCanonicalName(), EntityType.ALIGNMENT_RECORD);
        entityTypes.put(org.openepics.discs.conf.ent.AlignmentArtifact.class.getCanonicalName(), EntityType.ALIGNMENT_RECORD);
        entityTypes.put(org.openepics.discs.conf.ent.AlignmentPropertyValue.class.getCanonicalName(), EntityType.ALIGNMENT_RECORD);
        
        entityTypes.put(org.openepics.discs.conf.ent.Unit.class.getCanonicalName(), EntityType.UNIT);
        
        entityTypes.put(org.openepics.discs.conf.ent.User.class.getCanonicalName(), EntityType.USER);
        entityTypes.put(org.openepics.discs.conf.ent.Property.class.getCanonicalName(), EntityType.PROPERTY);
       
        entityTypes.put(org.openepics.discs.conf.ent.DataType.class.getCanonicalName(), EntityType.DATA_TYPE);       }
    
    /**
     * Resolves the {@link EntityType} of an entity object, for security purposes.
     * 
     * Child entities such as CompoenentTypePropertyValues are resolved as the security-relevant parent (such as COMPONENT_TYPE)
     * 
     * @param entity
     * @return
     */
    static public EntityType resolveEntityType(Object entity) {
        EntityType result = entityTypes.get(entity.getClass().getCanonicalName());
        if (result == null) {
            throw new SecurityException("Unhandled or invalid entity type in the security system.");
        }
        
        return result;
    }
    
}
