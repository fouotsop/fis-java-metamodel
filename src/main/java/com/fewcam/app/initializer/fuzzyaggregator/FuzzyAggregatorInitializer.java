package com.fewcam.app.initializer.fuzzyaggregator;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import ecorefewcam.FuzzyAggregator;

@ApplicationScoped
public interface FuzzyAggregatorInitializer {

    public Map<String, FuzzyAggregator> getFuzzyAggregators();
    
}
