package org.openepics.discs.conf.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.EntityType;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Stateless
public class AuditRecordEJB extends ReadOnlyDAO<AuditRecord> {
    @Override
    protected void defineEntity() {
        defineEntityClass(AuditRecord.class);
    }

    public List<AuditRecord> findByEntityIdAndType(Long entityId, EntityType entityType) {
        final List<AuditRecord> auditRecords = em.createNamedQuery("AuditRecord.findByEntityIdAndType", AuditRecord.class)
                                               .setParameter("entityId", entityId)
                                               .setParameter("entityType", entityType).getResultList();

        return auditRecords == null ? new ArrayList<AuditRecord>() : auditRecords;
    }

    @Override
    public AuditRecord findByName(String name) {
        throw new UnsupportedOperationException("findByName method not aplicable to AuditRecord class");
    }
}
