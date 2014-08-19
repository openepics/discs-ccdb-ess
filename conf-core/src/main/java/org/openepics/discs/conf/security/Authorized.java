package org.openepics.discs.conf.security;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used to define the use of interceptor for authorization checking.
 *
 * @author Andraz Pozar <andraz.pozar@cosylab.com>
 *
 */
@InterceptorBinding
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface Authorized {

}
