/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.conf.dl;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.commons.cli.Options;

/**
 *
 * @author vuppala
 */
public class CongfigLoader {

    private static final Logger logger = Logger.getLogger("org.openepics.conf.dl");
    private static FileHandler fh;
    private static SimpleFormatter formatter = new SimpleFormatter();
    private static final String DEF_PROPS_FILE = "configloader.properties"; // name of substream (sheet) that contains master relationship list
    private static EntityManager em;

    private static void loadData() throws Exception {
        Properties configProps = new Properties();
        FileInputStream in = new FileInputStream(DEF_PROPS_FILE);
        configProps.load(in);
        in.close();
        DataLoader dataLoader;
        String entityNames[] = configProps.getProperty("LoadOrder").split(",");

        for (String entityName : entityNames) {
            String dataStreams[] = configProps.getProperty(entityName).split(",");

            for (String dataStream : dataStreams) {
                String streamNames[] = dataStream.split(":");
                String stream = streamNames[0];
                String substream = streamNames.length == 2 ? streamNames[1] : "";

                logger.log(Level.FINE, entityName + " " + stream + " " + substream);
                dataLoader = null;
                switch (entityName) {
                    case "Property":
                        dataLoader = new PropertyLoader(em);
                        break;
                    case "ComponentType":
                        dataLoader = new ComponentTypeLoader(em);
                        break;
                    case "LogicalComponent":
                        dataLoader = new LogicalComponentLoader(em);
                        break;
                    case "PhysicalComponent":
                        dataLoader = new PhysicalComponentLoader(em);
                        break;
                    case "Relation":
                        dataLoader = new RelationLoader(em);
                        break;
                    case "LogicalComponentRelation":
                        dataLoader = new LogicalCompRelationLoader(em);
                        break;
                    case "Signal":
                        dataLoader = new SignalLoader(em);
                        break;
                    default:
                        System.out.println("Invalid option: " + entityName);
                        break;
                }
                if (dataLoader != null) {                
                    dataLoader.load(stream, substream, dataStream);
                }
            }
        }
    }

    public static void main(String[] args) {
//        if (args.length != 0) {
//            System.err.println("Usage: ConfigLoader");
//            System.exit(1);
//        }

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.openepics.conf.dl");
            em = emf.createEntityManager();
            fh = new FileHandler("cdl-log.txt");
            fh.setFormatter(formatter);
            logger.addHandler(fh);
            logger.setLevel(Level.ALL);
            loadData();
        } catch (Exception e) {
            //System.out.println(e);
            logger.log(Level.SEVERE, "Cannot load data", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}