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
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.openepics.discs.conf.dl.annotations.UnitsLoader;
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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * The Java EE managed bean for supporting UI actions for Unit manipulation.
 *
 * @author vuppala
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 */
@Named
@ViewScoped
public class UnitManager extends AbstractExcelSingleFileImportUI implements Serializable, SimpleTableExporter {
    private static final long serialVersionUID = 5504821804362597703L;
    private static final Logger LOGGER = Logger.getLogger(UnitManager.class.getCanonicalName());

    @Inject private transient  UnitEJB unitEJB;
    @Inject private transient DataLoaderHandler dataLoaderHandler;
    @Inject @UnitsLoader private transient DataLoader unitsDataLoader;

    private transient List<UnitView> unitViews;
    private transient List<UnitView> filteredUnits;
    private transient List<UnitView> selectedUnits;
    private transient List<UnitView> usedUnits;
    private transient List<UnitView> filteredDialogUnits;
    private transient UnitView unitToModify;

    // * * * * * * * Add/modify dialog fields * * * * * * *
    private String name;
    private String description;
    private String symbol;
    private boolean isUnitAdd;

    private transient ExportSimpleTableDialog simpleTableExporterDialog;

    private class ExportSimpleUnitTableDialog extends ExportSimpleTableDialog {
        @Override
        protected String getTableName() {
            return "Units";
        }

        @Override
        protected String getFileName() {
            return "ccdb_units";
        }

        @Override
        protected void addHeaderRow(ExportTable exportTable) {
            exportTable.addHeaderRow("Name", "Description", "Symbol");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<UnitView> exportData = filteredUnits;
            for (final UnitView unit : exportData) {
                exportTable.addDataRow(unit.getName(), unit.getDescription(), unit.getSymbol());
            }
        }
    }

    /** Creates a new instance of UnitManager */
    public UnitManager() {
    }

