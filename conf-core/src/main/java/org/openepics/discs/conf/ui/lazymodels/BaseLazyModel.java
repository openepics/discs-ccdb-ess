package org.openepics.discs.conf.ui.lazymodels;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

/**
 * This class implementes the workaround for the PrimeFaces lazy loading bug in combination with live scrolling.
 *
 * @author <a href="mailto:miha.vitorovic@cosylab.com">Miha Vitorovič</a>
 *
 * @param <T>
 */
public class BaseLazyModel<T> extends LazyDataModel<T>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(BaseLazyModel.class.getCanonicalName());

    private int pageSizeWorkaround;

    public BaseLazyModel() {
        pageSizeWorkaround = -1;
    }

    @Override
    public List<T> load(int first, int pageSize,
            List<SortMeta> multiSortMeta, Map<String, Object> filters) {
        LOGGER.log(Level.INFO, "!!load   2 !!");

        setPageSize(pageSize);
        return super.load(first, getPageSize(), multiSortMeta, filters);
    }

    @Override
    public void setPageSize(int pageSize) {
        if ((pageSizeWorkaround == -1) && (pageSize > 0)) {
            pageSizeWorkaround = pageSize;
        }

        if (pageSizeWorkaround != -1) {
            super.setPageSize(pageSizeWorkaround);
        } else {
            super.setPageSize(pageSize);
        }
    }

    @Override
    public int getPageSize() {
        return super.getPageSize();
    }

    @Override
    public void setRowCount(int rowCount) {
        super.setRowCount(rowCount);
    }
}
