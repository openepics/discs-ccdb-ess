/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.ConfigurationEJB;
import org.openepics.discs.conf.ent.DataType;

/**
 *
 * @author vuppala
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ViewScoped
public class DataTypeManager implements Serializable {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DataTypeManager.class.getCanonicalName());

    @EJB private ConfigurationEJB configurationEJB;

    private List<DataType> dataTypes;
    private List<DataType> fileteredDataTypes;
    /**
     * Creates a new instance of DataTypeManager
     */
    public DataTypeManager() {
    }

    public List<DataType> getDataTypes() {
        if (dataTypes == null) dataTypes = configurationEJB.findDataTypes();
        return dataTypes;
    }

    public List<DataType> getFileteredDataTypes() {
        return fileteredDataTypes;
    }

    public void setFileteredDataTypes(List<DataType> fileteredDataTypes) {
        this.fileteredDataTypes = fileteredDataTypes;
    }



}
