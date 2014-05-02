/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ejb;

import java.util.Date;
import org.openepics.discs.conf.ent.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author vuppala
 */
@Stateless
public class ConfigurationEJB implements ConfigurationEJBLocal {
   
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");   
    @PersistenceContext(unitName = "org.openepics.discs.conf.data")
    private EntityManager em;
       
    // --------------------  Property  ---------------------
    @Override
    public List<Property> findProperties() {
        List<Property> props;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Property> cq = cb.createQuery(Property.class);
        // Root<Property> prop = cq.from(Property.class);       
        
        TypedQuery<Property> query = em.createQuery(cq);
        props = query.getResultList();
        logger.log(Level.INFO, "Number of component properties: {0}", props.size());
        
        return props;        
    }
    
    @Override
    public Property findProperty(String id) {
        return em.find(Property.class, id);
    }
    
    @Override
    public void saveProperty(Property property) throws Exception {
        if (property == null ) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");        
        }
        property.setModifiedAt(new Date());
        em.merge(property);        
    }
    
    @Override
    public void addProperty(Property property) throws Exception {
        if (property == null ) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");        
        }
        property.setModifiedAt(new Date());
        em.persist(property);        
    }
    
    @Override
    public void deleteProperty(Property property) throws Exception {
        if (property == null ) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");        
        }
        Property prop = em.find(Property.class,property.getPropertyId());
        em.remove(prop);        
    }
    
    // --------------------  Unit ---------------------
    @Override
    public List<Unit> findUnits() {
        List<Unit> units;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
        Root<Unit> prop = cq.from(Unit.class);       
        
        TypedQuery<Unit> query = em.createQuery(cq);
        units = query.getResultList();
        logger.log(Level.INFO, "Number of units: {0}", units.size());
        
        return units;        
    }
    
    @Override
    public Unit findUnit(String id) {
        return em.find(Unit.class, id);
    }
    
    // ----------------  Data Type -------------------------
    @Override
    public List<DataType> findDataType() {
        List<DataType> datatypes;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DataType> cq = cb.createQuery(DataType.class);
        Root<DataType> prop = cq.from(DataType.class);       
        
        TypedQuery<DataType> query = em.createQuery(cq);
        datatypes = query.getResultList();
        logger.log(Level.INFO, "Number of units: {0}", datatypes.size());
        
        return datatypes;        
    }
    
    @Override
    public DataType findDataType(String id) {
        return em.find(DataType.class, id);
    }
    
    // ----------------  Component Type -------------------------
    @Override
    public List<ComponentType> findComponentType() {
        List<ComponentType> comptypes;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ComponentType> cq = cb.createQuery(ComponentType.class);
        Root<ComponentType> prop = cq.from(ComponentType.class);       
        
        TypedQuery<ComponentType> query = em.createQuery(cq);
        comptypes = query.getResultList();
        logger.log(Level.INFO, "Number of component types: {0}", comptypes.size());
        
        return comptypes;        
    }
    
    @Override
    public ComponentType findComponentType(String id) {
        return em.find(ComponentType.class, id);
    }
     
    @Override
    public void saveComponentType(ComponentType ctype) throws Exception {
        if (ctype == null ) {
            logger.log(Level.SEVERE, "Comp Type is null!");
            throw new Exception("Comp Type is null");        
        }
        ctype.setModifiedAt(new Date());
        
        em.merge(ctype);        
    }
    
    @Override
    public void addComponentType(ComponentType ctype) throws Exception {
        if (ctype == null ) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");        
        }
        ctype.setModifiedAt(new Date());
        em.persist(ctype);        
    }
    
    @Override
    public void deleteComponentType(ComponentType ctype) throws Exception {
        if (ctype == null ) {
            logger.log(Level.SEVERE, "Property is null!");
            throw new Exception("property is null");        
        }
        ComponentType ct = em.find(ComponentType.class,ctype.getComponentTypeId());
        em.remove(ct);        
    }
    
    // ---------------- Component Type Property ---------------------
    
    @Override
    public void saveCompTypeProp(ComponentType ctype, ComponentTypeProperty ctprop) {
        if (ctprop != null) {
            ctprop.setModifiedAt(new Date());
            // ctprop.setType("a");
            ctprop.setModifiedBy("user");
            // ctprop.setVersion(0);
            // ctprop.setComponentType1(findComponentType(ctprop.getComponentTypePropertyPK().getComponentType()));
            // ctprop.setProperty1(findProperty(ctprop.getComponentTypePropertyPK().getProperty()));
            // logger.info("save ctp: {0} {1} {2}", ctprop.getComponentTypePropertyPK().getComponentType(), ctprop.getComponentTypePropertyPK().getProperty(), ctprop.getType());
            em.merge(ctprop);
            em.merge(ctype);
        }
    }
    
    @Override
    public void deleteCompTypeProp(ComponentType ctype, ComponentTypeProperty ctp) {
        if (ctp != null) {            
            ComponentTypeProperty entity = em.find(ComponentTypeProperty.class, ctp.getComponentTypePropertyPK());
            em.remove(entity);
            em.merge(ctype);
        }
    }
    
    // ---------------- Component Type Artifact ---------------------
    
    @Override
    public void saveCompTypeArtifact(ComponentType ctype, CtArtifact art) {
        if (art != null) {
            art.setModifiedAt(new Date());
            // ctprop.setType("a");
            art.setModifiedBy("user");           
            // ctprop.setComponentType1(findComponentType(ctprop.getComponentTypePropertyPK().getComponentType()));
            // ctprop.setProperty1(findProperty(ctprop.getComponentTypePropertyPK().getProperty()));
            // logger.info("save ctp: {0} {1} {2}", ctprop.getComponentTypePropertyPK().getComponentType(), ctprop.getComponentTypePropertyPK().getProperty(), ctprop.getType());
            art.setComponentType(ctype);
            // ctype.getCtArtifactList().add(art);
            logger.log(Level.INFO, "CompType Artifact: name" + art.getName() + " description " + art.getDescription() + " uri " + art.getUri());
            em.merge(art);
            em.merge(ctype);
        }
    }
    
    @Override
    public void deleteCompTypeArtifact(ComponentType ctype, CtArtifact art) {
        if (art != null) {            
            CtArtifact entity = em.find(CtArtifact.class, art.getArtifactId());
            em.remove(entity);
            em.merge(ctype);
        }
    }
    
    // ---------------- Component Type Assmebly ---------------------
    
    @Override
    public void saveCompTypeAsm(ComponentType ctype, CompTypeAsm prt) {
        if (prt != null) {
            prt.setModifiedAt(new Date());
            // ctprop.setType("a");
            prt.setModifiedBy("user");           
            // ctprop.setComponentType1(findComponentType(ctprop.getComponentTypePropertyPK().getComponentType()));
            // ctprop.setProperty1(findProperty(ctprop.getComponentTypePropertyPK().getProperty()));
            // logger.info("save ctp: {0} {1} {2}", ctprop.getComponentTypePropertyPK().getComponentType(), ctprop.getComponentTypePropertyPK().getProperty(), ctprop.getType());
            em.merge(prt);
            em.merge(ctype);
        }
    }
    
    @Override
    public void deleteCompTypeAsm(ComponentType ctype, CompTypeAsm prt) {
        if (prt != null) {            
            CompTypeAsm entity = em.find(CompTypeAsm.class, prt.getCompTypeAsmPK());
            em.remove(entity);
            em.merge(ctype);
        }
    }
   
    
}
