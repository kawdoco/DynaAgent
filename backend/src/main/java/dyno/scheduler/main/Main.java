package dyno.scheduler.main;

import dyno.scheduler.agents.ManagerAgent;
import dyno.scheduler.data.DataReader;
import dyno.scheduler.datamodels.DataModelEnums;
import dyno.scheduler.datamodels.ShopOrderModel;
import dyno.scheduler.datamodels.ShopOrderOperationModel;
import dyno.scheduler.datamodels.WorkCenterModel;
import dyno.scheduler.jade.AgentsManager;
import dyno.scheduler.restservice.RESTServiceHandler;
import dyno.scheduler.utils.DateTimeUtil;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * Hello world!
 *
 */
public class Main
{
    private static Runtime platformRuntime;
    private static List<AgentController> agentList;// agents's ref
    
    /**
     * starting point of the Application
     * @param args 
     */
    public static void main(String[] args)
    {
        try
        {
            // start the rest services
            startRESTService();
            
            // get the platform
            platformRuntime = AgentsManager.getRuntimeInstance();
       
//            System.out.println("Press a key to Interrupt");
//            System.in.read();
//            
//            WorkCenterUtil.interruptWorkCenterOnPartUnavailability(DateTimeUtil.concatenateDateTime("2018-08-07", "13:00:00"), DateTimeUtil.concatenateDateTime("2018-08-07", "17:00:00"), "WC2");
//            
//            System.out.println("Press a key to continue creating agents");
//            System.in.read();
          
            // create the main container
            ContainerController mainContainer = AgentsManager.createMainContainer(platformRuntime);
            
            // create other containers (currently only one)
            // TODO: should check in to the possibility of supporting multiple containers
            List<String> containerNames = new ArrayList<>();
            containerNames.add("Container0");
            Map<String, ContainerController> createdContainers = AgentsManager.createContainers(platformRuntime, containerNames);
            
            // create monitoring agtents and added them to the main container
            AgentsManager.createMonitoringAgents(mainContainer);
            
            // create other agents and add them to the other container
            agentList = new ArrayList<>();
            ContainerController otherContainer = createdContainers.get(containerNames.get(0));
            
            // create manager agent
            Object [] initData = new Object [] {
                otherContainer
            };
            agentList.add(AgentsManager.createAgent(otherContainer, "ManagerAgent", ManagerAgent.class.getName(), initData));
            
            // start the manager agent
            AgentsManager.startAgents(agentList);
            
        } catch (Exception ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private static void startRESTService()
    {
        Thread restServiceHandler = new Thread(new RESTServiceHandler());
        restServiceHandler.start();
    }
    
    public static void InterruptWorkCenterTest()
    {
                // TEST
//        List<InterruptedOpDetailsDataModel> details = DataReader.getInterruptedOperationDetails(
//                DateTimeUtil.convertStringDateToDateTime("2018-08-08"), DateTimeUtil.convertStringTimeToDateTime("10:00:00"),
//                DateTimeUtil.convertStringDateToDateTime("2018-08-08"), DateTimeUtil.convertStringTimeToDateTime("15:00:00"),
//                "WC2");
        
        // TEST TO INTERRUPT WORK CENTER
        List<ShopOrderModel> shopOrders = DataReader.getShopOrderDetails(true);
        
        List<ShopOrderModel> lowPrioShopOrders = DataReader.getLowerPriorityBlockerShopOrders(DateTimeUtil.convertStringDateToDateTime("2018-08-07"), 
                DateTimeUtil.convertStringTimeToDateTime("08:00:00"), "Milling", 0.54);
        
        WorkCenterModel workCenter = DataReader.getWorkCenterByPrimaryKey("WC1");
        
        ShopOrderModel shopOrder = shopOrders.get(3);
        for (ShopOrderOperationModel operation : shopOrder.getOperations())
        {
//            if (operation.getOperationId() == 100)
//            {
//                operation.splitAndUnscheduleInterruptedOperation(DateTimeUtil.concatenateDateTime("2018-08-08", "09:00:00"), DateTimeUtil.concatenateDateTime("2018-08-08", "12:00:00"));
//            }
            
            if (operation.getOperationId() == 401)
            {
                operation.splitAndUnscheduleInterruptedOperation(DateTimeUtil.concatenateDateTime("2018-08-06", "08:00:00"), DataModelEnums.InerruptionType.Interruption);
            }
            
        }
//
//        WorkCenterModel test = new WorkCenterModel();
//        test.setWorkCenterNo("WC2");
//        //test.scheduleOperationFromBestOffer(DateTimeUtil.concatenateDateTime("2018-08-08", "09:00:00"), 0, 2);
//        test.unscheduleWorkCenterOnInterruption(DateTimeUtil.concatenateDateTime("2018-08-08", "09:00:00"), 3);
    }
}
