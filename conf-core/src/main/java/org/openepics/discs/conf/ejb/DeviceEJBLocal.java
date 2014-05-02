/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ejb;

import java.util.List;
import javax.ejb.Local;
import org.openepics.discs.conf.ent.Device;

/**
 *
 * @author vuppala
 */
@Local
public interface DeviceEJBLocal {
    List<Device> findDevice();
    Device findDevice(int id);
}
