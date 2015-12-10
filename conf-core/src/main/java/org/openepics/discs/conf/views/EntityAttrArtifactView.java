package org.openepics.discs.conf.views;

import java.io.IOException;
import java.io.InputStream;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.ent.Artifact;
import org.openepics.discs.conf.ent.NamedEntity;
import org.primefaces.event.FileUploadEvent;

import com.google.common.io.ByteStreams;

public class EntityAttrArtifactView<E extends NamedEntity> extends EntityAttributeView<E> {
    private final Artifact entity;
    protected byte[] importData;
    protected boolean isArtifactBeingModified;

    public EntityAttrArtifactView(Artifact entity, EntityAttributeViewKind kind, E parent, String usedBy) {
        this(entity, kind, parent, usedBy, true); // being modified means "not new"
    }

    public EntityAttrArtifactView(Artifact entity, EntityAttributeViewKind kind, E parent, String usedBy, boolean isArtifactBeingModified) {
        super(kind, parent, usedBy);
        this.entity = entity;
        this.isArtifactBeingModified = isArtifactBeingModified;
    }

    public EntityAttrArtifactView(Artifact entity, EntityAttributeViewKind kind, E parent) {
        this(entity, kind, parent, null);
    }

    @Override
    public String getId() {
        return entity.getId().toString();
    }

    @Override
    @NotNull
    @Size(min = 1, max = 128, message="Name can have at most 128 characters.")
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
        if (entity.isInternal() != isArtifactInternal) { /* If user changes the type of the artifact, any previously uploaded file gets deleted */
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
        return isArtifactBeingModified;
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

}
