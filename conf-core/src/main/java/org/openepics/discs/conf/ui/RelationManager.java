/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.SlotRelationEJB;
import org.openepics.discs.conf.ent.SlotRelation;

/**
 *
 * @author vuppala
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ViewScoped
public class RelationManager implements Serializable {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(RelationManager.class.getCanonicalName());

    @EJB private SlotRelationEJB slotRelationEJB;

    private List<SlotRelation> objects;

    public RelationManager() {
    }

    public List<SlotRelation> getObjects() {
        if (objects == null) objects = slotRelationEJB.findAll();
        return objects;
    }


}
