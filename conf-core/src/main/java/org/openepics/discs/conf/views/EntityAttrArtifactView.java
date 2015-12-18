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
package org.openepics.discs.conf.views;

import java.io.IOException;
import java.io.InputStream;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.ent.Artifact;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ConfigurationEntity;
import org.openepics.discs.conf.ent.Device;
import org.openepics.discs.conf.ent.NamedEntity;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.util.UnhandledCaseException;
import org.primefaces.event.FileUploadEvent;

import com.google.common.io.ByteStreams;

/**
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 * @param <E> the type of the view parent entity
 */
public class EntityAttrArtifactView<E extends ConfigurationEntity & NamedEntity> extends EntityAttributeView<E> {
    private final Artifact entity;
    protected byte[] importData;

    public <P extends ConfigurationEntity & NamedEntity>
            EntityAttrArtifactView(Artifact entity, E viewParent, P artifactParent) {
        super(viewParent, artifactParent != null ? artifactParent.getName() : "");
        this.entity = entity;
        setKind(artifactParent == null ? getEntityKind(viewParent) : getEntityKind(artifactParent));
    }

    public EntityAttrArtifactView(Artifact entity, E viewParent) {
        this(entity, viewParent, null);
    }

    @Override
    public String getId() {
        return entity.getId().toString();
    }

    @Override
    @NotNull
    @Size(min = 1, max = 128, message = "Name can have at most 128 characters.")
    public String getName() {
        return entity.getName();
    }

    @Override
    public String getValue() {
        return getHasURL() ? entity.getUri() : "Download attachment";
    }

    @Override
    public boolean getHasFile() {
        return entity.isInternal();
    }

    @Override
    public boolean getHasURL() {
        return !entity.isInternal();
    }

    /** @return The user specified {@link Artifact} name. */
    public String getArtifactName() {
        return entity.getName();
    }
    /** Called by the UI input control to set the value.
     * @param artifactName The user specified {@link Artifact} name.
     */
    public void setArtifactName(String artifactName) {
        entity.setName(artifactName);
    }

    /** @return The user specified {@link Artifact} description. */
    @NotNull
    @Size(min = 1, max = 255, message="Description can have at most 255 characters.")
    public String getArtifactDescription() {
        return entity.getDescription();
    }
    /** Called by the UI input control to set the value.
     * @param artifactDescription The user specified {@link Artifact} description.
     */
    public void setArtifactDescription(String artifactDescription) {
        entity.setDescription(artifactDescription);
    }

    /** @return <code>true</code> if the {@link Artifact} is a file attachment, <code>false</code> if its an URL. */
    public boolean isArtifactInternal() {
        return entity.isInternal();
    }
    /** Called by the UI input control to set the value.
     * @param isArtifactInternal <code>true</code> if the {@link Artifact} is a file attachment, <code>false</code> if its an URL.
     */
    public void setArtifactInternal(boolean isArtifactInternal) {
        /* If user changes the type of the artifact, any previously uploaded file gets deleted */
        if (entity.isInternal() != isArtifactInternal) {
            importData = null;
            entity.setName(null);
        }
        entity.setInternal(isArtifactInternal);
    }

    /** @return The URL the user stored in the database. */
    public String getArtifactURI() {
        return entity.getUri();
    }
    /** Called by the UI input control to set the value.
     * @param artifactURI The URL to store into the database.
     */
    public void setArtifactURI(String artifactURI) {
        entity.setUri(artifactURI.trim());
    }

    /** @return <code>true</code> if a "Modify artifact" dialog is open, <code>false</code> otherwise. */
    public boolean isArtifactBeingModified() {
        return entity.getId() != null;
    }

    @Override
    public Artifact getEntity() {
        return entity;
    }

    /**
     * Uploads file to be saved in the {@link Artifact}
     * @param event the {@link FileUploadEvent}
     */
    public void handleImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.entity.setName(FilenameUtils.getName(event.getFile().getFileName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getImportData() {
        return importData;
    }

    private <P extends ConfigurationEntity> EntityAttributeViewKind getEntityKind(P entity) {
        if (entity instanceof ComponentType) return EntityAttributeViewKind.DEVICE_TYPE_ARTIFACT;
        if (entity instanceof Slot) {
            if (((Slot) entity).isHostingSlot()) {
                return EntityAttributeViewKind.INSTALL_SLOT_ARTIFACT;
            } else {
                return EntityAttributeViewKind.CONTAINER_SLOT_ARTIFACT;
            }
        }
        if (entity instanceof Device) return EntityAttributeViewKind.DEVICE_ARTIFACT;
        throw new UnhandledCaseException();
    }
}
