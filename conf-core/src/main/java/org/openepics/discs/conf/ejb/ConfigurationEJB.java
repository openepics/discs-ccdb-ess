package org.openepics.discs.conf.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.ent.Unit;

/**
 *
 * @author vuppala
 */
@Stateless public class ConfigurationEJB {

    private static final Logger logger = Logger.getLogger(ConfigurationEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;

    // -------------------- Property ---------------------

    public List<Property> findProperties() {
        List<Property> props;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Property> cq = cb.createQuery(Property.class);
        Root<Property> prop = cq.from(Property.class);
        cq.select(prop);

        TypedQuery<Property> query = em.createQuery(cq);
        props = query.getResultList();
        logger.log(Level.INFO, "Number of component properties: {0}", props.size());

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

    public void saveProperty(Property property) {
        property.setModifiedAt(new Date());
        em.merge(property);
    }

    public void addProperty(Property property) {
        property.setModifiedAt(new Date());
        em.persist(property);
    }

    public void deleteProperty(Property property) {
        Property prop = em.find(Property.class, property.getId());
        em.remove(prop);
    }

    // -------------------- Unit ---------------------

    public List<Unit> findUnits() {
        List<Unit> units;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
        Root<Unit> prop = cq.from(Unit.class);

        TypedQuery<Unit> query = em.createQuery(cq);
        units = query.getResultList();
        logger.log(Level.INFO, "Number of units: {0}", units.size());

        return units;
    }

    public Unit findUnit(Long id) {
        return em.find(Unit.class, id);
    }

    public Unit findUnitByName(String name) {
        Unit unit;
        try {
            unit = em.createNamedQuery("Unit.findByUnitName", Unit.class).setParameter("unitName", name).getSingleResult();
        } catch (NoResultException e) {
            unit = null;
        }
        return unit;
    }

    public void addUnit(Unit unit) {
        unit.setModifiedAt(new Date());
        em.persist(unit);
    }

    public void saveUnit(Unit unit) {
        unit.setModifiedAt(new Date());
        em.merge(unit);
    }

    public void deleteUnit(Unit unit) {
        final Unit unitToDelete = findUnit(unit.getId());
        em.remove(unitToDelete);
    }

    // ---------------- Data Type -------------------------

    public List<DataType> findDataType() {
        List<DataType> datatypes;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DataType> cq = cb.createQuery(DataType.class);
        Root<DataType> prop = cq.from(DataType.class);

        TypedQuery<DataType> query = em.createQuery(cq);
        datatypes = query.getResultList();
        logger.log(Level.INFO, "Number of units: {0}", datatypes.size());

        return datatypes;
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
        List<SlotRelation> slotrels;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SlotRelation> cq = cb.createQuery(SlotRelation.class);
        Root<SlotRelation> prop = cq.from(SlotRelation.class);

        TypedQuery<SlotRelation> query = em.createQuery(cq);
        slotrels = query.getResultList();
        logger.log(Level.INFO, "Number of units: {0}", slotrels.size());

        return slotrels;
    }

    public SlotRelation findSlotRelation(Long id) {
        return em.find(SlotRelation.class, id);
    }

    // ---------------- Audit Records -------------------------

    public List<AuditRecord> findAuditRecord() {
        List<AuditRecord> auditRec;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AuditRecord> cq = cb.createQuery(AuditRecord.class);
        Root<AuditRecord> prop = cq.from(AuditRecord.class);

        TypedQuery<AuditRecord> query = em.createQuery(cq);
        auditRec = query.getResultList();
        logger.log(Level.INFO, "Number of audit records: {0}", auditRec.size());

        return auditRec;
    }

    public AuditRecord findDAuditRecord(int id) {
        return em.find(AuditRecord.class, id);
    }

    /*
     * public void logAuditEntry(String key, String oper, String entry) {
     * AuditRecord arec = new AuditRecord(); arec.setEntry(entry);
     * arec.setLogTime(new Date()); arec.setOperation(oper); arec.setUser(key);
     *
     * em.persist(arec); }
     */

}
