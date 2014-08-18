package org.openepics.discs.conf.security;

import java.util.HashMap;
import java.util.Map;

import org.openepics.discs.conf.ent.EntityType;

public class EntityTypeResolver {
    static Map<String, EntityType> entityTypes = new HashMap<> ();
    
    static {
        entityTypes.put("org.openepics.discs.conf.ent.Device", EntityType.DEVICE);
        entityTypes.put("org.openepics.discs.conf.ent.DevicePropertyValue", EntityType.DEVICE);
        entityTypes.put("org.openepics.discs.conf.ent.DeviceArtifact", EntityType.DEVICE);
                               
        entityTypes.put("org.openepics.discs.conf.ent.Slot", EntityType.SLOT);
        entityTypes.put("org.openepics.discs.conf.ent.SlotPropertyValue", EntityType.SLOT);
        entityTypes.put("org.openepics.discs.conf.ent.SlotArtifact", EntityType.SLOT);
        entityTypes.put("org.openepics.discs.conf.ent.SlotPair", EntityType.SLOT);       
        
        entityTypes.put("org.openepics.discs.conf.ent.ComponentType", EntityType.COMPONENT_TYPE);
        entityTypes.put("org.openepics.discs.conf.ent.ComptypePropertyValue", EntityType.COMPONENT_TYPE);
        entityTypes.put("org.openepics.discs.conf.ent.ComptypeArtifact", EntityType.COMPONENT_TYPE);
        
        
        entityTypes.put("org.openepics.discs.conf.ent.InstallationRecord", EntityType.INSTALLATION_RECORD);
        entityTypes.put("org.openepics.discs.conf.ent.InstallationArtifact", EntityType.INSTALLATION_RECORD);
        
        entityTypes.put("org.openepics.discs.conf.ent.AlignmentRecord", EntityType.ALIGNMENT_RECORD);
        entityTypes.put("org.openepics.discs.conf.ent.AlignmentArtifact", EntityType.ALIGNMENT_RECORD);

        entityTypes.put("org.openepics.discs.conf.ent.Unit", EntityType.UNIT);
        
        
        entityTypes.put("org.openepics.discs.conf.ent.User", EntityType.USER);
        entityTypes.put("org.openepics.discs.conf.ent.Property", EntityType.PROPERTY);
       
        entityTypes.put("org.openepics.discs.conf.ent.DataType", EntityType.DATA_TYPE);        
    }
    
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
