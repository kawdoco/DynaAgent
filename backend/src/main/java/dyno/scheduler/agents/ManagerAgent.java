/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.agents;

import dyno.scheduler.data.DataReader;
import dyno.scheduler.datamodels.DataModel;
import dyno.scheduler.jade.AgentsManager;
import dyno.scheduler.jade.ISchedulerAgent;
import dyno.scheduler.utils.LogUtil;
import dyno.scheduler.utils.StringUtil;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Prabash
 */
public class ManagerAgent extends Agent implements ISchedulerAgent
{

    private static final long serialVersionUID = 3369137004053108334L;
    private static transient List<AgentController> agentList;// agents's ref

    private static final Queue<ACLMessage> NEW_OPERATION_SCHEDULE_REQUESTS_QUEUE = new ConcurrentLinkedQueue<>();
    private static boolean NEW_OPERATION_SCHEDULED;
    private static final Object NEW_OPERATION_SCHEDULE_LOCK = new Object();
    
    static BQueueNewOperationScheduleRequests queueNewOperationScheduleRequests;
    static BProcessNewOperationScheduleQueue processNewOperationScheduleQueue;
    static BNotifyNewOperationScheduleQueue notifyNewOperationScheduleQueue;
    static BCreateAgents createAgentsBehavior;
    
    private static final long CREATE_AGENTS_INTERVAL = 30000L;
    private static final long OPERATION_QUEUE_PROCESS_INTERVAL = 5000L;
            
    @Override
    protected void setup()
    {
        super.setup();

        registerAgentService();

        //get the parameters given into the object[]
        final Object[] args = getArguments();

        if (args[0] != null)
        {
            ContainerController container = (ContainerController) args[0];
            createAgentsBehavior = new BCreateAgents(this, CREATE_AGENTS_INTERVAL, container);
            addBehaviour(createAgentsBehavior);
        }
    }

