/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.agents;

import dyno.scheduler.data.DataEnums;
import dyno.scheduler.data.DataReader;
import dyno.scheduler.datamodels.DataModelEnums;
import dyno.scheduler.datamodels.ShopOrderModel;
import dyno.scheduler.datamodels.ShopOrderOperationModel;
import dyno.scheduler.datamodels.WorkCenterOpAllocModel;
import dyno.scheduler.utils.DateTimeUtil;
import dyno.scheduler.utils.GeneralSettings;
import dyno.scheduler.utils.LogUtil;
import dyno.scheduler.utils.StringUtil;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Prabash
 */
public class ShopOrderAgent extends Agent
{

    private static final long serialVersionUID = 8846265486536139525L;

    // shop order handled by the agent
    private transient ShopOrderModel shopOrder;

    transient DateTimeFormatter dateFormat = DateTimeUtil.getDateFormat();
    transient DateTimeFormatter dateTimeFormat = DateTimeUtil.getDateTimeFormat();

    DateTime unscheduleFromDate = null;
    DateTime unscheduleFromTime = null;

    @Override
    protected void setup()
    {
        super.setup(); //To change body of generated methods, choose Tools | Templates.

        //get the parameters given into the object[]
        final Object[] args = getArguments();
        if (args[0] != null)
        {
            shopOrder = (ShopOrderModel) args[0];

        } else
        {
            System.out.println("Error with the Shop Order arguments");
        }
        addBehaviour(new BQueueNewOperations(shopOrder.getOperations(), shopOrder));
        addBehaviour(new BStartNewOperationScheduler(this));

        System.out.println("the Shop Order Agent " + this.getLocalName() + " is started");
    }

    public ShopOrderOperationModel getOperationById(String operationId)
    {
        ShopOrderOperationModel returnOp = null;
        for (ShopOrderOperationModel operation : shopOrder.getOperations())
        {
            System.out.println(" +++++ operation id : " + operationId);
            System.out.println(" +++++ primary key : " + operation.getPrimaryKey());
            if (operation.getPrimaryKey().equals(operationId))
            {
                returnOp = operation;
                break;
            }

        }
        return returnOp;
        //return shopOrder.getOperations().stream().filter(rec -> rec.getPrimaryKey().equals(operationId)).collect(Collectors.toList()).get(0);
    }

    public DateTime targetOpStartDate(String operationId)
    {
        return shopOrder.getOperationTargetStartDate(operationId);
    }

    public void updateShopOrderOperation(ShopOrderOperationModel operationOb)
    {
        shopOrder.updateOperation(operationOb);
    }

    public DateTime getUnscheduleFromDate()
    {
        return unscheduleFromDate;
    }

    public DateTime getUnscheduleFromTime()
    {
        return unscheduleFromTime;
    }

    public void setUnscheduleFromDate(DateTime unscheduleFromDate)
    {
        this.unscheduleFromDate = unscheduleFromDate;
    }

    public void setUnscheduleFromTime(DateTime unscheduleFromTime)
    {
        this.unscheduleFromTime = unscheduleFromTime;
    }

    @Override
    protected void takeDown()
    {
        // if there's any operation that was unable to be scheduled, Unschedule lower priority shop orders and continue the scheduling process
        if (getUnscheduleFromDate() != null && getUnscheduleFromTime() != null)
        {
            Thread unscheduleProcess = new Thread(new UnscheduleLowerPriorityShopOrders(shopOrder, getUnscheduleFromDate(), getUnscheduleFromTime()));
            unscheduleProcess.start();
        }
        else
        {
            shopOrder.setScheduleData();
        }

        super.takeDown();
    }
}

/**
 * This behavior will queue only the newly created operations
 *
 * @author Prabash
 */
class BQueueNewOperations extends Behaviour
{

    private static final long serialVersionUID = 5767062419481143156L;
    private boolean operationsQueued = false;

    // The list of known workcenter agents
    private AID[] managerAgents;

    // shop order operations will be added to the queue to be processed sequentially
    transient List<ShopOrderOperationModel> operations = new ArrayList<>();
    ShopOrderModel shopOrder;

