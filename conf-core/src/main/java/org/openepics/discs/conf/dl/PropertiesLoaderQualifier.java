package org.openepics.discs.conf.dl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.openepics.discs.conf.dl.common.DataLoader;

/**
 * Annotation to specify which implementation of {@link DataLoader} should be injected
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface PropertiesLoaderQualifier {}
