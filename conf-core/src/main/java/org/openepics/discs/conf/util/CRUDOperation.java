package org.openepics.discs.conf.util;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.openepics.discs.conf.ent.EntityType;
import org.openepics.discs.conf.ent.EntityTypeOperation;

/**
 * Custom annotation that describes which of CRUD operations is being performed in the method
 * in service layer
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface CRUDOperation {

    EntityTypeOperation operation();
}
