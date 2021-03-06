/*
 * Copyright (c) 2016 European Spallation Source
 * Copyright (c) 2016 Cosylab d.d.
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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.InstallationEJB;
import org.openepics.discs.conf.ejb.PropertyEJB;
import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.PropertyValue;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.util.PropertyValueNotUniqueException;
import org.openepics.discs.conf.views.EntityAttrArtifactView;
import org.openepics.discs.conf.views.EntityAttrPropertyValueView;
import org.openepics.discs.conf.views.EntityAttrTagView;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.discs.conf.views.EntityAttributeViewKind;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Named
@ViewScoped
public class SlotAttributeController
        extends AbstractAttributesController<Slot, SlotPropertyValue, SlotArtifact> {
    private static final long serialVersionUID = 1L;

    @Inject private SlotEJB slotEJB;
    @Inject private InstallationEJB installationEJB;
    @Inject private PropertyEJB propertyEJB;

    @Inject private HierarchiesController hierarchiesController;

    // ------ variables for attribute manipulation ------
    private List<Property> filteredProperties;

    public SlotAttributeController() {}

    /** Java EE post construct life-cycle method. */
    @PostConstruct
    public void init() {
        setDao(slotEJB);
    }

    @Override
    protected void populateAttributesList() {
        Preconditions.checkNotNull(hierarchiesController.getSelectedSlots());
        attributes = Lists.newArrayList();
        for (final Slot slot : hierarchiesController.getSelectedSlots()) {
            addPropertyValues(slot);
            addArtifacts(slot);
            addTags(slot);
        }
    }

    private void addPropertyValues(final Slot slot) {
        final InstallationRecord activeInstallationRecord = installationEJB.getActiveInstallationRecordForSlot(slot);

        for (final ComptypePropertyValue value : slot.getComponentType().getComptypePropertyList()) {
            if (!value.isPropertyDefinition()) {
                attributes.add(new EntityAttrPropertyValueView<Slot>(value, slot, slot.getComponentType()));
            }
        }

        for (final SlotPropertyValue value : slot.getSlotPropertyList()) {
            attributes.add(new EntityAttrPropertyValueView<Slot>(value, slot));
        }

        if (activeInstallationRecord != null) {
            for (final DevicePropertyValue devicePropertyValue : activeInstallationRecord.getDevice().
                                                                                        getDevicePropertyList()) {
                attributes.add(new EntityAttrPropertyValueView<Slot>(devicePropertyValue,
                                                            EntityAttributeViewKind.DEVICE_PROPERTY,
                                                            slot,
                                                            activeInstallationRecord.getDevice()));
            }
        }
    }

    private void addArtifacts(final Slot slot) {
        final InstallationRecord activeInstallationRecord = installationEJB.getActiveInstallationRecordForSlot(slot);

        for (final ComptypeArtifact artifact : slot.getComponentType().getComptypeArtifactList()) {
            attributes.add(new EntityAttrArtifactView<Slot>(artifact, slot, slot.getComponentType()));
        }

        for (final SlotArtifact artifact : slot.getSlotArtifactList()) {
            attributes.add(new EntityAttrArtifactView<Slot>(artifact, slot));
        }

        if (activeInstallationRecord != null) {
            for (final DeviceArtifact deviceArtifact : activeInstallationRecord.getDevice().getDeviceArtifactList()) {
                attributes.add(new EntityAttrArtifactView<Slot>(deviceArtifact, slot, activeInstallationRecord.getDevice()));
            }
        }
    }

    private void addTags(final Slot slot) {
        final InstallationRecord activeInstallationRecord = installationEJB.getActiveInstallationRecordForSlot(slot);

        for (final Tag tagInstance : slot.getComponentType().getTags()) {
            attributes.add(new EntityAttrTagView<Slot>(tagInstance, slot, slot.getComponentType()));
        }

        for (final Tag tagInstance : slot.getTags()) {
            attributes.add(new EntityAttrTagView<Slot>(tagInstance, slot));
        }

        if (activeInstallationRecord != null) {
            for (final Tag tagInstance : activeInstallationRecord.getDevice().getTags()) {
                attributes.add(new EntityAttrTagView<Slot>(tagInstance, slot, activeInstallationRecord.getDevice()));
            }
        }
    }

    /** @return <code>true</code> if the attribute "Delete" button can be enabled, <code>false</code> otherwise */
    public boolean canDeleteAttributes() {
        if (selectedAttributes == null || selectedAttributes.size() == 0) {
            return false;
        }
        boolean canDelete = true;
        for (EntityAttributeView<Slot> selectedAttribute : selectedAttributes) {
            canDelete = canDelete && canDelete(selectedAttribute);
        }
        return canDelete;
    }

    @Override
    protected boolean canDelete(EntityAttributeView<Slot> attributeView) {
        switch (attributeView.getKind()) {
            case CONTAINER_SLOT_ARTIFACT:
            case CONTAINER_SLOT_TAG:
            case CONTAINER_SLOT_PROPERTY:
            case INSTALL_SLOT_ARTIFACT:
            case INSTALL_SLOT_TAG:
                return true;
            case INSTALL_SLOT_PROPERTY:
                if (attributeView.getValue() != null) {
                    return true;
                } else {
                    return false;
                }
        default:
            return false;
        }
    }

    /** The handler called from the "Delete confirmation" dialog. This actually deletes an attribute */
    @Override
    public void deleteAttributes() {
        Preconditions.checkNotNull(selectedAttributes);
        int props = 0;
        for (EntityAttributeView<Slot> selectedAttribute : selectedAttributes) {
            final Slot slot = slotEJB.findByName(selectedAttribute.getParent());
            switch (selectedAttribute.getKind()) {
                case INSTALL_SLOT_ARTIFACT:
                case CONTAINER_SLOT_ARTIFACT:
                case CONTAINER_SLOT_PROPERTY:
                    slotEJB.deleteChild(selectedAttribute.getEntity());
                    hierarchiesController.refreshSlot(slot);
                    break;
                case INSTALL_SLOT_TAG:
                case CONTAINER_SLOT_TAG:
                    slot.getTags().remove(selectedAttribute.getEntity());
                    hierarchiesController.saveSlotAndRefresh(slot);
                    break;
                case INSTALL_SLOT_PROPERTY:
                    SlotPropertyValue prop = ((SlotPropertyValue)selectedAttribute.getEntity());
                    prop.setPropValue(null);
                    slotEJB.saveChild(prop);
                    hierarchiesController.refreshSlot(slot);
                    break;
                default:
                    throw new RuntimeException("Trying to delete an attribute that cannot be removed on home screen.");
            }
            ++props;
        }
        UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                "Deleted " + props + " properties.");
        clearRelatedAttributeInformation();
        populateAttributesList();
    }

    @Override
    public boolean canEdit(EntityAttributeView<Slot> attributeView) {
        switch (attributeView.getKind()) {
            case CONTAINER_SLOT_ARTIFACT:
            case CONTAINER_SLOT_PROPERTY:
            case INSTALL_SLOT_ARTIFACT:
            case INSTALL_SLOT_PROPERTY:
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void propertyNameChangeOverride(final EntityAttrPropertyValueView<Slot> propertyValueView) {
        propertyValueView.setPropertyNameChangeDisabled(propertyValueView.getParentEntity().isHostingSlot());
    }

    /** Prepares data for addition of {@link PropertyValue}. Only valid for containers. */
    public void prepareForPropertyValueAdd() {
        selectedAttributes = null;
        final SlotPropertyValue slotValueInstance = new SlotPropertyValue(false);
        slotValueInstance.setPropertiesParent(hierarchiesController.getSelectedNodeSlot());
        dialogAttribute = new EntityAttrPropertyValueView<Slot>(slotValueInstance, hierarchiesController.getSelectedEntity());
        filterProperties();
    }

    /** A method to add a {@link PropertyValue} to a container. */
    public void addPropertyValue() {
        Preconditions.checkNotNull(dialogAttribute);

        try {
            final EntityAttrPropertyValueView<Slot> view = getDialogAttrPropertyValue();
            final PropertyValue slotValueInstance = view.getEntity();

            slotEJB.addChild(slotValueInstance);
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                    "New property has been created");
            hierarchiesController.refreshSlot(view.getParentEntity());
        } catch (EJBException e) {
            if (UiUtility.causedBySpecifiedExceptionClass(e, PropertyValueNotUniqueException.class)) {
                FacesContext.getCurrentInstance().addMessage("uniqueMessage",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                                "Value is not unique."));
                FacesContext.getCurrentInstance().validationFailed();
            } else {
                throw e;
            }
        } finally {
            resetFields();
            populateAttributesList();
        }
    }

    @Override
    protected void filterProperties() {
        final List<Property> propertyCandidates = propertyEJB.findAllOrderedByName();

        final Property dialogProperty = getDialogAttrPropertyValue() != null
                                            ? getDialogAttrPropertyValue().getProperty() : null;

        // remove all properties that are already defined.
        for (final SlotPropertyValue slotPropertyValue : hierarchiesController.getSelectedNodeSlot().getSlotPropertyList()) {
            if (!slotPropertyValue.getProperty().equals(dialogProperty)) {
                propertyCandidates.remove(slotPropertyValue.getProperty());
            }
        }
        filteredProperties = propertyCandidates;
    }

    @Override
    protected void refreshParentEntity(EntityAttributeView<Slot> attributeView) {
        hierarchiesController.refreshSlot(attributeView.getParentEntity());
    }

    @Override
    protected Slot getSelectedEntity() {
        return hierarchiesController.getSelectedEntity();
    }

    public void setAttrbutes(List<EntityAttributeView<Slot>> attributes) {
        this.attributes = attributes;
    }

    /** @return the filteredProperties */
    public List<Property> getFilteredProperties() {
        return filteredProperties;
    }

    @Override
    protected SlotPropertyValue newPropertyValue() {
        return new SlotPropertyValue();
    }

    @Override
    protected SlotArtifact newArtifact() {
        return new SlotArtifact();
    }
}
