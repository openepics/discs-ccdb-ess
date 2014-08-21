package org.openepics.discs.conf.security;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 * Interceptor, that where defined, intercepts method call to check for user authorization permissions.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Authorized
@Interceptor
public class AuthorizationInterceptor {    
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(AuthorizationInterceptor.class.getCanonicalName());
           
    @Inject private SecurityPolicy securityPolicy;

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
        final EntityTypeOperation entityOperationType = context.getMethod().getAnnotation(CRUDOperation.class).operation();
                      
        if (entityOperationType == null) {
            throw new SecurityException("EntityOperation not specified around a authorize entry!");
        }
        
        securityPolicy.checkAuth(entity, entityOperationType);
        return context.proceed();
    }
}
