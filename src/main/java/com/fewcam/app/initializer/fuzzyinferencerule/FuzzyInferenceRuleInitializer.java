package com.fewcam.app.initializer.fuzzyinferencerule;

import java.util.Map;

import com.fewcam.app.model.FuzzyInferenceRuleData;

import efis.CompositePredicate;
import efis.EfisFactory;
import efis.FuzzyAggregator;
import efis.FuzzyInferenceRule;
import efis.Predicate;

public class FuzzyInferenceRuleInitializer {

	private final EfisFactory efisFactory;
	private final FuzzyAggregator aggregator;

	public FuzzyInferenceRuleInitializer(EfisFactory efisFactory, FuzzyAggregator aggregator) {
		this.efisFactory = efisFactory;
		this.aggregator = aggregator;

	}

	public Map<String, FuzzyInferenceRule> initializeFuzzyInferenceRules(Map<String, Predicate> predicates,
			Map<String, CompositePredicate> compositePredicates, Map<String, Predicate> outputPredicates,
			FuzzyInferenceRuleData[] rulesData) {

		Map<String, FuzzyInferenceRule> fuzzyInferenceRules = new java.util.HashMap<>();

		for (FuzzyInferenceRuleData ruleData : rulesData) {

			FuzzyInferenceRule rule = efisFactory.createFuzzyInferenceRule();
			if (ruleData.getE1Size() == 1 && ruleData.getE2Size() == 2) {

				Predicate e1 = predicates.get(ruleData.getE1());
				CompositePredicate e2 = compositePredicates.get(ruleData.getE2());
				CompositePredicate antecedent = efisFactory.createCompositePredicate();
				antecedent.getAbstractpredicates().add(e1);
				antecedent.getAbstractpredicates().add(e2);
				antecedent.setFuzzyaggregator(aggregator);

				rule.setAbstractpredicate(antecedent);
				Predicate output = outputPredicates.get(ruleData.getOutput());

				rule.setOutputpredicate(output);
			}

			if (ruleData.getE1Size() == 2 && ruleData.getE2Size() == 1) {

				CompositePredicate e1 = compositePredicates.get(ruleData.getE1());
				Predicate e2 = predicates.get(ruleData.getE2());
				CompositePredicate antecedent = efisFactory.createCompositePredicate();
				antecedent.getAbstractpredicates().add(e1);
				antecedent.getAbstractpredicates().add(e2);
				antecedent.setFuzzyaggregator(aggregator);

				rule.setAbstractpredicate(antecedent);
				Predicate output = outputPredicates.get(ruleData.getOutput());

				rule.setOutputpredicate(output);
			}

			if (ruleData.getE1Size() == 2 && ruleData.getE2Size() == 2) {

				CompositePredicate e1 = compositePredicates.get(ruleData.getE1());
				CompositePredicate e2 = compositePredicates.get(ruleData.getE2());
				CompositePredicate antecedent = efisFactory.createCompositePredicate();
				antecedent.getAbstractpredicates().add(e1);
				antecedent.getAbstractpredicates().add(e2);
				antecedent.setFuzzyaggregator(aggregator);

				rule.setAbstractpredicate(antecedent);
				Predicate output = outputPredicates.get(ruleData.getOutput());

				rule.setOutputpredicate(output);
			}

			fuzzyInferenceRules.put(ruleData.getE1() + "_" + aggregator.getName() + "_" + ruleData.getE2() + "_then_"
					+ ruleData.getOutput(), rule);
		}
		return fuzzyInferenceRules;
	}

}
