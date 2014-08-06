package org.openepics.discs.conf.ent;

/**
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public enum ReportFilterAction {
    /** Property value is a string (or enum) and contains a substring <code>filter</code>. Entities without this property will not be included. */
    CONTAINS,
    /**
     * Property value is a string (or enum) and its value is <code>filter</code>
     * . Entities without this property will not be included.
     */
    IS,
    /**
     * Property value is a string (or enum) and it starts with
     * <code>filter</code>. Entities without this property will not be included.
     */
    STARTS_WITH,
    /**
     * The value of the actual property does not matter, but it will be included
     * in the report, if it exists (optional).
     */
    DISPLAY_ONLY,
    /**
     * Property value is a scalar number and
     * <code>propertyValue == filter</code>.
     */
    EQ,
    /**
     * Property value is a scalar number and
     * <code>propertyValue != filter</code>.
     */
    NE,
    /**
     * Property value is a scalar number and
     * <code>propertyValue &lt; filter</code>.
     */
    LT,
    /**
     * Property value is a scalar number and
     * <code>propertyValue &lt;= filter</code>.
     */
    LE,
    /**
     * Property value is a scalar number and
     * <code>propertyValue &gt; filter</code>.
     */
    GT,
    /**
     * Property value is a scalar number and
     * <code>propertyValue &gt;= filter</code>.
     */
    GE
}
