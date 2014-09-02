package org.openepics.discs.conf.auditlog;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.security.SecurityPolicy;
import org.openepics.discs.conf.util.CRUDOperation;
import org.openepics.discs.conf.util.ParentEntityResolver;


/**
 * An interceptor that creates an audit log
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Audit
@Interceptor
public class AuditInterceptor {
    @PersistenceContext private EntityManager em;
    @Inject private AuditLogEntryCreator auditLogEntryCreator;
    @Inject private SecurityPolicy securityPolicy;

    /**
     * Creates audit log after the method annotated with this interceptor has finished executing.
     *
     * @param context
     * @return the return value from invoking {@link InvocationContext#proceed()}
     * @throws Exception
     */
    @AroundInvoke
    public Object createAuditLog(InvocationContext context) throws Exception {

        final Object returnContext = context.proceed();

        final Object entity = context.getParameters()[0];
        if (context.getMethod().getAnnotation(CRUDOperation.class) != null) {

            final String username = securityPolicy.getUserId();
            final Date now = new Date();

            final List<AuditRecord>  auditRecords = auditLogEntryCreator.auditRecords(ParentEntityResolver.resolveParentEntity(entity), context.getMethod().getAnnotation(CRUDOperation.class).operation());
            if (auditRecords != null) {
                for (AuditRecord auditRecord : auditRecords) {
                    auditRecord.setUser(username);
                    auditRecord.setLogTime(now);

                    em.persist(auditRecord);
                }
            }
        }
        return returnContext;
    }
}
