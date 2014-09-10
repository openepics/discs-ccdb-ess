/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
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
    @Inject private SecurityPolicy securityPolicy;

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
     * Resolves the user-id in case the securitypolicy returns null (meaningfull for system updates such as the
     * initial db population)
     *
     * @return
     */
    private String getUserId() {
        final String username = securityPolicy.getUserId();

        return username != null ? username : "system";
    }
}
