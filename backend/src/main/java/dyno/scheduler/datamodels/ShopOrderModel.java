/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.datamodels;

import dyno.scheduler.data.DataEnums;
import dyno.scheduler.data.DataWriter;
import dyno.scheduler.datamodels.DataModelEnums.ShopOrderPriority;
import dyno.scheduler.datamodels.DataModelEnums.ShopOrderScheduleStatus;
import dyno.scheduler.datamodels.DataModelEnums.ShopOrderSchedulingDirection;
import dyno.scheduler.datamodels.DataModelEnums.ShopOrderStatus;
import dyno.scheduler.utils.DateTimeUtil;
import dyno.scheduler.utils.GeneralSettings;
import dyno.scheduler.utils.LogUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Prabash
 */
@XmlRootElement
public class ShopOrderModel extends DataModel implements Comparator<ShopOrderModel>
{

    //<editor-fold defaultstate="collapsed" desc="properties">
    private int id;
    private String orderNo;
    private String description;
    private DateTime createdDate;
    private String partNo;
    private String structureRevision;
    private String routingRevision;
    private DateTime requiredDate;
    private DateTime startDate;
    private DateTime finishDate;
    private ShopOrderSchedulingDirection schedulingDirection;
    private String customerNo;
    private ShopOrderScheduleStatus schedulingStatus;
    private ShopOrderStatus shopOrderStatus;
    private ShopOrderPriority priority;
    private int revenueValue;
    private List<ShopOrderOperationModel> operations;
    private double importance;

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="constructors">
    public ShopOrderModel()
    {
        AGENT_PREFIX = "SHOP_ORDER_AGENT";
    }

