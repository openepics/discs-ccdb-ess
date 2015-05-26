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
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.openepics.discs.conf.ent.Property;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.export.ExportTable;
import org.openepics.discs.conf.ui.common.AbstractExcelSingleFileImportUI;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.UIException;
import org.openepics.discs.conf.ui.export.ExportSimpleTableDialog;
import org.openepics.discs.conf.ui.export.SimpleTableExporter;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.UnitView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * The Java EE managed bean for supporting UI actions for Unit manipulation.
 *
 * @author vuppala
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Named
@ManagedBean
@ViewScoped
public class UnitManager extends AbstractExcelSingleFileImportUI implements Serializable, SimpleTableExporter {
    private static final long serialVersionUID = 5504821804362597703L;
    private static final Logger LOGGER = Logger.getLogger(UnitManager.class.getCanonicalName());

    @Inject private transient  UnitEJB unitEJB;
    @Inject private transient DataLoaderHandler dataLoaderHandler;
    @Inject @UnitsLoaderQualifier private transient DataLoader unitsDataLoader;

    private List<Unit> units;
    private List<UnitView> unitViews;
    private transient List<UnitView> filteredUnits;

    private transient UnitView selectedUnit;

    // * * * * * * * Add/modify dialog fields * * * * * * *
    private String name;
    private String description;
    private String symbol;
    private boolean unitAdd;

    private ExportSimpleTableDialog simpleTableExporterDialog;

    private class ExportSimpleUnitTableDialog extends ExportSimpleTableDialog {
        @Override
        protected String getTableName() {
            return "Units";
        }

        @Override
        protected String getFileName() {
            return "units";
        }

        @Override
        protected void addHeaderRow(ExportTable exportTable) {
            exportTable.addHeaderRow("Name", "Description", "Symbol");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<UnitView> exportData = filteredUnits == null || filteredUnits.isEmpty() ? unitViews
                    : filteredUnits;
            for (final UnitView unit : exportData) {
                exportTable.addDataRow(unit.getName(), unit.getDescription(), unit.getSymbol());
            }
        }
    }

    /** Creates a new instance of UnitManager */
    public UnitManager() {
    }

    /** Java EE post-construct life-cycle callback */
    @PostConstruct
    public void init() {
        final String unitIdStr = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().
                                    getRequest()).getParameter("id");
        try {
            simpleTableExporterDialog = new ExportSimpleUnitTableDialog();
            refreshUnits();
            if (!Strings.isNullOrEmpty(unitIdStr)) {
                final long unitId = Long.parseLong(unitIdStr);
                int elementPosition = 0;
                for (Unit unit : units) {
                    if (unit.getId() == unitId) {
                        RequestContext.getCurrentInstance().execute("selectEntityInTable(" + elementPosition
                                + ", 'unitsTableVar');");
                        return;
                    }
                    ++elementPosition;
                }
            }
        } catch (NumberFormatException e) {
            // just log
            LOGGER.log(Level.WARNING, "URL contained strange unit ID: " + unitIdStr );
        } catch(Exception e) {
            throw new UIException("Device type display initialization fialed: " + e.getMessage(), e);
        }
    }

    /** @return The list of all user defined physics units in the database */
    public List<Unit> getUnits() {
        return units;
    }

    /** @return The list of all user defined physics units in the database */
    public List<UnitView> getUnitViews() {
        return unitViews;
    }

    /** @return The list of filtered units used by the PrimeFaces filter field */
    public List<UnitView> getFilteredUnits() {
        return filteredUnits;
    }
    /** @param filteredUnits The list of filtered units used by the PrimeFaces filter field */
    public void setFilteredUnits(List<UnitView> filteredUnits) {
        this.filteredUnits = filteredUnits;
    }

    @Override
    public void doImport() {
        try (InputStream inputStream = new ByteArrayInputStream(importData)) {
            setLoaderResult(dataLoaderHandler.loadData(inputStream, unitsDataLoader));
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

    /** This method clears all input fields used in the "Add unit" dialog */
    public void prepareAddPopup() {
        name = null;
        description = null;
        symbol = null;
        unitAdd = true;
    }

    /** This method prepares the input fields used in the "Edit unit" dialog */
    public void prepareModifyPopup() {
        name = selectedUnit.getName();
        description = selectedUnit.getDescription();
        symbol = selectedUnit.getSymbol();
        unitAdd = false;
    }

    /** Method creates a new unit definition when user presses the "Save" button in the "Add new" dialog  */
    public void onAdd() {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(description);
        Preconditions.checkNotNull(symbol);
        selectedUnit = null;
        final Unit unitToAdd = new Unit(name, symbol, description);
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
            Utility.showMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                    "The unit cannot be deleted because it is in use.");
        } else {
            unitEJB.delete(unitToDelete);
            refreshUnits();
            selectedUnit = null;
        }
    }

    /**
     * A validator to check whether the unit name is unique.
     * @param ctx {@link javax.faces.context.FacesContext}
     * @param component {@link javax.faces.component.UIComponent}
     * @param value The value
     * @throws ValidatorException validation failed
     */
    public void nameValidator(FacesContext ctx, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                                    "Please enter a name"));
        }

        final String unitName = value.toString();
        final Unit existingUnit = unitEJB.findByName(unitName);
        if ((selectedUnit == null && existingUnit != null)
                || (selectedUnit != null && existingUnit != null && !selectedUnit.getUnit().equals(existingUnit))) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                                    "The unit with this name already exists."));
        }
    }

    /** @return the selectedUnit */
    public UnitView getSelectedUnit() {
        return selectedUnit;
    }
    /** @param selectedUnit the selectedUnit to set */
    public void setSelectedUnit(UnitView selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    /** @return the name */
    public String getName() {
        return name;
    }
    /** @param name the name to set */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the description */
    public String getDescription() {
        return description;
    }
    /** @param description the description to set */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the symbol */
    public String getSymbol() {
        return symbol;
    }
    /** @param symbol the symbol to set */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /** @return the unitAdd */
    public boolean isUnitAdd() {
        return unitAdd;
    }

    @Override
    public ExportSimpleTableDialog getSimpleTableDialog() {
        return simpleTableExporterDialog;
    }

    @Override
    public void handleImportFileUpload(FileUploadEvent event) {
        super.handleImportFileUpload(event);
        importFileStatistics = getImportedFileStatistics(unitsDataLoader);
        // TODO once all import handling is the same, handled by single-file-DL.xhtml / fileUpload / oncomplete
        RequestContext.getCurrentInstance().update("importUnitsForm:importStatsDialog");
        RequestContext.getCurrentInstance().execute("PF('importStatsDialog').show();");
    }
}
