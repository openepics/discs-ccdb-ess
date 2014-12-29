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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An {@link Artifact} used in Alignment Records
 *
 * @author vuppala
 */
@Entity
@Table(name = "alignment_artifact", indexes = { @Index(columnList = "alignment_record") })
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlignmentArtifact.findAll", query = "SELECT a FROM AlignmentArtifact a"),
    @NamedQuery(name = "AlignmentArtifact.findByArtifactId", query = "SELECT a FROM AlignmentArtifact a "
            + "WHERE a.id = :id"),
    @NamedQuery(name = "AlignmentArtifact.findByName", query = "SELECT a FROM AlignmentArtifact a "
            + "WHERE a.name = :name"),
    @NamedQuery(name = "AlignmentArtifact.findByIsInternal", query = "SELECT a FROM AlignmentArtifact a "
            + "WHERE a.isInternal = :isInternal"),
    @NamedQuery(name = "AlignmentArtifact.findByModifiedBy", query = "SELECT a FROM AlignmentArtifact a "
            + "WHERE a.modifiedBy = :modifiedBy")
})
public class AlignmentArtifact extends Artifact {
    private static final long serialVersionUID = 5577832937866324727L;

    @JoinColumn(name = "alignment_record")
    @ManyToOne(optional = false)
    private AlignmentRecord alignmentRecord;

    protected AlignmentArtifact() { }

    /** Constructs a new alignment artifact
     * @param name the name of the artifact
     * @param isInternal <code>true</code> if the artifact is a file attachment, <code>false</code> if it's an URL.
     * @param description the user specified description
     * @param uri the user specified URL
     */
    public AlignmentArtifact(String name, boolean isInternal, String description, String uri) {
        super(name, isInternal, description, uri);
    }

    public AlignmentRecord getAlignmentRecord() {
        return alignmentRecord;
    }
    public void setAlignmentRecord(AlignmentRecord alignmentRecord) {
        this.alignmentRecord = alignmentRecord;
    }

    @Override
    public String toString() {
        return "AlignmentArtifact[ artifactId=" + id + " ]";
    }
}
