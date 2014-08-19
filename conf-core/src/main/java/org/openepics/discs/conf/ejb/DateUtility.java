package org.openepics.discs.conf.ejb;

import java.util.Date;

import org.openepics.discs.conf.ent.ConfigurationEntity;

public class DateUtility {
    public static void setModifiedAt(ConfigurationEntity parent, ConfigurationEntity child) {
        final Date now = new Date();
        
        parent.setModifiedAt(now);
        child.setModifiedAt(now);
    }
}
