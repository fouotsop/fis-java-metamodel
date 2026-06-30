package com.fewcam.app.initializer.simpleexpression;


import javax.enterprise.context.ApplicationScoped;

import ecorefewcam.SimpleExpression;
import ecorefewcam.VariableInstance;

@ApplicationScoped
public interface SimpleExpressionInitializer {

    SimpleExpression[][] initializeSimpleExpressions(VariableInstance[][] variableInstances, int variableCount);    

}
