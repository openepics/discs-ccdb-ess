/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.ccdb.gui.device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.openepics.discs.ccdb.core.ejb.DeviceEJB;
import org.openepics.discs.ccdb.core.ejb.InstallationEJB;
import org.openepics.discs.ccdb.core.ejb.SlotEJB;
import org.openepics.discs.ccdb.model.ComponentType;
import org.openepics.discs.ccdb.model.ComptypeArtifact;
import org.openepics.discs.ccdb.model.ComptypePropertyValue;
import org.openepics.discs.ccdb.model.Device;
import org.openepics.discs.ccdb.model.DeviceArtifact;
import org.openepics.discs.ccdb.model.DevicePropertyValue;
import org.openepics.discs.ccdb.model.InstallationRecord;
import org.openepics.discs.ccdb.model.Slot;
import org.openepics.discs.ccdb.model.SlotArtifact;
import org.openepics.discs.ccdb.model.SlotPropertyValue;
import org.openepics.discs.ccdb.model.Tag;
import org.openepics.discs.ccdb.gui.ui.util.UiUtility;
import org.openepics.discs.ccdb.gui.views.EntityAttrArtifactView;
import org.openepics.discs.ccdb.gui.views.EntityAttrPropertyValueView;
import org.openepics.discs.ccdb.gui.views.EntityAttrTagView;
import org.openepics.discs.ccdb.gui.views.EntityAttributeView;
import org.openepics.discs.ccdb.gui.views.EntityAttributeViewKind;

/**
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class DeviceManager implements Serializable {

    @EJB
    private SlotEJB slotEJB;
    @Inject
    private InstallationEJB installationEJB;
    @Inject private DeviceSlotManager slotManager;
    
    private static final Logger logger = Logger.getLogger(DeviceManager.class.getName());

    // parameters
    private Device selectedDevice;    
    private String deviceName;
    private String invenentoryId;

    
    private Slot selectedSlot;
    protected List<EntityAttributeView<Device>> deviceAttributes = new ArrayList<>();
    protected List<EntityAttributeView<Slot>> slotAttributes;
//    protected List<EntityAttributeView<Device>> filteredAttributes;
    
    public DeviceManager() {

    }

    @PostConstruct
    public void init() {
    }

    /**
     *
     */
    public void initializeView() {
        if (deviceName != null && !deviceName.isEmpty()) {
            selectedSlot = slotEJB.findByName(deviceName);
            if (selectedSlot == null) {
                UiUtility.showMessage(FacesMessage.SEVERITY_ERROR, "Invalid device name", deviceName);
                return;
            }
            slotManager.setSelectedSlot(selectedSlot);
            slotManager.populateAttributesList();
            slotAttributes = slotManager.getAttributes();
        }
        if (selectedDevice != null) {
            populateAttributesList(selectedDevice);
        }
    }

    
    protected void populateAttributesList(Device attrDevice) {
        deviceAttributes.clear();
      
        final ComponentType parent = attrDevice.getComponentType();

        for (final ComptypePropertyValue parentProp : parent.getComptypePropertyList()) {
            if (parentProp.getPropValue() != null) {
                deviceAttributes.add(new EntityAttrPropertyValueView<>(parentProp, attrDevice, parent));
            }
        }

        for (final ComptypeArtifact parentArtifact : parent.getComptypeArtifactList()) {
            deviceAttributes.add(new EntityAttrArtifactView<>(parentArtifact, attrDevice, parent));
        }

        for (final Tag parentTag : parent.getTags()) {
            deviceAttributes.add(new EntityAttrTagView<>(parentTag, attrDevice, parent));
        }

        for (final DevicePropertyValue propVal : attrDevice.getDevicePropertyList()) {
            deviceAttributes.add(new EntityAttrPropertyValueView<>(propVal, attrDevice));
        }

        for (final DeviceArtifact artf : attrDevice.getDeviceArtifactList()) {
            deviceAttributes.add(new EntityAttrArtifactView<>(artf, attrDevice));
        }

        for (final Tag tagAttr : attrDevice.getTags()) {
            deviceAttributes.add(new EntityAttrTagView<>(tagAttr, attrDevice));
        }

        final InstallationRecord installationRecord = installationEJB.getActiveInstallationRecordForDevice(attrDevice);
        final Slot slot = installationRecord != null ? installationRecord.getSlot() : null;

        if (slot != null) {
            for (final SlotPropertyValue value : slot.getSlotPropertyList()) {
                deviceAttributes.add(new EntityAttrPropertyValueView<>(value, attrDevice, slot));
            }
            for (final SlotArtifact value : slot.getSlotArtifactList()) {
                deviceAttributes.add(new EntityAttrArtifactView<>(value, attrDevice, slot));
            }
            for (final Tag tag : slot.getTags()) {
                deviceAttributes.add(new EntityAttrTagView<>(tag, attrDevice, slot));
            }
        } else {
            for (final ComptypePropertyValue parentProp : parent.getComptypePropertyList()) {
                if (parentProp.isDefinitionTargetSlot()) {
                    deviceAttributes.add(new EntityAttrPropertyValueView<>(parentProp,
                            EntityAttributeViewKind.INSTALL_SLOT_PROPERTY,
                            attrDevice, parent));
                }
            }
        }
    }
    
    // -- getters/setter

    public List<EntityAttributeView<Device>> getDeviceAttributes() {
        return deviceAttributes;
    }


    public Device getSelectedDevice() {
        return selectedDevice;
    }

    public void setSelectedDevice(Device selectedDevice) {
        this.selectedDevice = selectedDevice;
    }  

    

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getInvenentoryId() {
        return invenentoryId;
    }

    public void setInvenentoryId(String invenentoryId) {
        this.invenentoryId = invenentoryId;
    }

    public List<EntityAttributeView<Slot>> getSlotAttributes() {
        return slotAttributes;
    }

    public DeviceSlotManager getSlotManager() {
        return slotManager;
    }

    public Slot getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(Slot selectedSlot) {
        this.selectedSlot = selectedSlot;
    }
    
}
