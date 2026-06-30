package com.fewcam.app.initializer.compositeexpression;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import ecorefewcam.CompositeExpression;
import ecorefewcam.SimpleExpression;

@ApplicationScoped
public interface CompositeExpressionInitializer {
    
    public Map<String, CompositeExpression> initializeCompositeExpressions(SimpleExpression[][] simpleExpressions);

    public Map<String, CompositeExpression> getCompositeExpressions();
}
