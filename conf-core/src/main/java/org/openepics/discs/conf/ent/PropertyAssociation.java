package org.openepics.discs.conf.ent;

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
