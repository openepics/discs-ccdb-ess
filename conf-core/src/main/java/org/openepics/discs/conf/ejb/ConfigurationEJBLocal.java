/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.conf.ejb;

import org.openepics.discs.conf.ent.*;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author vuppala
 */
@Local
public interface ConfigurationEJBLocal {

    List<Property> findProperties();

    void saveProperty(Property property) throws Exception;

    void addProperty(Property property) throws Exception;

    void deleteProperty(Property property) throws Exception;

    List<Unit> findUnits();

    Unit findUnit(String id);

    List<DataType> findDataType();

    DataType findDataType(String id);

    Property findProperty(int id);

    List<SlotRelation> findSlotRelation();

    SlotRelation findSlotRelation(int id);

    List<AuditRecord> findAuditRecord();

    AuditRecord findDAuditRecord(int id);

}