    public BQueueNewOperations(List<ShopOrderOperationModel> operations, ShopOrderModel shopOrder)
    {
        this.operations.clear();
        this.operations.addAll(operations);
        this.shopOrder = shopOrder;
    }

    @Override
    public void action()
    {
        // for operations that are Created or Interrupted sorted by the operation Sequence
        for (ShopOrderOperationModel operation : operations.stream().filter(rec
                -> rec.getOperationStatus().equals(DataModelEnums.OperationStatus.Unscheduled)
                || rec.getOperationStatus().equals(DataModelEnums.OperationStatus.Interrupted)).sorted(new ShopOrderOperationModel()).collect(Collectors.toList()))
        {
            // Update the list of seller agents
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription serviceDesc = new ServiceDescription();
            // each agent belonging to a certain work center type will have "work-center-<TYPE>" in here.
            // therefore this should be dynamically set.
            serviceDesc.setType("manager-agent");
            serviceDesc.setName("manager-agent-service");

            template.addServices(serviceDesc);
            try
            {
                // find the agents belonging to the certain work center type
                DFAgentDescription[] result = DFService.search(myAgent, template);
                managerAgents = new AID[result.length];
                for (int i = 0; i < result.length; ++i)
                {
                    managerAgents[i] = result[i].getName();
                    System.out.println(managerAgents[i].getName());
                }

                // Send the cfp (Call for Proposal) message for the operation to the manager agent
                ACLMessage cfpMessage = new ACLMessage(ACLMessage.REQUEST);
                // since there's currently only one manager, this will be 0
                for (int i = 0; i < managerAgents.length; ++i)
                {
                    cfpMessage.addReceiver(managerAgents[i]);
                }
                cfpMessage.setContent(StringUtil.generateMessageContent(
                        String.valueOf(shopOrder.getOrderNo()), // Order No
                        String.valueOf(shopOrder.calculateImportance()), // Importance
                        operation.getPrimaryKey(), // Primary Key
                        String.valueOf(operation.getOperationSequence()))); // Operation Sequence
                myAgent.send(cfpMessage);

            } catch (FIPAException ex)
            {
                LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            }
        }
        operationsQueued = true;
    }

    @Override
    public boolean done()
    {
        return operationsQueued;
    }
}

class BStartNewOperationScheduler extends CyclicBehaviour
{

    private final ShopOrderAgent currentAgent;
    private static final long serialVersionUID = 3149611413717448878L;

    static final String CONVERSATION_ID = "work-center-request";
    transient ShopOrderOperationModel currentOperation;

    // The target date that the operation should be started (FS) or Ended (BS)
    private DateTime targetOperationStartDate = null;

    int step = 0; // is used in the switch statement inside the action method.
    AID bestWorkCenter; // work center that provides the best target date
    DateTime currentOfferedDate; // Date offered by the work center agent
    DateTime bestOfferedDate; // The best possible start date if forward scheduling / end date if backward scheduling
    int workCenterRepliesCount = 0; // The counter of replies from work center agents
    MessageTemplate msgTemplate; // The template to receive replies
    ACLMessage workCenterReply;
    ACLMessage replyMgr;
    int acceptProposal = 0; // acceptProposal Value should be 1 in order to send the acceptance of a date time proposal to a work center agent. This will become true if the offered date falls on or before the estimated latestOpFinishDateTime

    // The list of known workcenter agents
    private AID[] workCenterAgents;

    public BStartNewOperationScheduler(ShopOrderAgent currentAgent)
    {
        this.currentAgent = currentAgent;
    }

