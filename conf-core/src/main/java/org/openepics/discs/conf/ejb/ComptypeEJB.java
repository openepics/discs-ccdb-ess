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
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
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
        logger.log(Level.FINE, "Number of component types: {0}", comptypes.size());

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

    /** Deletes the component type and returns <code>true</code> if deletion was successful.
     * @param componentType - the component type to delete.
     * @return <code>true</code> indicates that deletion was possible and executed, <code>false</code> indicates
     * that the component type is referenced by some other entity and deletion was blocked.
     */
    @CRUDOperation(operation=EntityTypeOperation.DELETE)
    @Audit
    @Authorized
    public boolean deleteComponentType(ComponentType componentType) {
        final ComponentType componentTypeToDelete = em.find(ComponentType.class, componentType.getId());
        em.remove(componentTypeToDelete);
        return true;
    }


    // ---------------- Component Type Property ---------------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void addCompTypeProp(ComptypePropertyValue propertyValue) {
        final ComponentType parent = propertyValue.getComponentType();

        entityUtility.setModified(parent, propertyValue);

        parent.getComptypePropertyList().add(propertyValue);
        em.merge(parent);
    }

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveCompTypeProp(ComptypePropertyValue propertyValue) {
        final ComptypePropertyValue mergedPropertyValue = em.merge(propertyValue);

        entityUtility.setModified(mergedPropertyValue.getComponentType(), mergedPropertyValue);

        logger.log(Level.FINE, "Comp Type Property: id " + mergedPropertyValue.getId() + " name " + mergedPropertyValue.getProperty().getName());
    }

    /** Deletes the component type property value and returns <code>true</code> if deletion was successful.
     * @param propertyValue - the component type property value to delete.
     * @return <code>true</code> indicates that deletion was possible and executed, <code>false</code> indicates
     * that the component type property value is referenced by some other entity and deletion was blocked.
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public boolean deleteCompTypeProp(ComptypePropertyValue propertyValue) {
        logger.log(Level.FINE, "deleting comp type property id " + propertyValue.getId() + " name " + propertyValue.getProperty().getName());

        final ComptypePropertyValue propertyValueToDelete = em.find(ComptypePropertyValue.class, propertyValue.getId());
        final ComponentType parent = propertyValueToDelete.getComponentType();

        entityUtility.setModified(parent);

        parent.getComptypePropertyList().remove(propertyValueToDelete);
        em.remove(propertyValueToDelete);
        return true;
    }


    // ---------------- Component Type Artifact ---------------------

    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void addCompTypeArtifact(ComptypeArtifact artifact) {
        final ComponentType parent = artifact.getComponentType();

        entityUtility.setModified(parent, artifact);

        parent.getComptypeArtifactList().add(artifact);
        em.merge(parent);
    }


    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public void saveCompTypeArtifact(ComptypeArtifact artifact) {
        final ComptypeArtifact mergedArtifact = em.merge(artifact);

        entityUtility.setModified(mergedArtifact.getComponentType(), mergedArtifact);

        logger.log(Level.FINE, "Component Type Artifact: name " + mergedArtifact.getName() + " description " + mergedArtifact.getDescription() + " uri " + mergedArtifact.getUri() + "is int " + mergedArtifact.isInternal());
    }

    /** Deletes the component type artifact and returns <code>true</code> if deletion was successful.
     * @param artifact - the component type artifact to delete.
     * @return <code>true</code> indicates that deletion was possible and executed, <code>false</code> indicates
     * that the component type artifact is referenced by some other entity and deletion was blocked.
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public boolean deleteCompTypeArtifact(ComptypeArtifact artifact) {
        final ComptypeArtifact artifactToDelete = em.find(ComptypeArtifact.class, artifact.getId());
        final ComponentType parent = artifactToDelete.getComponentType();

        entityUtility.setModified(parent);

        parent.getComptypeArtifactList().remove(artifactToDelete);
        em.remove(artifactToDelete);
        return true;
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

    /** Deletes the component type assembly and returns <code>true</code> if deletion was successful.
     * @param assembly - the component type assembly to delete.
     * @return <code>true</code> indicates that deletion was possible and executed, <code>false</code> indicates
     * that the component type assembly is referenced by some other entity and deletion was blocked.
     */
    @CRUDOperation(operation=EntityTypeOperation.UPDATE)
    @Audit
    @Authorized
    public boolean deleteComptypeAsm(ComponentType componentType, ComptypeAsm assembly) {
        final ComptypeAsm assemblyToDelete = em.find(ComptypeAsm.class, assembly.getId());

        entityUtility.setModified(componentType);

        em.remove(assemblyToDelete);
        em.merge(componentType);
        return true;
    }
}
