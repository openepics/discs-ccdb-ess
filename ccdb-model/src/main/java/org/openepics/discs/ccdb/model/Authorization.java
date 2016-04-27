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
package org.openepics.discs.ccdb.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * A class holding information for authorization at a level of specific entities.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Entity
// "user_permission" is indexed by being "unique"
@Table(name = "entity_auth", indexes = {@Index(columnList = "is_permission")})
public class Authorization extends ConfigurationEntity {

    private static final long serialVersionUID = -4120200568461898284L;

    @Basic(optional = false)
    @Size(min = 1, max = 64, message = "User name or permission cannot be longer than 64 character.")
    @Column(name = "user_permission", length = 64, unique = true)
    private String userPermission;

    @Basic(optional = false)
    @Column(name = "is_permission")
    private boolean isPermission;

    public Authorization() {}

    /** @see #isPermission()
     * @return the RBAC username or permission */
    public String getUserPermission() {
        return userPermission;
    }

    /** @param userPermission the RBAC username or permission to set */
    public void setUserPermission(String userPermission) {
        this.userPermission = userPermission;
    }

    /** @return if <code>true</code> the data names a RBAC permission, <code>false</code> RBAC username */
    public boolean isPermission() {
        return isPermission;
    }

    /** @param isPermission <code>true</code> the data names a RBAC permission, <code>false</code> RBAC username */
    public void setPermission(boolean isPermission) {
        this.isPermission = isPermission;
    }
}