    @Override
    protected void takeDown()
    {
        super.takeDown(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerAgentService()
    {
        // Register the book-selling service in the yellow pages
        DFAgentDescription dfAgentDesc = new DFAgentDescription();
        dfAgentDesc.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();

        // each agent belonging to a certain work center type will have "work-center-<TYPE>" in here.
        // therefore this should be dynamically set.
        serviceDescription.setType("manager-agent");
        serviceDescription.setName("manager-agent-service");
        dfAgentDesc.addServices(serviceDescription);
        try
        {
            // register the work center agent service
            DFService.register(this, dfAgentDesc);
        } catch (FIPAException fe)
        {
            LogUtil.logSevereErrorMessage(this, fe.getMessage(), fe);
        }
    }

    @Override
    public void registerAgentService(DataModel obj)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void takeDownAgents()
    {
        if (agentList != null)
        {
            for (AgentController agentController : agentList)
            {
                try
                {
                    agentController.kill();
                } catch (StaleProxyException ex)
                {
                    Logger.getLogger(ManagerAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            agentList.clear();
        }
    }

    public static void addScheduleOperationRequest(ACLMessage scheduleOpRequest)
    {
        NEW_OPERATION_SCHEDULE_REQUESTS_QUEUE.add(scheduleOpRequest);
    }

    public static void clearScheduleOperationRequestsQueue()
    {
        NEW_OPERATION_SCHEDULE_REQUESTS_QUEUE.clear();
    }

    public static boolean scheduleOperationsQueueIsEmpty()
    {
        if (NEW_OPERATION_SCHEDULE_REQUESTS_QUEUE != null)
        {
            return NEW_OPERATION_SCHEDULE_REQUESTS_QUEUE.isEmpty();
        } else
        {
            return true;
        }
    }

    public static ACLMessage getNextFromScheduleOperationsQueue()
    {
        if (NEW_OPERATION_SCHEDULE_REQUESTS_QUEUE != null)
        {
            if (!NEW_OPERATION_SCHEDULE_REQUESTS_QUEUE.isEmpty())
            {
                return NEW_OPERATION_SCHEDULE_REQUESTS_QUEUE.poll();
            } else
            {
                return null;
            }
        } else
        {
            return null;
        }
    }

    public static Queue<ACLMessage> getQueueSnapshot()
    {
        return NEW_OPERATION_SCHEDULE_REQUESTS_QUEUE;
    }

    static class BCreateAgents extends TickerBehaviour
    {
        private static final long serialVersionUID = 730162033787578690L;
        private ContainerController container;

        public BCreateAgents(Agent agent, long period, ContainerController container)
        {
            super(agent, period);
            this.container = container;
        }

        @Override
        protected void onTick()
        {
            agentList = new ArrayList<>();
            agentList.addAll(AgentsManager.createAgentsFromData(container, DataReader.getUnscheduledOrders()));
            agentList.addAll(AgentsManager.createAgentsFromData(container, DataReader.getUnscheduledOpWorkCenters()));
            
            if (agentList.size() > 0)
            {
                try
                {
                    queueNewOperationScheduleRequests = new BQueueNewOperationScheduleRequests();
                    processNewOperationScheduleQueue = new BProcessNewOperationScheduleQueue(super.myAgent, OPERATION_QUEUE_PROCESS_INTERVAL);
                    notifyNewOperationScheduleQueue = new BNotifyNewOperationScheduleQueue();
                    
                    // Only add these behaviors when new shop orders are available to be scheduled
                    super.myAgent.addBehaviour(queueNewOperationScheduleRequests);
                    super.myAgent.addBehaviour(processNewOperationScheduleQueue);
                    super.myAgent.addBehaviour(notifyNewOperationScheduleQueue);
                    
                    // At that point remove the create agents behavior
                    super.myAgent.removeBehaviour(createAgentsBehavior);
                    
                    System.out.println("Press a key to start the agents");
                    System.in.read();
                } catch (Exception ex)
                {
                    LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
                }

                AgentsManager.startAgents(agentList);
            }
            else
            {
                
                
                System.out.println(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% NO ORDERS TO BE SCHEDULED!! ");
            }
        }
    }

    static class BQueueNewOperationScheduleRequests extends CyclicBehaviour
    {

        private static final long serialVersionUID = 8948436530894606064L;

        // <editor-fold desc="overriden methods" defaultstate="collapsed">
        
        /**
         * action overridden method
         */
        @Override
        public void action()
        {

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null)
            {
                ManagerAgent.addScheduleOperationRequest(msg);
                System.out.println("+++ Operation " + msg.getContent() + " from " + msg.getSender() + "  is queued!");
            } else
            {
                block();
            }
        }
        // </editor-fold>
    }

    static class BProcessNewOperationScheduleQueue extends TickerBehaviour
    {

        private static final long serialVersionUID = -6980306431467493127L;

        public BProcessNewOperationScheduleQueue(Agent agent, long period)
        {
            super(agent, period);
        }

        @Override
        protected void onTick()
        {
            System.out.println("operation queue timer thread locked!");
            Thread thread = new Thread(new ProcessOperationScheduleQueue(myAgent, this));
            thread.start();
        }
    }

    static class ProcessOperationScheduleQueue implements Runnable
    {

        Agent myAgent;
        BProcessNewOperationScheduleQueue tickerInstance;
        ArrayList<ACLMessage> processingQueue;

        public ProcessOperationScheduleQueue(Agent agent, BProcessNewOperationScheduleQueue tickerInstance)
        {
            this.myAgent = agent;
            this.tickerInstance = tickerInstance;
        }

        @Override
        public void run()
        {
            // remove the ticker behavior when processing the queue, so the queue processing wont overlap
            if (!ManagerAgent.scheduleOperationsQueueIsEmpty())
            {
                myAgent.removeBehaviour(tickerInstance);

                processingQueue = new ArrayList<>();
                while (!ManagerAgent.scheduleOperationsQueueIsEmpty())
                {
                    ACLMessage request = ManagerAgent.getNextFromScheduleOperationsQueue();
                    processingQueue.add(request);
                }

                processingQueue.sort(new ShopOrderACLMessageComparator());
                for (ACLMessage request : processingQueue)
                {
                    scheduleOperation(request);
                }
                // finally take down the agents created after processing the queue
                if (processingQueue.size() > 0)
                {
                    ManagerAgent.takeDownAgents();
                    
                    // After the scheduling process is done, remove the operation scheduling related behaviors
                    myAgent.removeBehaviour(queueNewOperationScheduleRequests);
                    myAgent.removeBehaviour(processNewOperationScheduleQueue);
                    myAgent.removeBehaviour(notifyNewOperationScheduleQueue);
                    
                    // Add the create agents behavior once the queue is processed
                    myAgent.addBehaviour(createAgentsBehavior);
                }
            } else
            {
                System.out.println(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% NO OPERATIONS TO BE SCHEDULED!! ");
                ManagerAgent.takeDownAgents();
            }
        }

        public boolean scheduleOperation(ACLMessage request)
        {
            NEW_OPERATION_SCHEDULED = false;
            synchronized (NEW_OPERATION_SCHEDULE_LOCK)
            {
                try
                {
                    AID shopOrderAgent = request.getSender();
                    // get the string array of message content
                    String[] msgContent = StringUtil.readMessageContent(request.getContent());
                    // second index of the message content array will have the operation id
                    String operationId = msgContent[2];

                    System.out.println(" ++++++  Shop Order Agent : " + shopOrderAgent);
                    System.out.println(" ++++++  operationId : " + operationId);

                    ACLMessage startOpScheduleMsg = new ACLMessage(ACLMessage.PROPAGATE);
                    startOpScheduleMsg.setConversationId("OPERATION_PROCESSING_QUEUE");
                    startOpScheduleMsg.addReceiver(shopOrderAgent);
                    startOpScheduleMsg.setContent(operationId);

                    if (!NEW_OPERATION_SCHEDULED)
                    {
                        myAgent.send(startOpScheduleMsg);
                        NEW_OPERATION_SCHEDULE_LOCK.wait();

                        System.out.println("operation queue locked to schedule operation : " + operationId);
                    }
                } catch (InterruptedException ex)
                {
                    LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
                }
            }

            return NEW_OPERATION_SCHEDULED;
        }
    }

    static class ShopOrderACLMessageComparator implements Comparator<ACLMessage>
    {

        @Override
        public int compare(ACLMessage o1, ACLMessage o2)
        {
            String[] o1_msgContent = StringUtil.readMessageContent(o1.getContent());
            String[] o2_msgContent = StringUtil.readMessageContent(o2.getContent());

            // Sort first by the importance
            Double o1_importance = Double.parseDouble(o1_msgContent[1]);
            Double o2_importance = Double.parseDouble(o2_msgContent[1]);
            int importanceResult = o2_importance.compareTo(o1_importance);
            if (importanceResult != 0)
            {
                return importanceResult;
            }

            // Sort second by the shop order no.
            Integer o1_shopOrderNo = Integer.parseInt(o1_msgContent[0]);
            Integer o2_shopOrderNo = Integer.parseInt(o2_msgContent[0]);
            int shopOrderNoResult = o1_shopOrderNo.compareTo(o2_shopOrderNo);
            if (shopOrderNoResult != 0)
            {
                return shopOrderNoResult;
            }

            // Sort last by the operation sequence.
            Double o1_operationSequence = Double.parseDouble(o1_msgContent[3]);
            Double o2_operationSequence = Double.parseDouble(o2_msgContent[3]);
            return o1_operationSequence.compareTo(o2_operationSequence);
        }
    }

    static class BNotifyNewOperationScheduleQueue extends CyclicBehaviour
    {

        private static final long serialVersionUID = -8707253852585581218L;

        @Override
        public void action()
        {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null)
            {
                try
                {
                    synchronized (NEW_OPERATION_SCHEDULE_LOCK)
                    {
                        System.out.println(msg.getContent());
                        System.out.println("Notify queue unlock");

                        NEW_OPERATION_SCHEDULED = true;
                        NEW_OPERATION_SCHEDULE_LOCK.notifyAll();
                    }

                } catch (Exception ex)
                {
                    LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
                }
            }
        }
    }
}
