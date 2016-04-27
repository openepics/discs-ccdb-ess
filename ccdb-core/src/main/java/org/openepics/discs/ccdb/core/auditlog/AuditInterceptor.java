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
package org.openepics.discs.ccdb.core.auditlog;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.ccdb.model.AuditRecord;
import org.openepics.discs.ccdb.core.security.SecurityPolicy;
import org.openepics.discs.ccdb.core.util.CRUDOperation;
import org.openepics.discs.ccdb.core.util.ParentEntityResolver;


/**
 * An interceptor that creates an audit log
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
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
     * @param context the context
     * @return the return value from invoking {@link InvocationContext#proceed()}
     * @throws Exception many possible exceptions
     */
    @AroundInvoke
    public Object createAuditLog(InvocationContext context) throws Exception {

        final Object returnContext = context.proceed();

        final Object entity = context.getParameters()[0];
        if (context.getMethod().getAnnotation(CRUDOperation.class) != null) {

            final String username = securityPolicy.getUserId();
            final Date now = new Date();

            final List<AuditRecord> auditRecords = auditLogEntryCreator.auditRecords(
                    ParentEntityResolver.resolveParentEntity(entity),
                    context.getMethod().getAnnotation(CRUDOperation.class).operation());

            for (AuditRecord auditRecord : auditRecords) {
                auditRecord.setUser(username);
                auditRecord.setLogTime(now);

                em.persist(auditRecord);
            }
        }
        return returnContext;
    }
}
