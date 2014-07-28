package org.openepics.discs.conf.ejb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openepics.discs.conf.ent.Config;

/**
 * A service bean managing global application settings and database initialization.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Singleton
@Startup
public class ApplicationService {

    @PersistenceContext private EntityManager em;
    @Inject private InitialDBPopulation initDB;

    /**
     * Initializes the database with the bundled initial data on the first run of the application.
     */
    @PostConstruct
    public void init() {
        final List<Config> confList = em.createQuery("SELECT c from Config c WHERE c.name = :name", Config.class).setParameter("name", "schema_version").getResultList();
        if (confList == null || confList.size() < 1) {
            initDB.initialPopulation();
            em.persist(new Config("schema_version", "1"));
        }
    }

}
