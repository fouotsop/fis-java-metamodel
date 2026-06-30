package com.fewcam.app.initializer.fuzzyinferencerule;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import ecorefewcam.CompositeExpression;
import ecorefewcam.EcorefewcamFactory;
import ecorefewcam.FuzzyInferenceRule;
import ecorefewcam.VariableInstance;

@ApplicationScoped
@Named("FuzzyInferenceRuleInitializerImpl")
public class FuzzyInferenceRuleInitializerImpl implements FuzzyInferenceRuleInitializer {

	private final EcorefewcamFactory factory;
	private final double[] thresholds;
	private final String[] linguisticTerms;
	private final double[] outputMembershipRange;
	private final String[] outputLinguisticTerms;

	@Inject
	public FuzzyInferenceRuleInitializerImpl(EcorefewcamFactory factory, @Named("thresholds") double[] thresholds,
			@Named("linguisticTerms") String[] linguisticTerms,
			@Named("outputMembershipRange") double[] outputMembershipRange,
			@Named("outputLinguisticTerms") String[] outputLinguisticTerms) {
		this.factory = factory;
		this.thresholds = thresholds;
		this.linguisticTerms = linguisticTerms;
		this.outputLinguisticTerms = outputLinguisticTerms;
		this.outputMembershipRange = outputMembershipRange;
	}

	@Override
	public Map<String, FuzzyInferenceRule> initializeFuzzyInferenceRules(CompositeExpression[] compositeExpressions,
			Map<String, VariableInstance> outputVariableInstances) {
		Map<String, FuzzyInferenceRule> fuzzyInferenceRules = new HashMap<>();

		String key = outputVariableInstances.keySet().iterator().next(); // Assuming there's only one output variable
																			// instance
		VariableInstance instance = outputVariableInstances.get(key);
		String variableName = instance.getLinguisticVariable().getName();

		for (int i = 0; i < compositeExpressions.length; i++) {
			CompositeExpression compositeExpression = compositeExpressions[i];

			FuzzyInferenceRule rule = factory.createFuzzyInferenceRule();
			rule.setExpression(compositeExpression);

			String outputTerm = determineOutputVariableInstance(compositeExpression);
			outputTerm = outputTerm + "_" + variableName; // Append variable name to ensure uniqueness

			VariableInstance outputVariableInstance = outputVariableInstances.get(outputTerm);
			rule.setOutputVariableInstance(outputVariableInstance);

			fuzzyInferenceRules.put(rule.getName(), rule);
		}

		return fuzzyInferenceRules;
	}

	protected String determineOutputVariableInstance(CompositeExpression expression) {

		int lenTerms = linguisticTerms.length;
		int termCounts[] = new int[lenTerms];

		for (int i = 0; i < lenTerms; i++) {
			termCounts[i] = expression.countTerm(linguisticTerms[i]);
		}

		double average = 0.0;
		for (int i = 0; i < lenTerms; i++) {
			average += (termCounts[i] * thresholds[i]); // Assuming a simple weighting based on the first threshold
		}

		int variableCount = expression.variableCount();
		average = average / variableCount;

		double currentnow = 0.0;

		int j = 0;
		while (j < outputMembershipRange.length) {
			if (currentnow <= average && average < outputMembershipRange[j]) {
				return outputLinguisticTerms[j];
			}
			currentnow = outputMembershipRange[j];
			j = j + 1;
		}

		return outputLinguisticTerms[outputLinguisticTerms.length - 1]; // Return the last term if average exceeds all
																		// thresholds

	}

	@Override
	public Map<String, FuzzyInferenceRule> initializeFuzzyInferenceRules(
			Map<String, CompositeExpression> compositeExpressions,
			Map<String, VariableInstance> outputVariableInstances) {

		Map<String, FuzzyInferenceRule> fuzzyInferenceRules = new HashMap<>();

		VariableInstance instance = outputVariableInstances.get(outputVariableInstances.keySet().iterator().next());
		String variableName = instance.getLinguisticVariable().getName();

		for (CompositeExpression compositeExpression : compositeExpressions.values()) {

			FuzzyInferenceRule rule = factory.createFuzzyInferenceRule();
			rule.setExpression(compositeExpression);

			String outputTerm = determineOutputVariableInstance(compositeExpression);
			outputTerm = outputTerm + "_" + variableName; // Append variable name to ensure uniqueness

			VariableInstance outputVariableInstance = outputVariableInstances.get(outputTerm);
			rule.setOutputVariableInstance(outputVariableInstance);

			fuzzyInferenceRules.put(rule.getName(), rule);
		}

		return fuzzyInferenceRules;

	}

}