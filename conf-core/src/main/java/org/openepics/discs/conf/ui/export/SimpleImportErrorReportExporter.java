package org.openepics.discs.conf.ui.export;

public interface SimpleImportErrorReportExporter {

    /** @return the actual implementation of the simple table exporter for the import errors table */
    public ExportSimpleTableDialog getSimpleErrorTableExportDialog();
}
