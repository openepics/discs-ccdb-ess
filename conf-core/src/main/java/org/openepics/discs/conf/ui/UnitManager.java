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
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.openepics.discs.conf.dl.UnitLoaderQualifier;
import org.openepics.discs.conf.dl.common.DataLoader;
import org.openepics.discs.conf.dl.common.DataLoaderResult;
import org.openepics.discs.conf.ejb.UnitEJB;
import org.openepics.discs.conf.ent.Unit;
import org.openepics.discs.conf.ui.common.DataLoaderHandler;
import org.openepics.discs.conf.ui.common.ExcelSingleFileImportUIHandlers;
import org.primefaces.event.FileUploadEvent;

import com.google.common.io.ByteStreams;

/**
 *
 * @author vuppala
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ManagedBean
@ViewScoped
public class UnitManager implements Serializable, ExcelSingleFileImportUIHandlers {
    @Inject transient private UnitEJB unitEJB;
    @Inject transient private DataLoaderHandler dataLoaderHandler;
    @Inject @UnitLoaderQualifier transient private DataLoader unitsDataLoader;

    private List<Unit> units;
    private List<Unit> filteredUnits;
    transient private DataLoaderResult loaderResult;

    private byte[] importData;
    private String importFileName;

    /**
     * Creates a new instance of UnitManager
     */
    public UnitManager() {
    }

    /**
     * @return The list of all user defined physics units in the database
     */
    public List<Unit> getUnits() {
        if (units == null) units = unitEJB.findAll();
        return units;
    }

    /**
     * @return The list of filtered units used by the PrimeFaces filter field.
     */
    public List<Unit> getFilteredUnits() {
        return filteredUnits;
    }

    /**
     * @param filteredUnits The list of filtered units used by the PrimeFaces filter field.
     */
    public void setFilteredUnits(List<Unit> filteredUnits) {
        this.filteredUnits = filteredUnits;
    }

    @Override
    public String getImportFileName() {
        return importFileName;
    }

    @Override
    public void doImport() {
        final InputStream inputStream = new ByteArrayInputStream(importData);
        loaderResult = dataLoaderHandler.loadData(inputStream, unitsDataLoader);
        return;
    }

    @Override
    public void prepareImportPopup() {
        importData = null;
        importFileName = null;
    }

    @Override
    public DataLoaderResult getLoaderResult() {
        return loaderResult;
    }

    @Override
    public void handleImportFileUpload(FileUploadEvent event) {
        try (InputStream inputStream = event.getFile().getInputstream()) {
            this.importData = ByteStreams.toByteArray(inputStream);
            this.importFileName = FilenameUtils.getName(event.getFile().getFileName());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
