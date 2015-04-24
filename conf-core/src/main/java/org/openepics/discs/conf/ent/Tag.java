/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the License,
 * or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.ent;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * @author Miha Vitorovič &lt;miha.vitorovic@cosylab.com&gt;
 *
 */
@Entity
@Table(name = "tag")
@NamedQueries({
    @NamedQuery(name = "Tag.findAllOrdered", query = "SELECT t FROM Tag t ORDER BY t.name")
})
public class Tag implements Serializable {
    private static final long serialVersionUID = 6824320954767876122L;

    @Id
    private String name;

    @Version
    protected Long version;

    protected Tag() {}

    /** Constructs a new tag
     * @param name the string to be used for tag
     */
    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Tag other = (Tag) obj;
        if (name == null) {
            return other.name == null;
        }

        return name.equals(other.name);
    }
}
