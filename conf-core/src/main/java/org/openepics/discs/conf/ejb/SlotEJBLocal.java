/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.conf.ejb;

import java.util.List;
import javax.ejb.Local;
import org.openepics.discs.conf.ent.SlotArtifact;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPair;
import org.openepics.discs.conf.ent.SlotProperty;

/**
 *
 * @author vuppala
 */
@Local
public interface SlotEJBLocal {

    void deleteLayoutSlot(Slot slot) throws Exception;

    void saveLayoutSlot(Slot slot) throws Exception;

    void saveSlotProp(SlotProperty prop, boolean create) throws Exception ;

    void deleteSlotProp(SlotProperty prop) throws Exception ;

    List<Slot> findLayoutSlot();

    Slot findLayoutSlot(int id);

    void saveSlotArtifact(SlotArtifact art, boolean create) throws Exception ;

    void deleteSlotArtifact(SlotArtifact art) throws Exception ;

    void deleteSlotPair(SlotPair pair) throws Exception ;

    void saveSlotPair(SlotPair art, boolean create) throws Exception ;

    List<Slot> getRootNodes(String relationName);

    List<Slot> getRootNodes();
    
    List<Slot> relatedChildren(String compName);
}
