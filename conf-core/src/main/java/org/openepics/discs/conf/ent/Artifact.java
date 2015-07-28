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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * An artifact is a user-defined value that is attached to database entities such as URL or a file
 *
 * @author vupalla
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Artifact extends ConfigurationEntity implements NamedEntity {

    private static final long serialVersionUID = 2926588619140123269L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;

    @Basic(optional = false)
    @NotNull
    @Column(name = "is_internal")
    private boolean isInternal;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Column(name = "uri", columnDefinition = "TEXT")
    private String uri;

    protected Artifact() { }

    /** Constructs a new artifact
     * @param name the name of the artifact
     * @param isInternal <code>true</code> if the artifact is a file attachment, <code>false</code> if it's an URL.
     * @param description the user specified description
     * @param uri the user specified URL
     */
    public Artifact(String name, boolean isInternal, String description, String uri) {
        this.name = name;
        this.isInternal = isInternal;
        this.description = description;
        this.uri = uri;
    }

    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isInternal() {
        return isInternal;
    }
    public void setInternal(boolean isInternal) {
        this.isInternal = isInternal;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }

    /** Implementing classes should implement this method to provide unified interface
     * for accessing the artifact parent
     *
     * @return An entity that is hosting this artifact
     */
    public abstract EntityWithArtifacts getArtifactsParent();

    /**
     * Implementing classes should implement this method to provide unified interface for accessing the
     * artifact parent
     *
     * @param parent the parent entity
     */
    public abstract void setArtifactsParent(EntityWithArtifacts parent);
}
