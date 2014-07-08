package org.openepics.discs.conf.ent;


/* ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! *
 * WARNING! PLEASE NOTE! WARNING! PLEASE NOTE! WARNING! PLEASE NOTE! WARNING! PLEASE NOTE! WARNING! PLEASE NOTE!
 *
 * The field in the Property.java file persisting this entity is limited to 12 characters.
 *
 * @Enumerated(EnumType.STRING)
 * @Column(name = "association", length = 12)
 *
 * If you add another enumeration property that goes over this limit, remember to update the database model accordingly.
 *
 * WARNING! PLEASE NOTE! WARNING! PLEASE NOTE! WARNING! PLEASE NOTE! WARNING! PLEASE NOTE! WARNING! PLEASE NOTE!
 * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! * ! */

public enum PropertyAssociation {
	/** The property is only for <i>Component Type</i>, E.g.: a CAD drawing. */
	TYPE,
	/** The property is only for <i>Slot</i>. */
	SLOT,
	/** The property is only for <i>Physical Device</i>. */
	DEVICE,
	/** The property is for both <i>Slot</i> and <i>Physical Device</i>. */
	SLOT_DEVICE,
	/** The property is for both <i>Component Type</i> and <i>Physical Device</i>. */
	TYPE_DEVICE,
	/** The property is for both <i>Component Type</i> and <i>Slot</i>. */
	TYPE_SLOT,
	/** The property is for <i>Component Type</i>, <i>Slot</i> and <i>Physical Device</i> .*/
	ALL
}
