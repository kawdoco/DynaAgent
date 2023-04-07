/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.datamodels;

import dyno.scheduler.data.DataReader;
import dyno.scheduler.data.DataWriter;
import dyno.scheduler.datamodels.DataModelEnums.InerruptionType;
import dyno.scheduler.utils.DateTimeUtil;
import dyno.scheduler.utils.GeneralSettings;
import dyno.scheduler.utils.LogUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 *
 * @author Prabash
 */
public class WorkCenterModel extends DataModel
{
    // <editor-fold desc="properties"> 

    private int id;
    private String workCenterNo;
    private String workCenterType;
    private String workCenterDescription;
    private String workCenterCapacity;
    private List<WorkCenterInterruptionsModel> workCenterInterruptions;
    
    private List<WorkCenterOpAllocModel> currentWorkCenterOpAllocs;
    private String currentTimeBlockName;
    private LocalDate currentDate;


    public WorkCenterModel()
    {
        AGENT_PREFIX = "WORK_CENTER_AGENT";
    }

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

    public String getWorkCenterType()
    {
        return workCenterType;
    }

    public void setWorkCenterType(String workCenterType)
    {
        this.workCenterType = workCenterType;
    }

    public String getWorkCenterDescription()
    {
        return workCenterDescription;
    }

    public void setWorkCenterDescription(String workCenterDescription)
    {
        this.workCenterDescription = workCenterDescription;
    }

    public String getWorkCenterCapacity()
    {
        return workCenterCapacity;
    }

    public void setWorkCenterCapacity(String workCenterCapacity)
    {
        this.workCenterCapacity = workCenterCapacity;
    }

    public List<WorkCenterInterruptionsModel> getWorkCenterInterruptions()
    {
        return workCenterInterruptions;
    }

    public void setWorkCenterInterruptions(List<WorkCenterInterruptionsModel> workCenterInterruptions)
    {
        this.workCenterInterruptions = workCenterInterruptions;
    }

    // </editor-fold>
    
