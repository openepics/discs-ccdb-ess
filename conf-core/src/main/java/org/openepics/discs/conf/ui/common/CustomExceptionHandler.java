/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Controls Configuration Database.
 *
 * Controls Configuration Database is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or any newer
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */

package org.openepics.discs.conf.ui.common;

import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

/**
 * A global JSF exception handler that displays caught exceptions in the UI as popup messages.
 *
 * @author Marko Kolar <marko.kolar@cosylab.com>
 * @author Sunil Sah <sunil.sah@cosylab.com>
 *
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

    private final ExceptionHandler wrapped;
    private static final Logger LOG = Logger.getLogger(CustomExceptionHandler.class.getCanonicalName());

    public CustomExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override public void handle() throws FacesException {
        final Iterator<ExceptionQueuedEvent> iterator = getUnhandledExceptionQueuedEvents().iterator();
        while (iterator.hasNext()) {
            final ExceptionQueuedEvent event = iterator.next();
            final ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

            // Handle UIException case which requires redirect to another page
            final InvocationTargetException ite = getInvocationTargetException(context.getException());
            if ((ite != null) && (ite.getTargetException() instanceof UIException)) {
                final ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                try {
                    // ExternalContext.redirect is the most low-level redirect I could find, and using uri parameters
                    // seems like the most straight-forward way of doing it.
                    ec.redirect(ec.getRequestContextPath() + "/error.xhtml?errorMsg=" +
                            URLEncoder.encode(((UIException)ite.getTargetException()).getMessage(), "UTF-8"));
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Failed to redirect to error page");
                } finally {
                    iterator.remove();
                }
            } else {
                // Handle all other cases where redirect is not needed.
                final Throwable throwable = getExceptionNonframeworkCause(context.getException());
                try {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unexpected error", throwable.getMessage()));
                } finally {
                    iterator.remove();
                }
            }
        }
        wrapped.handle();
    }

    /**
     * Check for leaf {@link InvocationTargetException}.
     *
     * @param exception The top exception
     * @return The invocation target exception or <code>null</code>
     */
    private InvocationTargetException getInvocationTargetException(Throwable exception) {
        Throwable iterated = exception;
        while (iterated!=null && iterated.getCause()!=null) {
            if (iterated instanceof InvocationTargetException)
                return (InvocationTargetException) iterated;

           iterated = iterated.getCause();
        }

        return null;
    }

    /* Returns the nested exception cause that is not Faces or EJB exception, if it exists. */
    private Throwable getExceptionNonframeworkCause(Throwable exception) {
        return (exception instanceof FacesException || exception instanceof EJBException) && (exception.getCause() != null)
               ? getExceptionNonframeworkCause(exception.getCause())
               : exception;
    }

}
