/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.openepics.discs.conf.ejb.DeviceEJBLocal;
import org.openepics.discs.conf.ent.PcArtifact;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.DeviceProperty;

/**
 *
 * @author vuppala
 */
@ManagedBean
@ViewScoped
public class DeviceManager {
    @EJB
    private DeviceEJBLocal deviceEJB;
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    
    private static String folderName = "/var/proteus/"; // ToDo: get it from configuration
    private List<Device> objects;
    private List<Device> sortedObjects;
    private List<Device> filteredObjects;
    private Device selectedObject;
    private Device inputObject;
    private List<DeviceProperty> selectedProperties;
    private List<PcArtifact> selectedArtifacts;
    // private List<PhysicalCompAsm> selectedParts;
    private PcArtifact inputArtifact;
    private char selectedOp = 'n'; // selected operation: [a]dd, [e]dit, [d]elete, [n]one
    
    private String uploadedFileName;
    private String downloadFileName;
    /**
     * Creates a new instance of DeviceManager
     */
    public DeviceManager() {
    }
    
    @PostConstruct
    public void init() {
        try {
            objects = deviceEJB.findDevice();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            logger.log(Level.SEVERE, "Cannot retrieve component types");
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error in getting component types", " ");
        }
    }
}
