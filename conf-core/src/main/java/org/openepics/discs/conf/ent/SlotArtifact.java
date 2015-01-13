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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vuppala
 */
@Entity
@Table(name = "slot_artifact")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SlotArtifact.findAll", query = "SELECT s FROM SlotArtifact s"),
    @NamedQuery(name = "SlotArtifact.findByArtifactId", query = "SELECT s FROM SlotArtifact s WHERE s.id = :id"),
    @NamedQuery(name = "SlotArtifact.findByIsInternal", query = "SELECT s FROM SlotArtifact s "
            + "WHERE s.isInternal = :isInternal"),
    @NamedQuery(name = "SlotArtifact.findByModifiedBy", query = "SELECT s FROM SlotArtifact s "
            + "WHERE s.modifiedBy = :modifiedBy")
})
public class SlotArtifact extends Artifact {
    private static final long serialVersionUID = 8850678212689818458L;

    @JoinColumn(name = "slot")
    @ManyToOne(optional = false)
    private Slot slot;

    public SlotArtifact() { }

    /** Constructs a new slot artifact
     * @param name the name of the artifact
     * @param isInternal <code>true</code> if the artifact is a file attachment, <code>false</code> if it's an URL.
     * @param description the user specified description
     * @param uri the user specified URL
     */
    public SlotArtifact(String name, boolean isInternal, String description, String uri) {
        super(name, isInternal, description, uri);
    }

    public Slot getSlot() {
        return slot;
    }
    public void setSlot(Slot slot) {
        this.slot = slot;
    }
    
    @Override
    public EntityWithArtifacts getArtifactsParent() {
        return getSlot();
    }

    @Override
    public void setArtifactsParent(EntityWithArtifacts parent) {
        setSlot((Slot) parent);
    }

    @Override
    public String toString() {
        return "SlotArtifact[ artifactId=" + id + " ]";
    }
}
