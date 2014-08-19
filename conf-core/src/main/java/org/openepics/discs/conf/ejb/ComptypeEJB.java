package org.openepics.discs.conf.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.openepics.discs.conf.auditlog.Audit;
import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypeAsm;
import org.openepics.discs.conf.ent.ComptypePropertyValue;
import org.openepics.discs.conf.ent.EntityTypeOperation;
import org.openepics.discs.conf.security.Authorized;
import org.openepics.discs.conf.util.CRUDOperation;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * 
 */
@Stateless public class ComptypeEJB {
    private static final Logger logger = Logger.getLogger(ComptypeEJB.class.getCanonicalName());
    @PersistenceContext private EntityManager em;
    @Inject private ConfigurationEntityUtility entityUtility;
    
    // ---------------- Component Type ---------------------
    
    public List<ComponentType> findComponentType() {
        final CriteriaQuery<ComponentType> cq = em.getCriteriaBuilder().createQuery(ComponentType.class);
        cq.from(ComponentType.class);
       
        final List<ComponentType> comptypes = em.createQuery(cq).getResultList();
        logger.log(Level.INFO, "Number of component types: {0}", comptypes.size());

        return comptypes;
    }

    public ComponentType findComponentType(Long id) {
        return em.find(ComponentType.class, id);
    }

    public ComponentType findComponentTypeByName(String name) {
        ComponentType componentType;
        try {
            componentType = em.createNamedQuery("ComponentType.findByName", ComponentType.class).setParameter("name", name).getSingleResult();
        } catch (NoResultException e) {
            componentType = null;
        }
        return componentType;
    }

    @CRUDOperation(operation=EntityTypeOperation.CREATE)
    @Audit
    @Authorized
    public void addComponentType(ComponentType componentType) {
        entityUtility.setModified(componentType);
        em.persist(componentType);
    }
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveComponentType(ComponentType componentType) {
        entityUtility.setModified(componentType);
        em.merge(componentType);
    }

    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public void deleteComponentType(ComponentType componentType) {
        final ComponentType mergedComponentType = em.merge(componentType);
        em.remove(mergedComponentType);
    }
    

    // ---------------- Component Type Property ---------------------
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void addCompTypeProp(ComptypePropertyValue propertyValue) {
        final ComptypePropertyValue mergedPropertyValue = em.merge(propertyValue);
        final ComponentType parent = mergedPropertyValue.getComponentType();
        
        entityUtility.setModified(parent, mergedPropertyValue);
        
        parent.getComptypePropertyList().add(mergedPropertyValue);
        em.merge(parent);
    }
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveCompTypeProp(ComptypePropertyValue propertyValue) {
        final ComptypePropertyValue mergedPropertyValue = em.merge(propertyValue);
        
        entityUtility.setModified(mergedPropertyValue.getComponentType(), mergedPropertyValue);
        
        logger.log(Level.INFO, "Comp Type Property: id " + mergedPropertyValue.getId() + " name " + mergedPropertyValue.getProperty().getName());
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteCompTypeProp(ComptypePropertyValue propertyValue) {
        logger.log(Level.INFO, "deleting comp type property id " + propertyValue.getId() + " name " + propertyValue.getProperty().getName());
        
        final ComptypePropertyValue mergedPropertyValue = em.merge(propertyValue);
        final ComponentType parent = mergedPropertyValue.getComponentType();
        
        entityUtility.setModified(parent);
        
        parent.getComptypePropertyList().remove(mergedPropertyValue);
        em.remove(mergedPropertyValue);
    }


    // ---------------- Component Type Artifact ---------------------
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void addCompTypeArtifact(ComptypeArtifact artifact) {
        final ComptypeArtifact mergedArtifact = em.merge(artifact);
        final ComponentType parent = mergedArtifact.getComponentType();

        entityUtility.setModified(parent, mergedArtifact);
        
        parent.getComptypeArtifactList().add(mergedArtifact);        
    }
    
    
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveCompTypeArtifact(ComptypeArtifact artifact) {
        final ComptypeArtifact mergedArtifact = em.merge(artifact);

        entityUtility.setModified(mergedArtifact.getComponentType(), mergedArtifact);
        
        logger.log(Level.INFO, "Component Type Artifact: name " + mergedArtifact.getName() + " description " + mergedArtifact.getDescription() + " uri " + mergedArtifact.getUri() + "is int " + mergedArtifact.isInternal());
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteCompTypeArtifact(ComptypeArtifact artifact) {
        final ComptypeArtifact mergedArtifact = em.merge(artifact);                       
        final ComponentType parent = mergedArtifact.getComponentType();

        entityUtility.setModified(parent);
        
        parent.getComptypeArtifactList().remove(mergedArtifact);
        em.remove(mergedArtifact);
    }


    // ---------------- Component Type Assmebly ---------------------
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveComptypeAsm(ComponentType componentType, ComptypeAsm assembly) {
        entityUtility.setModified(componentType, assembly);
            
        assembly.setParentType(componentType);
        
        em.merge(assembly);
        em.merge(componentType);   
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void deleteComptypeAsm(ComponentType componentType, ComptypeAsm assembly) {        
        final ComptypeAsm mergedAssembly = em.merge(assembly);
        
        entityUtility.setModified(componentType);
        
        em.remove(mergedAssembly);
        em.merge(componentType);
    }
}
