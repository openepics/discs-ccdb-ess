package org.openepics.discs.conf.ejb;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.security.SecurityPolicy;

/**
 * Helper class used to update modifedBy and modifiedAt of {@link ConfigurationEntity}-s
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Dependent
public class ConfigurationEntityUtility implements Serializable {
    private @Inject SecurityPolicy securityPolicy;

    /**
     * Updates modifiedBy and modifiedAt for one entity
     *
     * @param entity
     */
    public <T> void setModified(T entity) {
        if (entity instanceof ConfigurationEntity) {
            final Date now = new Date();
        	final ConfigurationEntity configEntity = (ConfigurationEntity) entity;
            configEntity.setModifiedAt(now);
            configEntity.setModifiedBy(getUserId());
        }
    }

    /**
     * Updates modifiedBy and modifiedAt of two entities
     *
     * @param parent
     * @param child
     */
    public <T,S> void setModified(T parent, S child) {
        final Date now = new Date();
        final String username = getUserId();

        if (parent instanceof ConfigurationEntity) {
            final ConfigurationEntity configParent = (ConfigurationEntity) parent;
            configParent.setModifiedAt(now);
            configParent.setModifiedBy(username);
        }

        if (child instanceof ConfigurationEntity) {
            final ConfigurationEntity configChild = (ConfigurationEntity) child;
            configChild.setModifiedAt(now);
            configChild.setModifiedBy(username);
        }
    }

    /**
     * Resolves the user-id in case the securitypolicy returns null (meaningfull for system updates such as the initial db population)
     *
     * @return
     */
    private String getUserId() {
        final String username = securityPolicy.getUserId();

        return username != null ? username : "system";
    }
}
