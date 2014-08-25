/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 * Copyright (c) 2041 FRIB
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.openepics.discs.conf.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
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
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 */

@Stateless public class ConfigurationEJB {

    private static final Logger logger = Logger.getLogger(ConfigurationEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;
    @Inject private ConfigurationEntityUtility entityUtility;

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
        entityUtility.setModified(property);
        em.persist(property);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveProperty(Property property) {
        entityUtility.setModified(property);
        em.merge(property);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void deleteProperty(Property property) {
        final Property mergedProp = em.find(Property.class, property.getId());
        //mergedProp.getUnit().getPropertyList().remove(mergedProp);
        //mergedProp.getDataType().getPropertyList().remove(mergedProp);
        em.remove(mergedProp);
    }

    // -------------------- Unit ---------------------

    public List<Unit> findUnits() {
        final CriteriaQuery<Unit> cq = em.getCriteriaBuilder().createQuery(Unit.class);
        cq.from(Unit.class);
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
        entityUtility.setModified(unit);
        em.persist(unit);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveUnit(Unit unit) {
        entityUtility.setModified(unit);
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

    public List<DataType> findDataTypes() {
        final CriteriaQuery<DataType> cq = em.getCriteriaBuilder().createQuery(DataType.class);
        cq.from(DataType.class);
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

    public List<SlotRelation> findSlotRelations() {
        final CriteriaQuery<SlotRelation> cq = em.getCriteriaBuilder().createQuery(SlotRelation.class);
        cq.from(SlotRelation.class);
        final List<SlotRelation> slotRelations = em.createQuery(cq).getResultList();

        logger.log(Level.INFO, "Number of slot relations: {0}", slotRelations.size());

        return slotRelations;
    }

    public SlotRelation findSlotRelation(Long id) {
        return em.find(SlotRelation.class, id);
    }

    // ---------------- Audit Records -------------------------

    public List<AuditRecord> findAuditRecords() {
        final CriteriaQuery<AuditRecord> cq = em.getCriteriaBuilder().createQuery(AuditRecord.class);
        cq.from(AuditRecord.class);
        final List<AuditRecord> auditRecords = em.createQuery(cq).getResultList();


        logger.log(Level.INFO, "Number of audit records: {0}", auditRecords.size());

        return auditRecords;
    }

    public List<AuditRecord> findAuditRecordsByEntityId(Long entityId) {
        final List<AuditRecord> auditRecords = em.createNamedQuery("AuditRecord.findByEntityId", AuditRecord.class).setParameter("entityId", entityId).getResultList();
        return auditRecords == null ? new ArrayList<AuditRecord>() : auditRecords;
    }

    public AuditRecord findDAuditRecord(int id) {
        return em.find(AuditRecord.class, id);
    }
}
