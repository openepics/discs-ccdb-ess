/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ejb;

import java.util.List;
import javax.ejb.Local;
import org.openepics.discs.conf.ent.DeviceArtifact;
import org.openepics.discs.conf.ent.DeviceProperty;
import org.openepics.discs.conf.ent.Device;

/**
 *
 * @author vuppala
 */
@Local
public interface DeviceEJBLocal {
    List<Device> findDevice();
    Device findDevice(int id);
    
    void saveDevice(String token, Device device) throws Exception;
    void deleteDevice(Device device) throws Exception ;
    void deleteDeviceProp(DeviceProperty prop)throws Exception;
    void saveDeviceProp(DeviceProperty prop, boolean create) throws Exception;
    void saveDeviceArtifact(DeviceArtifact art, boolean create) throws Exception;
    void deleteDeviceArtifact(DeviceArtifact art) throws Exception;
}
