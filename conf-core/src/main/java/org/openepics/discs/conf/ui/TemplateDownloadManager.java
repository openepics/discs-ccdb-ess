package org.openepics.discs.conf.ui;

import java.io.InputStream;
import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
*
* @author Miha Vitorovič <miha.vitorovic@cosylab.com>
*/
@Named
@ViewScoped
public class TemplateDownloadManager implements Serializable {
    private static final long serialVersionUID = -4473174196934847897L;

    private static final String RESOURCE_FOLDER_PATH = "/WEB-INF/classes/excel-templates/";

    // TODO remove after merge
    private static final String MIME_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final String UNITS_TEMPLATE = "ccdb_units.xlsx";
    private static final String PROPERTIES_TEMPLATE = "ccdb_properties.xlsx";

    public StreamedContent getUnitsTemplate() {
        InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE_FOLDER_PATH + UNITS_TEMPLATE);
        return new DefaultStreamedContent(is, MIME_TYPE_EXCEL, UNITS_TEMPLATE);
    }

    public StreamedContent getPropertiesTemplate() {
        InputStream is = getClass().getClassLoader().getResourceAsStream(RESOURCE_FOLDER_PATH + PROPERTIES_TEMPLATE);
        return new DefaultStreamedContent(is, MIME_TYPE_EXCEL, PROPERTIES_TEMPLATE);
    }
}
