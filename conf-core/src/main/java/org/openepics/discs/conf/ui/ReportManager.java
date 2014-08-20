/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.discs.conf.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.SlotEJB;
import org.openepics.discs.conf.ent.Slot;
import org.openepics.discs.conf.ent.SlotPropertyValue;
import org.openepics.discs.conf.util.Utility;

/**
 *
 * @author vuppala
 */
@Named(value = "reportManager")
@ViewScoped
public class ReportManager implements Serializable {

    static public class ColumnModel implements Serializable {

        private String header;
        private String property;

        public ColumnModel(String header, String property) {
            this.header = header;
            this.property = property;
        }

        public String getHeader() {
            return header;
        }

        public String getProperty() {
            return property;
        }
    }

    @EJB
    private SlotEJB slotEJB;
    private static final Logger logger = Logger.getLogger(ReportManager.class.getCanonicalName());

    private List<Slot> layoutSlots;
    private List<Slot> filteredObjects;
    private List<ColumnModel> columns = new ArrayList<>();

    // private String inputSlotFilter = "REA_";
    private String columnTemplate = "Beamline-Position Effective-Length Accumulated-C2C-Length Old-Name Length-From-Element-Before";

    /**
     * Creates a new instance of ReportManager
     */
    public ReportManager() {
    }

    @PostConstruct
    private void init() {
        try {
            layoutSlots = slotEJB.findLayoutSlot();
            createDynamicColumns();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            logger.log(Level.SEVERE, "Cannot retrieve slots");
            Utility.showMessage(FacesMessage.SEVERITY_INFO, "Error in getting slots", " ");
        }
    }

    private void createDynamicColumns() {
        String[] columnKeys = columnTemplate.split(" ");
        columns.clear();

        for (String columnKey : columnKeys) {
            String key = columnKey.trim();
            columns.add(new ColumnModel(columnKey.replace('-', ' '), key));
        }
    }

    public String propertyValue(Slot slot, String propName) {
        String pvalue = null;

        if (slot == null) {
            return null;
        }
        for (SlotPropertyValue sp : slot.getSlotPropertyList()) {
            if (sp.getProperty().getName().equals(propName)) {
                pvalue = sp.getPropValue();
                break;
            }
        }
        return pvalue;
    }

    public List<Slot> getFilteredObjects() {
        return filteredObjects;
    }

    public void setFilteredObjects(List<Slot> filteredObjects) {
        this.filteredObjects = filteredObjects;
    }

    public List<Slot> getLayoutSlots() {
        return layoutSlots;
    }

    public List<ColumnModel> getColumns() {
        return columns;
    }
}
