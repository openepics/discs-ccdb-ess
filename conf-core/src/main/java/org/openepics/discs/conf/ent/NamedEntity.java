package org.openepics.discs.conf.ent;

/**
 * A {@link ConfigurationEntity} that has a name as well.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 */
public interface NamedEntity {

    /** @return the database id of the entity */
    public Long getId();

    /** @return The logical name of the entity */
    public String getName();
}
