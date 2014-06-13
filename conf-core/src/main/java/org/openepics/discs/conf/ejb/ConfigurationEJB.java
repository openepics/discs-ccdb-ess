/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.conf.ejb;

import java.util.Date;
import org.openepics.discs.conf.ent.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author vuppala
 */
@Stateless
public class ConfigurationEJB implements ConfigurationEJBLocal {

    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    @PersistenceContext(unitName = "org.openepics.discs.conf.data")
    private EntityManager em;

    // --------------------  Property  ---------------------
    @Override
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

    @Override
    public Property findProperty(int id) {
        return em.find(Property.class, id);
    }

    @Override
    public void saveProperty(Property property) throws Exception {
        if (property == null) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");
        }
        property.setModifiedAt(new Date());
        em.merge(property);
    }

    @Override
    public void addProperty(Property property) throws Exception {
        if (property == null) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");
        }
        property.setModifiedAt(new Date());
        em.persist(property);
    }

    @Override
    public void deleteProperty(Property property) throws Exception {
        if (property == null) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");
        }
        Property prop = em.find(Property.class, property.getPropertyId());
        em.remove(prop);
    }

    // --------------------  Unit ---------------------
    @Override
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

    @Override
    public Unit findUnit(String id) {
        return em.find(Unit.class, id);
    }

    // ----------------  Data Type -------------------------
    @Override
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

    @Override
    public DataType findDataType(String id) {
        return em.find(DataType.class, id);
    }

    // ----------------  Slot Relations -------------------------
    @Override
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

    @Override
    public SlotRelation findSlotRelation(int id) {
        return em.find(SlotRelation.class, id);
    }

    // ---------------- Audit Records -------------------------
    @Override
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

    @Override
    public AuditRecord findDAuditRecord(int id) {
        return em.find(AuditRecord.class, id);
    }
    
    /*
    public void logAuditEntry(String key, String oper, String entry) {
        AuditRecord arec = new AuditRecord();
        arec.setEntry(entry);
        arec.setLogTime(new Date());
        arec.setOperation(oper);
        arec.setUser(key);
        
        em.persist(arec);
    }
    */
    
}
