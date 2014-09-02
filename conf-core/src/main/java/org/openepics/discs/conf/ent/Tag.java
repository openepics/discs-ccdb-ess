package org.openepics.discs.conf.ent;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "tag")
public class Tag {
    @Id
    private String name;

    @Version
    protected Long version;

    protected Tag() {}

    public Tag(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		Tag other = (Tag) obj;
		if (name == null) return other.name == null;

		return name.equals(other);
	}
}
