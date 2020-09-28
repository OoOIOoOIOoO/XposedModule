package com.ooo.xposedmodule.util;

import android.util.Log;

import com.ooo.xposedmodule.HookPkgNames;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class XPLog {
    protected static int iLogLevel = 2;
    /*
     * For filtering app specific output
     */
    private static final String TAG = HookPkgNames.TAG;

    public static int println(int priority, String tag, String msg) {
        int iPrintln = 0;
        if (priority > Log.ASSERT) {
            priority = Log.ASSERT;
        }
        if (iLogLevel <= priority) { //todo 循环打印
            String buf = null;
            int start = 0, end = 0, nsize = 4000, len = msg.length();
            while (start < len) {
                end = (start + nsize) < len ? start + nsize : len;
                buf = msg.substring(start, end);
                start += nsize;
                iPrintln = Log.println(priority, tag, buf);
            }
        }
        return iPrintln;
    }

    /**
     * @param obj   the object to log
     * @param cause The exception which caused this error, may not be null
     */
    public static void e(final Object obj, final Throwable cause) {
        println(Log.ERROR, getTrace() + String.valueOf(obj));
        println(Log.ERROR, getThrowableTrace(cause));
        cause.printStackTrace();
    }

    public static void e(Throwable ex) {
        println(Log.ERROR, getTrace() + getExceptionAllinformation(ex));
//        ex.printStackTrace();
    }

    public static void w(Throwable ex) {
        println(Log.WARN, getTrace() + getExceptionAllinformation(ex));
    }
    public static void e(final Object obj) {
        println(Log.ERROR, getTrace() + String.valueOf(obj));
    }

    public static void w(final Object obj, final Throwable cause) {
        println(Log.WARN, getTrace() + String.valueOf(obj));
        println(Log.WARN, getThrowableTrace(cause));
    }

    public static void w(final Object obj) {
        println(Log.WARN, getTrace() + String.valueOf(obj));
    }

    public static void i(final Object obj) {
        println(Log.INFO, getTrace() + String.valueOf(obj));
    }

    public static void d(final Object obj) {
        println(Log.DEBUG, getTrace() + String.valueOf(obj));
    }

    public static void p2(int priority, String tag, String format, Object...args) {
        try {
            if (iLogLevel <= priority) {
                String output = String.format(format, args);
                println(priority, tag + output);
            }
        }
        catch (Exception e) {

        }
    }

    public static void ww(String format, Object...args) {
        p2(Log.WARN, getTrace(), format, args);
    }

    public static void ii(String format, Object...args) {
        p2(Log.INFO, getTrace(), format, args);
    }

    public static void dd(String format, Object...args) {
        p2(Log.DEBUG, getTrace(), format, args);
    }

    public static void vv(String format, Object...args) {
        p2(Log.VERBOSE, getTrace(), format, args);
    }

    public static void v(final Object obj) {
        println(Log.VERBOSE, getTrace() + String.valueOf(obj));
    }

    public static int println(int prioty, String msg) {
        return println(prioty, TAG, msg);
    }


    private static String getThrowableTrace(final Throwable thr) {
        StringWriter b = new StringWriter();
        thr.printStackTrace(new PrintWriter(b));
        return b.toString();
    }

    private static String getExceptionAllinformation(Throwable ex) {
        String sOut = "";
        StackTraceElement[] trace = ex.getStackTrace();
        for (StackTraceElement s : trace) {
            sOut += "\tat " + s + "\r\n";
        }
        return sOut;
    }

    private static String getTrace() {
        return "";
/*

        int depth = 2;
        Throwable t = new Throwable();
        StackTraceElement[] elements = t.getStackTrace();
        String callerMethodName = elements[depth].getMethodName();
        String callerClassPath = elements[depth].getClassName();
        int lineNo = elements[depth].getLineNumber();
        int i = callerClassPath.lastIndexOf('.');
        String callerClassName = callerClassPath.substring(i + 1);
        return callerClassName + ": " + callerMethodName + "() ["
                + lineNo + "] - ";

*/
    }

    /**
     * Prints the stack trace to mubaloo log and standard log
     *
     * @param e the exception to log
     */
    public static void handleException(final Throwable e) {
        XPLog.e(e.toString());
        e.printStackTrace();
    }

    private XPLog() {
    }
    public static void printStack(String msg) {
        Log.e(TAG, msg + "\r\n" + Log.getStackTraceString(new Throwable()));
    }
}
