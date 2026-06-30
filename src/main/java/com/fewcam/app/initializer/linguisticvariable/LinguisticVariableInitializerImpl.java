package com.fewcam.app.initializer.linguisticvariable;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.fewcam.app.initializer.linguisticterm.LinguisticTermInitializer;

import ecorefewcam.EcorefewcamFactory;
import ecorefewcam.LinguisticTerm;
import ecorefewcam.LinguisticVariable;

@ApplicationScoped
@Named("LinguisticVariableInitializerImpl")
public class LinguisticVariableInitializerImpl implements LinguisticVariableInitializer {


    private Map<String, LinguisticVariable> variables = new HashMap<>();
    private LinguisticVariable outputVariable;
    private final EcorefewcamFactory factory;       

    private final LinguisticTermInitializer linguisticTermInitializer;

    @Inject
    public LinguisticVariableInitializerImpl(
    		@Named("LinguisticTermInitializerImpl") LinguisticTermInitializer linguisticTermInitializer,
    		EcorefewcamFactory factory
    		) {
        this.linguisticTermInitializer = linguisticTermInitializer;
        this.factory = factory;
    }

    @Override
    public Map<String, LinguisticVariable> initializeVariables(String[] variableNames) {

        Map<String, LinguisticTerm> linguisticTerms = this.linguisticTermInitializer.getLinguisticTerms();

        for (String variableName: variableNames) {
            LinguisticVariable variable = factory.createLinguisticVariable();
            variable.setName(variableName);
            variable.getLinguisticTerms().addAll(linguisticTerms.values());
            variables.put(variableName, variable);
            
        }
        variables = this.getLinguisticVariables();

        return variables;
    }

    @Override
    public Map<String, LinguisticVariable> getLinguisticVariables() {
        return variables;
    }

    @Override
    public LinguisticVariable initializeOutputVariable(String name) {
        LinguisticVariable outputVariable = factory.createLinguisticVariable();
        outputVariable.setName(name);
        outputVariable.getLinguisticTerms().addAll(this.linguisticTermInitializer.getLinguisticTerms().values());
        this.outputVariable = outputVariable;
        return outputVariable;
    }

    @Override
    public String[] getLinguisticVariableNames() {
        return variables.keySet().toArray(new String[0]);
    }

    @Override
    public LinguisticVariable getOutputVariable() {
        return this.outputVariable;
    }

}