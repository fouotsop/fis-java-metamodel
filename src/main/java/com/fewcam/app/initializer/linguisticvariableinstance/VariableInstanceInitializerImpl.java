package com.fewcam.app.initializer.linguisticvariableinstance;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.fewcam.app.initializer.linguistichedges.LinguisticHedgeInitializer;
import com.fewcam.app.initializer.linguisticvariable.LinguisticVariableInitializer;

import ecorefewcam.EcorefewcamFactory;
import ecorefewcam.LinguisticHedge;
import ecorefewcam.LinguisticTerm;
import ecorefewcam.LinguisticVariable;
import ecorefewcam.VariableInstance;


@ApplicationScoped
@Named("VariableInstanceInitializerImpl")
public class VariableInstanceInitializerImpl implements VariableInstanceInitializer {

    private final LinguisticVariableInitializer linguisticVariableInitializer;
    private final EcorefewcamFactory factory;
    private final LinguisticHedgeInitializer linguisticHedgeInitializer;


    private VariableInstance[][] variableInstances;
    private VariableInstance[] outputVariableInstances;

    
    @Inject
    public VariableInstanceInitializerImpl(
        @Named("LinguisticVariableInitializerImpl") LinguisticVariableInitializer linguisticVariableInitializer,
        @Named("LinguisticHedgeInitializerImpl") LinguisticHedgeInitializer linguisticHedgeInitializer,
        EcorefewcamFactory factory) {
        this.linguisticVariableInitializer = linguisticVariableInitializer;
        this.factory = factory;
        this.linguisticHedgeInitializer = linguisticHedgeInitializer;
    }


    @Override
    public VariableInstance[][] getVariableInstances() {

        return variableInstances;
    }
    
    @Override
    public VariableInstance[][] initializeVariableInstances(String[] variableNames) {
        final Map<String, LinguisticVariable> linguisticVariables = linguisticVariableInitializer.initializeVariables(variableNames);

        List<LinguisticTerm> terms = linguisticVariables.get(variableNames[0]).getLinguisticTerms();
        variableInstances = new VariableInstance[terms.size()][variableNames.length];

        for(int k = 0; k < terms.size(); k++) {
            for (int j = 0; j < variableNames.length; j++) {
                VariableInstance instance = factory.createVariableInstance();
                instance.setLinguisticVariable(linguisticVariables.get(variableNames[j]));
                instance.setLinguisticValue(terms.get(k));
                variableInstances[k][j] = instance;
            }
        }


        return variableInstances;

    }


    @Override
    public String[] getLinguisticVariableNames() {
        return this.linguisticVariableInitializer.getLinguisticVariableNames();
    }


    @Override
    public VariableInstance[] initializeOutputVariableInstances(String name) {

        LinguisticVariable outputVariable = linguisticVariableInitializer.initializeOutputVariable(name);

        int terms = outputVariable.getLinguisticTerms().size();
        LinguisticHedge[] linguisticHedges = linguisticHedgeInitializer.intializeLinguisticHedges();
        int hedges = linguisticHedges.length;

        int length = (hedges*(terms - 1)) + terms;

        this.outputVariableInstances = new VariableInstance[length];

        int step = 0;
        for (int i = 0; i < terms; i++) {
            if (outputVariable.getLinguisticTerms().get(i).getName().equals("medium")) {
                continue;
            }
            for (int j = 0; j < hedges; j++) {
                VariableInstance instance = factory.createVariableInstance();
                instance.setLinguisticHedge(linguisticHedges[j]);
                instance.setLinguisticVariable(outputVariable);
                instance.setLinguisticValue(outputVariable.getLinguisticTerms().get(i));
                outputVariableInstances[j + step] = instance;
            }
            step += hedges;
        }

        for (int i = length - terms; i < length; i++) {
            VariableInstance instance = factory.createVariableInstance();
            instance.setLinguisticVariable(outputVariable);
            int index = i - (terms + 1);
            instance.setLinguisticValue(outputVariable.getLinguisticTerms().get(index));
            outputVariableInstances[i] = instance;
        }


        return this.outputVariableInstances;

    }


    @Override
    public VariableInstance[] getOutputVariableInstances() {
        return this.outputVariableInstances;
    }

}
