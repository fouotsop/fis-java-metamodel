package com.fewcam.app.routes;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/api")
public class FewcamApplication extends Application{

	@Override
	public Set<Class<?>> getClasses() {
		
        Set<Class<?>> classes = new HashSet<>();
        classes.add(EvaluationRoute.class); // Register your resource class
        return classes;
	}

	
	

	
}
