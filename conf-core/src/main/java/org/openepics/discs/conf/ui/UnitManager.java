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
package org.openepics.discs.conf.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import joptsimple.internal.Strings;

import org.openepics.discs.conf.dl.UnitsLoaderQualifier;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.ejb.UnitEJB;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.UnitView;
import org.primefaces.context.RequestContext;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * The Java EE managed bean for supporting UI actions for Unit manipulation.
 *
 * @author vuppala
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ManagedBean
@ViewScoped
public class UnitManager extends AbstractExcelSingleFileImportUI implements Serializable {
    private static final long serialVersionUID = 5504821804362597703L;

    @Inject private transient  UnitEJB unitEJB;
    @Inject private transient DataLoaderHandler dataLoaderHandler;
    @Inject @UnitsLoaderQualifier private transient DataLoader unitsDataLoader;

    private List<Unit> units;
    private List<UnitView> unitViews;
    private List<UnitView> filteredUnits;

    private UnitView selectedUnit;

    // * * * * * * * Add/modify dialog fields * * * * * * *
    private String name;
    private String description;
    private String symbol;
    private String quantity;

    // * * * * * * * Unit scroll position * * * * * * *
    private long unitPosition;

    /**
     * Creates a new instance of UnitManager
     */
    public UnitManager() {
    }

    /**
     * Java EE post-construct life-cycle callback.
     */
    @PostConstruct
    public void init() {
        unitPosition = -1;
        final String unitId = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().
                                    getRequest()).getParameter("id");
        refreshUnits();
        getUnitPosition(unitId);
    }

    private void getUnitPosition(final String unitId) {
        if (!Strings.isNullOrEmpty(unitId)) {
            final long id = Long.parseLong(unitId);
            int elementPosition = 0;
            for (Unit unit : units) {
                if (unit.getId() == id) {
                    unitPosition = elementPosition;
                    break;
                }
                ++elementPosition;
            }
        }
    }

    /**
     * @return The list of all user defined physics units in the database
     */
    public List<Unit> getUnits() {
        return units;
    }

    /**
     * @return The list of all user defined physics units in the database
     */
    public List<UnitView> getUnitViews() {
        return unitViews;
    }

    /**
     * @return The list of filtered units used by the PrimeFaces filter field.
     */
    public List<UnitView> getFilteredUnits() {
        return filteredUnits;
    }

    /**
     * @param filteredUnits The list of filtered units used by the PrimeFaces filter field.
     */
    public void setFilteredUnits(List<UnitView> filteredUnits) {
        this.filteredUnits = filteredUnits;
    }

    @Override
    public void doImport() {
        try (InputStream inputStream = new ByteArrayInputStream(importData)) {
            loaderResult = dataLoaderHandler.loadData(inputStream, unitsDataLoader);
            refreshUnits();
            RequestContext.getCurrentInstance().update("unitsForm");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshUnits() {
        units = ImmutableList.copyOf(unitEJB.findAllOrdered());

        // transform the list of Unit into a list of UnitView
        unitViews = ImmutableList.copyOf(Lists.transform(units, new Function<Unit, UnitView>() {
                                                                        @Override
                                                                        public UnitView apply(Unit input) {
                                                                            return new UnitView(input);
                                                                        }}));
    }

    /**
     * This method clears all input fields used in the "Add unit" dialog.
     */
    public void prepareAddPopup() {
        selectedUnit = null;
        name = null;
        description = null;
        symbol = null;
        quantity = null;
    }

    private void prepareModifyPopup() {
        name = selectedUnit.getName();
        description = selectedUnit.getDescription();
        symbol = selectedUnit.getSymbol();
        quantity = selectedUnit.getQuantity();
    }

    /**
     * Method creates a new unit definition when user presses the "Save" button in the "Add new" dialog.
     */
    public void onAdd() {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(description);
        Preconditions.checkNotNull(symbol);
        Preconditions.checkNotNull(quantity);
        final Unit unitToAdd = new Unit(name, quantity, symbol, description);
        unitEJB.add(unitToAdd);
        refreshUnits();
    }

    /**
     * Method that saves the modified unit definition when user presses the "Save" button in the
     * "Modify unit" dialog.
     */
    public void onModify() {
        final Unit unitToSave = selectedUnit.getUnit();
        unitToSave.setName(name);
        unitToSave.setDescription(description);
        unitToSave.setSymbol(symbol);
        unitToSave.setQuantity(quantity);
        unitEJB.save(unitToSave);

        // reset the input fields
        refreshUnits();
        prepareAddPopup();
    }

    /**
     * @return <code>true</code> if the <code>selectedUnit</code> is used in some {@link Property},
     * <code>false</code> otherwise.
     */
    public boolean isSelectedUnitInUse() {
        return (selectedUnit != null) && unitEJB.isUnitUsed(selectedUnit.getUnit());
    }

    /**
     * Method that deletes the unit definition if that is allowed. Unit deletion is prevented if the unit
     * is used in some {@link Property} definition.
     */
    public void onDelete() {
        Preconditions.checkNotNull(selectedUnit);
        final Unit unitToDelete = selectedUnit.getUnit();
        if (unitEJB.isUnitUsed(unitToDelete)) {
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, "In use",
                                                    "The unit cannot be deleted because it is in use.");
        } else {
            unitEJB.delete(unitToDelete);
            refreshUnits();
        }
    }

    public void nameValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                                    "Please enter a name"));
        }

        final String unitName = value.toString();
        final Unit existingUnit = unitEJB.findByName(unitName);
        if ((selectedUnit == null && existingUnit != null)
                || (selectedUnit != null && !selectedUnit.getUnit().equals(existingUnit))) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                                    "The unit with this name already exists."));
        }
    }

    /**
     * @return the selectedUnit
     */
    public UnitView getSelectedUnit() {
        return selectedUnit;
    }

    /**
     * @param selectedUnit the selectedUnit to set
     */
    public void setSelectedUnit(UnitView selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    /**
     * @param selectedUnit the selectedUnit to set
     */
    public void setSelectedUnitToModify(UnitView selectedUnit) {
        this.selectedUnit = selectedUnit;
        prepareModifyPopup();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * @return the quantity
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getStartupScript() {
        final StringBuilder js = new StringBuilder();
        js.append("<script type=\"text/javascript\">").append("\r\n");
        js.append("jQuery(document).ready(function() {").append("\r\n");

        //js.append("alert(\"Debug CP#1\");").append("\r\n");
        js.append("if ((").append(unitPosition).append(" < 0) || (").append(unitPosition)
                        .append(" > unitsTableVar.getPaginator().cfg.rowCount)) {").append("\r\n");
        js.append("    return;").append("\r\n");
        js.append("}").append("\r\n");
        js.append("var page = Math.floor(").append(unitPosition).append(" / unitsTableVar.getPaginator().cfg.rows);").append("\r\n");
        js.append("unitsTableVar.getPaginator().setPage(page);").append("\r\n");
        js.append("});").append("\r\n");
        js.append("</script>").append("\r\n");
        return js.toString();
    }

}
