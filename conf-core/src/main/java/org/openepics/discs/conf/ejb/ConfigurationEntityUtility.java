package org.openepics.discs.conf.ejb;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;

import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.security.SecurityPolicy;

/**
 * Helper class used to update modifedBy and modifiedAt of {@link ConfigurationEntity}-s
 * 
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
public class ConfigurationEntityUtility implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private @EJB SecurityPolicy securityPolicy;
    
    
    /**
     * Updates modifiedBy and modifiedAt for one entity
     * 
     * @param entity
     */
    public void setModified(ConfigurationEntity entity) {
        final Date now = new Date();
                    
        entity.setModifiedAt(now);
        entity.setModifiedBy(securityPolicy.getUserId());
    }
    
    
    /**
     * Updates modifiedBy and modifiedBy of two entities
     * 
     * @param parent
     * @param child
     */
    public void setModified(ConfigurationEntity parent, ConfigurationEntity child) {
        final Date now = new Date();
        final String username = securityPolicy.getUserId();
            
        parent.setModifiedAt(now);
        parent.setModifiedBy(username);
        
        child.setModifiedAt(now);
        child.setModifiedBy(username);
    }
}
