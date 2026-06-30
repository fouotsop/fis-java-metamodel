package com.fewcam.app.routes;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;


@ApplicationPath("/api")
public class FewcamApplication extends Application{

	@Override
	public Set<Class<?>> getClasses() {
		
        Set<Class<?>> classes = new HashSet<>();
        classes.add(EvaluationRoute.class); // Register your resource class
        return classes;
	}

	
	

	
}
