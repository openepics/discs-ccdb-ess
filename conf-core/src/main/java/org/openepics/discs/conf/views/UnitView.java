package org.openepics.discs.conf.views;

import org.openepics.discs.conf.ent.Unit;

/**
 * An UI view object for showing {@link Unit} entity in a table.
 *
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 *
 */
public class UnitView {
    private final String name;
    private final String description;
    private final String symbol;
    private final String quantity;

    private final Unit unit;

    /** Constructs a new immutable view of the {@link Unit}
     * @param unit the {@link Unit} to base the view on
     */
    public UnitView(Unit unit) {
        name = unit.getName();
        description = unit.getDescription();
        symbol = unit.getSymbol();
        quantity = unit.getQuantity();

        this.unit = unit;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /** @return the description */
    public String getDescription() {
        return description;
    }

    /** @return the symbol */
    public String getSymbol() {
        return symbol;
    }

    /** @return the quantity */
    public String getQuantity() {
        return quantity;
    }

    /** @return the unit */
    public Unit getUnit() {
        return unit;
    }
}
