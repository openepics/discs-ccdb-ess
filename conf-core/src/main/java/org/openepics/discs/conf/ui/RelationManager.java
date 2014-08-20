/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.SlotRelation;
import org.openepics.discs.conf.util.Utility;

/**
 *
 * @author vuppala
 */
@Named
@ViewScoped
public class RelationManager implements Serializable {    
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(RelationManager.class.getCanonicalName());

    @EJB private ConfigurationEJB configurationEJB;

    private List<SlotRelation> objects;
    /**
     * Creates a new instance of RelationManager
     */
    public RelationManager() {
    }

    @PostConstruct
    public void init() {
        try {
            objects = configurationEJB.findSlotRelation();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error in getting slot relationships", " ");
        }
    }

    public List<SlotRelation> getObjects() {
        return objects;
    }


}
