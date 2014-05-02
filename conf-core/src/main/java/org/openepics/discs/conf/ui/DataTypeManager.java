/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.openepics.discs.conf.ejb.ConfigurationEJBLocal;
import org.openepics.discs.conf.ent.DataType;

/**
 *
 * @author vuppala
 */
@ManagedBean
@ViewScoped
public class DataTypeManager implements Serializable {

    @EJB
    private ConfigurationEJBLocal configurationEJB;
    private static final Logger logger = Logger.getLogger("org.openepics.discs.conf");
    
    private List<DataType> dataTypes;
    /**
     * Creates a new instance of DataTypeManager
     */
    public DataTypeManager() {
    }
    
    @PostConstruct
    public void init() {
        try {
            dataTypes = configurationEJB.findDataType();           
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error in getting data types", " ");
        }
    }

    public List<DataType> getDataTypes() {
        return dataTypes;
    }
        
}
