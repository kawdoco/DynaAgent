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
public class WorkCenterInterruptionsModel extends DataModel
{
    // <editor-fold defaultstate="collapsed" desc="properties">
    
    private int id;
    private String workCenterNo; 
    private DateTime interruptionFromDate;
    private DateTime interruptionFromTime;
    private DateTime interruptionToDate;
    private DateTime interruptionToTime;
    
    // </editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="constructors">

    public WorkCenterInterruptionsModel(int id, String workCenterNo, DateTime unavailableFromDate, DateTime unavailableFromTime, DateTime unavailableToDate, DateTime unavailableToTime)
    {
        this.id = id;
        this.workCenterNo = workCenterNo;
        this.interruptionFromDate = unavailableFromDate;
        this.interruptionFromTime = unavailableFromTime;
        this.interruptionToDate = unavailableToDate;
        this.interruptionToTime = unavailableToTime;
    }

    public WorkCenterInterruptionsModel()
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

    public String getWorkCenterNo()
    {
        return workCenterNo;
    }

    public void setWorkCenterNo(String workCenterNo)
    {
        this.workCenterNo = workCenterNo;
    }

    public DateTime getInterruptionFromDate()
    {
        return interruptionFromDate;
    }

    public void setInterruptionFromDate(DateTime interruptionFromDate)
    {
        this.interruptionFromDate = interruptionFromDate;
    }

    public DateTime getInterruptionFromTime()
    {
        return interruptionFromTime;
    }

    public void setInterruptionFromTime(DateTime interruptionFromTime)
    {
        this.interruptionFromTime = interruptionFromTime;
    }

    public DateTime getInterruptionToDate()
    {
        return interruptionToDate;
    }

    public void setInterruptionToDate(DateTime interruptionToDate)
    {
        this.interruptionToDate = interruptionToDate;
    }

    public DateTime getInterruptionToTime()
    {
        return interruptionToTime;
    }

    public void setInterruptionToTime(DateTime interruptionToTime)
    {
        this.interruptionToTime = interruptionToTime;
    }
    
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="overriden methods"> 
    
    @Override
    public WorkCenterInterruptionsModel getModelObject(Object row)
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
                this.setWorkCenterNo(resultSetRow.getString(++i));
                this.setInterruptionFromDate(resultSetRow.getDate(++i) == null ? null : DateTimeUtil.convertSqlDatetoDateTime(resultSetRow.getDate(i)));
                this.setInterruptionFromTime(resultSetRow.getTime(++i) == null ? null : DateTimeUtil.convertSqlTimetoDateTime(resultSetRow.getTime(i)));
                this.setInterruptionToDate(resultSetRow.getDate(++i) == null ? null : DateTimeUtil.convertSqlDatetoDateTime(resultSetRow.getDate(i)));
                this.setInterruptionToTime(resultSetRow.getTime(++i) == null ? null : DateTimeUtil.convertSqlTimetoDateTime(resultSetRow.getTime(i)));
                
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
        return WorkCenterInterruptionsModel.class.getName();
    }
    
    //</editor-fold>

}
