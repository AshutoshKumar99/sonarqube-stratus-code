package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ControllerConfiguration;

import java.util.LinkedList;
import java.util.List;

/**
 * This factory class is responsible for creating individual Action class instances for every action url
 */
public class ControllerActionFactory 
{
    
    private ControllerConfiguration config;
    
    private NotFoundControllerAction notFoundAction 
            = new NotFoundControllerAction();
    
    private List<ControllerAction> actionChain = new LinkedList<ControllerAction>();
    
    public void mapControllerAction(ControllerAction action, String path)
    {
        action.setConfig(config);
        action.setPath(path);
        action.init();
        actionChain.add(action);
    }
    
    public ControllerActionFactory(ControllerConfiguration config)
    {
        this.config = config;
    }
    
    public ControllerAction getControllerAction(String path)
    {
        for (ControllerAction action : actionChain)
        {
            if (action.matches(path))
            {
                return action;
            }
        }
        return notFoundAction;
    }
    
}