    public ShopOrderModel(String orderNo, String description, DateTime createdDate, String partNo, String structureRevision, String routingRevision, DateTime requiredDate,
            DateTime startDate, DateTime finishDate, ShopOrderSchedulingDirection schedulingDirection, String customerNo, ShopOrderStatus shopOrderStatus, ShopOrderPriority priority, List<ShopOrderOperationModel> operaitons)
    {
        this.orderNo = orderNo;
        this.description = description;
        this.createdDate = createdDate;
        this.partNo = partNo;
        this.structureRevision = structureRevision;
        this.routingRevision = routingRevision;
        this.requiredDate = requiredDate;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.schedulingDirection = schedulingDirection;
        this.customerNo = customerNo;
        this.shopOrderStatus = shopOrderStatus;
        this.priority = priority;
        this.operations = operaitons;
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

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public DateTime getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(DateTime createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getPartNo()
    {
        return partNo;
    }

    public void setPartNo(String partNo)
    {
        this.partNo = partNo;
    }

    public String getStructureRevision()
    {
        return structureRevision;
    }

    public void setStructureRevision(String structureRevision)
    {
        this.structureRevision = structureRevision;
    }

    public String getRoutingRevision()
    {
        return routingRevision;
    }

    public void setRoutingRevision(String routingRevision)
    {
        this.routingRevision = routingRevision;
    }

    public DateTime getRequiredDate()
    {
        return requiredDate;
    }

    public void setRequiredDate(DateTime requiredDate)
    {
        this.requiredDate = requiredDate;
    }

    public DateTime getStartDate()
    {
        return startDate;
    }

    public void setStartDate(DateTime startDate)
    {
        this.startDate = startDate;
    }

    public DateTime getFinishDate()
    {
        return finishDate;
    }

    public void setFinishDate(DateTime finishDate)
    {
        this.finishDate = finishDate;
    }

    public ShopOrderSchedulingDirection getSchedulingDirection()
    {
        return schedulingDirection;
    }

    public void setSchedulingDirection(ShopOrderSchedulingDirection schedulingDirection)
    {
        this.schedulingDirection = schedulingDirection;
    }

    public String getCustomerNo()
    {
        return customerNo;
    }

    public void setCustomerNo(String customerNo)
    {
        this.customerNo = customerNo;
    }

    public ShopOrderScheduleStatus getSchedulingStatus()
    {
        return schedulingStatus;
    }

    public void setSchedulingStatus(ShopOrderScheduleStatus schedulingStatus)
    {
        this.schedulingStatus = schedulingStatus;
    }

    public ShopOrderStatus getShopOrderStatus()
    {
        return shopOrderStatus;
    }

    public void setShopOrderStatus(ShopOrderStatus thisStatus)
    {
        this.shopOrderStatus = thisStatus;
    }

    public ShopOrderPriority getPriority()
    {
        return priority;
    }

    public void setPriority(ShopOrderPriority priority)
    {
        this.priority = priority;
    }

    public List<ShopOrderOperationModel> getOperations()
    {
        Collections.sort(operations, (ShopOrderOperationModel o1, ShopOrderOperationModel o2) -> Double.compare(o1.getOperationSequence(), o2.getOperationSequence()));
        return operations;
    }

    public void setOperations(List<ShopOrderOperationModel> operations)
    {
        this.operations = operations;
    }

    public int getRevenueValue()
    {
        return revenueValue;
    }

    public void setRevenueValue(int revenueValue)
    {
        this.revenueValue = revenueValue;
    }

    public double getImportance()
    {
        return importance;
    }

    public void setImportance(double importance)
    {
        this.importance = importance;
    }
    
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="overriden methods"> 
    
    /**
     * get ShopOrderModel object by passing Excel or MySql table row
     *
     * @param row relevant data object
     * @return ShopOrderModel object
     */
    @Override
    public ShopOrderModel getModelObject(Object row)
    {
        if (row instanceof Row)
        {
            // Create a DataFormatter to format and get each cell's value as String
            DataFormatter dataFormatter = new DataFormatter();
            DateTimeFormatter dateFormat = DateTimeUtil.getDateFormat();

            Row excelRow = (Row) row;
            int i = -1;

            this.setOrderNo(dataFormatter.formatCellValue(excelRow.getCell(++i)));
            this.setDescription(dataFormatter.formatCellValue(excelRow.getCell(++i)));
            this.setCreatedDate(excelRow.getCell(++i) == null ? null : dateFormat.parseDateTime(dataFormatter.formatCellValue(excelRow.getCell(i))));
            this.setPartNo(dataFormatter.formatCellValue(excelRow.getCell(++i)));
            this.setStructureRevision(dataFormatter.formatCellValue(excelRow.getCell(++i)));
            this.setRoutingRevision(dataFormatter.formatCellValue(excelRow.getCell(++i)));
            this.setRequiredDate(excelRow.getCell(++i) == null ? null : dateFormat.parseDateTime(dataFormatter.formatCellValue(excelRow.getCell(i))));
            this.setStartDate(excelRow.getCell(++i) == null ? null : dateFormat.parseDateTime(dataFormatter.formatCellValue(excelRow.getCell(i))));
            this.setFinishDate(excelRow.getCell(++i) == null ? null : dateFormat.parseDateTime(dataFormatter.formatCellValue(excelRow.getCell(i))));
            this.setSchedulingDirection(ShopOrderSchedulingDirection.valueOf(dataFormatter.formatCellValue(excelRow.getCell(++i))));
            this.setCustomerNo(dataFormatter.formatCellValue(excelRow.getCell(++i)));
            this.setSchedulingStatus(ShopOrderScheduleStatus.valueOf(dataFormatter.formatCellValue(excelRow.getCell(++i))));
            this.setShopOrderStatus(ShopOrderStatus.valueOf(dataFormatter.formatCellValue(excelRow.getCell(++i))));
            this.setPriority(ShopOrderPriority.valueOf(dataFormatter.formatCellValue(excelRow.getCell(++i))));
            this.setRevenueValue(Integer.parseInt(dataFormatter.formatCellValue(excelRow.getCell(++i))));

        } else
        {
            ResultSet resultSetRow = (ResultSet) row;
            int i = 0; // indeces start from 1 on the table
            try
            {
                this.setId(resultSetRow.getInt(++i));
                this.setOrderNo(resultSetRow.getString(++i));
                this.setDescription(resultSetRow.getString(++i));
                this.setCreatedDate(resultSetRow.getDate(++i) == null ? null : DateTimeUtil.convertSqlDatetoDateTime(resultSetRow.getDate(i)));
                this.setPartNo(resultSetRow.getString(++i));
                this.setStructureRevision(resultSetRow.getString(++i));
                this.setRoutingRevision(resultSetRow.getString(++i));
                this.setRequiredDate(resultSetRow.getDate(++i) == null ? null : DateTimeUtil.convertSqlDatetoDateTime(resultSetRow.getDate(i)));
                this.setStartDate(resultSetRow.getDate(++i) == null ? null : DateTimeUtil.convertSqlDatetoDateTime(resultSetRow.getDate(i)));
                this.setFinishDate(resultSetRow.getDate(++i) == null ? null : DateTimeUtil.convertSqlDatetoDateTime(resultSetRow.getDate(i)));
                this.setSchedulingDirection(ShopOrderSchedulingDirection.valueOf(resultSetRow.getString(++i)));
                this.setCustomerNo(resultSetRow.getString(++i));
                this.setSchedulingStatus(ShopOrderScheduleStatus.valueOf(resultSetRow.getString(++i)));
                this.setShopOrderStatus(ShopOrderStatus.valueOf(resultSetRow.getString(++i)));
                this.setPriority(ShopOrderPriority.valueOf(resultSetRow.getString(++i)));
                this.setRevenueValue(resultSetRow.getInt(++i));
                this.setImportance(resultSetRow.getDouble(++i));

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
        return getOrderNo();
    }

    @Override
    public String getClassName()
    {
        return ShopOrderModel.class.getName();
    }

    @Override
    public String getAgentPrefix()
    {
        return this.AGENT_PREFIX;
    }

    /**
     * this method is used to get a target start date for the operation by
     * sending its primary key (operation id) If the precedingOperation ID is 0,
     * get the shopOrder start date or else get the precedingOperation ID's
     * operation finish date/time
     *
     * @param opPrimaryKey primary key
     * @return target operation start date
     */
    public DateTime getOperationTargetStartDate(String opPrimaryKey)
    {
        List<ShopOrderOperationModel> currentOperations = getOperations();
        DateTime opTargetStartDate = null;

        for (ShopOrderOperationModel operation : currentOperations)
        {
            if (operation.getPrimaryKey().equals(opPrimaryKey))
            {
                // first operation sequence
                if (operation.getPrecedingOperationId() == 0)
                {
                    // set the shop order created date as the opTargetStartDate for the first operation     
                    opTargetStartDate = getShopOrderStartDateTime();
                    break;
                } // subsequent operations
                else
                {
                    // the finish datetime of the Preceding Operation Id, should be taken as the target start date/time of the operations
                    ShopOrderOperationModel precedingOp = currentOperations.stream().
                            filter(rec -> rec.getPrimaryKey().equals(String.valueOf(operation.getPrecedingOperationId()))).
                            collect(Collectors.toList()).get(0);
                    opTargetStartDate = DateTimeUtil.concatenateDateTime(precedingOp.getOpFinishDate(), precedingOp.getOpFinishTime());
                    break;
                }
            }
        }

        return opTargetStartDate;
    }

    /**
     * Return the startDate of a Shop Order by considering various parameters
     *
     * @return Starting Date Time of the Shop Order
     */
    public DateTime getShopOrderStartDateTime()
    {
        DateTime shopOrderStartDateTime = null;
        if (getSchedulingDirection() == ShopOrderSchedulingDirection.Forward)
        {
            shopOrderStartDateTime = getStartTimeOnCapacityType(getCreatedDate());
        } else if (getSchedulingDirection() == ShopOrderSchedulingDirection.Backward)
        {
            double bufferPercentage = 20.0; // percentage of the total runtime duration that will be a buffer, minimum buffer value is 1 day

            int runtimeInDays = getShopOrderTotalRunTimeDays();
            int bufferDays = Math.round((float) Math.ceil(runtimeInDays * bufferPercentage / 100.0));
            // set the minimum buffer days
            if (bufferDays < 1)
            {
                bufferDays = 1;
            }

            // total runtime and the buffer will be reducted from the required date and calculate the start date
            DateTime shopOrderStartDate = getRequiredDate().minusDays(runtimeInDays + bufferDays);
            shopOrderStartDateTime = getStartTimeOnCapacityType(shopOrderStartDate);
        }

        return shopOrderStartDateTime;
    }

    /**
     * Get the latest possible start date of the order in order to meet the
     * required date
     *
     * @return latestPossibleStartDate
     */
    private DateTime getLatestOrderStartDate()
    {
        int runtimeInDays = getShopOrderTotalRunTimeDays();
        DateTime shopOrderStartDate = getRequiredDate().minusDays(runtimeInDays);
        return getStartTimeOnCapacityType(shopOrderStartDate);
    }

    /**
     * this will concatenate a start-time to a given date based on the capacity
     * type
     *
     * @param date the date to which the time should be concatenated
     * @return
     */
    private DateTime getStartTimeOnCapacityType(DateTime date)
    {
        // Calcualate the start datetime depending on the capacity type
        if (GeneralSettings.getCapacityType() == DataEnums.CapacityType.FiniteCapacity)
        {
            // for finite capacity starting time of the day is 0800HRS
            return DateTimeUtil.concatenateDateTime(date.toString(DateTimeUtil.getDateFormat()), "08:00:00");
        } else
        {
            // for finite capacity starting time of the day is 0000HRS
            return DateTimeUtil.concatenateDateTime(date.toString(DateTimeUtil.getDateFormat()), "00:00:00");
        }
    }

    /**
     * This method is used assign the latest finish times for operations.
     */
    public void assignEstimatedLatestFinishTimeForOperations()
    {
        List<ShopOrderOperationModel> currentOperations = getOperations();
        DateTime latestOrderStartDate = getLatestOrderStartDate();
        DateTime latestOpEndDateTime = null;

        for (ShopOrderOperationModel operation : currentOperations)
        {
            // if the operation is the starting operation/s
            if (operation.getPrecedingOperationId() == 0)
            {
                // increment the order start datetime (starting date time of the first operation) by the first operation's work center run time
                latestOpEndDateTime = WorkCenterOpAllocModel.incrementTime(latestOrderStartDate, operation.getWorkCenterRuntime());

                operation.setLatestOpFinishDate(latestOpEndDateTime);
                operation.setLatestOpFinishTime(latestOpEndDateTime);
            } // else get the previous operation latest finish date time and add the workcenter runtime
            else
            {
                // the finish datetime of the Preceding Operation Id, should be taken as the target start date/time of the operations
                ShopOrderOperationModel precedingOp = currentOperations.stream().
                        filter(rec -> rec.getPrimaryKey().equals(String.valueOf(operation.getPrecedingOperationId()))).
                        collect(Collectors.toList()).get(0);
                // calculate the current op latestStartDate by concatenating the prev. op finish date and time
                DateTime previousOpLatestFinishDateTime = DateTimeUtil.concatenateDateTime(precedingOp.getLatestOpFinishDate(), precedingOp.getLatestOpFinishTime());

                // increment the previous operation latest finish datetime by current operation work center runtime.
                latestOpEndDateTime = WorkCenterOpAllocModel.incrementTime(previousOpLatestFinishDateTime, operation.getWorkCenterRuntime());
                operation.setLatestOpFinishDate(latestOpEndDateTime);
                operation.setLatestOpFinishTime(latestOpEndDateTime);
            }
//            System.out.println("SO : " + operation.getOrderNo() + " Op No : " + operation.getOperationId() + " Latest Op End Date : "
//                    + DateTimeUtil.concatenateDateTime(operation.getLatestOpFinishDate(), operation.getLatestOpFinishTime()).toString(DateTimeUtil.getDateTimeFormat()));
        }
        //System.out.println("Completed assigning latest finish times for operations");
    }

    /**
     * This method is used to get the total runtime of a shop order in days
     * @return 
     */
    public int getShopOrderTotalRunTimeDays()
    {
        int totalRuntTimeInHours = 0;
        int totalRuntimeDays = 0;
        // calculate the total number of runtime hours 
        totalRuntTimeInHours = getOperations().stream().map((operation) -> operation.getWorkCenterRuntime()).reduce(totalRuntTimeInHours, (accumulator, _item) -> accumulator + _item);

        // for finite capacity, get the number of runtime in days by dividing the no. of hours by 8
        if (GeneralSettings.getCapacityType() == DataEnums.CapacityType.FiniteCapacity)
        {
            totalRuntimeDays = Math.round((float) Math.ceil(totalRuntTimeInHours / 8.0));
        } // for infinite capacity, get the number of runtime in days by dividing the no. of hours by 24
        else if (GeneralSettings.getCapacityType() == DataEnums.CapacityType.InfiniteCapacity)
        {
            totalRuntimeDays = Math.round((float) Math.ceil(totalRuntTimeInHours / 24.0));
        }

        return totalRuntimeDays;
    }

    /**
     * this method is used to update the operations of the current shop order
     * object by providing the operation object
     *
     * @param operationOb operation object
     */
    public void updateOperation(ShopOrderOperationModel operationOb)
    {
        List<ShopOrderOperationModel> currentOperations = getOperations();
        int index = 0;
        // for each of the available operations
        for (ShopOrderOperationModel operation : currentOperations)
        {
            // check if the current operationId matches with the sent operation's operationId
            // and if so break the loop, keeping the index value;
            if (operation.getPrimaryKey().equals(operationOb.getPrimaryKey()))
            {
                break;
            }

            // increment index value
            index++;
        }

        // replace the operation in the index by the nex operation object
        currentOperations.set(index, operationOb);

        // update the operations list
        setOperations(currentOperations);
    }
    
    /**
     * Get the importance of the shop order in terms of its customer priority and Revenue value.
     * This is calculated with a weighted average method, with 30% Customer priority and 70% for revenue value.
     * @return totalImportance value
     */
    public double calculateImportance()
    {
        double importance = Math.round((((getPriority().getValue() / 5.0) * 0.3) + ((getRevenueValue() / 5.0) * 0.7)) * 100.0) / 100.0;
        return importance;
    }
    
    /**
     * This method will un-schedule the current shop order operations that comes after the unscheduleFromDateTime
     * the operations that comes in between the unscheduleFromDateTime will be splitted and updated and others will just be updated.
     * @param unscheduleFromDateTime 
     */
    public void unscheduleOperationsFrom(DateTime unscheduleFromDateTime)
    {
        // for each of the operations
        for (ShopOrderOperationModel operation : this.getOperations())
        {
            // concatenate the operation start datetime and finish datetime
            DateTime opStartDateTime = DateTimeUtil.concatenateDateTime(operation.getOpStartDate(), operation.getOpStartTime());
            DateTime opFinishDateTime = DateTimeUtil.concatenateDateTime(operation.getOpFinishDate(), operation.getOpFinishTime());

            // if the operation end time comes before or when the unscheduleFromDateTime such operations should not be unscheduled
            // hence they are ignored.
            if(opFinishDateTime.isEqual(unscheduleFromDateTime) || opFinishDateTime.isBefore(unscheduleFromDateTime))
            {
                continue;
            }
            // if the unscheduleFromDate falls in between the operation start datetime and finish datetime such operation should be split into 2
            // by the unscheduleFromDateTime to forward
            else if(opStartDateTime.isBefore(unscheduleFromDateTime) && opFinishDateTime.isAfter(unscheduleFromDateTime))
            {
                operation.splitAndUnscheduleInterruptedOperation(unscheduleFromDateTime, DataModelEnums.InerruptionType.Normal);
            }
            // if the operation start datetime comes after the unscheduleFromDateTime, such operations should just be unscheduled without splitting
            else if (opStartDateTime.isEqual(unscheduleFromDateTime) || opStartDateTime.isAfter(unscheduleFromDateTime))
            {
                operation.unscheduleOperation(DataModelEnums.OperationStatus.Unscheduled);
            }
        }
        // Finally set the scheduling status of the shop order to be partially scheduled or unscheduled depending on if there's an already assigned startDate
        if(getStartDate() != null)
        {
            DataWriter.changeShopOrderScheduleData(getOrderNo(), ShopOrderScheduleStatus.PartiallyScheduled, getStartDate(), null);
        }
        else
        {
            DataWriter.changeShopOrderScheduleData(getOrderNo(), ShopOrderScheduleStatus.Unscheduled, null, null);
        }
    }
    
    public List<Integer> unscheduleOperationsOnInterruption(DateTime interruptionStartDateTime, DateTime interruptionEndDateTime, DataModelEnums.InerruptionType interruptionType, double startOpSequence)
    {
        List<Integer> affectedOperations = new ArrayList<>();
        
        // for each of the operations
        for (ShopOrderOperationModel operation : this.getOperations())
        {
            // only operations with a higher or equal sequence to the startOpSequence should be unscheduled.
            if (operation.getOperationSequence() < startOpSequence)
            {
                continue;
            }
            
            // concatenate the operation start datetime and finish datetime
            DateTime opStartDateTime = DateTimeUtil.concatenateDateTime(operation.getOpStartDate(), operation.getOpStartTime());
            DateTime opFinishDateTime = DateTimeUtil.concatenateDateTime(operation.getOpFinishDate(), operation.getOpFinishTime());

            // if the operation end time comes before or when the interruptionStartDateTime such operations should not be unscheduled
            // hence they are ignored.
            if(opFinishDateTime.isEqual(interruptionStartDateTime) || opFinishDateTime.isBefore(interruptionStartDateTime))
            {
                continue;
            }
            // if the operation start datetime comes after the unscheduleFromDateTime, such operations should just be unscheduled without splitting
            else if (opStartDateTime.isEqual(interruptionEndDateTime) || opStartDateTime.isAfter(interruptionEndDateTime))
            {
                affectedOperations.add(operation.getOperationId());
                operation.unscheduleOperation(DataModelEnums.OperationStatus.Unscheduled);
            }
            // if the unscheduleFromDate falls in between the operation start datetime and finish datetime such operation should be split into 2
            // by the unscheduleFromDateTime to forward
            else if((opStartDateTime.isBefore(interruptionStartDateTime) && opFinishDateTime.isAfter(interruptionEndDateTime)) || 
                    (opStartDateTime.isBefore(interruptionStartDateTime) && (opFinishDateTime.isBefore(interruptionEndDateTime) || opFinishDateTime.isEqual(interruptionEndDateTime))) ||
                    (opStartDateTime.isEqual(interruptionStartDateTime) && opFinishDateTime.isEqual(interruptionEndDateTime)) ||
                    (opStartDateTime.isEqual(interruptionStartDateTime) && opFinishDateTime.isAfter(interruptionEndDateTime)) ||
                    (opStartDateTime.isEqual(interruptionStartDateTime) && opFinishDateTime.isBefore(interruptionEndDateTime)) ||
                    ((opStartDateTime.isAfter(interruptionStartDateTime) && opStartDateTime.isBefore(interruptionEndDateTime)) && opFinishDateTime.isAfter(interruptionEndDateTime)) || 
                    ((opStartDateTime.isAfter(interruptionStartDateTime) && opStartDateTime.isBefore(interruptionEndDateTime)) && opFinishDateTime.isEqual(interruptionEndDateTime)) ||
                    ((opStartDateTime.isAfter(interruptionStartDateTime) && opStartDateTime.isBefore(interruptionEndDateTime)) && opFinishDateTime.isBefore(interruptionEndDateTime)))
            {
                affectedOperations.add(operation.getOperationId());
                operation.splitAndUnscheduleInterruptedOperation(interruptionStartDateTime, interruptionEndDateTime, interruptionType);
            }
        }
        // Finally set the scheduling status of the shop order to be partially scheduled or unscheduled depending on if there's an already assigned startDate
        if(getStartDate() != null)
        {
            DataWriter.changeShopOrderScheduleData(getOrderNo(), ShopOrderScheduleStatus.PartiallyScheduled, getStartDate(), null);
        }
        else
        {
            DataWriter.changeShopOrderScheduleData(getOrderNo(), ShopOrderScheduleStatus.Unscheduled, null, null);
        }
        
        return affectedOperations;
    }
    
    /**
     * Un-schedule the entire shop order
     */
    public void unschedule()
    {
        for (ShopOrderOperationModel operation : this.getOperations())
        {
            operation.unscheduleOperation(DataModelEnums.OperationStatus.Unscheduled);
        }
        DataWriter.changeShopOrderScheduleData(getOrderNo(), ShopOrderScheduleStatus.Unscheduled, null, null);
    }
    
    /**
     * Cancel shop Order
     */
    public void cancel()
    {
        for (ShopOrderOperationModel operation : this.getOperations())
        {
            operation.unscheduleOperation(DataModelEnums.OperationStatus.Cancelled);
        }
        DataWriter.changeShopOrderScheduleData(getOrderNo(), ShopOrderScheduleStatus.Unscheduled, null, null);
    }
    
    /**
     * Set Shop Order to the Scheduled status with start and finish dates
     */
    public void setScheduleData()
    {
        // get operations sorted by the operation sequence
        List<ShopOrderOperationModel> operations = this.getOperations();
        // get the first operation
        ShopOrderOperationModel firstOp = operations.get(0);
        // get the last operation
        ShopOrderOperationModel lastOp = operations.get(operations.size() - 1);
        
        // update the shop order with schedule data
        DataWriter.changeShopOrderScheduleData(getOrderNo(), ShopOrderScheduleStatus.Scheduled, firstOp.getOpStartDate(), lastOp.getOpFinishDate());
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="comparator implementation"> 
    
    @Override
    public int compare(ShopOrderModel o1, ShopOrderModel o2)
    {
        int returnVal = o1.calculateImportance() > o2.calculateImportance() ? 1 : -1;
        return returnVal;
    }
    
    // </editor-fold> 
}
