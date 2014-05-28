/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ejb;

import java.util.List;
import javax.ejb.Local;
import org.openepics.discs.conf.ent.AlignmentArtifact;
import org.openepics.discs.conf.ent.AlignmentProperty;
import org.openepics.discs.conf.ent.AlignmentRecord;

/**
 *
 * @author vuppala
 */
@Local
public interface AlignmentEJBLocal {
    List<AlignmentRecord> findAlignmentRec();
    AlignmentRecord findAlignmentRec(int id);
    void saveAlignment(AlignmentRecord arec) throws Exception  ;
    void deleteAlignment(AlignmentRecord arec) throws Exception ;
    void saveAlignmentProp(AlignmentProperty prop, boolean create) throws Exception ;
    void deleteAlignmentProp(AlignmentProperty prop) throws Exception ;
    void saveAlignmentArtifact(AlignmentArtifact art, boolean create) throws Exception ;
    void deleteAlignmentArtifact(AlignmentArtifact art) throws Exception ;
}
