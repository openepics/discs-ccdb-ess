/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections4.CollectionUtils;
import org.openepics.discs.conf.ent.Config;

/**
 * A service bean managing global application settings and database initialization.
 *
 * @author <a href="mailto:andraz.pozar@cosylab.com">Andraž Požar</a>
 *
 */
@Singleton
@Startup
public class ApplicationService {
    private static final Logger LOGGER = Logger.getLogger(ApplicationService.class.getCanonicalName());

    @PersistenceContext private EntityManager em;
    @Inject private InitialDBPopulation initDB;

    /**
     * Initializes the database with the bundled initial data on the first run of the application.
     */
    @PostConstruct
    public void init() {
        final List<Config> confList = em.createQuery("SELECT c from Config c WHERE c.name = :name", Config.class)
                .setParameter("name", "schema_version").getResultList();
        if (CollectionUtils.isEmpty(confList)) {
            initDB.initialPopulation();
            em.persist(new Config("schema_version", "1"));
        }
        checkDevicesTable();
    }

    private void checkDevicesTable() {
        final Number oldSchemaColumns = (Number)em.createNativeQuery("SELECT COUNT(*) "
                + "FROM information_schema.columns "
                + "WHERE table_schema = 'public' "
                + "AND table_name = 'device' "
                + "AND column_name IN "
                + "('description', 'location', 'manuf_model', 'manuf_serial_number', "
                + "'manufacturer', 'purchase_order', 'status')").getSingleResult();
        if (oldSchemaColumns.longValue() > 0) {
            LOGGER.log(Level.WARNING, "Database table 'device' contains columns which are no longer needed. "
                    + "Execute 'postgres-db-schemas/device_update1.sql' SQL queries to remove them.");
            LOGGER.log(Level.WARNING, "* * * THIS IS A POSTGRESQL SCRIPT. TRANSLATE IF USING ANOTHER BACKEND! * * *");
        }
    }
}
