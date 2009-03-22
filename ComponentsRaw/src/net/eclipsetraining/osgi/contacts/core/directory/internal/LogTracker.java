/*******************************************************************************
 * Copyright (c) 2008 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Heiko Seeberger - initial implementation
 ******************************************************************************/

package net.eclipsetraining.osgi.contacts.core.directory.internal;

import java.io.PrintStream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class LogTracker extends ServiceTracker implements LogService {
	
	public LogTracker(BundleContext context) {
		super(context, LogService.class.getName(), null);
	}

    public void log(final int level, final String message) {
        log(null, level, message, null);
    }

    public void log(final int level, final String message,
                    final Throwable throwable) {
        log(null, level, message, throwable);
    }

    public void log(final ServiceReference ref, final int level,
                    final String message) {
        log(ref, level, message, null);
    }

    public void log(final ServiceReference ref, final int level,
                    final String message, final Throwable throwable) {
        try {
        	LogService logService = (LogService) getService();
        	
        	// No null check, allow NPE to be caught below.
            logService.log(ref, level, message, throwable);
        } catch (final Exception e) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(getLvlString(level))
                  .append(": ")
                  .append(message);
            if (ref != null) buffer.append(" [")
                                   .append(ref)
                                   .append("]");
            if (throwable != null) buffer.append(" -> ")
                                         .append(throwable.getMessage())
                                         .append(" (")
                                         .append(throwable.getClass().getName())
                                         .append(")");
            final PrintStream ps = (level == LogService.LOG_ERROR) ?
                    System.err : System.out;
            ps.println(buffer.toString());
            if (throwable != null) throwable.printStackTrace(ps);
        }
    }

    private static String getLvlString(final int level) {
        String result;
        switch (level) {
        case LogService.LOG_DEBUG:
            result = "DEBUG";
            break;
        case LogService.LOG_INFO:
            result = "INFO";
            break;
        case LogService.LOG_WARNING:
            result = "WARNING";
            break;
        case LogService.LOG_ERROR:
            result = "ERROR";
            break;
        default:
            result = "UNKNOWN";
        }
        return result;
    }
}
