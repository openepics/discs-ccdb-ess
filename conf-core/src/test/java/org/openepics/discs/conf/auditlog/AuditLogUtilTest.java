package org.openepics.discs.conf.auditlog;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openepics.discs.conf.ent.Unit;

import com.google.common.collect.Sets;

public class AuditLogUtilTest {

    @Test
    public void testAuditLogUtil() {
        
        Unit unit = new Unit("Ampre", "Current", "A", "BlahBlha", "Miki");
        
        
        // Serialize all 
        String all = (new AuditLogUtil((Unit) unit)).serialize();
        System.out.println("all:" + all);
        
        // Remove unneeded
        String removedValidNames = (new AuditLogUtil((Unit) unit)).
                removeTopProperties(Sets.newHashSet(
                "id", "modifiedAt", "modifiedBy", "version", "name")).
        serialize();
        System.out.println("removedValidNames:" + removedValidNames);
        
        
        // Remove invalid (should silently fail)
        String removedInvalidNames = (new AuditLogUtil((Unit) unit)).
                removeTopProperties(Sets.newHashSet(
                "idasdsadsad")).
        serialize();
        System.out.println("removedInvalidNames:" + removedInvalidNames);   
        
        // Replace item
        String replacedNames = (new AuditLogUtil((Unit) unit)).
                removeTopProperties((Sets.newHashSet("id"))).addStringProperty("id", "42").serialize();                  
        System.out.println("replacedNames" + replacedNames);
    }

}
