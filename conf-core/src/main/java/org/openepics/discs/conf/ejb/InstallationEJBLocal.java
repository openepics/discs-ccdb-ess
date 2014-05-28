/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ejb;

import java.util.List;
import javax.ejb.Local;
import org.openepics.discs.conf.ent.InstallationArtifact;
import org.openepics.discs.conf.ent.InstallationRecord;

/**
 *
 * @author vuppala
 */
@Local
public interface InstallationEJBLocal {
    List<InstallationRecord> findInstallationRec();
    void deleteIRecord(InstallationRecord irec) throws Exception;
    void saveIRecord(InstallationRecord irec, boolean create) throws Exception ;
    void saveInstallationArtifact(InstallationArtifact art, boolean create) throws Exception ;
    void deleteInstallationArtifact(InstallationArtifact art) throws Exception ;
}
