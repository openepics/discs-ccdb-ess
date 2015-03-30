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
import org.openepics.seds.api.datatypes.SedsTime;

class ITime implements SedsTime {

    private final Long unixSec;
    private final Integer nanoSec;
    private final Integer userTag;

    ITime(Long unixSec, Integer nanoSec, Integer userTag) {
        this.unixSec = unixSec;
        this.nanoSec = nanoSec;
        this.userTag = userTag;
    }

    @Override
    public Long getUnixSec() {
        return unixSec;
    }

    @Override
    public Integer getNanoSec() {
        return nanoSec;
    }

    @Override
    public Integer getUserTag() {
        return userTag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ITime other = (ITime) obj;
        if (!Objects.equals(this.unixSec, other.unixSec)) {
            return false;
        }
        if (!Objects.equals(this.nanoSec, other.nanoSec)) {
            return false;
        }
        if (!Objects.equals(this.userTag, other.userTag)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Time{" + "unixSec=" + unixSec + ", nanoSec=" + nanoSec + ", userTag=" + userTag + '}';
    }

}
