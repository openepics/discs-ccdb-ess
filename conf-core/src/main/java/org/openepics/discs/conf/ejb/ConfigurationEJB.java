package org.openepics.discs.conf.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 */

@Stateless public class ConfigurationEJB {

    private static final Logger logger = Logger.getLogger(ConfigurationEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;

    // -------------------- Property ---------------------

    public List<Property> findProperties() {        
        final CriteriaQuery<Property> cq = em.getCriteriaBuilder().createQuery(Property.class);
        final Root<Property> prop = cq.from(Property.class);
        cq.select(prop);

        final List<Property> props= em.createQuery(cq).getResultList();
        logger.log(Level.INFO, "Number of properties: {0}", props.size());

        return props;
    }

    public Property findProperty(Long id) {
        return em.find(Property.class, id);
    }

    public Property findPropertyByName(String name) {
        Property property;
        try {
            property = em.createNamedQuery("Property.findByName", Property.class).setParameter("name", name).getSingleResult();
        } catch (NoResultException e) {
            property = null;
        }
        return property;
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized 
    public void addProperty(Property property) {
        property.setModifiedAt(new Date());
        em.persist(property);
    }
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized 
    public void saveProperty(Property property) {
        property.setModifiedAt(new Date());
        em.merge(property);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void deleteProperty(Property property) {
        final Property mergedProp = em.merge(property);
        em.remove(mergedProp);
    }

    // -------------------- Unit ---------------------

    public List<Unit> findUnits() {
        final CriteriaQuery<Unit> cq = em.getCriteriaBuilder().createQuery(Unit.class);
        final List<Unit> units = em.createQuery(cq).getResultList();
        
        logger.log(Level.INFO, "Number of units: {0}", units.size());

        return units;
    }

    public Unit findUnit(Long id) {
        return em.find(Unit.class, id);
    }

    public Unit findUnitByName(String name) {
        Unit unit;
        try {
            unit = em.createNamedQuery("Unit.findByName", Unit.class).setParameter("unitName", name).getSingleResult();
        } catch (NoResultException e) {
            unit = null;
        }
        return unit;
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addUnit(Unit unit) {
        unit.setModifiedAt(new Date());
        em.persist(unit);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveUnit(Unit unit) {
        unit.setModifiedAt(new Date());
        em.merge(unit);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void deleteUnit(Unit unit) {
        final Unit mergedUnit = em.merge(unit);
        em.remove(mergedUnit);
    }

    // ---------------- Data Types -------------------------

    public List<DataType> findDataType() {
        final CriteriaQuery<DataType> cq = em.getCriteriaBuilder().createQuery(DataType.class);
        final List<DataType> dataTypes = em.createQuery(cq).getResultList();
        
        logger.log(Level.INFO, "Number of data-types: {0}", dataTypes.size());

        return dataTypes;
    }

    public DataType findDataType(Long id) {
        return em.find(DataType.class, id);
    }

    public DataType findDataTypeByName(String name) {
        DataType dataType;
        try {
            dataType = em.createNamedQuery("DataType.findByName", DataType.class).setParameter("name", name).getSingleResult();
        } catch (NoResultException e) {
            dataType = null;
        }
        return dataType;
    }

    // ---------------- Slot Relations -------------------------

    public List<SlotRelation> findSlotRelation() {
        final CriteriaQuery<SlotRelation> cq = em.getCriteriaBuilder().createQuery(SlotRelation.class);
        final List<SlotRelation> slotRelations = em.createQuery(cq).getResultList();

        logger.log(Level.INFO, "Number of slot relations: {0}", slotRelations.size());

        return slotRelations;
    }

    public SlotRelation findSlotRelation(Long id) {
        return em.find(SlotRelation.class, id);
    }

    // ---------------- Audit Records -------------------------

    public List<AuditRecord> findAuditRecord() {
        final CriteriaQuery<AuditRecord> cq = em.getCriteriaBuilder().createQuery(AuditRecord.class);
        final List<AuditRecord> auditRecords = em.createQuery(cq).getResultList();

        logger.log(Level.INFO, "Number of audit records: {0}", auditRecords.size());

        return auditRecords;
    }

    public AuditRecord findDAuditRecord(int id) {
        return em.find(AuditRecord.class, id);
    }
}
