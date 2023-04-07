/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author prabash.dharshanapri
 */
public class LogUtil
{
    /**
     * log info messages
     * @param classOb class object
     * @param message message to be logged
     */
    public static void logInfoMessage(Object classOb, String message)
    {
        logInfoMessage(classOb.getClass().getName(), message);
    }
    
    
    /**
     * log info messages
     * @param className name of the class
     * @param message message to be logged
     */
    public static void logInfoMessage(String className, String message)
    {
        Logger.getLogger(className).log(Level.INFO, message);
    }
    
    /**
     * log error messages
     * @param classOb class object
     * @param message message to be logged
     * @param ex exception
     */
    public static void logSevereErrorMessage(Object classOb, String message, Exception ex)
    {
        logSevereErrorMessage(classOb.getClass().getName(), message, ex);
    }
    
    /**
     * log error messages
     * @param className name of the class
     * @param message message to be logged
     * @param ex exception
     */
    public static void logSevereErrorMessage(String className, String message, Exception ex)
    {
        Logger.getLogger(className).log(Level.SEVERE, message, ex);
    }
    
}
