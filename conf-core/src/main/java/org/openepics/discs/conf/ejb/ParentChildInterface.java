package org.openepics.discs.conf.ejb;

import java.util.List;

public interface ParentChildInterface<T, S> {    
    public List<S> getChildCollection(T type);
    public T getParentFromChild(S child);    
}
