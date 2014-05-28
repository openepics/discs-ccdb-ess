/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ejb;

import java.util.List;
import javax.ejb.Local;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypeAsm;
import org.openepics.discs.conf.ent.ComptypeProperty;

/**
 *
 * @author vuppala
 */
@Local
public interface ComptypeEJBLocal {
    List<ComponentType> findComponentType();

    ComponentType findComponentType(int id);  
    
    void saveComponentType(ComponentType ctype) throws Exception;

    void addComponentType(ComponentType ctype) throws Exception;

    void deleteComponentType(ComponentType ctype) throws Exception;   

    void saveCompTypeProp(ComptypeProperty ctprop, boolean create) throws Exception ;

    void deleteCompTypeProp(ComptypeProperty ctp) throws Exception ;

    public void saveCompTypeArtifact(ComptypeArtifact art, boolean create) throws Exception ;

    void deleteCompTypeArtifact(ComptypeArtifact art) throws Exception ;

    void deleteComptypeAsm(ComponentType ctype, ComptypeAsm prt) throws Exception ;

    void saveComptypeAsm(ComponentType ctype, ComptypeAsm prt) throws Exception ;  
}
