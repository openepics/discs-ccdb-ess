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
package org.openepics.discs.conf.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.PositionInformation;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ent.values.DblValue;
import org.openepics.discs.conf.ent.values.StrValue;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.BuiltInProperty;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.EntityAttributeViewKind;
import org.openepics.discs.conf.views.SlotBuiltInPropertyName;
import org.primefaces.context.RequestContext;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

/**
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Named
@ViewScoped
public class SlotAttributesController extends AbstractAttributesController<SlotPropertyValue, SlotArtifact> {

    private static final Logger LOGGER = Logger.getLogger(SlotAttributesController.class.getCanonicalName());

    @Inject transient private SlotEJB slotEJB;
    @Inject transient private PropertyEJB propertyEJB;
    @Inject transient private InstallationEJB installationEJB;

    private Slot slot;
    private String parentSlot;

    private ComponentType deviceType;

    @Override
    @PostConstruct
    public void init() {
        try {
            super.init();
            final Long id = Long.parseLong(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext()
                    .getRequest()).getParameter("id"));
            slot = slotEJB.findById(id);
            super.setArtifactClass(SlotArtifact.class);
            super.setPropertyValueClass(SlotPropertyValue.class);
            super.setDao(slotEJB);

            parentProperties = slot.getComponentType().getComptypePropertyList();
            parentArtifacts = slot.getComponentType().getComptypeArtifactList();
            populateParentTags();
            entityName = slot.getName();
            deviceType = slot.getComponentType();

            populateAttributesList();
            filterProperties();
            parentSlot = slot.getPairsInWhichThisSlotIsAChildList().size() > 0
                    ? slot.getPairsInWhichThisSlotIsAChildList().get(0).getParentSlot().getName()
                            : null;
            if ("_ROOT".equals(parentSlot)) {
                parentSlot = null;
            }
        } catch(Exception e) {
            throw new UIException("Slot details display initialization failed: " + e.getMessage(), e);
        }
    }

    @Override
    protected void populateAttributesList() {
        attributes = new ArrayList<>();
        // refresh the component type from database. This refreshes all related collections as well.
        slot = slotEJB.findById(slot.getId());

        attributes.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_DESCRIPTION, slot.getDescription(), strDataType)));
        if (slot.isHostingSlot()) {
            attributes.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_BEAMLINE_POS, slot.getBeamlinePosition(), dblDataType)));
            final PositionInformation slotPosition = slot.getPositionInformation();
            attributes.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_GLOBAL_X, slotPosition.getGlobalX(), dblDataType)));
            attributes.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_GLOBAL_Y, slotPosition.getGlobalY(), dblDataType)));
            attributes.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_GLOBAL_Z, slotPosition.getGlobalZ(), dblDataType)));
            attributes.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_GLOBAL_PITCH, slotPosition.getGlobalPitch(), dblDataType)));
            attributes.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_GLOBAL_ROLL, slotPosition.getGlobalRoll(), dblDataType)));
            attributes.add(new EntityAttributeView(new BuiltInProperty(SlotBuiltInPropertyName.BIP_GLOBAL_YAW, slotPosition.getGlobalYaw(), dblDataType)));
        }

        for (ComptypePropertyValue parentProp : parentProperties) {
            if (parentProp.getPropValue() != null) attributes.add(new EntityAttributeView(parentProp, EntityAttributeViewKind.DEVICE_TYPE_PROPERTY));
        }

        for (ComptypeArtifact parentArtifact : parentArtifacts) {
            attributes.add(new EntityAttributeView(parentArtifact, EntityAttributeViewKind.DEVICE_TYPE_ARTIFACT));
        }

        for (Tag parentTag : parentTags) {
            attributes.add(new EntityAttributeView(parentTag, EntityAttributeViewKind.DEVICE_TYPE_TAG));
        }

        for (SlotPropertyValue prop : slot.getSlotPropertyList()) {
            attributes.add(new EntityAttributeView(prop, EntityAttributeViewKind.INSTALL_SLOT_PROPERTY));
        }

        for (SlotArtifact art : slot.getSlotArtifactList()) {
            attributes.add(new EntityAttributeView(art, EntityAttributeViewKind.INSTALL_SLOT_ARTIFACT));
        }

        for (Tag tag : slot.getTags()) {
            attributes.add(new EntityAttributeView(tag, EntityAttributeViewKind.INSTALL_SLOT_TAG));
        }
    }

    @Override
    protected void filterProperties() {
        List<Property> propertyCandidates = propertyEJB.findAllOrderedByName();

        // remove all properties that are already defined in device type either as value or as property.
        ComponentType compType = slot.getComponentType();
        for (ComptypePropertyValue comptypePropertyValue : compType.getComptypePropertyList()) {
            propertyCandidates.remove(comptypePropertyValue.getProperty());
        }

        // remove all properties that are already defined.
        for (SlotPropertyValue slotPropertyValue : slot.getSlotPropertyList()) {
            propertyCandidates.remove(slotPropertyValue.getProperty());
        }

        filteredProperties = ImmutableList.copyOf(Collections2.filter(propertyCandidates, new Predicate<Property>() {
            @Override
            public boolean apply(Property property) {
                return property.isSlotAssociation();
            }
        }));
    }

    /**
     * Returns {@link Slot} for which attributes are being manipulated
     */
    public Slot getSlot() {
        return slot;
    }

    @Override
    protected void setPropertyValueParent(SlotPropertyValue child) {
        child.setSlot(slot);
    }

    @Override
    protected void setArtifactParent(SlotArtifact child) {
        child.setSlot(slot);
    }

    public String getParentSlot() {
        return parentSlot;
    }

    @Override
    protected void setTagParent(Tag tag) {
        final Set<Tag> existingTags = slot.getTags();
        if (!existingTags.contains(tag)) {
            existingTags.add(tag);
            slotEJB.save(slot);
        }
    }

    @Override
    protected void deleteTagFromParent(Tag tag) {
        slot.getTags().remove(tag);
        slotEJB.save(slot);
    }

    @Override
    public void prepareForPropertyValueAdd() {
        isPropertyDefinition = false;
        super.prepareForPropertyValueAdd();
    }

    @Override
    protected void populateParentTags() {
        parentTags = new HashSet<Tag>();
        for (Tag parentTag : slot.getComponentType().getTags()) {
            parentTags.add(parentTag);
        }
    }

    @Override
    public void modifyBuiltInProperty() {
        final BuiltInProperty builtInProperty = (BuiltInProperty) selectedAttribute.getEntity();
        final SlotBuiltInPropertyName builtInPropertyName = (SlotBuiltInPropertyName)builtInProperty.getName();

        if (!slot.isHostingSlot() && !builtInPropertyName.equals(SlotBuiltInPropertyName.BIP_DESCRIPTION)) {
            LOGGER.log(Level.WARNING, "Modifying built-in property on container that should not be used.");
            return;
        }

        final String userValueStr = (propertyValue == null ? null
                        : (propertyValue instanceof StrValue ? ((StrValue)propertyValue).getStrValue() : null));
        final Double userValueDbl = (propertyValue == null ? null
                        : (propertyValue instanceof DblValue ? ((DblValue)propertyValue).getDblValue() : null));
        switch (builtInPropertyName) {
            case BIP_DESCRIPTION:
                if ((userValueStr == null) || !userValueStr.equals(slot.getDescription())) {
                    slot.setDescription(userValueStr);
                    slotEJB.save(slot);
                }
                break;
            case BIP_BEAMLINE_POS:
                if ((userValueDbl == null) || !userValueDbl.equals(slot.getBeamlinePosition())) {
                    slot.setBeamlinePosition(userValueDbl);
                    slotEJB.save(slot);
                }
                break;
            case BIP_GLOBAL_X:
                if ((userValueDbl == null) || !userValueDbl.equals(slot.getPositionInformation().getGlobalX())) {
                    slot.getPositionInformation().setGlobalX(userValueDbl);;
                    slotEJB.save(slot);
                }
                break;
            case BIP_GLOBAL_Y:
                if ((userValueDbl == null) || !userValueDbl.equals(slot.getPositionInformation().getGlobalY())) {
                    slot.getPositionInformation().setGlobalY(userValueDbl);;
                    slotEJB.save(slot);
                }
                break;
            case BIP_GLOBAL_Z:
                if ((userValueDbl != null) && !userValueDbl.equals(slot.getPositionInformation().getGlobalZ())) {
                    slot.getPositionInformation().setGlobalZ(userValueDbl);;
                    slotEJB.save(slot);
                }
                break;
            case BIP_GLOBAL_PITCH:
                if ((userValueDbl != null) && !userValueDbl.equals(slot.getPositionInformation().getGlobalPitch())) {
                    slot.getPositionInformation().setGlobalPitch(userValueDbl);;
                    slotEJB.save(slot);
                }
                break;
            case BIP_GLOBAL_ROLL:
                if ((userValueDbl != null) && !userValueDbl.equals(slot.getPositionInformation().getGlobalRoll())) {
                    slot.getPositionInformation().setGlobalRoll(userValueDbl);;
                    slotEJB.save(slot);
                }
                break;
            case BIP_GLOBAL_YAW:
                if ((userValueDbl != null) && !userValueDbl.equals(slot.getPositionInformation().getGlobalYaw())) {
                    slot.getPositionInformation().setGlobalYaw(userValueDbl);;
                    slotEJB.save(slot);
                }
                break;
            default:
                throw new UnhandledCaseException();
        }
        populateAttributesList();
    }

    @Override
    public void saveNewName() {
        slot.setName(entityName);
        slot.setComponentType(deviceType);
        slotEJB.save(slot);
        populateAttributesList();
        RequestContext.getCurrentInstance().update("slotPropertiesManagerForm");
    }

    /**
     * @return <code>true</code> if a {@link Device} is installed into the current slot, <code>false</code> otherwise.
     */
    public boolean isInstallationSlotFull() {
        if (!slot.isHostingSlot()) {
            throw new IllegalStateException("Installation information required on non installation slot.");
        }
        InstallationRecord installationRecord = installationEJB.getLastInstallationRecordForSlot(slot);
        return (installationRecord != null) && (installationRecord.getUninstallDate() == null);
    }

    public ComponentType getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(ComponentType deviceType) {
        this.deviceType = deviceType;
    }

    /** This method is used in the installation slot attributes screen to determine whether the user has changed either
     * the installation slot name or its device type.
     * @return <code>true</code> if the installation slot name or device type have not been changed,
     * <code>false</code> otherwise.
     */
    public boolean isBasicInfoUnchanged() {
        return (slot.getName().equals(entityName) && slot.getComponentType().equals(deviceType));
    }

    /**
     * @return path from the root slot to the currently selected slot
     */
    public String getSlotPath() {
        final String slotPath = Utility.buildSlotPath(slot).toString();
        return slotPath.substring(1, slotPath.length() - 1);
    }
}
