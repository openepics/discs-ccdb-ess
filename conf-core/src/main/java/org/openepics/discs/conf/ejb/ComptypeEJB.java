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
                return type.getComptypeAsmList();
            }
            @Override
            public ComponentType getParentFromChild(ComptypeAsm child) {
                return child.getParentType();
            }
        });
    }

    public List<ComponentType> findComponentTypeOrderedByName() {
        return em.createNamedQuery("ComponentType.findAllOrdered", ComponentType.class).getResultList();
    }
}
