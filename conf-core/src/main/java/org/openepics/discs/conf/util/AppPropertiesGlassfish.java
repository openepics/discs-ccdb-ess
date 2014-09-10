/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2012.
 *
 * You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *       http://www.gnu.org/licenses/gpl.txt
 *
 * Contact Information:
 *   Facilitty for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 *
 */

package org.openepics.discs.conf.util;

import java.util.Properties;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

/**
 *
 * @author vuppala
 */
@Alternative
@ApplicationScoped
public class AppPropertiesGlassfish implements AppProperties {
    @Resource(name="org.openepics.discs.conf.props")
    private Properties properties;

    public String getProperty(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        return properties.getProperty(name);
    }
}
