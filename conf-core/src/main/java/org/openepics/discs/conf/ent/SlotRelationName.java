package org.openepics.discs.conf.ent;

/**
 * System defined slot relation types
 *
 * @author Miroslav Pavleski <miroslav.pavleski@cosylab.com>
 *
 */
public enum SlotRelationName {
    /** Slot contains another slot */
    CONTAINS,
    /** Slot powers another slot */
    POWERS,
    /** Slot controls another slot */
    CONTROLS
}
