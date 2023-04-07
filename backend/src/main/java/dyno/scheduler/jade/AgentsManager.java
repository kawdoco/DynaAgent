/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.jade;

import dyno.scheduler.agents.ShopOrderAgent;
import dyno.scheduler.agents.WorkCenterAgent;
import dyno.scheduler.datamodels.DataModel;
import dyno.scheduler.datamodels.ShopOrderModel;
import dyno.scheduler.datamodels.WorkCenterModel;
import dyno.scheduler.utils.GeneralSettings;
import dyno.scheduler.utils.LogUtil;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Prabash
 */
public class AgentsManager
{
    private static final String CLASS_NAME = AgentsManager.class.getName();

    /**
     * return an instance of the runtime
     * @return the runtime instance
     */
    public static Runtime getRuntimeInstance()
    {
        return Runtime.instance();
    }
    
    /**
     * This method will create the main container for  the runtime platform
     * 
     * @param runtime runtime on which the main container should be created
     * @return created main ContainerController reference
     */
    public static ContainerController createMainContainer(Runtime runtime)
    {
        // Create a platform (main container + DF + AMS)
        Profile mainProf = new ProfileImpl(GeneralSettings.getHostName(), 8888, null);
        ContainerController mainContainerRef = runtime.createMainContainer(mainProf); // Including DF and AMS

        return mainContainerRef;
    }
    
    /**
     * Create the containers used to hold the agents
     *
     * @param runtime The reference to the main container
     * @param containerNames the list of container names that should be created
     * @return an HashMap associating the name of a container and its object
     * reference.
     */
    public static Map<String, ContainerController> createContainers(Runtime runtime, List<String> containerNames)
    {
        HashMap<String, ContainerController> containerList = new HashMap<>();

        containerNames.stream().forEach(container ->
        {
            ProfileImpl pContainer = new ProfileImpl(null, 8888, null);
            // ContainerController replace AgentContainer in the new versions of Jade.
            ContainerController containerRef = runtime.createAgentContainer(pContainer);
            containerList.put(container, containerRef);
        });

        LogUtil.logInfoMessage(CLASS_NAME, "Launching containers done");
        return containerList;
    }

    /**
     * this method is used to create work center agents depending on a set of
     * data
     *
     * @param container the container to which the agents should be added
     * @param dataSet agents will be added for each of the objects in the
     * @return the set of agents created
     */
    public static List<AgentController> createAgentsFromData(ContainerController container, List<? extends DataModel> dataSet)
    {
        String agentName;
        List<AgentController> agentsList = new ArrayList();

        for (DataModel data : dataSet)
        {
            Object[] initInfo = new Object[]
                {
                    data
                };//used to give informations to the agent
            
            agentName = data.getAgentPrefix() + data.getPrimaryKey();
            agentsList.add(createAgent(container, agentName, getAgentClassNameByModel(data), initInfo));
        }
        return agentsList;
    }
    
    
    public static AgentController createAgent(ContainerController container, String agentName, String className, Object[] initInfo)
    {
        AgentController agentController = null;
        try
        {
            agentController = container.createNewAgent(agentName, className, initInfo);
            System.out.println(agentName + " launched");
        }
        catch (StaleProxyException ex)
        {
            LogUtil.logSevereErrorMessage(CLASS_NAME, ex.getMessage(), ex);
        }
        return agentController;
    }

    /**
     * Start the agents
     *
     * @param agentList the list of agents that should be started
     */
    public static void startAgents(List<AgentController> agentList)
    {
        agentList.stream().forEach((agent) ->
        {
            try
            {
                agent.start();
            }
            catch (StaleProxyException ex)
            {
                LogUtil.logSevereErrorMessage(CLASS_NAME, null, ex);
            }
        });
        LogUtil.logInfoMessage(CLASS_NAME, "Agents started...");
    }

    /**
     * Create default monitoring agents provided by Jade
     *
     * @param container container on which the agents are created
     */
    public static void createMonitoringAgents(ContainerController container)
    {
        try
        {
            AgentController rmaAgent;
            rmaAgent = container.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
            rmaAgent.start();

            LogUtil.logInfoMessage(CLASS_NAME, "Launched RMA Agent on " + container.getContainerName());
        }
        catch (StaleProxyException ex)
        {
            LogUtil.logSevereErrorMessage(CLASS_NAME, "Launching of RMA Agent failed", ex);
        }
        catch (ControllerException ex)
        {
            LogUtil.logSevereErrorMessage(CLASS_NAME, ex.getMessage(), ex);
        }

        try
        {
            AgentController snifferAgent;
            snifferAgent = container.createNewAgent("sniffeur", "jade.tools.sniffer.Sniffer", new Object[0]);
            snifferAgent.start();

            LogUtil.logInfoMessage(CLASS_NAME, "Launched Sinffer Agent on " + container.getContainerName());
        }
        catch (StaleProxyException ex)
        {
            LogUtil.logSevereErrorMessage(CLASS_NAME, "Launching of Sinffer failed", ex);
        }
        catch (ControllerException ex)
        {
            LogUtil.logSevereErrorMessage(CLASS_NAME, ex.getMessage(), ex);
        }
    }
    
    private static String getAgentClassNameByModel(DataModel object)
    {
        if (object instanceof ShopOrderModel)
        {
            return ShopOrderAgent.class.getName();
        }
        else if (object instanceof WorkCenterModel)
        {
            return WorkCenterAgent.class.getName();
        }
        else
        {
            return null;
        }
    }
    
}
