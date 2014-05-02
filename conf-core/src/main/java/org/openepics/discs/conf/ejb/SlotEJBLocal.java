/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.conf.ejb;

import java.util.List;
import javax.ejb.Local;
import org.openepics.discs.conf.ent.LsArtifact;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotProperty;

/**
 *
 * @author vuppala
 */
@Local
public interface SlotEJBLocal {

    void deleteLayoutSlot(Slot slot) throws Exception;

    void saveLayoutSlot(Slot slot) throws Exception;

    void saveSlotProp(Slot slot, SlotProperty prop);

    void deleteSlotProp(Slot slot, SlotProperty key);

    List<Slot> findLayoutSlot();

    Slot findLayoutSlot(int id);
       
    
    void saveSlotArtifact(Slot slot, LsArtifact art);
    
    void deleteSlotArtifact(Slot ctype, LsArtifact art);
}