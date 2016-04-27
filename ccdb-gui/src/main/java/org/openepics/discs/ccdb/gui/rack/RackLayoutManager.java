/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2012.
 *
 * You may use this software under the terms of the GNU public license
 *  (GPL). The terms of this license are described at:
 *       http://www.gnu.org/licenses/gpl.txt
 *
 * Contact Information:
 *   Facilitty for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 *
 */
package org.openepics.discs.ccdb.gui.rack;

import org.openepics.discs.ccdb.core.ejb.RackEJB;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.openepics.discs.ccdb.model.Rack;
import org.openepics.discs.ccdb.model.RackSlot;
import org.openepics.discs.ccdb.model.Slot;
import org.openepics.discs.ccdb.gui.ui.util.UiUtility;

/**
 * Bean to support rack layout view
 *
 * @author vuppala
 *
 */
@Named
@ViewScoped
public class RackLayoutManager implements Serializable {

    private static final Logger logger = Logger.getLogger(RackLayoutManager.class.getName());
    @EJB
    private RackEJB rackEJB;

    public static class RackSlotView {

        int number;
        private List<Slot> frontDevices = new ArrayList<>();
        private List<Slot> rearDevices = new ArrayList<>();
        boolean frontMatch = false;  // devices in front slot matched a search query
        boolean rearMatch = false;  // devices in rear slot matched a search query

        public RackSlotView(int num, List<Slot> fdevs, List<Slot> rdevs, boolean fmatch, boolean rmatch) {
            number = num;
            frontDevices = fdevs;
            rearDevices = rdevs;
            frontMatch = fmatch;
            rearMatch = rmatch;
        }

        // getter/setters
        public List<Slot> getFrontDevices() {
            return frontDevices;
        }

        public List<Slot> getRearDevices() {
            return rearDevices;
        }

        public int getNumber() {
            return number;
        }

        public boolean isFrontMatch() {
            return frontMatch;
        }

        public boolean isRearMatch() {
            return rearMatch;
        }      
    }

    private List<Rack> racks;
    private String rackSearchString;
    private String deviceSearchString;
    
    // display parameters
    private int rows = 1;
    private int columns = 6;
    private String inputRackName;
    private String inputDeviceName;
    private int currentPage;
    private int maximumPages;
    private int numberOfRacks;

    public RackLayoutManager() {

    }

    /**
     *
     */
    @PostConstruct
    public void init() {
        inputRackName = "";
        inputDeviceName = "";
       
        submitSearch();
    }

    /**
     * Submit a new search
     */
    public void submitSearch() {
        currentPage = 1;
        inputRackName = inputRackName.toLowerCase(); // case insensitive search
        inputDeviceName = inputDeviceName.toLowerCase();
        rackSearchString  = inputRackName == null || inputRackName.isEmpty() ? "%" : "%" + inputRackName.replaceAll("_", "\\\\_").replaceAll("%", "\\\\%") + "%";        
        deviceSearchString =  inputDeviceName == null || inputDeviceName.isEmpty()? inputDeviceName :  "%" + inputDeviceName.replaceAll("_", "\\\\_").replaceAll("%", "\\\\%") + "%";
        
        numberOfRacks = (int) rackEJB.findNumberOfRacks(rackSearchString, deviceSearchString);
        if (numberOfRacks == 0) {
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, "No racks found with the given search criteria","Please try something different");
            racks.clear();
            maximumPages = 0;
        } else {
            searchRacks();
            maximumPages = (numberOfRacks / (rows * columns)) + (numberOfRacks % (rows * columns) == 0 ? 0 : 1);
        }        
    }
    
    /**
     * Search rack slots
     * 
     */
    private void searchRacks() {            
        racks = rackEJB.findRacks(rackSearchString, deviceSearchString, currentPage - 1, rows * columns);      
    }

    /**
     * previous page
     * 
     */
    public void gotoPrevPage() {
        if (prevPage()) {
            --currentPage;
            searchRacks();
        }
    }

    /**
     * next page
     */
    public void gotoNextPage() {
        if (nextPage()) {
            ++currentPage;
            searchRacks();
        }
    }

    /**
     * first page
     */
    public void gotoFirstPage() {
        currentPage = 1;
        searchRacks();
    }

    /**
     * last page
     */
    public void gotoLastPage() {
        currentPage = maximumPages;
        searchRacks();
    }

    /**
     * is there a  next page?
     * @return false if last page
     */
    public boolean nextPage() {
        return currentPage < maximumPages;
    }

    /**
     * is there a previous page?
     * 
     * @return false if first page
     */
    public boolean prevPage() {
        return currentPage > 1;
    }

    /**
     * on last page?
     * 
     * @return true if last page
     */
    public boolean isLastPage() {
        return currentPage == maximumPages;
    }

    /**
     * on first page?
     * 
     * @return true if first page
     */
    public boolean isFirstPage() {
        return currentPage == 1;
    }

    /**
     * Find slots of a rack
     * 
     * @param rack rack
     * @return list of rack slots
     */
    public List<RackSlotView> slotsOfRack(Rack rack) {
        List<RackSlot> slots = rackEJB.findSlotsOrdered(rack);
        List<RackSlotView> vslots = new ArrayList<>();
        Iterator<RackSlot> slotIterator = slots.iterator();
        RackSlot frontSlot, rearSlot;

        if (slots.size() % 2 != 0) {
            logger.log(Level.SEVERE, "Slots are not ordered correctly");
            return vslots;
        }

        boolean fmatch, rmatch;
        while (slotIterator.hasNext()) {
            frontSlot = slotIterator.next();
            rearSlot = slotIterator.next();

            fmatch = false;
            rmatch = false;
            if ( !(inputDeviceName == null || inputDeviceName.isEmpty()) ) {
                for (Slot slot : frontSlot.getDeviceSlots()) {
                    if (slot.getName().toLowerCase().contains(inputDeviceName)) {
                        fmatch = true;
                        break;
                    }
                }
                for (Slot slot : rearSlot.getDeviceSlots()) {
                    if (slot.getName().toLowerCase().contains(inputDeviceName)) {
                        rmatch = true;
                        break;
                    }
                }
            }
            if (frontSlot.getSlotNumber().compareTo(rearSlot.getSlotNumber()) == 0) {
                vslots.add(new RackSlotView(frontSlot.getSlotNumber(), frontSlot.getDeviceSlots(), rearSlot.getDeviceSlots(), fmatch, rmatch));
            } else {
                logger.log(Level.SEVERE, "Slots are not ordered correctly");
            }
        }

        return vslots;
    }

    /**
     * Find the rack at given row, column
     * 
     * @param row row
     * @param col column
     * @return rack at given row and column
     */
    public Rack rackAt(int row, int col) {
        int index = (row - 1) * columns + (col - 1);

        if (racks == null || index >= racks.size()) {
            return null;
        }

        return racks.get(index);
    }

    // getters and setters
    public List<Rack> getRacks() {
        return racks;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getNumberOfRacks() {
        return numberOfRacks;
    }

    public int getMaximumPages() {
        return maximumPages;
    }

    public String getInputRackName() {
        return inputRackName;
    }

    public void setInputRackName(String inputRackName) {
        this.inputRackName = inputRackName;
    }

    public String getInputDeviceName() {
        return inputDeviceName;
    }

    public void setInputDeviceName(String inputDeviceName) {
        this.inputDeviceName = inputDeviceName;
    }

}