    @Override
    public void action()
    {
        MessageTemplate mt = MessageTemplate.MatchConversationId("OPERATION_PROCESSING_QUEUE");
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null)
        {
            if (msg.getPerformative() == ACLMessage.PROPAGATE)
            {
                replyMgr = msg.createReply();
                String opIdToSchedule = msg.getContent();
                currentOperation = currentAgent.getOperationById(opIdToSchedule);
                // if the previous operation has an acceptedProposal, then only the current operation should start its usual process by taking the target op start date from the prev. op
                // else just start the process and it will notify to the manager agent without scheduling the operation
                if (currentOperation != null && acceptProposal == 0)
                {
                    // get the target operation start date before scheduling
                    targetOperationStartDate = currentAgent.targetOpStartDate(currentOperation.getPrimaryKey());
                    getWorkCenterAgents();

                    step = 0;
                    workCenterRepliesCount = 0;
                    bestOfferedDate = null;
                    scheduleOperation();
                } else
                {
                    step = 0;
                    workCenterRepliesCount = 0;
                    bestOfferedDate = null;
                    scheduleOperation();
                }
            } else
            {
                workCenterReply = msg;
                scheduleOperation();
            }
        }
    }

    public void getWorkCenterAgents()
    {
        // Update the list of seller agents
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription serviceDesc = new ServiceDescription();
        // each agent belonging to a certain work center type will have "work-center-<TYPE>" in here.
        // therefore this should be dynamically set.
        serviceDesc.setType("work-center-" + currentOperation.getWorkCenterType());
        serviceDesc.setName("schedule-work-center-service");

        template.addServices(serviceDesc);
        try
        {
            // find the agents belonging to the certain work center type
            DFAgentDescription[] result = DFService.search(myAgent, template);
            System.out.println("Found the WorkCenterAgents :");
            workCenterAgents = new AID[result.length];
            for (int i = 0; i < result.length; ++i)
            {
                workCenterAgents[i] = result[i].getName();
                System.out.println(workCenterAgents[i].getName());
            }
        } catch (FIPAException ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
        }
    }

    public void scheduleOperation()
    {
        switch (step)
        {
            case 0:
            {
                // if acceptProposal is 2, that means the previous operation has not met it estimated latest finish date, therefore all the subsequent operations should be in the unscheduled/creataed status
                if (acceptProposal != 2)
                {
                    // Send the cfp (Call for Proposal) to all sellers
                    ACLMessage cfpMessage = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < workCenterAgents.length; ++i)
                    {
                        cfpMessage.addReceiver(workCenterAgents[i]);
                    }
                    cfpMessage.setContent(StringUtil.generateMessageContent(targetOperationStartDate.toString(DateTimeUtil.getDateTimeFormat()), String.valueOf(currentOperation.getWorkCenterRuntime()), currentOperation.getPartNo()));
                    cfpMessage.setConversationId(CONVERSATION_ID);
                    cfpMessage.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value (can be something with the Shop Order No + operation No. and time)
                    myAgent.send(cfpMessage);

                    // Prepare the template to get proposals
                    msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId(CONVERSATION_ID),
                            MessageTemplate.MatchInReplyTo(cfpMessage.getReplyWith()));
                    step = 1;
                    break;
                } else
                {
                    System.out.println("++++++ OPERATION " + currentOperation.getOperationId() + " WAS NOT SCHEDULED: Could not meet Estimated Latest Operation Finish Date : "
                            + DateTimeUtil.concatenateDateTime(currentOperation.getLatestOpFinishDate(), currentOperation.getLatestOpFinishTime()) + " ! ++++++++");
                    System.out.println("______________________________________________________________________________________________________________________________________");
                    notifyManagerAgent();
                    break;
                }
            }
            case 1:
            {
                
                if (workCenterReply != null)
                {
                    // Reply received
                    if (workCenterReply.getPerformative() == ACLMessage.PROPOSE)
                    {
                        // This is an offer, recieved with the date and the time
                        currentOfferedDate = DateTimeUtil.getDateTimeFormat().parseDateTime(workCenterReply.getContent());
                        // get the estimated latest operation end datetime 
                        DateTime estimatedLatestOpFinishDateTime = DateTimeUtil.concatenateDateTime(currentOperation.getLatestOpFinishDate(), currentOperation.getLatestOpFinishTime());
                        // get the estimated operation end date time based on the CURRENT BEST OFFER
                        DateTime estimatedCurrentOfferFinishDateTime = WorkCenterOpAllocModel.incrementTime(currentOfferedDate, currentOperation.getWorkCenterRuntime());

                        System.out.println("++++++ currentOfferedDate : " + currentOfferedDate + " by Work Center Agent : " + workCenterReply.getSender());
                        System.out.println("++++++ targetOperationDate : " + targetOperationStartDate);
                        System.out.println("++++++ bestOfferedDate : " + bestOfferedDate);
                        System.out.println("++++++ estimatedLatestOpFinishDateTime : " + estimatedLatestOpFinishDateTime);
                        System.out.println("++++++ estimatedCurrentOfferFinishDateTime : " + estimatedCurrentOfferFinishDateTime);

                        // if forward scheduling the offered date should be the earliest date/time that comes on or after the target date
                        if (bestWorkCenter == null || ((currentOfferedDate.equals(targetOperationStartDate) || currentOfferedDate.isAfter(targetOperationStartDate)) && currentOfferedDate.isBefore(bestOfferedDate)))
                        {
                            // Check if the estimatedCurrentOfferFinishDateTime is on or before the estimated latest operation finish datetime for that operation and if so set it as the best currentOfferedDate
                            if (estimatedCurrentOfferFinishDateTime.isBefore(estimatedLatestOpFinishDateTime) || estimatedCurrentOfferFinishDateTime.isEqual(estimatedLatestOpFinishDateTime))
                            {
                                // This is the best offer at present
                                bestOfferedDate = currentOfferedDate;
                                bestWorkCenter = workCenterReply.getSender();
                                acceptProposal = 1;
                                System.out.println("Current best offered time : " + bestOfferedDate + " by Work Center Agent : " + bestWorkCenter);
                            } else
                            {
                                // if there are at least 1 proposal that can be accepted this should not be set to 2.
                                if (acceptProposal != 1)
                                {
                                    currentAgent.setUnscheduleFromDate(targetOperationStartDate);
                                    currentAgent.setUnscheduleFromTime(targetOperationStartDate);
                                    acceptProposal = 2;
                                }
                            }
                        }

                        workCenterRepliesCount++;
                    }

                    if (workCenterRepliesCount >= workCenterAgents.length)
                    {
                        if (acceptProposal == 1)
                        {
                            // We received all replies
                            step = 2;
                            System.out.println("++++++ RECEIVED ALL OFFERES! ++++++++");

                            // Send the confirmation to the work center that sent the best date
                            ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                            order.addReceiver(bestWorkCenter);
                            order.setContent(StringUtil.generateMessageContent(String.valueOf(currentOperation.getOperationId()), String.valueOf(currentOperation.getWorkCenterRuntime())));
                            order.setConversationId(CONVERSATION_ID);
                            order.setReplyWith("setOperation" + System.currentTimeMillis());
                            myAgent.send(order);
                            // Prepare the template to get the purchase order workCenterReply
                            msgTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId(CONVERSATION_ID),
                                    MessageTemplate.MatchInReplyTo(order.getReplyWith()));

                            step = 3;
                        } else
                        {
                            System.out.println("++++++ OPERATION " + currentOperation.getOperationId() + " WAS NOT SCHEDULED: Could not meet Estimated Latest Operation Finish Date : "
                                    + DateTimeUtil.concatenateDateTime(currentOperation.getLatestOpFinishDate(), currentOperation.getLatestOpFinishTime()) + " ! ++++++++");
                            System.out.println("______________________________________________________________________________________________________________________________________");
                            notifyManagerAgent();
                        }

                        break;
                    }
                } else
                {
                    block();
                }
                break;
            }
            case 2:
            {
                break;
            }
            case 3:
            {
                if (workCenterReply != null)
                {
                    // confirmation workCenterReply received
                    if (workCenterReply.getPerformative() == ACLMessage.INFORM)
                    {

                        // the next possible op start date and the work center no. is sent by the work center agent in the workCenterReply
                        String[] msgContent = StringUtil.readMessageContent(workCenterReply.getContent());
                        targetOperationStartDate = DateTime.parse(msgContent[0], DateTimeUtil.getDateTimeFormat());
                        String workCenterNo = msgContent[1];

                        // current operation details should be updated on the database
                        updateOperationDetails(bestOfferedDate, targetOperationStartDate, workCenterNo, DataModelEnums.OperationStatus.Scheduled);
                        // update operation details on the current shop order object
                        currentAgent.updateShopOrderOperation(currentOperation);

                        // Date set successfully. We can terminate
                        System.out.println("Operation " + currentOperation.getOperationId() + " was successfully scheduled on " + bestOfferedDate + " at work center : " + workCenterReply.getSender().getName());
                        System.out.println("______________________________________________________________________________________________________________________________________");
                        // after accepting the proposal for previous operation, it should be reset.
                        acceptProposal = 0;

                    } else
                    {
                        System.out.println("Operation " + currentOperation.getOperationId() + " could not be scheduled on " + bestOfferedDate + " at work center : " + workCenterReply.getSender().getName());
                    }

                    notifyManagerAgent();
                    step = 4;

                } else
                {
                    block();
                }
                break;
            }
            default:
                break;
        }
    }

    private void updateOperationDetails(DateTime opStartDate, DateTime opFinishDate, String workCenterNo, DataModelEnums.OperationStatus opStatus)
    {
        currentOperation.setOpStartDate(opStartDate);
        currentOperation.setOpStartTime(opStartDate);

        // when the capacity type is finite, the operation cannot end at 13:00:00, it should always end at 12:00:00.
        // therefore if 13:00:00 is set as the end time, reduce 1 hour and set 12:00:00 as the operation finish time
        if (GeneralSettings.getCapacityType() == DataEnums.CapacityType.FiniteCapacity)
        {
            if (opFinishDate.toString(DateTimeUtil.getTimeFormat()).equals("13:00:00"))
            {
                opFinishDate = opFinishDate.minusHours(1);
            }
        }
        currentOperation.setOpFinishDate(opFinishDate);
        currentOperation.setOpFinishTime(opFinishDate);

        currentOperation.setWorkCenterNo(workCenterNo);
        currentOperation.setOperationStatus(opStatus);
        currentOperation.updateOperationDetails();
    }

    private void notifyManagerAgent()
    {
        if (replyMgr != null)
        {
            replyMgr.setPerformative(ACLMessage.AGREE);
            replyMgr.setContent("Release Lock");
            myAgent.send(replyMgr);
        }
    }
}

