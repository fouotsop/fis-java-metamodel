package com.fewcam.app.routes;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.fewcam.app.initializer.fuzzyinferenceengine.FuzzyInferenceEngineInitializer;

import ecorefewcam.FuzzyInferenceEngine;

@Path("/evaluate")
public class EvaluationRoute {

	@Inject
	@Named("FuzzyInferenceEngineInitializerImpl")
	private FuzzyInferenceEngineInitializer initialzer;

	public EvaluationRoute() {
	}

	@GET
	public String evaluate() {

		String[] variables = { "security", "transparency", "reputation" };
		FuzzyInferenceEngine engine = this.initialzer.initializeFuzzyInferenceEngine(variables, "credibility");
		double score = engine.defuzzify();
		return "Credibility value is : " + String.valueOf(score);

	}

}
