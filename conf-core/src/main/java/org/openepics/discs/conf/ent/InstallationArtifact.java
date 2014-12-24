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
 * An {@link Artifact} attached to {@link InstallationRecord} entities
 *
 * @author vuppala
 */
@Entity
@Table(name = "installation_artifact")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InstallationArtifact.findAll", query = "SELECT i FROM InstallationArtifact i"),
    @NamedQuery(name = "InstallationArtifact.findByArtifactId", query = "SELECT i FROM InstallationArtifact i "
            + "WHERE i.id = :id"),
    @NamedQuery(name = "InstallationArtifact.findByName", query = "SELECT i FROM InstallationArtifact i "
            + "WHERE i.name = :name"),
    @NamedQuery(name = "InstallationArtifact.findByIsInternal", query = "SELECT i FROM InstallationArtifact i "
            + "WHERE i.isInternal = :isInternal"),
    @NamedQuery(name = "InstallationArtifact.findByModifiedBy", query = "SELECT i FROM InstallationArtifact i "
            + "WHERE i.modifiedBy = :modifiedBy")
})
public class InstallationArtifact extends Artifact {
    @JoinColumn(name = "installation_record")
    @ManyToOne(optional = false)
    private InstallationRecord installationRecord;

    protected InstallationArtifact() { }

    public InstallationArtifact(String name, boolean isInternal, String description, String uri) {
        super(name, isInternal, description, uri);
    }

    public InstallationRecord getInstallationRecord() {
        return installationRecord;
    }
    public void setInstallationRecord(InstallationRecord installationRecord) {
        this.installationRecord = installationRecord;
    }

    @Override
    public String toString() {
        return "InstallationArtifact[ artifactId=" + id + " ]";
    }
}