class UnscheduleLowerPriorityShopOrders implements Runnable
{

    ShopOrderModel shopOrder;
    DateTime unscheduleFromDate;
    DateTime unscheduleFromTime;

    public UnscheduleLowerPriorityShopOrders(ShopOrderModel shopOrder, DateTime unscheduleFromDate, DateTime unscheduleFromTime)
    {
        this.shopOrder = shopOrder;
        this.unscheduleFromDate = unscheduleFromDate;
        this.unscheduleFromTime = unscheduleFromTime;
    }

    @Override
    public void run()
    {
        HashSet<String> workCenterTypes = new HashSet<>();

        // When taking down the Shop Order Agent, check if there are any operations that have not been scheduled. If so perform dynamic unscheduling of low priority orders
        // related to the work center types of the unscheduled operations
        List<ShopOrderOperationModel> unscheduledOperations = shopOrder.getOperations().stream().filter(rec
                -> rec.getOperationStatus().equals(DataModelEnums.OperationStatus.Unscheduled)
                || rec.getOperationStatus().equals(DataModelEnums.OperationStatus.Interrupted)).sorted(new ShopOrderOperationModel()).collect(Collectors.toList());
        for (int i = 0; i < unscheduledOperations.size(); i++)
        {
            workCenterTypes.add(unscheduledOperations.get(i).getWorkCenterType());
        }
        // Concatenate unscheduled from date and t ime
        DateTime unscheduleFromDateTime = DateTimeUtil.concatenateDateTime(unscheduleFromDate, unscheduleFromTime);

        // for each of the work center types related to the unscheduled operations
        for (String workCenterType : workCenterTypes)
        {
            // get the low priority shop orders to be unscheduled to make way for these high priority operations
            List<ShopOrderModel> lowerPriorityShopOrders = DataReader.getLowerPriorityBlockerShopOrders(unscheduleFromDate, unscheduleFromTime,
                    workCenterType, shopOrder.getImportance());
            // foreach of the orders
            for (ShopOrderModel lowerPriorityShopOrder : lowerPriorityShopOrders)
            {
                lowerPriorityShopOrder.unscheduleOperationsFrom(unscheduleFromDateTime);
            }
        }
    }
}
