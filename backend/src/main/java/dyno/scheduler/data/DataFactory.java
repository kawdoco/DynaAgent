/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.data;

import dyno.scheduler.data.DataEnums.DataAccessMethod;
import dyno.scheduler.utils.GeneralSettings;

/**
 *
 * @author Prabash
 */
public class DataFactory
{
    /**
     * return a new instance of the DataReadManager depending on the current data get method
     * available in the general settings
     * @return DataReadManager instance
     */
    public static DataReadManager getDataReadManagerInstance()
    {
        DataAccessMethod dataGetMethod = GeneralSettings.getDataAccessMethod();
        switch (dataGetMethod)
        {
            case Database:
                return new MySqlReadManager();
            case Excel:
                return new ExcelReadManager();
            default:
                return null;
        }
    }
    
    /**
     * return a new instance of the DataWriteManager depending on the current
     * @return DataWriteManager instance
     */
    public static DataWriteManager getDataWriteManagerInstance()
    {
        DataAccessMethod dataGetMethod = GeneralSettings.getDataAccessMethod();
        switch (dataGetMethod)
        {
            case Database:
                return new MySqlWriterManager();
            case Excel:
                return new ExcelWriteManager();
            default:
                return null;
        }
    }
}
