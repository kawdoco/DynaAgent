/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.datamodels;

import dyno.scheduler.data.DataEnums;
import dyno.scheduler.datamodels.DataModelEnums.TimeBlockParamType;
import dyno.scheduler.utils.DateTimeUtil;
import dyno.scheduler.utils.GeneralSettings;
import dyno.scheduler.utils.LogUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Prabash
 */
public class WorkCenterOpAllocModel extends DataModel
{
    // <editor-fold desc="properties"> 

    private int id;
    private String workCenterNo;
    private DateTime operationDate;
    private HashMap<String, Integer> timeBlockAllocation;

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

    public WorkCenterOpAllocModel()
    {
        timeBlockAllocation = new HashMap<>();
    }

    public void setWorkCenterNo(String workCenterNo)
    {
        this.workCenterNo = workCenterNo;
    }

    public DateTime getOperationDate()
    {
        return operationDate;
    }

    public void setOperationDate(DateTime operationDate)
    {
        this.operationDate = operationDate;
    }

    public HashMap<String, Integer> getTimeBlockAllocation()
    {
        return timeBlockAllocation;
    }

    public void setTimeBlockAllocation(HashMap<String, Integer> timeBlockAllocation)
    {
        this.timeBlockAllocation = timeBlockAllocation;
    }

    public void addToTimeBlockAllocation(String timBlockName, int operationId)
    {
        this.timeBlockAllocation.put(timBlockName, operationId);
    }

    // </editor-fold>
    
