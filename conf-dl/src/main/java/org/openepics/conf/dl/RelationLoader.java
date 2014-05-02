/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.conf.dl;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.openepics.conf.entity.*;

/**
 *
 * @author vuppala
 */
public class RelationLoader extends DataLoader {
    private int maxRelationId = 0;
    
    RelationLoader(EntityManager em) {
        super(em);
    }

    private ComponentRelation getRelation(String name) { // ToDo: improve the search query
        List<ComponentRelation> relations;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<ComponentRelation> root = cq.from(ComponentRelation.class);

        cq.where(cb.like(root.get(ComponentRelation_.name),name));
        TypedQuery<ComponentRelation> query = entityManager.createQuery(cq);
        relations = query.getResultList();
        logger.log(Level.FINE, "Number of relationshiups: {0}", relations.size());        
        if ( relations.size() > 0 ) {
            return relations.get(0);
        } else {
            return null;
        }
    }
    
    private int getMaxId() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<ComponentRelation> comp = query.from(ComponentRelation.class);
        
        query.select(cb.max(comp.get(ComponentRelation_.componentRelationId)));
        TypedQuery<Integer> q = entityManager.createQuery(query);
        Integer maxId = q.getSingleResult();
        
        return (maxId == null? 1 : maxId); 
    }
    
    private void printRelation(ComponentRelation rel) {
        System.out.println("Name: " + rel.getName());
        System.out.println("Description: " + rel.getDescription());
        System.out.println("Inverse: " + rel.getIname());
    }
    
    @Override
    public void updateRecord(DSRecord record) throws Exception {
        ComponentRelation relation;
        Date today = new Date();
 
        String relName = record.getField("NAME");
        if (relName.trim().isEmpty()) {
            throw new CDLException(CDLExceptionCode.NONAME, "Relationship name is empty");
        }
 
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        }
        entityManager.getTransaction().begin();
        
        relation = getRelation(relName);
        if (relation == null) {        
            relation = new ComponentRelation();
            if ( maxRelationId == 0 ) {
                maxRelationId = getMaxId();
            }
            relation.setComponentRelationId(++maxRelationId);
            relation.setName(relName);
            entityManager.persist(relation);
        }       
        
        relation.setDescription(record.getField("DESCRIPTION"));
        relation.setIname(record.getField("INAME"));        
        relation.setModifiedBy(this.dataSource);
        relation.setDateModified(today);       
 
        printRelation(relation);       
    }
}
