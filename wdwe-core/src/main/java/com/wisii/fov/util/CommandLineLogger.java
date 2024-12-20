/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//* $Id: CommandLineLogger.java,v 1.1 2007/04/12 06:41:19 cvsuser Exp $ */

package com.wisii.fov.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is a commons-logging logger for command line use.
 */
public class CommandLineLogger implements Log {
    /** "Trace" level logging. */
    public static final int LOG_LEVEL_TRACE  = 1;
    /** "Debug" level logging. */
    public static final int LOG_LEVEL_DEBUG  = 2;
    /** "Info" level logging. */
    public static final int LOG_LEVEL_INFO   = 3;
    /** "Warn" level logging. */
    public static final int LOG_LEVEL_WARN   = 4;
    /** "Error" level logging. */
    public static final int LOG_LEVEL_ERROR  = 5;
    /** "Fatal" level logging. */
    public static final int LOG_LEVEL_FATAL  = 6;

    private int logLevel;
    private String logName;

    /**
     * Construct the logger with a default log level taken from the LogFactory
     * attribute "level".
     * @param logName the logger name.
     */
    public CommandLineLogger(String logName) {
        this.logName = logName;
        setLogLevel((String) LogFactory.getFactory().getAttribute("level"));
    }

    /**
     * Set a log level for the logger.
     * @param level the log level
     */
    public void setLogLevel(String level) {
        if ("fatal".equals(level)) {
            logLevel = LOG_LEVEL_FATAL;
        } else if ("error".equals(level)) {
            logLevel = LOG_LEVEL_ERROR;
        } else if ("warn".equals(level)) {
            logLevel = LOG_LEVEL_WARN;
        } else if ("info".equals(level)) {
            logLevel = LOG_LEVEL_INFO;
        } else if ("debug".equals(level)) {
            logLevel = LOG_LEVEL_DEBUG;
        } else if ("trace".equals(level)) {
            logLevel = LOG_LEVEL_TRACE;
        } else {
            logLevel = LOG_LEVEL_INFO;
        }
    }

    /**
     * @see org.apache.commons.logging.Log#isTraceEnabled()
     */
    public final boolean isTraceEnabled() {
        return logLevel <= LOG_LEVEL_TRACE;
    }

    /**
     * @see org.apache.commons.logging.Log#isDebugEnabled()
     */
    public final boolean isDebugEnabled() {
        return logLevel <= LOG_LEVEL_DEBUG;
    }

    /**
     * @see org.apache.commons.logging.Log#isInfoEnabled()
     */
    public final boolean isInfoEnabled() {
        return logLevel <= LOG_LEVEL_INFO;
    }

    /**
     * @see org.apache.commons.logging.Log#isWarnEnabled()
     */
    public final boolean isWarnEnabled() {
        return logLevel <= LOG_LEVEL_WARN;
    }

    /**
     * @see org.apache.commons.logging.Log#isErrorEnabled()
     */
    public final boolean isErrorEnabled() {
        return logLevel <= LOG_LEVEL_ERROR;
    }

    /**
     * @see org.apache.commons.logging.Log#isFatalEnabled()
     */
    public final boolean isFatalEnabled() {
        return logLevel <= LOG_LEVEL_FATAL;
    }

    /**
     * @see org.apache.commons.logging.Log#trace(java.lang.Object)
     */
    public final void trace(Object message) {
        if (isTraceEnabled()) {
            log(LOG_LEVEL_TRACE, message, null);
        }
    }

    /**
     * @see org.apache.commons.logging.Log#trace(java.lang.Object, java.lang.Throwable)
     */
    public final void trace(Object message, Throwable t) {
        if (isTraceEnabled()) {
            log(LOG_LEVEL_TRACE, message, t);
        }
    }

    /**
     * @see org.apache.commons.logging.Log#debug(java.lang.Object)
     */
    public final void debug(Object message) {
        if (isDebugEnabled()) {
            log(LOG_LEVEL_DEBUG, message, null);
        }
    }

    /**
     * @see org.apache.commons.logging.Log#debug(java.lang.Object, java.lang.Throwable)
     */
    public final void debug(Object message, Throwable t) {
        if (isDebugEnabled()) {
            log(LOG_LEVEL_DEBUG, message, t);
        }
    }

    /**
     * @see org.apache.commons.logging.Log#info(java.lang.Object)
     */
    public final void info(Object message) {
        if (isInfoEnabled()) {
            log(LOG_LEVEL_INFO, message, null);
        }
    }

    /**
     * @see org.apache.commons.logging.Log#info(java.lang.Object, java.lang.Throwable)
     */
    public final void info(Object message, Throwable t) {
        if (isInfoEnabled()) {
            log(LOG_LEVEL_INFO, message, t);
        }
    }

    /**
     * @see org.apache.commons.logging.Log#warn(java.lang.Object)
     */
    public final void warn(Object message) {
        if (isWarnEnabled()) {
            log(LOG_LEVEL_WARN, message, null);
        }
    }

    /**
     * @see org.apache.commons.logging.Log#warn(java.lang.Object, java.lang.Throwable)
     */
    public final void warn(Object message, Throwable t) {
        if (isWarnEnabled()) {
            log(LOG_LEVEL_WARN, message, t);
        }
    }

    /**
     * @see org.apache.commons.logging.Log#error(java.lang.Object)
     */
    public final void error(Object message) {
        if (isErrorEnabled()) {
            log(LOG_LEVEL_ERROR, message, null);
        }
    }

    /**
     * @see org.apache.commons.logging.Log#error(java.lang.Object, java.lang.Throwable)
     */
    public final void error(Object message, Throwable t) {
        if (isErrorEnabled()) {
            log(LOG_LEVEL_ERROR, message, t);
        }
    }

    /**
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object)
     */
    public final void fatal(Object message) {
        if (isFatalEnabled()) {
            log(LOG_LEVEL_FATAL, message, null);
        }
    }

    /**
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object, java.lang.Throwable)
     */
    public final void fatal(Object message, Throwable t) {
        if (isFatalEnabled()) {
            log(LOG_LEVEL_FATAL, message, t);
        }
    }

    /**
     * Do the actual logging.
     * This method assembles the message and prints it to
     * and then calls <code>write()</code> to cause it to be written.</p>
     *
     * @param type One of the LOG_LEVEL_XXX constants defining the log level
     * @param message The message itself (typically a String)
     * @param t The exception whose stack trace should be logged
     */
    protected void log(int type, Object message, Throwable t) {
        StringBuffer buf = new StringBuffer();
        // Append the message
        buf.append(String.valueOf(message));
        if (t != null) {
            buf.append("\n");
            // Append a stack trace or just the stack trace message.
            if (!isDebugEnabled()) {
                buf.append(t.toString());
                buf.append("\n");
            } else {
                java.io.StringWriter sw = new java.io.StringWriter(1024);
                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                t.printStackTrace(pw);
                pw.close();
                buf.append(sw.toString());
            }
        }

        // Print to the appropriate destination
        if (type >= LOG_LEVEL_WARN) {
            System.err.println(buf);
        } else {
            System.out.println(buf);
        }

    }
}
