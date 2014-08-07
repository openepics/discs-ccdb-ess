package org.openepics.discs.conf.util;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.openepics.discs.conf.util.CRUDAnnotation.Operation;
import org.openepics.discs.conf.util.SecurityInterceptorBinding;

@Interceptor
@SecurityInterceptorBinding
public class SecurityInterceptor {
    @Inject private RBACMock rbac;

    @AroundInvoke
    public Object authorizationCheck(InvocationContext context) throws Exception {
        Object entity = context.getParameters()[0];
        CRUDAnnotation annotation = context.getMethod().getAnnotation(CRUDAnnotation.class);
        Operation operation = annotation.operation();
        if (rbac.isAuthorized(operation, entity)) {
            return context.proceed();
        } else {
            return null;
        }

    }
}
