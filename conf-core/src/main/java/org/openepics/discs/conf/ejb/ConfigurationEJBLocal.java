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

    List<ComponentType> findComponentType();

    ComponentType findComponentType(String id);    

   
    void saveComponentType(ComponentType ctype) throws Exception;

    void addComponentType(ComponentType ctype) throws Exception;

    void deleteComponentType(ComponentType ctype) throws Exception;
    
    Property findProperty(String id);

    void saveCompTypeProp(ComponentType ctype, ComponentTypeProperty ctprop);

    void deleteCompTypeProp(ComponentType ctype, ComponentTypeProperty ctp);

    void saveCompTypeArtifact(ComponentType ctype, CtArtifact art);

    void deleteCompTypeArtifact(ComponentType ctype, CtArtifact art);

    void deleteCompTypeAsm(ComponentType ctype, CompTypeAsm prt);

    void saveCompTypeAsm(ComponentType ctype, CompTypeAsm prt);   
}
