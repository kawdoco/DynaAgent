/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.datamodels;

import dyno.scheduler.data.DataReader;
import dyno.scheduler.utils.DateTimeUtil;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;

/**
 *
 * @author Prabash
 */
public class WorkCenterUtil
{

    public static HashMap<String, List<Integer>> interruptWorkCenter(DateTime interruptionStartDateTime, DateTime interruptionEndDateTime, String workCenterNo)
    {
        List<InterruptedOpDetailsDataModel> interruptionDetails = DataReader.getInterruptedOperationDetails(interruptionStartDateTime, interruptionStartDateTime, interruptionEndDateTime, interruptionEndDateTime, workCenterNo);
        
        HashMap<String, List<Integer>> affectedOrders = new HashMap<>();
        for (InterruptedOpDetailsDataModel interruptionDetail : interruptionDetails)
        {
            ShopOrderModel shopOrder = DataReader.getShopOrderByPrimaryKey(interruptionDetail.getOrderNo());
            double interruptedOpSequenceNo = shopOrder.getOperations().stream().filter(rec -> rec.getOperationId() == interruptionDetail.getOperationId()).collect(Collectors.toList()).get(0).getOperationSequence();
            DateTime interruptionOnOpEndDateTime = WorkCenterOpAllocModel.incrementTime(interruptionDetail.getInterruptionOnOpStartDateTime(), interruptionDetail.getInterruptedRunTime());
            List<Integer> affectedOps = shopOrder.unscheduleOperationsOnInterruption(interruptionDetail.getInterruptionOnOpStartDateTime(), interruptionOnOpEndDateTime, DataModelEnums.InerruptionType.Interruption, interruptedOpSequenceNo);
            
            affectedOrders.put(shopOrder.getOrderNo(), affectedOps);
        }
        return affectedOrders;
    }

    public static HashMap<String, List<Integer>> interruptWorkCenterOnPartUnavailability(DateTime partUnavailableStartDateTime, DateTime partUnavailableEndDateTime, String partNo)
    {
        List<ShopOrderOperationModel> affectedOperations = DataReader.getAffectedOperationsByPartUnavailabiility(partNo, partUnavailableStartDateTime, partUnavailableStartDateTime, partUnavailableEndDateTime, partUnavailableEndDateTime);
        
        // sort operations by the operation start date time
        affectedOperations.sort(new OperationDateComparator());
        HashMap<String, List<Integer>> affectedOrders = new HashMap<>();
        
        // by going through the operations, find out the interruption start date and end dates of the work centers
        for (int i = 0; i < affectedOperations.size(); i++)
        {
            DateTime interruptionStartDateTime = new DateTime();
            DateTime interruptionEndDateTime = new DateTime();
            
            ShopOrderOperationModel currentOperation = affectedOperations.get(i);
            DateTime currentOpStartDateTime = DateTimeUtil.concatenateDateTime(currentOperation.getOpStartDate(), currentOperation.getOpStartTime());
            DateTime currentOpFinishDateTime = DateTimeUtil.concatenateDateTime(currentOperation.getOpFinishDate(), currentOperation.getOpFinishTime());

            // if the current operation has started before the part unavailability start date time, the interruption start date time should be the partUnavailableStartDateTime
            if (currentOpStartDateTime.isBefore(partUnavailableStartDateTime))
            {
                interruptionStartDateTime = partUnavailableStartDateTime;
            }
            // else if current operation starts after or on the partUnavailableStartDateTime, then the interrupton start datetime should be the current operation start datetime
            else if(currentOpStartDateTime.isEqual(partUnavailableStartDateTime) || currentOpStartDateTime.isAfter(partUnavailableStartDateTime))
            {
                interruptionStartDateTime = currentOpStartDateTime;
            } // This part is only checked for the 1st operation since the operations are sorted by the start date time, there cannot be operations earlier than that


            // if the current operation finish date time goes after the partUnavailableEndDateTime, then the interruptionEndDateTime should be the partUnavailableEndDateTime
            if (currentOpFinishDateTime.isAfter(partUnavailableEndDateTime))
            {
                interruptionEndDateTime = partUnavailableEndDateTime;
            }
            // else if the current operation finish date time is equal to before the partUnavailableEndDateTime, then the interruptionEndDateTime should be the current operation finish datetime
            else if(currentOpFinishDateTime.isEqual(partUnavailableEndDateTime) || currentOpFinishDateTime.isBefore(partUnavailableEndDateTime))
            {
                interruptionEndDateTime = currentOpFinishDateTime;
            }

            List<InterruptedOpDetailsDataModel> interruptionDetails = DataReader.getInterruptedOperationDetails(interruptionStartDateTime, interruptionStartDateTime, interruptionEndDateTime, interruptionEndDateTime, currentOperation.getWorkCenterNo());
            System.out.println("");
            
            for (InterruptedOpDetailsDataModel interruptionDetail : interruptionDetails)
            {
                ShopOrderModel shopOrder = DataReader.getShopOrderByPrimaryKey(interruptionDetail.getOrderNo());
                double interruptedOpSequenceNo = shopOrder.getOperations().stream().filter(rec -> rec.getOperationId() == interruptionDetail.getOperationId()).collect(Collectors.toList()).get(0).getOperationSequence();
                DateTime interruptionOnOpEndDateTime = WorkCenterOpAllocModel.incrementTime(interruptionDetail.getInterruptionOnOpStartDateTime(), interruptionDetail.getInterruptedRunTime());
                List<Integer> affectedOps = shopOrder.unscheduleOperationsOnInterruption(interruptionDetail.getInterruptionOnOpStartDateTime(), interruptionOnOpEndDateTime, DataModelEnums.InerruptionType.Normal, interruptedOpSequenceNo);
                
                affectedOrders.put(shopOrder.getOrderNo(), affectedOps);
            }
        }
        
        return affectedOrders;
    }
    

    static class OperationDateComparator implements Comparator<ShopOrderOperationModel>
    {
        @Override
        public int compare(ShopOrderOperationModel o1, ShopOrderOperationModel o2)
        {
            DateTime o1StartDate = DateTimeUtil.concatenateDateTime(o1.getOpStartDate(), o1.getOpStartTime());
            DateTime o2StartDate = DateTimeUtil.concatenateDateTime(o2.getOpStartDate(), o2.getOpStartTime());
            int returnVal = o1StartDate.isAfter(o2StartDate) ? 1 : -1;
            return returnVal;
        }
    }
}
