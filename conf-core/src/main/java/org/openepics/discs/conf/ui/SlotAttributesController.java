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

import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
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
import org.openepics.discs.conf.views.BuiltInProperty;
import org.openepics.discs.conf.views.EntityAttributeView;
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

    private static final Logger logger = Logger.getLogger(SlotAttributesController.class.getCanonicalName());

    // BIP = Built-In Property
    private static final String BIP_DESCRIPTION = "Description";
    private static final String BIP_BEAMLINE_POS = "Beamline position";
    private static final String BIP_GLOBAL_X = "Global X";
    private static final String BIP_GLOBAL_Y = "Global Y";
    private static final String BIP_GLOBAL_Z = "Global Z";
    private static final String BIP_GLOBAL_PITCH = "Global pitch";
    private static final String BIP_GLOBAL_ROLL = "Global roll";
    private static final String BIP_GLOBAL_YAW = "Global yaw";

    @Inject private SlotEJB slotEJB;
    @Inject private PropertyEJB propertyEJB;

    private Slot slot;
    private String parentSlot;

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
            entityName = slot.getName();

            populateAttributesList();
            filterProperties();
            parentSlot = slot.getChildrenSlotsPairList().size() > 0
                    ? slot.getChildrenSlotsPairList().get(0).getParentSlot().getName()
                            : null;
            if ("_ROOT".equals(parentSlot)) {
                parentSlot = null;
            }
        } catch(Exception e) {
            throw new UIException("Slot details display initialization failed: " + e.getMessage(), e);
        }
    }

    /**
     * Redirection back to view of all container {@link Slot}s
     */
    public String containerRedirect() {
        return "containers-manager.xhtml?faces-redirect=true";
    }

    /**
     * Redirection back to view of all installation {@link Slot}s
     */
    public String installSlotRedirect() {
        return "installation-slots-manager.xhtml?faces-redirect=true";
    }

    @Override
    protected void populateAttributesList() {
        attributes = new ArrayList<>();
        // refresh the component type from database. This refreshes all related collections as well.
        slot = slotEJB.findById(slot.getId());

        attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_DESCRIPTION, slot.getDescription(), strDataType)));
        if (slot.isHostingSlot()) {
            attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_BEAMLINE_POS, slot.getBeamlinePosition(), dblDataType)));
            final PositionInformation slotPosition = slot.getPositionInformation();
            attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_GLOBAL_X, slotPosition.getGlobalX(), dblDataType)));
            attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_GLOBAL_Y, slotPosition.getGlobalY(), dblDataType)));
            attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_GLOBAL_Z, slotPosition.getGlobalZ(), dblDataType)));
            attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_GLOBAL_PITCH, slotPosition.getGlobalPitch(), dblDataType)));
            attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_GLOBAL_ROLL, slotPosition.getGlobalRoll(), dblDataType)));
            attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_GLOBAL_YAW, slotPosition.getGlobalYaw(), dblDataType)));
        }

        for (ComptypePropertyValue parentProp : parentProperties) {
            if (parentProp.getPropValue() != null) attributes.add(new EntityAttributeView(parentProp));
        }

        for (SlotPropertyValue prop : slot.getSlotPropertyList()) {
            attributes.add(new EntityAttributeView(prop));
        }

        for (SlotArtifact art : slot.getSlotArtifactList()) {
            attributes.add(new EntityAttributeView(art));
        }

        for (Tag tag : slot.getTags()) {
            attributes.add(new EntityAttributeView(tag));
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

    public String getParentSlot() { return parentSlot; }

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
    public void modifyBuiltInProperty() {
        final BuiltInProperty builtInProperty = (BuiltInProperty) selectedAttribute.getEntity();
        final String builtInPropertyName = builtInProperty.getName();

        if (!slot.isHostingSlot() && !builtInPropertyName.equals(BIP_DESCRIPTION)) {
            logger.log(Level.WARNING, "Modifying built-in property on container that should not be used.");
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
        slotEJB.save(slot);
        populateAttributesList();
        RequestContext.getCurrentInstance().update("slotPropertiesManagerForm");
    }
}
