package com.fewcam.app.initializer.fuzzyinferenceengine;


import javax.enterprise.context.ApplicationScoped;

import ecorefewcam.FuzzyInferenceEngine;

@ApplicationScoped
public interface FuzzyInferenceEngineInitializer {

    FuzzyInferenceEngine initializeFuzzyInferenceEngine(String[] inputVariableNames, String outputVariableName);
    
}
