package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.core.configuration.ControllerConfiguration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ControllerAction 
{
    public void setPath(String path);
    
    public void setConfig(ControllerConfiguration config);
    
    public boolean matches(String path);
    
    public void init();
    
	public void execute(HttpServletRequest request, 
	        HttpServletResponse response) throws ServletException, IOException,
            InvalidGazetteerException;
}