    // <editor-fold desc="overriden methods"> 
    /**
     * get WorkCenterOpAllocModel object by passing Excel or MySql table row
     *
     * @param rowData relevant data object
     * @return WorkCenterOpAllocModel object
     */
    @Override
    public WorkCenterOpAllocModel getModelObject(Object row)
    {
        int noOfTimeBlocks = 8;

        if (row instanceof Row)
        {

            // Create a DataFormatter to format and get each cell's value as String
            DataFormatter dataFormatter = new DataFormatter();
            DateTimeFormatter dateFormat = DateTimeUtil.getDateFormat();

            Row excelRow = (Row) row;
            int i = -1;

            this.setWorkCenterNo(dataFormatter.formatCellValue(excelRow.getCell(++i)));
            this.setOperationDate(excelRow.getCell(++i) == null ? null : dateFormat.parseDateTime(dataFormatter.formatCellValue(excelRow.getCell(i))));
            this.setTimeBlockAllocation(new HashMap<>());
            int timeBlockId = 1;
            for (int j = ++i; j < i + noOfTimeBlocks; j++)
            {
                if (excelRow.getCell(j) != null)
                {
                    this.addToTimeBlockAllocation("TB" + timeBlockId, Integer.parseInt(dataFormatter.formatCellValue(excelRow.getCell(j))));
                } else
                {
                    this.addToTimeBlockAllocation("TB" + timeBlockId, 0);
                }
                timeBlockId++;
            }
        } else
        {
            ResultSet resultSetRow = (ResultSet) row;
            int i = 0;
            try
            {
                this.setId(resultSetRow.getInt(++i));
                this.setWorkCenterNo(resultSetRow.getString(++i));
                this.setOperationDate(resultSetRow.getDate(++i) == null ? null : DateTimeUtil.convertSqlDatetoDateTime(resultSetRow.getDate(i)));
                this.setTimeBlockAllocation(new HashMap<>());
                int timeBlockId = 1;
                for (int j = ++i; j < i + noOfTimeBlocks; j++)
                {
                    this.addToTimeBlockAllocation("TB" + timeBlockId, resultSetRow.getInt(j));
                    timeBlockId++;
                }
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
        // TODO: should change this
        return getWorkCenterNo() + getOperationDate().toString();
    }

    @Override
    public String getClassName()
    {
        return WorkCenterOpAllocModel.class.getName();
    }

    // </editor-fold> 
    
    public static LocalTime getTimeBlockValue(String timeBlock)
    {
        return (LocalTime) getTimeBlockDetail(timeBlock, TimeBlockParamType.TimeBlockValue);
    }

    public static String getTimeBlockName(LocalTime timeValue)
    {
        return getTimeBlockDetail(timeValue, TimeBlockParamType.TimeBlockName).toString();
    }

    /**
     * this method is used to get the timeBlockValue by giving the timeBlockName
     * or get the timeBlockName by giving the timeBlockValue
     *
     * @param timeBlockParam
     * @param returnType the type of the value to be returned
     * @return timeBlockValue or timeBlockName depending on the returnType value
     */
    public static Object getTimeBlockDetail(Object timeBlockParam, TimeBlockParamType returnType)
    {
        Object returnTimeBlockParam = null;

        int noOfhours;
        LocalTime currentTimeValue;
        LocalTime intervalStartTime = null;
        String currentTimeBlockName;

        // if finite capacity, set the required parameters
        if (GeneralSettings.getCapacityType() == DataEnums.CapacityType.FiniteCapacity)
        {
            // interval start time is 12PM for the finite capacity
            intervalStartTime = LocalTime.parse("12:00:00", DateTimeUtil.getTimeFormat());

            // when return type is timeBlockName (parameter type would be the timeblockValue of LocalTime)
            // if the time value equals to the interval start time, skip it by adding an hour to get the next working timeblock
            if (returnType == TimeBlockParamType.TimeBlockName)
            {
                if (timeBlockParam.equals(intervalStartTime))
                {
                    timeBlockParam = ((LocalTime) timeBlockParam).plusHours(1);
                }
            }

            // there are 9 hours in infinite capacity
            noOfhours = 9;
            // initially the currentTimeValue will be the starting hour of the day, then it will increment one by one
            currentTimeValue = LocalTime.parse("08:00:00", DateTimeUtil.getTimeFormat());
            // initially the currentTimeBlockValue will be the starting timeblock (TB1), then it will increment one by one
            currentTimeBlockName = "TB1";
        } // else infinite capacity, set the required parameters
        else
        {
            // there are 24 hours in finite capacity
            noOfhours = 24;
            // initially the currentTimeValue will be the starting hour of the day, then it will increment one by one
            currentTimeValue = LocalTime.parse("00:00:00", DateTimeUtil.getTimeFormat());
            // initially the currentTimeBlockValue will be the starting timeblock (TB1), then it will increment one by one
            currentTimeBlockName = "TB1";
        }

        for (int i = 0; i < noOfhours; i++)
        {
            // check if the currentTimeValue is equals to the interval value and if so, skip the iteration of the loop
            if (GeneralSettings.getCapacityType() == DataEnums.CapacityType.FiniteCapacity && currentTimeValue.equals(intervalStartTime))
            {
                // before skipping, we should increment the interval hour by 1, to get the beginning of the next working hour.
                currentTimeValue = currentTimeValue.plusHours(1);
                continue;
            }

            // if return type is TimeBlockName, we should compare timeBlockValues and get the respective timeBlockName
            if (returnType == TimeBlockParamType.TimeBlockName)
            {
                if (timeBlockParam.equals(currentTimeValue))
                {
                    returnTimeBlockParam = currentTimeBlockName;
                }
            } // if return type is TimeBlockValue, we should compare timeBlockName and get the respective timeBlockValue
            else if (returnType == TimeBlockParamType.TimeBlockValue)
            {
                if (timeBlockParam.equals(currentTimeBlockName))
                {
                    returnTimeBlockParam = currentTimeValue;
                }
            }

            // increment the current time value by 1 hr, and assign it to itself
            currentTimeValue = currentTimeValue.plusHours(1);
            // increment the current timeblock value by 1, and assign it to itself
            HashMap<String, Object> incrementDetails = incrementTimeBlock(currentTimeBlockName, 1);
            currentTimeBlockName = incrementDetails.get(GeneralSettings.getStrTimeBlockName()).toString();
        }
        return returnTimeBlockParam;
    }

    /**
     * *
     * This method is used to increment a given datetime by a given number of
     * hours
     *
     * @param startingDateTime dateTime that should be incremented
     * @param incrementByHours the no. of hours that should be incremented
     * @return incrementedDateTime
     */
    public static DateTime incrementTime(DateTime startingDateTime, int incrementByHours)
    {
        String timeBlockName = getTimeBlockName(startingDateTime.toLocalTime());

        // use the start date and add  the incrementByHours
        HashMap<String, Object> incrementDetails = incrementTimeBlock(timeBlockName, incrementByHours);
        // from the return value use the time block name and get the time after incrementing
        LocalTime latestOpEndTime = getTimeBlockValue(incrementDetails.get(GeneralSettings.getStrTimeBlockName()).toString());
        // use the number of days added (if any) and add it to the starting date.
        DateTime latestOpEndDate = startingDateTime.plusDays(Integer.parseInt(incrementDetails.get(GeneralSettings.getStrDaysAdded()).toString()));

        return DateTimeUtil.concatenateDateTime(latestOpEndDate.toLocalDate(), latestOpEndTime);
    }

    /**
     * this method is used to increment a given time block value by an integer.
     * if the timeblock TB4 of a given day is incremented by 13, the
     * newTimeBlock value is 17 (more than 8) and if the capacity type is finite
     * (only 8 hrs), then after incrementing, the new time block should be the
     * TB1 after 2 days (4 + 8 + "1"). Therefore returns newTimeBlock value as
     * T1 and daysAdded as 2 in a list
     *
     * @param timeBlockName current time block
     * @param incrementBy the value to be incremented by
     * @return a list: first element is the new Time block name, second element
     * is the daysAdded after incrementing
     */
    public static HashMap<String, Object> incrementTimeBlock(String timeBlockName, int incrementBy)
    {
        HashMap<String, Object> returnList = new HashMap<>();
        int currentTimeBlock = Integer.parseInt(timeBlockName.substring(2));
        int newTimeBlock = currentTimeBlock + incrementBy;
        int daysAdded = 0;

        if (newTimeBlock > 8 && GeneralSettings.getCapacityType() == DataEnums.CapacityType.FiniteCapacity)
        {
            while (newTimeBlock > 8)
            {
                newTimeBlock = newTimeBlock - 8;
                daysAdded++;
            }
        } else if (newTimeBlock > 24 && GeneralSettings.getCapacityType() == DataEnums.CapacityType.InfiniteCapacity)
        {
            while (newTimeBlock >= 24)
            {
                newTimeBlock = newTimeBlock - 24;
                daysAdded++;
            }
        }

        String newTimeBlockName = "TB" + newTimeBlock;

        returnList.put(GeneralSettings.getStrTimeBlockName(), newTimeBlockName);
        returnList.put(GeneralSettings.getStrDaysAdded(), daysAdded);

        return returnList;
    }
}
