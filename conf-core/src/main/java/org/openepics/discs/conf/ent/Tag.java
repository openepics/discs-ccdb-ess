package org.openepics.discs.conf.ent;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tag")
public class Tag extends ConfigurationEntity {
    @Basic(optional = false)
    @NotNull
    @Column(name = "name", unique = true)
    private String name;

    protected Tag() {
    }

    public Tag(String name, String modifiedBy) {
        this.name = name;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = new Date();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
