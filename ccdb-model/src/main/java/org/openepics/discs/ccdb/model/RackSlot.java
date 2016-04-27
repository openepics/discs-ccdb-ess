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
package org.openepics.discs.ccdb.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * The slot in a rack. Note that this is very different from a device slot (Slot.java).
 * 
 * @author vuppala
 */
@Entity
@Table(name = "rack_slot", uniqueConstraints=@UniqueConstraint(columnNames={"rack", "side", "slot_number"}))
@NamedQueries({
        @NamedQuery(name = "RackSlot.findSlotsByRack", query = "SELECT rs FROM RackSlot rs WHERE rs.rack = :rack ORDER BY rs.slotNumber DESC, rs.side")
        })
public class RackSlot implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotNull   
    @ManyToOne
    @JoinColumn(name="rack", nullable = false)
    private Rack rack;
    
    @NotNull
    @Column(name="side", length = 8, nullable = false)
    private String side; // Front, rear etc
    
    @NotNull
    @Column(name="slot_number", nullable = false)
    private Short slotNumber; 
    
    @NotNull
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    
    @Column(name = "comments", length = 1024)
    private String comments;
       
    @ManyToMany
    @JoinTable(name = "rackslot_devslot",
                joinColumns = { @JoinColumn(name = "rackslot_id") },
                inverseJoinColumns = { @JoinColumn(name = "devslot_id") })
    private List<Slot> deviceSlots;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RackSlot)) {
            return false;
        }
        RackSlot other = (RackSlot) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.RackSlot[ id=" + id + " ]";
    }
    
    // getters and setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rack getRack() {
        return rack;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Short getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Short slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<Slot> getDeviceSlots() {
        return deviceSlots;
    }

    public void setDeviceSlots(List<Slot> deviceSlots) {
        this.deviceSlots = deviceSlots;
    }
    
}
