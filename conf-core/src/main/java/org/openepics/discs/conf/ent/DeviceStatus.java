package org.openepics.discs.conf.ent;

/**
 * Enumerator that marks current status of the {@link Device}
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
public enum DeviceStatus {
    IN_FABRICATION("In fabrication"),
    UNDER_TESTING("Under testing"),
    UNDER_REPAIR("Under repair"),
    READY("Ready"),
    SPARE("Spare");

    private String label;

    private DeviceStatus(String label) {
        this.label = label;
    }

    /**
     * Gets the string representation of the enum value
     *
     * @return string representation of the enum value
     */
    public String getLabel() {
        return label;
    }
}
