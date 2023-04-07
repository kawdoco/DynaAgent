/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.datamodels;

import dyno.scheduler.utils.DateTimeUtil;
import dyno.scheduler.utils.LogUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;

/**
 *
 * @author Prabash
 */
public class PartUnavailabilityModel extends DataModel
{

    // <editor-fold defaultstate="collapsed" desc="properties">
    
    private int id;
    private String partNo; 
    private DateTime unavailableFromDate;
    private DateTime unavailableFromTime;
    private DateTime unavailableToDate;
    private DateTime unavailableToTime;
    
    // </editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="constructors">

    public PartUnavailabilityModel(int id, String partNo, DateTime unavailableFromDate, DateTime unavailableFromTime, DateTime unavailableToDate, DateTime unavailableToTime)
    {
        this.id = id;
        this.partNo = partNo;
        this.unavailableFromDate = unavailableFromDate;
        this.unavailableFromTime = unavailableFromTime;
        this.unavailableToDate = unavailableToDate;
        this.unavailableToTime = unavailableToTime;
    }

    public PartUnavailabilityModel()
    {
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getters/setters">

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getPartNo()
    {
        return partNo;
    }

    public void setPartNo(String partNo)
    {
        this.partNo = partNo;
    }

    public DateTime getUnavailableFromDate()
    {
        return unavailableFromDate;
    }

    public void setUnavailableFromDate(DateTime unavailableFromDate)
    {
        this.unavailableFromDate = unavailableFromDate;
    }

    public DateTime getUnavailableFromTime()
    {
        return unavailableFromTime;
    }

    public void setUnavailableFromTime(DateTime unavailableFromTime)
    {
        this.unavailableFromTime = unavailableFromTime;
    }

    public DateTime getUnavailableToDate()
    {
        return unavailableToDate;
    }

    public void setUnavailableToDate(DateTime unavailableToDate)
    {
        this.unavailableToDate = unavailableToDate;
    }

    public DateTime getUnavailableToTime()
    {
        return unavailableToTime;
    }

    public void setUnavailableToTime(DateTime unavailableToTime)
    {
        this.unavailableToTime = unavailableToTime;
    }
    
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="overriden methods"> 
    
    @Override
    public PartUnavailabilityModel getModelObject(Object row)
    {
        if (row instanceof Row)
        {
        }
        else
        {
            ResultSet resultSetRow = (ResultSet) row;
            int i = 0;
            try
            {
                this.setId(resultSetRow.getInt(++i));
                this.setPartNo(resultSetRow.getString(++i));
                this.setUnavailableFromDate(resultSetRow.getDate(++i) == null ? null : DateTimeUtil.convertSqlDatetoDateTime(resultSetRow.getDate(i)));
                this.setUnavailableFromTime(resultSetRow.getTime(++i) == null ? null : DateTimeUtil.convertSqlTimetoDateTime(resultSetRow.getTime(i)));
                this.setUnavailableToDate(resultSetRow.getDate(++i) == null ? null : DateTimeUtil.convertSqlDatetoDateTime(resultSetRow.getDate(i)));
                this.setUnavailableToTime(resultSetRow.getTime(++i) == null ? null : DateTimeUtil.convertSqlTimetoDateTime(resultSetRow.getTime(i)));
                
            } catch (SQLException ex)
            {
                LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            }
            
        }
        return this;
    }

    @Override
    public String getPrimaryKey()
    {
        return String.valueOf(this.getId());
    }

    @Override
    public String getClassName()
    {
        return PartUnavailabilityModel.class.getName();
    }
    
    //</editor-fold>
}
