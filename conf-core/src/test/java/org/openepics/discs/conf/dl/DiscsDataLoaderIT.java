package org.openepics.discs.conf.dl;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openepics.discs.conf.ejb.ConfigurationEJB;

@RunWith(Arquillian.class)
public class DiscsDataLoaderIT {

    @Deployment
    public static WebArchive createDeployment() {
        return CCDBPackager.createWebArchive();
    }

    @Inject ConfigurationEJB confEJB;

    @Test
    @UsingDataSet("units.yml")
    public void getCablesTest() {
        assertEquals(1, confEJB.findUnits().size());
    }
}
