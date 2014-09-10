package org.openepics.discs.conf.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.openepics.discs.conf.ent.ComponentType;
import org.openepics.discs.conf.ent.ComptypeArtifact;
import org.openepics.discs.conf.ent.ComptypeAsm;
import org.openepics.discs.conf.ent.ComptypePropertyValue;

/**
 *
 * @author vuppala
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */

@Stateless
public class ComptypeEJB extends DAO<ComponentType> {
    @Override
    protected void defineEntity() {
        defineEntityClass(ComponentType.class);

        defineParentChildInterface(ComptypePropertyValue.class, new ParentChildInterface<ComponentType, ComptypePropertyValue>() {
            @Override
            public List<ComptypePropertyValue> getChildCollection(ComponentType type) {
                return type.getComptypePropertyList();
            }
            @Override
            public ComponentType getParentFromChild(ComptypePropertyValue child) {
                return child.getComponentType();
            }
        });

        defineParentChildInterface(ComptypeArtifact.class, new ParentChildInterface<ComponentType, ComptypeArtifact>() {
            @Override
            public List<ComptypeArtifact> getChildCollection(ComponentType type) {
                return type.getComptypeArtifactList();
            }
            @Override
            public ComponentType getParentFromChild(ComptypeArtifact child) {
                return child.getComponentType();
            }
        });

        defineParentChildInterface(ComptypeAsm.class, new ParentChildInterface<ComponentType, ComptypeAsm>() {
            @Override
            public List<ComptypeAsm> getChildCollection(ComponentType type) {
                return type.getChildrenComptypeAsmList();
            }
            @Override
            public ComponentType getParentFromChild(ComptypeAsm child) {
                return child.getParentType();
            }
        });
    }

    /**
     * @return A list of all device types ordered by name.
     */
    public List<ComponentType> findComponentTypeOrderedByName() {
        return em.createNamedQuery("ComponentType.findAllOrdered", ComponentType.class).getResultList();
    }

    /**
     * @param componentType - the device type
     * @return A list of all property definitions for the selected device type.
     */
    public List<ComptypePropertyValue> findPropertyDefinitions(ComponentType componentType) {
        return em.createNamedQuery("ComptypePropertyValue.findPropertyDefs", ComptypePropertyValue.class)
               .setParameter("componentType", componentType).getResultList();
    }

}
