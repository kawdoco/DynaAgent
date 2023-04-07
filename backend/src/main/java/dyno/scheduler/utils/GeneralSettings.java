/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.utils;

import dyno.scheduler.data.DataEnums;
import dyno.scheduler.data.DataEnums.CapacityType;
import dyno.scheduler.data.DataEnums.DataAccessMethod;

/**
 *
 * @author Prabash
 */
public class GeneralSettings
{
    // <editor-fold desc="properties">
    
    private static final String HOST_NAME = "127.0.0.1";
    private static final String EXCEL_FILE = "data.xlsx";
    private static final DataAccessMethod DATA_ACCESS_METHOD = DataEnums.DataAccessMethod.Database;
    private static final CapacityType CAPACITY_TYPE = CapacityType.FiniteCapacity;
    
    private static final String STR_TIME_BLOCK_NAME = "TimeBlockName";
    private static final String STR_DAYS_ADDED = "DaysAdded";

    // </editor-fold>
    
    // <editor-fold desc="getters/setters">
    
    /**
     * @return return the host name for the application instance
     */
    public static String getHostName()
    {
        return HOST_NAME;
    }
    
    /**
     * @return the DATA_ACCESS_METHOD for the application instance
     */
    public static DataAccessMethod getDataAccessMethod()
    {
        return DATA_ACCESS_METHOD;
    }
    
    /**
     * @return the EXCEL_FILE path for the application instance
     */
    public static String getDefaultExcelFile()
    {
        return EXCEL_FILE;
    }
    
    /**
     * @return the CAPACITY_TYPE for the application instance
     */
    public static CapacityType getCapacityType()
    {
        return CAPACITY_TYPE;
    }
    
    // </editor-fold>

    /**
     * @return the STR_TIME_BLOCK_NAME
     */
    public static String getStrTimeBlockName()
    {
        return STR_TIME_BLOCK_NAME;
    }

    /**
     * @return the STR_DAYS_ADDED
     */
    public static String getStrDaysAdded()
    {
        return STR_DAYS_ADDED;
    }


}
