package org.openepics.discs.conf.util;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RUNTIME)
public @interface CRUDAnnotation {
    public enum Operation {
        CREATE, REMOVE, UPDATE, DELETE;
    }

    Operation operation();
}
