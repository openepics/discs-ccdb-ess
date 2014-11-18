package org.openepics.discs.conf.ent;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Properties are given to Devices, Device Types and INstallation Slots
 *
 * @author vuppala
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Entity
@Table(name = "property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Property.findAll", query = "SELECT p FROM Property p"),
    @NamedQuery(name = "Property.findAllOrderedByName", query = "SELECT p FROM Property p ORDER BY p.name"),
    @NamedQuery(name = "Property.findByPropertyId", query = "SELECT p FROM Property p WHERE p.id = :id"),
    @NamedQuery(name = "Property.findByName", query = "SELECT p FROM Property p WHERE p.name = :name"),
    @NamedQuery(name = "Property.findByModifiedBy", query = "SELECT p FROM Property p "
            + "WHERE p.modifiedBy = :modifiedBy")
})
public class Property extends ConfigurationEntity {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name", unique = true)
    private String name;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;

    @JoinColumn(name = "data_type")
    @ManyToOne(optional = false)
    private DataType dataType;

    @JoinColumn(name = "unit")
    @ManyToOne
    private Unit unit;

    /* * * * * Property association section * * * * * * * * */
    @Basic
    @Column(name = "is_type_asspociation")
    private Boolean isTypeAssociation = Boolean.FALSE;

    @Basic
    @Column(name = "is_slot_asspociation")
    private Boolean isSlotAssociation = Boolean.FALSE;

    @Basic
    @Column(name = "is_device_asspociation")
    private Boolean isDeviceAssociation = Boolean.FALSE;

    @Basic
    @Column(name = "is_alignment_asspociation")
    private Boolean isAlignmentAssociation = Boolean.FALSE;

    protected Property() {
    }

    public Property(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public DataType getDataType() {
        return dataType;
    }
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Unit getUnit() {
        return unit;
    }
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public boolean isTypeAssociation() {
        return isTypeAssociation != null ? isTypeAssociation : false;
    }

    public void setTypeAssociation(Boolean isTypeAssociation) {
        this.isTypeAssociation = isTypeAssociation;
    }

    public boolean isSlotAssociation() {
        return isSlotAssociation != null ? isSlotAssociation : false;
    }

    public void setSlotAssociation(Boolean isSlotAssociation) {
        this.isSlotAssociation = isSlotAssociation;
    }

    public boolean isDeviceAssociation() {
        return isDeviceAssociation != null ? isDeviceAssociation : false;
    }

    public void setDeviceAssociation(Boolean isDeviceAssociation) {
        this.isDeviceAssociation = isDeviceAssociation;
    }

    public boolean isAlignmentAssociation() {
        return isAlignmentAssociation != null ? isAlignmentAssociation : false;
    }

    public void setAlignmentAssociation(Boolean isAlignmentAssociation) {
        this.isAlignmentAssociation = isAlignmentAssociation;
    }

    public boolean isAssociationAll() {
        return isTypeAssociation() && isSlotAssociation() && isDeviceAssociation() && isAlignmentAssociation();
    }

    public boolean isAssociationNone() {
        return !(isTypeAssociation() || isSlotAssociation() || isDeviceAssociation() || isAlignmentAssociation());
    }

    public void setAllAssociation() {
        isTypeAssociation = Boolean.TRUE;
        isSlotAssociation = Boolean.TRUE;
        isDeviceAssociation = Boolean.TRUE;
        isAlignmentAssociation = Boolean.TRUE;
    }

    public void setNoneAssociation() {
        isTypeAssociation = Boolean.FALSE;
        isSlotAssociation = Boolean.FALSE;
        isDeviceAssociation = Boolean.FALSE;
        isAlignmentAssociation = Boolean.FALSE;
    }

    @Override
    public String toString() {
        return "Property[ propertyId=" + id + " ]";
    }
}
