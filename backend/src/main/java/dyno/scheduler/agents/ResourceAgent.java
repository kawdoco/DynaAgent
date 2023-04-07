/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.agents;

import dyno.scheduler.datamodels.PartModel;
import dyno.scheduler.utils.DateTimeUtil;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Prabash
 */
public class ResourceAgent extends Agent
{
    private static final long serialVersionUID = 5323871501633594037L;
    PartModel partModel;

    @Override
    protected void setup()
    {
        super.setup(); //To change body of generated methods, choose Tools | Templates.

        //get the parameters given into the object[]
        final Object[] args = getArguments();
        if (args[0] != null)
        {
            partModel = (PartModel) args[0];

        } else
        {
            System.out.println("Error with the Shop Order arguments");
        }
    }
    
    private class CheckPartAvailability extends CyclicBehaviour
    {
        private static final long serialVersionUID = 5732553544478476057L;
        
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
                
            } else
            {
                block();
            }
        }
        
        // </editor-fold>
    }
}
