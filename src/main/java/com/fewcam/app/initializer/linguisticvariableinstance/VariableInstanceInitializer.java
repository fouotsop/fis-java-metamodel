package com.fewcam.app.initializer.linguisticvariableinstance;

import javax.enterprise.context.ApplicationScoped;

import ecorefewcam.VariableInstance;

@ApplicationScoped
public interface VariableInstanceInitializer {
    

    public VariableInstance[] initializeOutputVariableInstances(String name);

    public VariableInstance[][] getVariableInstances();

    public String[] getLinguisticVariableNames();

    public VariableInstance[][] initializeVariableInstances(String[] variableNames);

    public VariableInstance[] getOutputVariableInstances();

}

