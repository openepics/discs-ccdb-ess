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

import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.security.SecurityPolicy;

/**
 * Helper class used to update modifedBy and modifiedAt of {@link ConfigurationEntity}-s
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@Dependent
public class ConfigurationEntityUtility {
    @Inject private SecurityPolicy securityPolicy;

    /**
     * Updates modifiedBy and modifiedAt for one entity
     *
     * @param <T> the entity type
     * @param entity the entity
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
     * @param <T> the parent entity type
     * @param <S> the child entity type
     * @param parent the parent
     * @param child the child
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
     * Resolves the user-id in case the security policy returns <code>null</code> (meaningful for system
     * updates such as the initial DB population)
     *
     * @return resolved user name from the {@link SecurityPolicy} implementation or "system" if none found
     */
    protected String getUserId() {
        final String username = securityPolicy.getUserId();

        return username != null ? username : "system";
    }
}
