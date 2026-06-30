package com.fewcam.app.initializer.fuzzyaggregator;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import ecorefewcam.EcorefewcamFactory;
import ecorefewcam.FuzzyAggregator;

@ApplicationScoped
@Named("FuzzyAggregatorInitializerImpl")
public class FuzzyAggregatorInitializerImpl implements FuzzyAggregatorInitializer {

    private final EcorefewcamFactory factory;
    private Map<String, FuzzyAggregator> fuzzyAggregators;

    @Inject
    public FuzzyAggregatorInitializerImpl(EcorefewcamFactory factory) {
        this.factory = factory;
    }

    protected Map<String, FuzzyAggregator> initializeFuzzyAggregators() {

        Map<String, FuzzyAggregator> fuzzyAggregators = Map.of(
            "or", factory.createMaximum(),
            "and", factory.createMinimum()
        );

        this.fuzzyAggregators = fuzzyAggregators;
        return fuzzyAggregators;
    }

    public Map<String, FuzzyAggregator> getFuzzyAggregators() {
        if (fuzzyAggregators == null) {
            return initializeFuzzyAggregators();
        }
        return fuzzyAggregators;
    
    }
}
