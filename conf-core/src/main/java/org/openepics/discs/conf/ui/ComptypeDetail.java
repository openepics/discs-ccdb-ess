/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import org.openepics.discs.conf.ejb.ComptypeEJBLocal;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.util.Utility;

/**
 *
 * @author vuppala
 */
@Named
@RequestScoped
public class ComptypeDetail {
    @EJB
    private ComptypeEJBLocal comptypeEJB;
    private static final Logger logger = Logger.getLogger(DeviceDetail.class.getName());
    private ComponentType selectedObject;
    private int id = 0; // given identifier
    
    /**
     * Creates a new instance of ComptypeDetails
     */
    public ComptypeDetail() {
    }
    
    public void init() {
        // logger.entering(DeviceDetail.class.getName(), "init", this);
        // logger.log(Level.INFO, "entering init {0}", id);
        try {
            selectedObject = comptypeEJB.findComponentType(id);
            if ( selectedObject == null ) {
                Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Component Type with given ID not found"," Type ID: " + id);
            }           
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cannot retrieve component type", e.getMessage());
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "Error in finding component type", e.getMessage());
        }
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public ComponentType getSelectedObject() {
        return selectedObject;
    }
}
