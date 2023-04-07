/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.agents;

import dyno.scheduler.datamodels.WorkCenterModel;
import dyno.scheduler.datamodels.WorkCenterOpAllocModel;
import dyno.scheduler.utils.DateTimeUtil;
import dyno.scheduler.utils.GeneralSettings;
import dyno.scheduler.utils.LogUtil;
import dyno.scheduler.utils.StringUtil;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Prabash
 */
public class WorkCenterAgent extends Agent
{
    private static final long serialVersionUID = 263288753945767774L;
    private transient WorkCenterModel workCenter;
    
    // the target date requested by the shop order agent for a particular operation
    DateTime requestedOpDate;
    // the best date and timeblock available to the work center agent
    DateTime bestOfferedDate;

    // <editor-fold desc="overriden methods" defaultstate="collapsed">
    
    /**
     * takeDown overridden method
     */
    @Override
    protected void takeDown()
    {
        try
        {
            // If there are any temporary unavailable allocations, they should be made available again
            // workCenter.makeAvailableTempUnavailableAllocs();
            DFService.deregister(this);
            super.takeDown(); //To change body of generated methods, choose Tools | Templates.
        } catch (FIPAException ex)
        {
            Logger.getLogger(WorkCenterAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * setup overridden method
     */
    @Override
    protected void setup()
    {
        // Register the work center agent
        DFAgentDescription dfAgentDesc = new DFAgentDescription();
        dfAgentDesc.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();

        //get the parameters given into the object[]
        final Object[] args = getArguments();
        if (args[0] != null)
        {
            workCenter = (WorkCenterModel) args[0];

            // each agent belonging to a certain work center type will have "work-center-<TYPE>" in here.
            // therefore this should be dynamically set.
            serviceDescription.setType("work-center-" + workCenter.getWorkCenterType());
            serviceDescription.setName("schedule-work-center-service");
            dfAgentDesc.addServices(serviceDescription);
            try
            {
                // register the work center agent service
                DFService.register(this, dfAgentDesc);
            } catch (FIPAException fe)
            {
                LogUtil.logSevereErrorMessage(this, MSG_QUEUE_CLASS, fe);
            }

        } else
        {
            System.out.println("Error with the Work Center arguments");
        }

        //Add the behaviours
        //addBehaviour(new ReceiveMessage(this));
        addBehaviour(new BOfferBestAvailableDate());
        addBehaviour(new BAssignOperationToWorkCenter());

        System.out.println("the Work Center agent " + this.getLocalName() + " is started");
    }

    
    // </editor-fold>
    
    // <editor-fold desc="behaviors" defaultstate="collapsed"> 
    
    // <editor-fold desc="BOfferBestAvailableDate behavior" defaultstate="collapsed"> 
    
    /**
     * This behaviour offer the best available date for a specific operation date request
     * sent in by the ShopOrderAgent
     */
    private class BOfferBestAvailableDate extends CyclicBehaviour
    {
        private static final long serialVersionUID = -7860101940083496148L;
        
        DateTimeFormatter dateFormat = DateTimeUtil.getDateFormat();
        DateTimeFormatter dateTimeFormat = DateTimeUtil.getDateTimeFormat();
        
        // <editor-fold desc="overriden methods" defaultstate="collapsed">
        
        /**
         * action overridden method
         */
        @Override
        public void action()
        {
            
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null)
            {
                // CFP Message received. Process it
                String [] messageContent = StringUtil.readMessageContent(msg.getContent());
                requestedOpDate = dateTimeFormat.parseDateTime(messageContent[0]);
                int workCenterRuntime = Double.valueOf(messageContent[1]).intValue();
                String partNo = (messageContent[2]);
                ACLMessage reply = msg.createReply();
                
                // you should get the date related to the work center that is the earliest date after the target date
                bestOfferedDate = workCenter.getBestDateTimeOffer(requestedOpDate, workCenterRuntime, partNo);

                // reply with the earliest available date/timeblock that comes after the target date
                reply.setPerformative(ACLMessage.PROPOSE);

                // offer should be included with the time as well, therefore the dateTimeFormat is used
                reply.setContent(bestOfferedDate.toString(dateTimeFormat));
                
                reply.setConversationId("OPERATION_PROCESSING_QUEUE");
                myAgent.send(reply);
                
            } else
            {
                block();
            }
        }
        
        // </editor-fold>
    }
    
    // </editor-fold>
    
    // <editor-fold desc="BAssignOperationToWorkCenter behavior" defaultstate="collapsed"> 
    
    /**
     * This behaviour gets the acknowledgement for the offered date from the ShopOrderAgent
     * and schedules the operation on the offered date/time
     */
    private class BAssignOperationToWorkCenter extends CyclicBehaviour
    {
        private static final long serialVersionUID = 4660381226186754715L;
        
        // <editor-fold desc="overriden methods" defaultstate="collapsed">
        
        /**
         * action overridden method
         */
        @Override
        public void action()
        {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null)
            {
                // ACCEPT_PROPOSAL Message received with the OperationNo
                String [] messageContent = StringUtil.readMessageContent(msg.getContent());
                String operationId = messageContent[0];
                int workCenterRuntime = Double.valueOf(messageContent[1]).intValue();
                ACLMessage reply = msg.createReply();

                //Integer price = (Integer) catalogue.remove(title);
                if (bestOfferedDate != null)
                {
                    reply.setPerformative(ACLMessage.INFORM);
                    
                    // return the time block date and the days added
                    HashMap<String, Object> timeBlockDetails = workCenter.scheduleOperationFromBestOffer(bestOfferedDate, Integer.parseInt(operationId), workCenterRuntime);
                    // set the end time of the operation to be taken as the beginning of the next operation when scheduling
                    // in order to do so, increment the received TimeBlockName by 1

                    // calculate the finish date time of the current operation (to update on the table/ to be used as the start operation of the next sequential operation)
                    LocalDate currentOpFinishDate = bestOfferedDate.plusDays(Integer.parseInt(timeBlockDetails.get(GeneralSettings.getStrDaysAdded()).toString())).toLocalDate();
                    LocalTime currentOpFinishTime = WorkCenterOpAllocModel.getTimeBlockValue(timeBlockDetails.get(GeneralSettings.getStrTimeBlockName()).toString());
                    DateTime currentOpFinishDateTime = DateTimeUtil.concatenateDateTime(currentOpFinishDate, currentOpFinishTime);
                    
                    // set the end date time of the current operation (possible start date of the next operation)
                    // and the work center no in the reply content
                    reply.setContent(StringUtil.generateMessageContent(currentOpFinishDateTime.toString(DateTimeUtil.getDateTimeFormat()), 
                            workCenter.getWorkCenterNo()));
                    
                    //update the excel sheet with the date
                    System.out.println("WC --> SCHEDULED OPERATION " + Integer.valueOf(operationId) + " ON " + bestOfferedDate);
                }
                reply.setConversationId("OPERATION_PROCESSING_QUEUE");
                myAgent.send(reply);
            } else
            {
                block();
            }
        }
        
        // </editor-fold>
    }
    
    // </editor-fold>
    
    // </editor-fold>
}
