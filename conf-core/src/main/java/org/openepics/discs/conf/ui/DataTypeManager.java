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

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.openepics.discs.conf.ejb.DataTypeEJB;
import org.openepics.discs.conf.ent.DataType;

/**
 *
 * @author vuppala
 * @author Miha Vitorovič <miha.vitorovic@cosylab.com>
 */
@Named
@ViewScoped
public class DataTypeManager implements Serializable {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DataTypeManager.class.getCanonicalName());

    @EJB private DataTypeEJB dataTypeEJB;

    private List<DataType> dataTypes;
    private List<DataType> fileteredDataTypes;
    /**
     * Creates a new instance of DataTypeManager
     */
    public DataTypeManager() {
    }

    /**
     * @return A list of all {@link DataType} entities in the database.
     */
    public List<DataType> getDataTypes() {
        if (dataTypes == null) dataTypes = dataTypeEJB.findAll();
        return dataTypes;
    }

    public List<DataType> getFileteredDataTypes() {
        return fileteredDataTypes;
    }

    public void setFileteredDataTypes(List<DataType> fileteredDataTypes) {
        this.fileteredDataTypes = fileteredDataTypes;
    }

}
