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
import org.joda.time.DateTime;

/**
 *
 * @author Prabash
 */
public class OperationScheduleTimeBlocksDataModel extends DataModel
{
    // <editor-fold defaultstate="collapsed" desc="properties"> 
    
    private int operationId;
    private DateTime operationDate;
    private DateTime timeBlockStartTime;
    private String timeBlockName;
    private String workCenterNo;
    
    // </editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getters/setters">

    public int getOperationId()
    {
        return operationId;
    }

    public void setOperationId(int operationId)
    {
        this.operationId = operationId;
    }

    public DateTime getOperationDate()
    {
        return operationDate;
    }

    public void setOperationDate(DateTime operationDate)
    {
        this.operationDate = operationDate;
    }

    public DateTime getTimeBlockStartTime()
    {
        return timeBlockStartTime;
    }

    public void setTimeBlockStartTime(DateTime timeBlockStartTime)
    {
        this.timeBlockStartTime = timeBlockStartTime;
    }

    public String getTimeBlockName()
    {
        return timeBlockName;
    }

    public void setTimeBlockName(String timeBlockName)
    {
        this.timeBlockName = timeBlockName;
    }

    public String getWorkCenterNo()
    {
        return workCenterNo;
    }

    public void setWorkCenterNo(String workCenterNo)
    {
        this.workCenterNo = workCenterNo;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="overriden methods"> 

    @Override
    public OperationScheduleTimeBlocksDataModel getModelObject(Object row)
    {
        if (row instanceof ResultSet)
        {
            ResultSet resultSetRow = (ResultSet) row;
            int i = 0;
            try
            {
                this.setWorkCenterNo(resultSetRow.getString(++i));
                this.setOperationDate(resultSetRow.getDate(++i) == null ? null : DateTimeUtil.convertSqlDatetoDateTime(resultSetRow.getDate(i)));
                this.setTimeBlockStartTime(resultSetRow.getTime(++i) == null ? null : DateTimeUtil.convertSqlTimetoDateTime(resultSetRow.getTime(i)));
                this.setTimeBlockName(resultSetRow.getString(++i));
                this.setOperationId(resultSetRow.getInt(++i));
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
        return DateTimeUtil.concatenateDateTime(getOperationDate(), getTimeBlockStartTime()).toString(DateTimeUtil.getDateTimeFormat());
    }

    @Override
    public String getClassName()
    {
        return OperationScheduleTimeBlocksDataModel.class.getName();
    }
    
    // </editor-fold>
    
}
