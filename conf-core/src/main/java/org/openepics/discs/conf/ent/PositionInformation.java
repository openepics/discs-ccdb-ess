/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ent;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Positional information used in entities
 *
 * ToDo: Maybe rename to more appropriate name PositionInformation
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
@Embeddable
public class PositionInformation implements Serializable {
    private static final long serialVersionUID = 4639879699747739635L;

    @Column(name = "global_x")
    private Double globalX;

    @Column(name = "global_y")
    private Double globalY;

    @Column(name = "global_z")
    private Double globalZ;

    @Column(name = "global_pitch")
    private Double globalPitch;

    @Column(name = "global_yaw")
    private Double globalYaw;

    @Column(name = "global_roll")
    private Double globalRoll;

    public Double getGlobalX() {
        return globalX;
    }
    public void setGlobalX(Double globalX) {
        this.globalX = globalX;
    }

    public Double getGlobalY() {
        return globalY;
    }
    public void setGlobalY(Double globalY) {
        this.globalY = globalY;
    }

    public Double getGlobalZ() {
        return globalZ;
    }
    public void setGlobalZ(Double globalZ) {
        this.globalZ = globalZ;
    }

    public Double getGlobalPitch() {
        return globalPitch;
    }
    public void setGlobalPitch(Double globalPitch) {
        this.globalPitch = globalPitch;
    }

    public Double getGlobalYaw() {
        return globalYaw;
    }
    public void setGlobalYaw(Double globalYaw) {
        this.globalYaw = globalYaw;
    }

    public Double getGlobalRoll() {
        return globalRoll;
    }
    public void setGlobalRoll(Double globalRoll) {
        this.globalRoll = globalRoll;
    }

    /**
     * @return <code>true</code> if all position information is undefined, <code>false</code> otherwise.
     */
    public boolean isEmpty() {
        return globalX == null && globalY == null && globalZ == null && globalPitch == null
                && globalRoll == null && globalRoll == null;
    }
}
