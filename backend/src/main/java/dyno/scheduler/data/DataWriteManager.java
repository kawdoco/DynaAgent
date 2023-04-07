/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.data;

import dyno.scheduler.datamodels.*;
import dyno.scheduler.datamodels.DataModelEnums.OperationStatus;
import dyno.scheduler.datamodels.DataModelEnums.ShopOrderScheduleStatus;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Prabash
 */
public abstract class DataWriteManager
{
    public abstract boolean addData(List<? extends DataModel> dataList, DataModelEnums.DataModelType dataModelType);
    
    public abstract boolean updateData(List<? extends DataModel> dataList, DataModelEnums.DataModelType dataModelType);
    
    public abstract boolean addShopOrderData(List<ShopOrderModel> dataList, String storageName);
    
    public abstract boolean addShopOrderOperationData(List<ShopOrderOperationModel> dataList, String storageName);
    
    public abstract boolean addWorkCenterData(List<WorkCenterModel> dataList, String storageName);
    
    public abstract boolean addWorkCenterOpALlocData(List<WorkCenterOpAllocModel> dataList, String storageName);
    
    public abstract boolean updateShopOrderData(List<ShopOrderModel> dataList, String storageName);
    
    public abstract boolean updateShopOrderOperationData(List<ShopOrderOperationModel> dataList, String storageName);
    
    public abstract boolean updateWorkCenterData(List<WorkCenterModel> dataList, String storageName);
    
    public abstract boolean updateWorkCenterOpAllocData(List<WorkCenterOpAllocModel> dataList, String storageName);
    
    /**
     * This method will unschedule all the given operations available in the operationsList
     * @param operationsList operations to be unscheduled
     * @param storageName table name
     * @return true if operation is successful
     */
    public abstract boolean unscheduleOperations(List<ShopOrderOperationModel> operationsList, String storageName);
    
    /**
     * This method will unschedule all the subsequent operations (ordered by the operation sequence) starting from the given operation
     * @param operation operation to be unscheduled from
     * @param storageName table name
     * @return true if operation is successful
     */
    public abstract boolean unscheduleAllOperationsFrom(ShopOrderOperationModel operation, String storageName);
    
    /**
     * This method is used to interrupt a given work center for the given start and end time
     * There, all the operation assigned within the time period will be unscheduled
     * @param workCenterNo work center that will be interrupted
     * @param startTime start time
     * @param endTime end time
     * @return 
     */
    public abstract boolean interruptWorkCenter(String workCenterNo, DateTime startTime, DateTime endTime);
    
    /**
     * This method is used to add a single operation to the table and return its generated ID.
     * @param shopOrderOperation
     * @param storageName
     * @return 
     */
    public abstract int addShopOrderOperation(ShopOrderOperationModel shopOrderOperation, String storageName);
    
    /**
     * This method is used to replace a preceding operation id by a given replacedById.
     * This is usually done after an operation is splitted.
     * @param precedingOperationId
     * @param replacedById
     * @param exceptOpId
     * @param orderNo
     * @return 
     */
    public abstract boolean replacePrecedingOperationId(int precedingOperationId, int replacedById, int exceptOpId, String orderNo);
    
    /**
     * Update the operation status by the given status and the operation id
     * @param operationId
     * @param operationStatus
     * @return 
     */
    public abstract boolean changeOperationStatus(int operationId, OperationStatus operationStatus);
    
    /**
     * When un-scheduling an operation, the operation's scheduling related data should also be removed.
     * This method handles that task
     * @param operationId
     * @param operationStatus
     * @return 
     */
    public abstract boolean removeOperationScheduleData(int operationId, OperationStatus operationStatus);
    
    /**
     * Change shop order scheduled status with relevant data
     * @param orderNo
     * @param scheduleStatus
     * @param startDate
     * @param finishDate
     * @return 
     */
    public abstract boolean changeShopOrderScheduleData(String orderNo, ShopOrderScheduleStatus scheduleStatus, DateTime startDate, DateTime finishDate);
    
    /**
     * Make the work center alloc type to 0 from temporary unavailable status (-2)
     * @param workCenterNo
     * @return 
     */
    public abstract boolean makeAvailableTempUnavailableTimeblocks(String workCenterNo);
    
    /**
     * Insert part details to the table
     * @param partDetails
     * @param storageName
     * @return 
     */
    public abstract boolean addPartDetails(PartModel partDetails, String storageName);
    
    /**
     * Update part details to the table
     * @param partDetails
     * @param storageName
     * @return 
     */
    public abstract boolean updatePartDetails(PartModel partDetails, String storageName);
    
    /**
     * Insert part unavailability details
     * @param partUnavailabilityDetail
     * @param storageName
     * @return 
     */
    public abstract boolean addPartUnavailabilityDetails(PartUnavailabilityModel partUnavailabilityDetail, String storageName);
    
    /**
     * Update part unavailability details
     * @param partUnavailabilityDetail
     * @param storageName
     * @return 
     */
    public abstract boolean updatePartUnavailabilityDetails(PartUnavailabilityModel partUnavailabilityDetail, String storageName);
    
    /**
     * Add work center interruption details
     * @param workCenterInterruptionDetail
     * @param storageName
     * @return 
     */
    public abstract boolean addWorkCenterInterruptionDetails(WorkCenterInterruptionsModel workCenterInterruptionDetail, String storageName);
    
    /**
     * Update work center interruption details
     * @param workCenterInterruptionDetail
     * @param storageName
     * @return 
     */
    public abstract boolean updateWorkCenterInterruptionDetails(WorkCenterInterruptionsModel workCenterInterruptionDetail, String storageName);
    
    
    /**
     * Update new operations (From Created Status) to Unscheduled status
     * @param orderNo
     * @return 
     */
    public abstract boolean changeOperationStatusToUnschedule(String orderNo);
}