    /** Java EE post-construct life-cycle callback */
    @Override
    @PostConstruct
    public void init() {
        super.init();
        final String unitIdStr = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().
                                    getRequest()).getParameter("id");
        try {
            simpleTableExporterDialog = new ExportSimpleUnitTableDialog();
            refreshUnits();
            if (!Strings.isNullOrEmpty(unitIdStr)) {
                final long unitId = Long.parseLong(unitIdStr);
                int elementPosition = 0;
                for (UnitView unit : unitViews) {
                    if (unit.getUnit().getId() == unitId) {
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

    @Override
    public void doImport() {
        try (InputStream inputStream = new ByteArrayInputStream(importData)) {
            setLoaderResult(dataLoaderHandler.loadData(inputStream, unitsDataLoader));
            refreshUnits();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshUnits() {
        unitViews = unitEJB.findAllOrdered().stream().map(UnitView::new).collect(Collectors.toList());
        filteredUnits = unitViews;
        selectedUnits = null;
    }

    /** This method clears all input fields used in the "Add unit" dialog */
    public void prepareAddPopup() {
        name = null;
        description = null;
        symbol = null;
        isUnitAdd = true;
        unitToModify = null;
    }

    /** This method prepares the input fields used in the "Edit unit" dialog */
    public void prepareModifyPopup() {
        Preconditions.checkState(isSingleUnitSelected());
        unitToModify = selectedUnits.get(0);
        name = unitToModify.getName();
        description = unitToModify.getDescription();
        symbol = unitToModify.getSymbol();
        isUnitAdd = false;
    }

    /** Method creates a new unit definition when user presses the "Save" button in the "Add new" dialog  */
    public void onAdd() {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(description);
        Preconditions.checkNotNull(symbol);
        selectedUnits = null;
        final Unit unitToAdd = new Unit(name, symbol, description);
        unitEJB.add(unitToAdd);
        refreshUnits();
    }

    /**
     * Method that saves the modified unit definition when user presses the "Save" button in the
     * "Modify unit" dialog.
     */
    public void onModify() {
        Preconditions.checkNotNull(unitToModify);
        final Unit unitToSave = unitToModify.getUnit();
        unitToSave.setName(name);
        unitToSave.setDescription(description);
        unitToSave.setSymbol(symbol);
        unitEJB.save(unitToSave);

        refreshUnits();
        // reset the input fields
        prepareAddPopup();
    }

    /**
     * @return <code>true</code> if the <code>selectedUnit</code> is used in some {@link Property},
     * <code>false</code> otherwise.
     */
    public boolean isModifiedUnitInUse() {
        return (unitToModify != null) && unitEJB.isUnitUsed(unitToModify.getUnit());
    }

    /** @return <code>true</code> if a single {@link Unit} is selected, <code>false</code> otherwise */
    public boolean isSingleUnitSelected() {
        return (selectedUnits != null) && (selectedUnits.size() == 1);
    }

    /**
     * The method builds a list of units that are already used. If the list is not empty, it is displayed
     * to the user and the user is prevented from deleting them.
     */
    public void checkUnitsForDeletion() {
        Preconditions.checkNotNull(selectedUnits);
        Preconditions.checkState(!selectedUnits.isEmpty());

        usedUnits = Lists.newArrayList();
        for (final UnitView unitToDelete : selectedUnits) {
            if (unitEJB.isUnitUsed(unitToDelete.getUnit())) {
                usedUnits.add(unitToDelete);
            }
        }
    }

    /**
     * Method that deletes all the selected unit definitions. Unit deletion is prevented if one of the units is used in
     * some {@link Property} definition.
     */
    public void onDelete() {
        Preconditions.checkNotNull(selectedUnits);
        Preconditions.checkState(!selectedUnits.isEmpty());
        Preconditions.checkNotNull(usedUnits);
        Preconditions.checkState(usedUnits.isEmpty());

        int deletedUnits = 0;
        for (UnitView unitToDelete : selectedUnits) {
            unitEJB.delete(unitToDelete.getUnit());
            ++deletedUnits;
        }

        selectedUnits = null;
        usedUnits = null;
        refreshUnits();
        Utility.showMessage(FacesMessage.SEVERITY_INFO, Utility.MESSAGE_SUMMARY_SUCCESS,
                "Deleted " + deletedUnits + " units.");
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
        if ((isUnitAdd && existingUnit != null)
                || (!isUnitAdd && (existingUnit != null) && !unitToModify.getUnit().equals(existingUnit))) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, Utility.MESSAGE_SUMMARY_ERROR,
                                                                    "The unit with this name already exists."));
        }
    }

    /**
     * The method creates a new copy of the currently selected {@link Unit}(s)
     */
    public void duplicate() {
        Preconditions.checkState(!Utility.isNullOrEmpty(selectedUnits));

        for (final UnitView unitView : selectedUnits) {
            final Unit unitToCopy = unitView.getUnit();
            final String newUnitName = Utility.findFreeName(unitToCopy.getName(), unitEJB);
            final Unit newUnit = new Unit(newUnitName, unitToCopy.getSymbol(), unitToCopy.getDescription());
            unitEJB.add(newUnit);
        }
        refreshUnits();
    }

    //-------------------------------------------------------------------------------------------
    //                              Getters and setters
    //-------------------------------------------------------------------------------------------
    @Override
    public void setDataLoader() {
        dataLoader = unitsDataLoader;
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

    /** @return the usedUnits */
    public List<UnitView> getUsedUnits() {
        return usedUnits;
    }

    /** @return the selectedUnits */
    public List<UnitView> getSelectedUnits() {
        return selectedUnits;
    }
    /** @param selectedUnits the selectedUnits to set */
    public void setSelectedUnits(List<UnitView> selectedUnits) {
        this.selectedUnits = selectedUnits;
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
        return isUnitAdd;
    }

    @Override
    public ExportSimpleTableDialog getSimpleTableDialog() {
        return simpleTableExporterDialog;
    }

    /** @return the filteredDialogUnits */
    public List<UnitView> getFilteredDialogUnits() {
        return filteredDialogUnits;
    }

    /** @param filteredDialogUnits the filteredDialogUnits to set */
    public void setFilteredDialogUnits(List<UnitView> filteredDialogUnits) {
        this.filteredDialogUnits = filteredDialogUnits;
    }
}
