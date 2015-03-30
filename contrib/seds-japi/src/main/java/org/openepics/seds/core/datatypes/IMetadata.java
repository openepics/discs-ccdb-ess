/* 
 * This software is Copyright by the Board of Trustees of Michigan
 *  State University (c) Copyright 2013, 2014.
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
package org.openepics.seds.core.datatypes;

import java.util.Objects;
import org.openepics.seds.api.datatypes.SedsMetadata;

class IMetadata implements SedsMetadata {

    private final String type;
    private final String protocol;
    private final String version;

    public IMetadata(String type, String protocol, String version) {
        this.type = type;
        this.protocol = protocol;
        this.version = version;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IMetadata other = (IMetadata) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.protocol, other.protocol)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "IMetadata{" + "type=" + type + ", protocol=" + protocol + ", version=" + version + '}';
    }

}
