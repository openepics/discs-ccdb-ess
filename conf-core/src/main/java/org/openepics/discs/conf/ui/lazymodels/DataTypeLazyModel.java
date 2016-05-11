package org.openepics.discs.conf.ui.lazymodels;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.openepics.discs.conf.ejb.DataTypeEJB;
import org.openepics.discs.conf.ent.DataType;
import org.openepics.discs.conf.ent.fields.EnumFields;
import org.openepics.discs.conf.ui.util.UiUtility;
import org.openepics.discs.conf.views.UserEnumerationView;
import org.primefaces.model.SortOrder;

public class DataTypeLazyModel extends CCDBLazyModel<UserEnumerationView> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DataTypeLazyModel.class.getCanonicalName());

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String DISPLAY_DEFINITION = "displayDefinition";

    private final DataTypeEJB dataTypeEJB;
    private List<String> builtInDataTypeNames;

    public DataTypeLazyModel(final DataTypeEJB dataTypeEJB, final List<String> builtInDataTypeNames) {
        super(dataTypeEJB);
        this.dataTypeEJB = dataTypeEJB;
        this.builtInDataTypeNames = builtInDataTypeNames;
    }

    @Override
    public List<UserEnumerationView> load(int first, int pageSize, String sortField,
            SortOrder sortOrder, Map<String, Object> filters) {
        LOGGER.log(Level.FINEST, "---->pageSize: " + pageSize);
        LOGGER.log(Level.FINEST, "---->first: " + first);

        for (final String filterKey : filters.keySet()) {
            LOGGER.log(Level.FINER, "filter[" + filterKey + "] = " + filters.get(filterKey).toString());
        }

        setLatestLoadData(sortField, sortOrder, filters);

        final String name = filters.containsKey(NAME) ? filters.get(NAME).toString() : null;
        final String description = filters.containsKey(DESCRIPTION) ? filters.get(DESCRIPTION).toString() : null;
        final String definition = filters.containsKey(DISPLAY_DEFINITION)
                                                ? filters.get(DISPLAY_DEFINITION).toString()
                                                : null;

        // sort by name is the default
        final List<DataType> results = dataTypeEJB.findLazy(first, pageSize,
                selectSortField(sortField), (sortField == null) ? org.openepics.discs.conf.util.SortOrder.ASCENDING :
                UiUtility.translateToCCDBSortOrder(sortOrder),
                name, description, definition);

        final List<UserEnumerationView> transformedResults = results == null
                                                ? null
                                                : results.stream().
                                                    filter(dt -> !builtInDataTypeNames.contains(dt.getName())).
                                                    map(UserEnumerationView::new).collect(Collectors.toList());
        setEmpty(first, transformedResults);

        return transformedResults;
    }

    @Override
    public Object getRowKey(UserEnumerationView object) {
        return object.getEnumeration().getId();
    }

    @Override
    public UserEnumerationView getRowData(String rowKey) {
        final DataType foundEnum = dataTypeEJB.findById(Long.parseLong(rowKey));
        return foundEnum != null ? new UserEnumerationView(foundEnum) : null;
    }

    private EnumFields selectSortField(final String sortField) {
        if (sortField == null) return EnumFields.NAME;

        switch (sortField) {
        case NAME:
            return EnumFields.NAME;
        case DESCRIPTION:
            return EnumFields.DESCRIPTION;
        case DISPLAY_DEFINITION:
            return EnumFields.DEFINITION;
        default:
            return null;
        }
    }
}
