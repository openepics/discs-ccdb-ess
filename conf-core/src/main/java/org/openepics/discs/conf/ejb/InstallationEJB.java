package org.openepics.discs.conf.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 */
@Stateless
public class InstallationEJB extends DAO<InstallationRecord> {

    @Override
    protected void defineEntity() {
        defineEntityClass(InstallationRecord.class);

        defineParentChildInterface(InstallationArtifact.class, new ParentChildInterface<InstallationRecord, InstallationArtifact>() {

            @Override
            public List<InstallationArtifact> getChildCollection(InstallationRecord iRecord) {
                return iRecord.getInstallationArtifactList();
            }

            @Override
            public InstallationRecord getParentFromChild(InstallationArtifact child) {
                return child.getInstallationRecord();
            }
        });
    }
}
