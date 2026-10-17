package com.fewcam.app.initializer.defuzzificationalgorithm;

import efis.CentroidAlgorithm;
import efis.DefuzzificationAlgorithm;
import efis.EfisFactory;
import efis.FuzzyInferenceEngine;

public class DefuzzificationAlgorithmInitializer {

	private final EfisFactory factory;

	public DefuzzificationAlgorithmInitializer(EfisFactory factory) {
		this.factory = factory;
	}

	public DefuzzificationAlgorithm initializeAlgorithm(FuzzyInferenceEngine engine) {

		CentroidAlgorithm algorithm = factory.createCentroidAlgorithm();
		algorithm.setFuzzyinferenceengine(engine);

		return algorithm;
	}
}
