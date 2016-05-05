package org.openepics.discs.conf.ui.lazymodels;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openepics.discs.conf.ejb.AuditRecordEJB;
import org.openepics.discs.conf.ent.AuditRecord;
import org.openepics.discs.conf.ent.fields.AuditRecordFields;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class AuditLazyModel extends LazyDataModel<AuditRecord> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuditLazyModel.class.getCanonicalName());

    private static final String LOG_TIME_FORMATTED = "logTimeFormatted";
    private static final String USER = "user";
    private static final String OPER = "oper";
    private static final String ENTITY_KEY = "entityKey";
    private static final String ENTITY_TYPE = "entityType";
    private static final String ENTITY_ID = "entityId";
    private static final String ENTRY = "entry";

    final private AuditRecordEJB auditRecordEJB;

    public AuditLazyModel(AuditRecordEJB auditRecordEJB) {
        this.auditRecordEJB = auditRecordEJB;
    }

    @Override
    public List<AuditRecord> load(int first, int pageSize, String sortField,
            SortOrder sortOrder, Map<String, Object> filters) {
        LOGGER.log(Level.FINE, "---->pageSize: " + pageSize);
        LOGGER.log(Level.FINE, "---->first: " + first);

        for (final String filterKey : filters.keySet()) {
            LOGGER.log(Level.INFO, "filter[" + filterKey + "]=   " + filters.get(filterKey).toString());
        }

        final List<AuditRecord> results = auditRecordEJB.findLazy(first, getPageSize(),
                selectSortField(sortField), UiUtility.translateToCCDBSortOrder(sortOrder),
                null, null, null, null, null, null, null);

        return results;
    }

    @Override
    public Object getRowKey(AuditRecord object) {
        return object.getId();
    }

    @Override
    public AuditRecord getRowData(String rowKey) {
        return auditRecordEJB.findById(Long.parseLong(rowKey));
    }

    @Override
    public int getRowCount() {
        final long rowCount = auditRecordEJB.getRowCount();
        return rowCount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)rowCount;
    }

    private AuditRecordFields selectSortField(final String sortField) {
        if (sortField == null) return null;

        switch (sortField) {
        case LOG_TIME_FORMATTED:
            return AuditRecordFields.LOG_TIME;
        case USER:
            return AuditRecordFields.USER;
        case OPER:
            return AuditRecordFields.OPER;
        case ENTITY_ID:
            return AuditRecordFields.ENTITY_ID;
        case ENTITY_KEY:
            return AuditRecordFields.ENTITY_KEY;
        case ENTITY_TYPE:
            return AuditRecordFields.ENTITY_TYPE;
        case ENTRY:
            return AuditRecordFields.ENTRY;
        default:
            return null;
        }
    }
}
