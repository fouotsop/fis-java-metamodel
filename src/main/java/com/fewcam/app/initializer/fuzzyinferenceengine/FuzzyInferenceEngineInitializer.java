package com.fewcam.app.initializer.fuzzyinferenceengine;

import java.util.HashMap;
import java.util.Map;

import efis.EfisFactory;
import efis.FuzzyInferenceEngine;
import efis.FuzzyInferenceRule;
import efis.LinguisticTerm;

public class FuzzyInferenceEngineInitializer {

	private final EfisFactory efisFactory;

	public FuzzyInferenceEngineInitializer(EfisFactory efisFactory) {
		this.efisFactory = efisFactory;
	}

	public FuzzyInferenceEngine initializeFuzzyInferenceEngine(Map<String, FuzzyInferenceRule> rules) {

		FuzzyInferenceEngine engine = efisFactory.createFuzzyInferenceEngine();

		rules.forEach((name, rule) -> {
			engine.getFuzzyinferencerules().add(rule);
		});
		engine.setTotalRules(rules.size());
		Map<LinguisticTerm, Double> firingStrengths = new HashMap<>();
		engine.setFiringStrengths(firingStrengths);
		return engine;
	}

}
