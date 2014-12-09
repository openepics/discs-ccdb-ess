/**
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any
 * newer version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.openepics.discs.conf.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DevicePropertyValue;
import org.openepics.discs.conf.ent.DeviceStatus;
import org.openepics.discs.conf.ent.Tag;
import org.openepics.discs.conf.ent.values.EnumValue;
import org.openepics.discs.conf.ent.values.StrValue;
import org.openepics.discs.conf.ui.common.AbstractAttributesController;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.openepics.discs.conf.views.BuiltInProperty;
import org.openepics.discs.conf.views.EntityAttributeView;
import org.openepics.seds.api.datatypes.SedsEnum;
import org.openepics.seds.core.Seds;

/**
 * Controller bean for manipulation of {@link Device} attributes
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
@Named
@ViewScoped
public class DeviceDetailsAttributesController extends AbstractAttributesController<DevicePropertyValue, DeviceArtifact> {

    // BIP = Built-In Property
    private static final String BIP_INVENTORY_ID = "Inventory ID";
    private static final String BIP_DESCRIPTION = "Description";
    private static final String BIP_LOCATION = "Location";
    private static final String BIP_MANUFACTURER = "Manufacturer";
    private static final String BIP_MANUFACTURER_MODEL = "Manufacturer model";
    private static final String BIP_MANUFACTURER_SERIAL_NO = "Manufacturer serial #";
    private static final String BIP_P_O_REFERENCE = "Purchase order reference";
    private static final String BIP_STATUS = "Status";

    @Inject private DeviceEJB deviceEJB;

    private Device device;

    @Override
    @PostConstruct
    public void init() {
        try {
            super.init();
            final Long id = Long.parseLong(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("id"));
            device = deviceEJB.findById(id);
            super.setArtifactClass(DeviceArtifact.class);
            super.setPropertyValueClass(DevicePropertyValue.class);
            super.setDao(deviceEJB);

            parentProperties = device.getComponentType().getComptypePropertyList();

            constructDeviceStatusEnum();
            populateAttributesList();
        } catch(Exception e) {
            throw new UIException("Device details display initialization fialed: " + e.getMessage(), e);
        }
    }

    @Override
    protected void setPropertyValueParent(DevicePropertyValue child) {
        child.setDevice(device);
    }

    @Override
    protected void setArtifactParent(DeviceArtifact child) {
        child.setDevice(device);
    }

    @Override
    protected void setTagParent(Tag tag) {
        final Set<Tag> existingTags = device.getTags();
        if (!existingTags.contains(tag)) {
            existingTags.add(tag);
            deviceEJB.save(device);
        }
    }

    @Override
    protected void deleteTagFromParent(Tag tag) {
        device.getTags().remove(tag);
        deviceEJB.save(device);
    }

    @Override
    protected void filterProperties() {
        // nothing to do
    }

    @Override
    protected void populateAttributesList() {
        attributes = new ArrayList<EntityAttributeView>();

        // refresh the device from database. This refreshes all related collections as well.
        device = deviceEJB.findById(device.getId());

        attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_INVENTORY_ID, device.getSerialNumber(), strDataType)));
        attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_DESCRIPTION, device.getDescription(), strDataType)));
        attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_LOCATION, device.getLocation(), strDataType)));
        attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_P_O_REFERENCE, device.getPurchaseOrder(), strDataType)));
        attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_STATUS, new EnumValue(device.getStatus().name()), enumDataType)));
        attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_MANUFACTURER, device.getManufacturer(), strDataType)));
        attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_MANUFACTURER_MODEL, device.getManufacturerModel(), strDataType)));
        attributes.add(new EntityAttributeView(new BuiltInProperty(BIP_MANUFACTURER_SERIAL_NO, device.getManufacturerSerialNumber(), strDataType)));

        for (ComptypePropertyValue parentProp : parentProperties) {
            if (parentProp.getPropValue() != null) attributes.add(new EntityAttributeView(parentProp));
        }

        for (DevicePropertyValue propVal : device.getDevicePropertyList()) {
            attributes.add(new EntityAttributeView(propVal));
        }

        // TODO check whether to show inherited artifacts and prevent their deletion
        for (DeviceArtifact artf : device.getDeviceArtifactList()) {
            attributes.add(new EntityAttributeView(artf));
        }

        // TODO solve and add inherited tags.
        for (Tag tag : device.getTags()) {
            attributes.add(new EntityAttributeView(tag));
        }
    }

    @Override
    public void modifyBuiltInProperty() {
        final BuiltInProperty builtInProperty = (BuiltInProperty) selectedAttribute.getEntity();
        final String builtInPropertyName = builtInProperty.getName();

        final String userValueStr = (propertyValue == null ? null
                : (propertyValue instanceof StrValue ? ((StrValue)propertyValue).getStrValue() : null));

        switch (builtInPropertyName) {
            case BIP_INVENTORY_ID:
                if ((userValueStr == null) || !userValueStr.equals(device.getSerialNumber())) {
                    device.setSerialNumber(userValueStr);
                    deviceEJB.save(device);
                }
                break;
            case BIP_DESCRIPTION:
                if ((userValueStr == null) || !userValueStr.equals(device.getDescription())) {
                    device.setDescription(userValueStr);
                    deviceEJB.save(device);
                }
                break;
            case BIP_LOCATION:
                if ((userValueStr == null) || !userValueStr.equals(device.getLocation())) {
                    device.setLocation(userValueStr);
                    deviceEJB.save(device);
                }
                break;
            case BIP_P_O_REFERENCE:
                if ((userValueStr == null) || !userValueStr.equals(device.getPurchaseOrder())) {
                    device.setPurchaseOrder(userValueStr);
                    deviceEJB.save(device);
                }
                break;
            case BIP_MANUFACTURER:
                if ((userValueStr == null) || !userValueStr.equals(device.getManufacturer())) {
                    device.setManufacturer(userValueStr);
                    deviceEJB.save(device);
                }
                break;
            case BIP_MANUFACTURER_MODEL:
                if ((userValueStr == null) || !userValueStr.equals(device.getManufacturerModel())) {
                    device.setManufacturerModel(userValueStr);
                    deviceEJB.save(device);
                }
                break;
            case BIP_MANUFACTURER_SERIAL_NO:
                if ((userValueStr == null) || !userValueStr.equals(device.getManufacturerSerialNumber())) {
                    device.setManufacturerSerialNumber(userValueStr);
                    deviceEJB.save(device);
                }
                break;
            case BIP_STATUS:
                final String userValueEnum = (propertyValue == null ? null
                        : (propertyValue instanceof EnumValue ? ((EnumValue)propertyValue).getEnumValue() : null));
                if ((userValueEnum == null) || !userValueEnum.equals(device.getStatus().name())) {
                    device.setStatus(Enum.valueOf(DeviceStatus.class, userValueEnum));;
                    deviceEJB.save(device);
                }
                break;
            default:
                throw new UnhandledCaseException();
        }
        populateAttributesList();
    }

    private void constructDeviceStatusEnum() {
        final DeviceStatus[] devStatusEnumValues = DeviceStatus.values();
        final String[] devStatusEnumStrs = new String[devStatusEnumValues.length];
        int i = 0;
        for (DeviceStatus status : devStatusEnumValues) {
            devStatusEnumStrs[i++] = status.name();
        }
        final SedsEnum devStatusEnum = Seds.newFactory().newEnum(devStatusEnumStrs[0], devStatusEnumStrs);
        JsonObject jsonEnum = Seds.newDBConverter().serialize(devStatusEnum);

        enumDataType = new DataType("Built-in status", "Built in device status temporary data type", false,
                                        jsonEnum.toString());
        enumDataType.setModifiedBy("system");
        enumDataType.setModifiedAt(new Date());
    }

    public Device getDevice() {
        return device;
    }
    public void setDevice(Device device) {
        this.device = device;
    }
}
