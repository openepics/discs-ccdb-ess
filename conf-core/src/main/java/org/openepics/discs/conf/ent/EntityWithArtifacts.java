package org.openepics.discs.conf.ent;

import java.util.List;

public interface EntityWithArtifacts {
    <T extends Artifact> List<T> getEntityArtifactList();
}
