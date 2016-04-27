/*
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013.
 *  State University (c) Copyright 2013.
 *  
 *  You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *    http://www.gnu.org/licenses/gpl.txt
 *  
 *  Contact Information:
 *       Facility for Rare Isotope Beam
 *       Michigan State University
 *       East Lansing, MI 48824-1321
 *        http://frib.msu.edu
 */
package org.openepics.discs.ccdb.gui.converter;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Named;

/**
 * String to Char Array converter (used mostly for getting rid of passwords from
 * memory)
 *
 * @author vuppala
 */

@Named
@RequestScoped
public class CharArrayConverter implements Converter {

    private static final Logger logger = Logger.getLogger(CharArrayConverter.class.getName());

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            logger.log(Level.FINE, "charArray converter: empty string");
            return new char[0];
        } else {
            // logger.log(Level.FINE, "charArray converter: {0}",  value);
            return value.toCharArray();
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            logger.log(Level.FINE, "Null object");
            return "";
        } 
        if (value instanceof char[]) {
            return new String((char[]) value);
        }
        else {
            // logger.log(Level.FINE, "Exp number: " + ((Experiment) value).getId().toString());
           throw new ConverterException("Not a valid char array");
        }
    }
}
