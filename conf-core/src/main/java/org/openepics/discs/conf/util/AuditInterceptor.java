package org.openepics.discs.conf.util;

import java.util.Date;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.openepics.discs.conf.ent.AlignmentArtifact;
import org.openepics.discs.conf.ent.AlignmentPropertyValue;
import org.openepics.discs.conf.ent.AlignmentRecord;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ui.LoginManager;


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
    @Inject private LoginManager loginManager;

    /**
     * Creates audit log after the method annotated with this interceptor has finished executing.
     *
     * @param context
     * @return
     * @throws Exception
     */
    @AroundInvoke
    public Object createAuditLog(InvocationContext context) throws Exception {

        final Object returnValue = context.proceed();

        final Object entity = context.getParameters()[0];
        if (context.getMethod().getAnnotation(CRUDOperation.class) != null) {
            final EntityTypeOperation operation = context.getMethod().getAnnotation(CRUDOperation.class).operation();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(Feature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(Feature.WRITE_DATES_AS_TIMESTAMPS, false);
            if (entity instanceof Property) {
                final Property property = (Property) entity;
                makeAuditEntry(operation, EntityType.PROPERTY, property.getName(), mapper.writeValueAsString(property), property.getId());
            } else if (entity instanceof Unit) {
                final Unit unit = (Unit) entity;
                makeAuditEntry(operation, EntityType.UNIT, unit.getName(), mapper.writeValueAsString(unit), unit.getId());
            } else if (entity instanceof Slot) {
                final Slot slot = (Slot) entity;
                makeAuditEntry(operation, EntityType.SLOT, slot.getName(), mapper.writeValueAsString(slot), slot.getId());
            } else if (entity instanceof SlotPropertyValue) {
                final SlotPropertyValue slotProperty = (SlotPropertyValue) entity;
                makeAuditEntry(operation, EntityType.SLOT, slotProperty.getSlot().getName(), mapper.writeValueAsString(slotProperty), slotProperty.getSlot().getId());
            } else if (entity instanceof SlotArtifact) {
                final SlotArtifact slotArtifact = (SlotArtifact) entity;
                makeAuditEntry(operation, EntityType.SLOT, slotArtifact.getSlot().getName(), mapper.writeValueAsString(slotArtifact), slotArtifact.getSlot().getId());
            } else if (entity instanceof SlotPair) {
                final SlotPair slotPair = (SlotPair) entity;
                makeAuditEntry(operation, EntityType.SLOT, slotPair.getChildSlot().getName(), mapper.writeValueAsString(slotPair), slotPair.getChildSlot().getId());
                makeAuditEntry(operation, EntityType.SLOT, slotPair.getParentSlot().getName(), mapper.writeValueAsString(slotPair), slotPair.getParentSlot().getId());
            } else if (entity instanceof InstallationRecord) {
                final InstallationRecord installationRecord = (InstallationRecord) entity;
                makeAuditEntry(operation, EntityType.INSTALLATION_RECORD, installationRecord.getRecordNumber(), mapper.writeValueAsString(installationRecord), installationRecord.getId());
            } else if (entity instanceof InstallationArtifact) {
                final InstallationArtifact installationArtifact = (InstallationArtifact) entity;
                makeAuditEntry(operation, EntityType.INSTALLATION_RECORD, installationArtifact.getInstallationRecord().getRecordNumber(), mapper.writeValueAsString(installationArtifact), installationArtifact.getInstallationRecord().getId());
            } else if (entity instanceof Device) {
                final Device device = (Device) entity;
                makeAuditEntry(operation, EntityType.DEVICE, device.getSerialNumber(), mapper.writeValueAsString(device), device.getId());
            } else if (entity instanceof DevicePropertyValue) {
                final DevicePropertyValue deviceProperty = (DevicePropertyValue) entity;
                makeAuditEntry(operation, EntityType.DEVICE, deviceProperty.getDevice().getSerialNumber(), mapper.writeValueAsString(deviceProperty), deviceProperty.getDevice().getId());
            } else if (entity instanceof DeviceArtifact) {
                final DeviceArtifact deviceArtifact = (DeviceArtifact) entity;
                makeAuditEntry(operation, EntityType.DEVICE, deviceArtifact.getDevice().getSerialNumber(), mapper.writeValueAsString(deviceArtifact), deviceArtifact.getDevice().getId());
            } else if (entity instanceof ComponentType) {
                final ComponentType componentType = (ComponentType) entity;
                makeAuditEntry(operation, EntityType.COMPONENT_TYPE, componentType.getName(), mapper.writeValueAsString(componentType), componentType.getId());
            } else if (entity instanceof ComptypePropertyValue) {
                final ComptypePropertyValue comptypeProperty = (ComptypePropertyValue) entity;
                makeAuditEntry(operation, EntityType.COMPONENT_TYPE, comptypeProperty.getComponentType().getName(), mapper.writeValueAsString(comptypeProperty), comptypeProperty.getComponentType().getId());
            } else if (entity instanceof ComptypeArtifact) {
                final ComptypeArtifact comptypeArtifact = (ComptypeArtifact) entity;
                makeAuditEntry(operation, EntityType.COMPONENT_TYPE, comptypeArtifact.getComponentType().getName(), mapper.writeValueAsString(comptypeArtifact), comptypeArtifact.getComponentType().getId());
            } else if (entity instanceof AlignmentRecord) {
                final AlignmentRecord alignmentRecord = (AlignmentRecord) entity;
                makeAuditEntry(operation, EntityType.ALIGNMENT_RECORD, alignmentRecord.getRecordNumber(), mapper.writeValueAsString(alignmentRecord), alignmentRecord.getId());
            } else if (entity instanceof AlignmentPropertyValue) {
                final AlignmentPropertyValue alignmentProperty = (AlignmentPropertyValue) entity;
                makeAuditEntry(operation, EntityType.ALIGNMENT_RECORD, alignmentProperty.getAlignmentRecord().getRecordNumber(), mapper.writeValueAsString(alignmentProperty), alignmentProperty.getAlignmentRecord().getId());
            } else if (entity instanceof AlignmentArtifact) {
                final AlignmentArtifact alignmentArtifact = (AlignmentArtifact) entity;
                makeAuditEntry(operation, EntityType.ALIGNMENT_RECORD, alignmentArtifact.getAlignmentRecord().getRecordNumber(), mapper.writeValueAsString(alignmentArtifact), alignmentArtifact.getAlignmentRecord().getId());
            }
        }

        return returnValue;
    }

    private void makeAuditEntry(EntityTypeOperation oper, EntityType entityType, String key, String entry, Long id) {
        AuditRecord arec = new AuditRecord(oper, loginManager.getUserid(), entry, id);
        arec.setEntityType(entityType);
        arec.setEntityKey(key);
        em.persist(arec);
    }
}
