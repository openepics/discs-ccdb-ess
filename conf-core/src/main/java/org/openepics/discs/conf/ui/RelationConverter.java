/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.SlotRelation;

/**
 *
 * @author vuppala
 */
@ManagedBean  // workaround for injecting an EJB in a converter (for older versions of Glassfish)
// @FacesConverter(value = "experimentConverter")
// @ViewScoped
public class RelationConverter implements Converter  {
@EJB
    private ConfigurationEJB configurationEJB;
    private static final Logger logger = Logger.getLogger(RelationConverter.class.getCanonicalName());
    /**
     * Creates a new instance of RelationConverter
     */
    public RelationConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        SlotRelation dtype;

        if (value == null || value.equals("")) {
            logger.log(Level.INFO, "relation converter: empty key");
            return null;
        } else {
            dtype = configurationEJB.findSlotRelation(Integer.parseInt(value));
            return dtype;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            logger.log(Level.INFO, "relation converter: empty object");
            return "";
        } else {
            // logger.log(Level.INFO, "Exp number: " + ((Experiment) value).getId().toString());
            return ((SlotRelation) value).getSlotRelationId().toString();
        }
    }
}
