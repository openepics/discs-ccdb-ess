package org.openepics.discs.conf.ent;

/**
 * Enum representing on entities C(R)UD operations + rename
 * Reading is allowed for logged in users by design.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public enum EntityTypeOperation {
    UPDATE, CREATE, DELETE, RENAME;
}
