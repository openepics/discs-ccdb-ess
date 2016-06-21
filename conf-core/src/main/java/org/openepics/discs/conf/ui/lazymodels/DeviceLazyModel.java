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
package org.openepics.discs.conf.ui.lazymodels;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.openepics.discs.conf.ejb.DeviceEJB;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.InstallationRecord;
import org.openepics.discs.conf.ent.fields.DeviceFields;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.views.DeviceView;
import org.primefaces.model.SortOrder;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
public class DeviceLazyModel extends CCDBLazyModel<DeviceView> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DeviceLazyModel.class.getCanonicalName());

    private static final String DEVICE_TYPE = "device.componentType.name";
    private static final String SERIAL_NUMBER = "inventoryId";
    private static final String INSTALLATION_SLOT = "installedIn";
    private static final String INSTALLATION_DATE = "installationDate";


    private final DeviceEJB deviceEJB;

    public DeviceLazyModel(DeviceEJB deviceEJB) {
        super(deviceEJB);
        this.deviceEJB = deviceEJB;
    }

    @Override
    public List<DeviceView> load(int first, int pageSize, String sortField,
            SortOrder sortOrder, Map<String, Object> filters) {
        LOGGER.log(Level.FINEST, "---->pageSize: " + pageSize);
        LOGGER.log(Level.FINEST, "---->first: " + first);

        for (final String filterKey : filters.keySet()) {
            LOGGER.log(Level.FINER, "filter[" + filterKey + "] = " + filters.get(filterKey).toString());
        }

        setLatestLoadData(sortField, sortOrder, filters);

        final String deviceType = filters.containsKey(DEVICE_TYPE) ? filters.get(DEVICE_TYPE).toString() : null;
        final String serialNumber = filters.containsKey(SERIAL_NUMBER) ? filters.get(SERIAL_NUMBER).toString() : null;
        final String installationSlot = filters.containsKey(INSTALLATION_SLOT)
                                                ? filters.get(INSTALLATION_SLOT).toString()
                                                : null;
        final Date installationDate = parseLogDateTime(filters);

        final List<Device> devices = deviceEJB.findLazy(first, pageSize,
                selectSortField(sortField), UiUtility.translateToCCDBSortOrder(sortOrder),
                deviceType, serialNumber, installationSlot, installationDate);

        final List<DeviceView> results = devices.stream().map(e ->
                    {
                        final InstallationRecord activeInstallation = findActiveInstallation(e);
                        return new DeviceView(e,
                                    activeInstallation == null ? "-" : activeInstallation.getSlot().getName(),
                                    activeInstallation == null ? null
                                                                : Long.toString(activeInstallation.getSlot().getId()),
                                    activeInstallation == null ? null : activeInstallation.getInstallDate());
                    }).collect(Collectors.toList());

        setEmpty(first, results);

        return results;
    }

    @Override
    public Object getRowKey(DeviceView object) {
        return object.getDevice().getId();
    }

    @Override
    public DeviceView getRowData(String rowKey) {
        final Device device = deviceEJB.findById(Long.parseLong(rowKey));
        final InstallationRecord activeInstallation = findActiveInstallation(device);

        return new DeviceView(device,
                activeInstallation == null ? "-" : activeInstallation.getSlot().getName(),
                activeInstallation == null ? null : Long.toString(activeInstallation.getSlot().getId()),
                activeInstallation == null ? null : activeInstallation.getInstallDate());
    }

    private DeviceFields selectSortField(final String sortField) {
        if (sortField == null) return null;

        switch (sortField) {
        case DEVICE_TYPE:
            return DeviceFields.DEVICE_TYPE;
        case SERIAL_NUMBER:
            return DeviceFields.SERIAL_NUMBER;
        case INSTALLATION_SLOT:
            return DeviceFields.INSTALLATION_SLOT;
        case INSTALLATION_DATE:
            return DeviceFields.TIMESTAMP;
        default:
            return null;
        }
    }

    private InstallationRecord findActiveInstallation(final Device device) {
        for (final InstallationRecord rec : device.getInstallationRecordList()) {
            if (rec.getUninstallDate() == null) {
                return rec;
            }
        }
        return null;
    }

    private Date parseLogDateTime(final Map<String, Object> filters) {
        if (filters.containsKey(INSTALLATION_DATE)) {
            final LocalDateTime filter = UiUtility.processUIDateTime(filters.get(INSTALLATION_DATE).toString());
            return Date.from(filter.atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }

}
