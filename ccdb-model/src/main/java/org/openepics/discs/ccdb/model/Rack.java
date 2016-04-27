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

package org.openepics.discs.conf.ent;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * A rack for holding devices.
 * 
 * @author vuppala
 */
@Entity
@Table(name = "rack")
@NamedQueries({
        @NamedQuery(name = "Rack.findAllOrdered", query = "SELECT r FROM Rack r ORDER BY r.name")
        })
public class Rack implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", unique=true, nullable = false, length = 255)
    private String name;
    
    @Column(name = "rack_alias", length = 255)
    private String rackAlias; // another name
    
    @Column(name = "rack_type", length = 16)
    private String rackType; // type of rack. for future use. currently they are assumed to be double-sided (front/rear)
    
    @Column(name = "site_row", length = 255)
    private String siteRow; // if racks are arranged in rows (site specific)
    
    @Column(name = "site_system", length = 255)
    private String siteSystem; // Site specific. segment of the facility for which this rack holds devices
    
    @Column(name = "site_subsystem", length = 255)
    private String siteSubsystem; // Site specific. system of the facility for which this rack holds devices
    
    @Column(name = "site_devtype", length = 255)
    private String siteDeviceType;  // Site specific. The type of devices that would go into the rack.
    
       
    public Long getId() {
        return id;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rack)) {
            return false;
        }
        Rack other = (Rack) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.openepics.discs.conf.ent.Rack[ id=" + id + " ]";
    }
    
    // Getters and setters
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRackAlias() {
        return rackAlias;
    }

    public void setRackAlias(String rackAlias) {
        this.rackAlias = rackAlias;
    }

    public String getRackType() {
        return rackType;
    }

    public void setRackType(String rackType) {
        this.rackType = rackType;
    }

    public String getSiteRow() {
        return siteRow;
    }

    public void setSiteRow(String siteRow) {
        this.siteRow = siteRow;
    }

    public String getSiteSystem() {
        return siteSystem;
    }

    public void setSiteSystem(String siteSystem) {
        this.siteSystem = siteSystem;
    }

    public String getSiteSubsystem() {
        return siteSubsystem;
    }

    public void setSiteSubsystem(String siteSubsystem) {
        this.siteSubsystem = siteSubsystem;
    }

    public String getSiteDeviceType() {
        return siteDeviceType;
    }

    public void setSiteDeviceType(String siteDeviceType) {
        this.siteDeviceType = siteDeviceType;
    }

   
    
}
