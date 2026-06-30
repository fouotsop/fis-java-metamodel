package com.fewcam.app.initializer.fuzzyinferencerule;

import java.util.Map;


import ecorefewcam.CompositeExpression;
import ecorefewcam.FuzzyInferenceRule;
import ecorefewcam.VariableInstance;

public interface FuzzyInferenceRuleInitializer {
    
    public Map<String, FuzzyInferenceRule> initializeFuzzyInferenceRules(CompositeExpression[] compositeExpressions, Map<String, VariableInstance> outputVariableInstances);

    public Map<String, FuzzyInferenceRule> initializeFuzzyInferenceRules(Map<String, CompositeExpression> compositeExpressions, Map<String, VariableInstance> outputVariableInstance);

}
