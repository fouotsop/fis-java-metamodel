package com.fewcam.app.initializer.linguisticvariable;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import ecorefewcam.LinguisticVariable;

@ApplicationScoped
public interface LinguisticVariableInitializer {
    
    public Map<String, LinguisticVariable> initializeVariables(String[] variableNames);

    public LinguisticVariable initializeOutputVariable(String name);

    public String[] getLinguisticVariableNames();

    public Map<String, LinguisticVariable> getLinguisticVariables();

    public LinguisticVariable getOutputVariable();


}
