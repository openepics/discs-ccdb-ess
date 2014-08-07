package org.openepics.discs.conf.util;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.openepics.discs.conf.util.CRUDOperation.Operation;
import org.openepics.discs.conf.util.Authorized;

@Interceptor
@Authorized
public class AuthorizationInterceptor {
    @Inject private RBACMock rbac;

    @AroundInvoke
    public Object authorizationCheck(InvocationContext context) throws Exception {
        Object entity = context.getParameters()[0];
        CRUDOperation annotation = context.getMethod().getAnnotation(CRUDOperation.class);
        Operation operation = annotation.operation();
        if (rbac.isAuthorized(operation, entity)) {
            return context.proceed();
        } else {
            return null;
        }

    }
}
