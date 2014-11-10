/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.openepics.discs.conf.ejb.UnitEJB;
import org.openepics.discs.conf.ent.Unit;

/**
 *
 * @author vuppala
 */
@ManagedBean
public class UnitConverter implements Converter {

    @EJB
    private UnitEJB unitEJB;
    /**
     * Creates a new instance of UnitConverter
     */
    public UnitConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Unit unit;

        if (value == null || value.equals("")) {
            return null;
        } else {
            unit = unitEJB.findById(Long.valueOf(value));
            return unit;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((Unit) value).getId());
        }
    }
}
