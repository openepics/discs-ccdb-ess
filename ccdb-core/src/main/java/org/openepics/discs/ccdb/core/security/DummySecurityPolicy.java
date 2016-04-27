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
package org.openepics.discs.conf.security;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;
import javax.interceptor.Interceptor;

import org.openepics.discs.conf.ent.EntityTypeOperation;

/**
 * Dummy implementation of a security policy.
 *
 * Authorizes everything.
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@RequestScoped
@Named("securityPolicy")
@Alternative
@Priority(Interceptor.Priority.APPLICATION)
public class DummySecurityPolicy implements SecurityPolicy, Serializable {
    private static final long serialVersionUID = 4280076418469633096L;

    private static final Logger LOGGER = Logger.getLogger(DummySecurityPolicy.class.getCanonicalName());

    private boolean loggedIn;

    /**
     * Default no-param constructor
     */
    public DummySecurityPolicy() {
        LOGGER.log(Level.INFO, "Creating " + this.getClass().getCanonicalName());
    }

    @Override
    public String getUserId() {
        return loggedIn ? "admin" : null;
    }

    @Override
    public void login(String userName, String password) {
        // always succeeds
        loggedIn = true;
    }

    @Override
    public void logout() {
        // always succeeds
        loggedIn = false;
    }

    @Override
    public void checkAuth(Object entity, EntityTypeOperation operationType) {
        if (!loggedIn)
            throw SecurityException.generateExceptionMessage(entity, EntityTypeResolver.resolveEntityType(entity),
                    operationType);
    }

    @Override
    public boolean getUIHint(String param) {
        return true;
    }

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }
}
