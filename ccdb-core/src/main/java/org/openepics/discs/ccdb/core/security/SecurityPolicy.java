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
package org.openepics.discs.ccdb.core.security;

import org.openepics.discs.ccdb.model.EntityTypeOperation;


/**
 * Abstract SecurityPolicy interface. Implementations should contain all needed A&amp;A functionality.
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public interface SecurityPolicy {
    public static final String UI_HINT_HIERARCHY_CREATE = "HIERARCHY_CREATE";
    public static final String UI_HINT_HIERARCHY_DELETE = "HIERARCHY_DELETE";
    public static final String UI_HINT_HIERARCHY_MODIFY = "HIERARCHY_MODIFY";
    public static final String UI_HINT_HIERARCHY_ALL = "HIERARCHY_ALL";

    public static final String UI_HINT_DEVICES_CREATE = "DEVICES_CREATE";
    public static final String UI_HINT_DEVICES_DELETE = "DEVICES_DELETE";
    public static final String UI_HINT_DEVICES_MODIFY = "DEVICES_MODIFY";
    public static final String UI_HINT_DEVICES_ALL = "DEVICES_ALL";

    public static final String UI_HINT_DEVTYPE_CREATE = "DEVTYPE_CREATE";
    public static final String UI_HINT_DEVTYPE_DELETE = "DEVTYPE_DELETE";
    public static final String UI_HINT_DEVTYPE_MODIFY = "DEVTYPE_MODIFY";
    public static final String UI_HINT_DEVTYPE_ALL = "DEVTYPE_ALL";

    public static final String UI_HINT_PROP_CREATE = "PROP_CREATE";
    public static final String UI_HINT_PROP_DELETE = "PROP_DELETE";
    public static final String UI_HINT_PROP_MODIFY = "PROP_MODIFY";
    public static final String UI_HINT_PROP_ALL = "PROP_ALL";

    public static final String UI_HINT_ENUM_CREATE = "ENUM_CREATE";
    public static final String UI_HINT_ENUM_DELETE = "ENUM_DELETE";
    public static final String UI_HINT_ENUM_MODIFY = "ENUM_MODIFY";
    public static final String UI_HINT_ENUM_ALL = "ENUM_ALL";

    public static final String UI_HINT_UNIT_CREATE = "UNIT_CREATE";
    public static final String UI_HINT_UNIT_DELETE = "UNIT_DELETE";
    public static final String UI_HINT_UNIT_MODIFY = "UNIT_MODIFY";
    public static final String UI_HINT_UNIT_ALL = "UNIT_ALL";

    public static final String UI_HINT_MOVE_SLOT = "MOVE_SLOT";
    public static final String UI_HINT_IMPORT_SIGNALS = "IMPORT_SIGNALS";
    public static final String UI_HINT_INSTALLATION = "INSTALLATION";
    public static final String UI_HINT_RELATIONSHIP = "RELATIONSHIP";

    /**
     * Returns the user id (user-name) for the current user
     *
     * @return the user-name
     */
    public String getUserId();


    /**
     * Method used to login (authenticate)
     *
     * @param userName the username of the user being logged-in
     * @param password the password of the user being logged-in
     */
    public void login(String userName, String password);


    /**
     * Method used for logout
     */
    public void logout();


    /**
     * Checks if user is authorized to do operation operationType on entity of entityType
     *
     * @param entity The target entity
     * @param operationType The operation type
     */
    public void checkAuth(Object entity, EntityTypeOperation operationType);

    /**
     * Returns UI hints for the JSF/ManagedBeans layer
     *
     * @param param the parameter
     * @return a {@link Boolean} value for the hint. <code>true</code> means that action is allowed,
     * <code>false</code> not allowed.
     */
    public boolean getUIHint(String param);

    /**
     * @return <code>true</code> if the user is logged in, <code>false</code> otherwise.
     */
    public boolean isLoggedIn();
}