    // <editor-fold desc="overriden methods"> 
    /**
     * get WorkCenterModel object by passing Excel or MySql table row
     *
     * @return WorkCenterModel object
     */
    @Override
    public WorkCenterModel getModelObject(Object row)
    {
        if (row instanceof Row)
        {
            // Create a DataFormatter to format and get each cell's value as String
            DataFormatter dataFormatter = new DataFormatter();
            Row excelRow = (Row) row;
            int i = -1;

            this.setWorkCenterNo(dataFormatter.formatCellValue(excelRow.getCell(++i)));
            this.setWorkCenterType(dataFormatter.formatCellValue(excelRow.getCell(++i)));
            this.setWorkCenterDescription(dataFormatter.formatCellValue(excelRow.getCell(++i)));
            this.setWorkCenterCapacity(dataFormatter.formatCellValue(excelRow.getCell(++i)));

        } else
        {
            ResultSet resultSetRow = (ResultSet) row;
            int i = 0;
            try
            {
                this.setId(resultSetRow.getInt(++i));
                this.setWorkCenterNo(resultSetRow.getString(++i));
                this.setWorkCenterType(resultSetRow.getString(++i));
                this.setWorkCenterDescription(resultSetRow.getString(++i));
                this.setWorkCenterCapacity(resultSetRow.getString(++i));
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
        return getWorkCenterNo();
    }

    @Override
    public String getClassName()
    {
        return WorkCenterModel.class.getName();
    }

    @Override
    public String getAgentPrefix()
    {
        return this.AGENT_PREFIX;
    }

    /**
     * *
     * this method will return the best date time offer for the work center no
     * and the required date TODO: should also incorporate scheduling direction
     * and time factors
     *
     * @param requiredDateTime
     * @param workCenterRuntime
     * @return
     */
    public DateTime getBestDateTimeOffer(DateTime requiredDateTime, int workCenterRuntime, String partNo)
    {
        DateTime bestDateTimeOffer = null;
        PartModel partDetails = DataReader.getPartDetailsByPartNo(partNo);

        // get the date and the time portion of the required datetime
        currentDate = requiredDateTime.toLocalDate();
        currentTimeBlockName = WorkCenterOpAllocModel.getTimeBlockName(requiredDateTime.toLocalTime());
        System.out.println("%%%%%%%%%%%%%  " + this.getWorkCenterNo() + "  " + currentDate + "  " + currentTimeBlockName);

        // get the work center allocation details, filter it by the current work center no.
        // TODO: The filteration should happen when taking from the database.
        currentWorkCenterOpAllocs = DataReader.getWorkCenterOpAllocDetails(true).stream()
                .filter(rec -> rec.getWorkCenterNo().equals(this.getWorkCenterNo()))
                .collect(Collectors.toList());

        // sort the work center allocations by date on the ascending order
        Collections.sort(currentWorkCenterOpAllocs, (WorkCenterOpAllocModel o1, WorkCenterOpAllocModel o2) -> o1.getOperationDate().compareTo(o2.getOperationDate()));

        while (bestDateTimeOffer == null)
        {
            // get the WorkCenterOpAllocModel object from the list, related to the currentDate
            WorkCenterOpAllocModel workCenterOpAlloc = currentWorkCenterOpAllocs.stream().filter(aloc -> aloc.getOperationDate().toLocalDate().equals(currentDate)).
                    collect(Collectors.toList()).get(0);

            // get timeBlock allocation for the given currentDate
            HashMap<String, Integer> timeBlockAllocation = workCenterOpAlloc.getTimeBlockAllocation();

            // if the currentTimeBlock is not allocated
            if (timeBlockAllocation.get(currentTimeBlockName) == 0)
            {
                // check if there's enough consecutive time available in the work center to allocate the workCenterRuntime
                if (checkConsecutiveTimeBlockAvailability(currentDate, currentTimeBlockName, workCenterRuntime) && 
                        partDetails.checkPartAvailability(DateTimeUtil.concatenateDateTime(currentDate, WorkCenterOpAllocModel.getTimeBlockValue(currentTimeBlockName)), workCenterRuntime))
                {
                    bestDateTimeOffer = DateTimeUtil.concatenateDateTime(currentDate, WorkCenterOpAllocModel.getTimeBlockValue(currentTimeBlockName));
                } else
                {
                    // increment the time by 1 (an hour) and get the timeblock name and assign it;
                    HashMap<String, Object> incrementDetails = WorkCenterOpAllocModel.incrementTimeBlock(currentTimeBlockName, 1);
                    currentTimeBlockName = incrementDetails.get(GeneralSettings.getStrTimeBlockName()).toString();
                    currentDate = currentDate.plusDays(Integer.parseInt(incrementDetails.get(GeneralSettings.getStrDaysAdded()).toString()));
                }
            } // if the currentTimeBlock is already allocated 
            else
            {
                // increment the time by 1 (an hour) and get the timeblock name and assign it;
                HashMap<String, Object> incrementDetails = WorkCenterOpAllocModel.incrementTimeBlock(currentTimeBlockName, 1);
                currentTimeBlockName = incrementDetails.get(GeneralSettings.getStrTimeBlockName()).toString();
                currentDate = currentDate.plusDays(Integer.parseInt(incrementDetails.get(GeneralSettings.getStrDaysAdded()).toString()));
            }
        }

        return bestDateTimeOffer;
    }

    List<WorkCenterOpAllocModel> workCenterOpAllocUpdate = new ArrayList<>();

    /**
     * update the work center operation allocation details
     *
     * @param bestOfferedDate
     * @param operationId
     * @param workCenterRuntime
     * @return a hashmap will return next time timeblock name and the days added
     * so that the subsequent operations can be scheduled
     */
    public HashMap<String, Object> scheduleOperationFromBestOffer(DateTime bestOfferedDate, int operationId, int workCenterRuntime)
    {
        workCenterOpAllocUpdate = new ArrayList<>();

        System.out.println("*************** UPDATING : " + this.getWorkCenterNo() + " " + bestOfferedDate + " " + workCenterRuntime);

        // this hashmap will return next time timeblock name and the days added so that the subsequent operations can be scheduled
        HashMap<String, Object> timeBlockDetails;

        String bestOfferStartTimeBlock = WorkCenterOpAllocModel.getTimeBlockName(bestOfferedDate.toLocalTime());

        System.out.println("*************** UPDATING : bestOfferStartTimeBlock " + bestOfferStartTimeBlock);

        // this method will add necessary and workCenterOpAlloc objects to be updated
        // return the the next timeblock after the last timeblock where the operation is scheduled on
        timeBlockDetails = getWorkCenterOpAllocObjectForUpdate(bestOfferedDate, bestOfferStartTimeBlock, operationId, workCenterRuntime);

        // update work center allocation data with provided information
        DataWriter.updateWorkCenterAllocData(workCenterOpAllocUpdate);

        return timeBlockDetails;
    }
    
    public void unscheduleWorkCenterOnInterruption(DateTime interruptionStartDateTime, int workCenterRuntime)
    {
        // when interrupted, the work center cannot be scheduled for that time period, which is indicated by -1
        WorkCenterModel.this.unscheduleWorkCenter(interruptionStartDateTime, workCenterRuntime, InerruptionType.Interruption.getValue());
    }
    
    public void unscheduleWorkCenterOnPartUnavailability(DateTime interruptionStartDateTime, int workCenterRuntime)
    {
        // when part is not available to continue work, the operation should be unscheduled and moved to a seperate time period.
        // this should be temporary and only affect that operation. This is indicated by -2.
        WorkCenterModel.this.unscheduleWorkCenter(interruptionStartDateTime, workCenterRuntime, InerruptionType.PartUnavailable.getValue());
    }
    
    public void unscheduleWorkCenter(DateTime interruptionStartDateTime, int workCenterRuntime)
    {
        // when unscheduling lower priority operations, that time should be utilized by higher priority operations,
        // which is indicated by 0
        WorkCenterModel.this.unscheduleWorkCenter(interruptionStartDateTime, workCenterRuntime, InerruptionType.Normal.getValue());
    }
    
    private void unscheduleWorkCenter(DateTime interruptionStartDateTime, int workCenterRuntime, int unscheduleType)
    {
        workCenterOpAllocUpdate = new ArrayList<>();
        System.out.println("*************** UNSCHEDULING INTERRUPTED OPERATIONS : " + this.getWorkCenterNo() + " " + interruptionStartDateTime + " " + workCenterRuntime);
        String interruptionStartTimeBlock = WorkCenterOpAllocModel.getTimeBlockName(interruptionStartDateTime.toLocalTime());

        // this method will add necessary and workCenterOpAlloc objects to be updated
        // return the the next timeblock after the last timeblock where the operation is scheduled on
        // Operation ID is set to -1 to indicate Interruption
        getWorkCenterOpAllocObjectForUpdate(interruptionStartDateTime, interruptionStartTimeBlock, unscheduleType, workCenterRuntime);

        // update work center allocation data with provided information
        DataWriter.updateWorkCenterAllocData(workCenterOpAllocUpdate);
    }

    /**
     * this method will recursively add WorkCenterOpAllocModel objects to the
     * workCenterOpAllocUpdate list
     *
     * @param currentDate initially this should be the bestOfferedDate,
     * afterwards, the subsequent days depending on the workCenterRuntime
     * @param timeBlockName startingTimeBlock name for the day
     * @param operationId operationId that should be allocated for
     * @param workCenterRuntime workCenterRuntime
     */
    private HashMap<String, Object> getWorkCenterOpAllocObjectForUpdate(DateTime currentDate, String timeBlockName, int operationId, int workCenterRuntime)
    {
        WorkCenterOpAllocModel allocObj = new WorkCenterOpAllocModel();
        // this hashmap will return the last timeblock name and the days added 
        HashMap<String, Object> timeBlockDetails = new HashMap<>();

        // bestOfferedDate value also has the time portion in it. Therefore, convert it to string and only add the Date portion
        allocObj.setOperationDate(DateTimeUtil.getDateFormat().parseDateTime(currentDate.toString(DateTimeUtil.getDateFormat())));
        allocObj.setWorkCenterNo(this.getWorkCenterNo());

        while (workCenterRuntime > 0)
        {
            workCenterRuntime--;

            timeBlockDetails.clear();
            // add the timeblock to the allocObj
            allocObj.addToTimeBlockAllocation(timeBlockName, operationId);

            // increment the timeblock by 1
            HashMap<String, Object> incrementDetails = WorkCenterOpAllocModel.incrementTimeBlock(timeBlockName, 1);
            timeBlockName = incrementDetails.get(GeneralSettings.getStrTimeBlockName()).toString();
            // if days are added when incrementing the timeblock, recursively call this method again
            // by sending the currentDate as the currentDate+daysAdded, currentTimeBlockName, and the remaining workCenterRuntime (i)
            int daysAdded = Integer.parseInt(incrementDetails.get(GeneralSettings.getStrDaysAdded()).toString());

            // add the timeblock name and the time added
            timeBlockDetails.put(GeneralSettings.getStrTimeBlockName(), timeBlockName);
            timeBlockDetails.put(GeneralSettings.getStrDaysAdded(), daysAdded);

            if (daysAdded > 0)
            {
                // add the currentDay timeblock details to the updateList first, and then invoke this method recursively
                //workCenterOpAllocUpdate.add(allocObj);
                // from the inner time block details, replace the existing timeblock name, but have to add the daysAdded value to find how many days have been added in total
                HashMap<String, Object> innerTimeBlockDetails = getWorkCenterOpAllocObjectForUpdate(currentDate.plusDays(daysAdded), timeBlockName, operationId, workCenterRuntime);
                if (innerTimeBlockDetails.size() > 0)
                {
                    timeBlockDetails.clear();
                    // add the timeblock name and the time added
                    timeBlockDetails.put(GeneralSettings.getStrTimeBlockName(), innerTimeBlockDetails.get(GeneralSettings.getStrTimeBlockName()));
                    timeBlockDetails.put(GeneralSettings.getStrDaysAdded(), daysAdded + Integer.parseInt(innerTimeBlockDetails.get(GeneralSettings.getStrDaysAdded()).toString()));
                }
                break;
            }
        }
        
        if (allocObj.getTimeBlockAllocation() != null && allocObj.getTimeBlockAllocation().size() > 0)
        {
            // add the operation to the update list when all the timeblocks are on the same day
            workCenterOpAllocUpdate.add(allocObj);
        }

        return timeBlockDetails;
    }

    /**
     * This method will check if the set of TimeBlocks are available from the
     * given given date and the timeBlock Name for the amount of work center
     * runtime
     *
     * @param currentDate the date to be checked
     * @param timeBlockName the starting timeblock name
     * @param workCenterRuntime amount of timeblocks to be checked is taken from
     * the workCenterRuntime
     * @return return true if available, false if not.
     */
    private boolean checkConsecutiveTimeBlockAvailability(LocalDate currentDate, String timeBlockName, int workCenterRuntime)
    {
        boolean timeBlocksAvailable = true;

        WorkCenterOpAllocModel workCenterOpAlloc = currentWorkCenterOpAllocs.stream().filter(aloc -> aloc.getOperationDate().toLocalDate().equals(currentDate)).
                collect(Collectors.toList()).get(0);
        // get timeBlock allocation for the currentDate
        HashMap<String, Integer> timeBlockAllocation = workCenterOpAlloc.getTimeBlockAllocation();

        while (workCenterRuntime > 0)
        {
            workCenterRuntime--;
            // timeblock is allocated to an operation, therefore time blocks are not consecutively available to be allocated
            // for the workCenterRuntime of the operation 
            if (timeBlockAllocation.get(timeBlockName) != 0)
            {
                timeBlocksAvailable = false;
                // break the loop since there's no need of checking the consequent timeblocks
                break;
            } // timeblock is free, increment the timeblock by one and check for the next time block
            else
            {
                // increment the timeblock by 1
                HashMap<String, Object> incrementDetails = WorkCenterOpAllocModel.incrementTimeBlock(timeBlockName, 1);
                timeBlockName = incrementDetails.get(GeneralSettings.getStrTimeBlockName()).toString();
                // if days are added when incrementing the timeblock, recursively call this method again
                // by sending the currentDate as the currentDate+daysAdded, currentTimeBlockName, and the remaining workCenterRuntime (i)
                int daysAdded = Integer.parseInt(incrementDetails.get(GeneralSettings.getStrDaysAdded()).toString());
                if (daysAdded > 0)
                {
                    timeBlocksAvailable = checkConsecutiveTimeBlockAvailability(currentDate.plusDays(daysAdded), timeBlockName, workCenterRuntime); // decrement i value before sending recursively
                    break;
                }
            }
        }

        return timeBlocksAvailable;
    }
    
    public void makeAvailableTempUnavailableAllocs()
    {
        DataWriter.makeAvailableTempUnavailableAllocs(getWorkCenterNo());
    }

    // </editor-fold> 
}
