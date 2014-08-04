package org.openepics.discs.conf.dl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.openepics.discs.conf.dl.common.DataLoader;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Annotation to specify which implementation of {@link DataLoader} should be injected
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface PropertiesLoaderQualifier {}
