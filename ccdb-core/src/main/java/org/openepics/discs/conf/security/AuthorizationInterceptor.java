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

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 * Interceptor, that where defined, intercepts method call to check for user authorization permissions.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 *
 */
@Authorized
@Interceptor
public class AuthorizationInterceptor {
    @Inject private SecurityPolicy securityPolicy;

    /**
     * Method that checks if current user is authorized to perform operation defined with {@link CRUDOperation} on
     * given entity.
     * If user is not authorized to perform operation, targeted method is never executed, otherwise
     * method executes normally.
     *
     * @param context {@link InvocationContext}
     * @return the return value from invoking {@link InvocationContext#proceed()}
     * @throws Exception indicates that the authorization has failed
     */
    @AroundInvoke
    public Object authorizationCheck(InvocationContext context) throws Exception {
        final Object entity = context.getParameters()[0];
        final EntityTypeOperation entityOperationType = context.getMethod().
                getAnnotation(CRUDOperation.class).operation();

        if (entityOperationType == null) {
            throw new SecurityException("EntityOperation not specified around a authorize entry!");
        }

        securityPolicy.checkAuth(entity, entityOperationType);
        return context.proceed();
    }
}
