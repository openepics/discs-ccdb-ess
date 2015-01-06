package org.openepics.discs.conf.ejb;

import java.util.List;

import org.openepics.discs.conf.ent.PropertyValue;

public interface EntityWithProperties {
    <T extends PropertyValue> List<T> getEntityPropertyList();
}
