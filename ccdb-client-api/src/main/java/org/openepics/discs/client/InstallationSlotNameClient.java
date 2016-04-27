/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.client;

import java.util.logging.Logger;
import javax.annotation.Nonnull;
import org.openepics.discs.client.impl.ClosableResponse;
import org.openepics.discs.client.impl.ResponseException;
import org.openepics.discs.ccdb.jaxb.InstallationSlotNames;
import org.openepics.discs.ccdb.jaxrs.InstallationSlotNameResource;

/**
 *
 * @author <a href="mailto:miroslav.pavleski@cosylab.com">Miroslav Pavleski</a>
 */
class InstallationSlotNameClient implements 
        InstallationSlotNameResource {
    
    private static final Logger LOG = Logger.getLogger(InstallationSlotNameClient.class.getName());
    
    private static final String PATH_SLOT_NAMES = "slotName";
    
    @Nonnull private final CCDBClient client;

    InstallationSlotNameClient(CCDBClient client) { this.client = client; }
    
    @Override
    public InstallationSlotNames getAllInstallationSlotNames(String deviceTypeName) {
        LOG.fine("Invoking getAllInstallationSlotNames.");

        final String url = deviceTypeName != null ? 
                client.buildUrl(PATH_SLOT_NAMES, deviceTypeName) : 
                client.buildUrl(PATH_SLOT_NAMES);
        try (final ClosableResponse response = client.getResponse(url)) {
            return response.readEntity(InstallationSlotNames.class);
        } catch (Exception e) {
            throw new ResponseException("Couldn't retrieve data from service at " + url + ".", e);
        }
    }
}
