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
import org.openepics.discs.conf.ui.lazymodels.CCDBLazyModel;
import org.openepics.discs.conf.ui.lazymodels.UnitLazyModel;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.util.Utility;
import org.openepics.discs.conf.views.UnitView;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

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

    @Inject private UnitEJB unitEJB;
    @Inject private DataLoaderHandler dataLoaderHandler;
    @Inject @UnitsLoader private DataLoader unitsDataLoader;

    private CCDBLazyModel<UnitView> lazyModel;

    private List<UnitView> filteredUnits;
    private List<UnitView> selectedUnits;
    private List<UnitView> usedUnits;
    private List<UnitView> filteredDialogUnits;

    private UnitView dialogUnit;

    // * * * * * * * Add/modify dialog fields * * * * * * *

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
            exportTable.addHeaderRow("Operation", "Name", "Description", "Symbol");
        }

        @Override
        protected void addData(ExportTable exportTable) {
            final List<UnitView> exportData = lazyModel.load(0, Integer.MAX_VALUE,
                    lazyModel.getSortField(), lazyModel.getSortOrder(), lazyModel.getFilters());
            for (final UnitView unit : exportData) {
                exportTable.addDataRow(DataLoader.CMD_UPDATE,  unit.getName(), unit.getDescription(), unit.getSymbol());
            }
        }

        @Override
        protected String getExcelTemplatePath() {
            return "/resources/templates/ccdb_units.xlsx";
        }

        @Override
        protected int getExcelDataStartRow() {
            return 9;
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
                final Unit unit = unitEJB.findById(unitId);
                if (unit != null) {
                    // XXX getNamedPosition() might not be returning correct position
                    long elementPosition = unitEJB.getNamedPosition(unit.getName());
                    RequestContext.getCurrentInstance().execute("selectEntityInTable(" + elementPosition
                            + ", 'unitsTableVar');");
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
        lazyModel = new UnitLazyModel(unitEJB);
        selectedUnits = null;
    }

    /** This method clears all input fields used in the "Add unit" dialog */
    public void prepareAddPopup() {
        dialogUnit = new UnitView();
    }

    /** This method prepares the input fields used in the "Edit unit" dialog */
    public void prepareModifyPopup() {
        Preconditions.checkState(isSingleUnitSelected());
        dialogUnit = selectedUnits.get(0);
        List<Property> usedBy = unitEJB.findProperties(dialogUnit.getUnit(), 2);
        if (!usedBy.isEmpty()) {
            dialogUnit.setUsedBy(usedBy.get(0).getName() + (usedBy.size() > 1 ? ", ..." : ""));
        }
    }

    /** Method creates a new unit definition when user presses the "Save" button in the "Add new" dialog */
    public void onAdd() {
        try {
            Preconditions.checkNotNull(dialogUnit);
            final Unit unitToAdd = dialogUnit.getUnit();
            unitEJB.add(unitToAdd);
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                                                                "Unit has been successfully created.");
        } finally {
            selectedUnits = null;
            dialogUnit = null;
            refreshUnits();
        }
    }

    /**
     * Method that saves the modified unit definition when user presses the "Save" button in the "Modify unit" dialog.
     */
    public void onModify() {
        try {
            Preconditions.checkNotNull(dialogUnit);
            final Unit unitToSave = dialogUnit.getUnit();
            unitEJB.save(unitToSave);
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                                                                "Unit has been successfully modified.");
        } finally {
            dialogUnit = null;
            refreshUnits();
        }
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
            List<Property> properties = unitEJB.findProperties(unitToDelete.getUnit(), 2);
            if (!properties.isEmpty()) {
                unitToDelete.setUsedBy(properties.get(0).getName()+(properties.size()>1 ? ", ..." : ""));
                usedUnits.add(unitToDelete);
            }
        }
    }

    /**
     * Method that deletes all the selected unit definitions. Unit deletion is prevented if one of the units is used in
     * some {@link Property} definition.
     */
    public void onDelete() {
        try {
            Preconditions.checkNotNull(selectedUnits);
            Preconditions.checkState(!selectedUnits.isEmpty());
            Preconditions.checkNotNull(usedUnits);
            Preconditions.checkState(usedUnits.isEmpty());

            int deletedUnits = 0;
            for (UnitView unitToDelete : selectedUnits) {
                unitEJB.delete(unitToDelete.getUnit());
                ++deletedUnits;
            }
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                                                                "Deleted " + deletedUnits + " units.");
        } finally {
            selectedUnits = null;
            usedUnits = null;
            refreshUnits();
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
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, UiUtility.MESSAGE_SUMMARY_ERROR,
                                                                    "Please enter a name"));
        }

        final String unitName = value.toString();

        if (dialogUnit.isUnitAdd() || !unitName.equals(dialogUnit.getName())) {
            final Unit existingUnit = unitEJB.findByName(unitName);
            if (existingUnit != null) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                    UiUtility.MESSAGE_SUMMARY_ERROR,
                                                                    "The unit with this name already exists."));
            }
        }
    }

    /** The method creates a new copy of the currently selected {@link Unit}(s) */
    public void duplicate() {
        try {
            Preconditions.checkState(!Utility.isNullOrEmpty(selectedUnits));

            final int duplicated = unitEJB.duplicate(selectedUnits.stream().map(UnitView::getUnit).
                                        collect(Collectors.toList()));
            UiUtility.showMessage(FacesMessage.SEVERITY_INFO, UiUtility.MESSAGE_SUMMARY_SUCCESS,
                    "Duplicated " + duplicated + " units.");
        } finally {
            refreshUnits();
        }
    }

    //-------------------------------------------------------------------------------------------
    //                              Getters and setters
    //-------------------------------------------------------------------------------------------
    @Override
    public void setDataLoader() {
        dataLoader = unitsDataLoader;
    }

    /** @return a list of all {@link UnitView}s */
    public List<UnitView> getUnitViews() {
        return unitEJB.findAllOrdered().stream().map(UnitView::new).collect(Collectors.toList());
    }

    /** @return the lazy loading data model */
    public LazyDataModel<UnitView> getLazyModel() {
        return lazyModel;
    }

    /** @return <code>true</code> if no data was found, <code>false</code> otherwise */
    public boolean isDataTableEmpty() {
        return lazyModel.isEmpty();
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

    /** @return the dialogUnit */
    public UnitView getDialogUnit() {
        return dialogUnit;
    }
}
