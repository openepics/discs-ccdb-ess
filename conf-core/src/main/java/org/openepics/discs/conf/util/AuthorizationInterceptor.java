package org.openepics.discs.conf.util;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.util.Authorized;

/**
 * Interceptor, that where defined, intercepts method call to check for user authorization permissions.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Interceptor
@Authorized
public class AuthorizationInterceptor {
    @Inject private RBACMock rbac;

    /**
     * Method that checks if current user is authorized to perform operation defined with {@link CRUDOperation} on given entity.
     * If user is not authorized to perform operation, targeted method is never executed, otherwise
     * method executes normally.
     *
     * @param context {@link InvocationContext}
     * @return
     * @throws Exception
     */
    @AroundInvoke
    public Object authorizationCheck(InvocationContext context) throws Exception {
        final Object entity = context.getParameters()[0];
        final CRUDOperation annotation = context.getMethod().getAnnotation(CRUDOperation.class);

        if (rbac.isAuthorized(annotation.operation(), entity)) {
            return context.proceed();
        } else {
            return null;
        }

    }
}
