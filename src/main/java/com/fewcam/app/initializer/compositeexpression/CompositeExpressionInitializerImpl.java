package com.fewcam.app.initializer.compositeexpression;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.fewcam.app.initializer.fuzzyaggregator.FuzzyAggregatorInitializer;

import ecorefewcam.CompositeExpression;
import ecorefewcam.EcorefewcamFactory;
import ecorefewcam.Expression;
import ecorefewcam.FuzzyAggregator;
import ecorefewcam.SimpleExpression;

@ApplicationScoped
@Named("CompositeExpressionIntializerImpl")
public class CompositeExpressionInitializerImpl implements CompositeExpressionInitializer {

	private final EcorefewcamFactory factory;
	private final FuzzyAggregatorInitializer fuzzyAggregatorInitializer;
	private Map<String, CompositeExpression> compositeExpressions;

	@Inject
	public CompositeExpressionInitializerImpl(EcorefewcamFactory factory,
			@Named("FuzzyAggregatorInitializerImpl") FuzzyAggregatorInitializer fuzzyAggregatorInitializer) {
		this.factory = factory;
		this.fuzzyAggregatorInitializer = fuzzyAggregatorInitializer;
	}

	@Override
	public Map<String, CompositeExpression> initializeCompositeExpressions(SimpleExpression[][] simpleExpressions) {

		FuzzyAggregator and = fuzzyAggregatorInitializer.getFuzzyAggregators().get("and");

		Expression[] firsts = new Expression[simpleExpressions.length];
		for (int i = 0; i < simpleExpressions.length; i++) {
			firsts[i] = simpleExpressions[i][0];
		}

		int varsLength = simpleExpressions[0].length;

		for (int j = 0; j < varsLength - 1; j++) {
			Expression[] nexts = new Expression[simpleExpressions.length];

			for (int i = 0; i < nexts.length; i++) {
				nexts[i] = simpleExpressions[i][j + 1];
			}

			Expression[] cartesianProduct = new Expression[firsts.length * nexts.length];
			for (int i = 0; i < firsts.length; i++) {
				for (int k = 0; k < nexts.length; k++) {
					CompositeExpression compositeExpression = factory.createCompositeExpression();
					compositeExpression.setFuzzyAggregator(and);
					compositeExpression.getExpressions().addAll(List.of(firsts[i], nexts[k]));
					cartesianProduct[i * nexts.length + k] = compositeExpression;
				}

			}
			firsts = cartesianProduct;

		}

		compositeExpressions = new java.util.HashMap<>();
		for (int i = 0; i < firsts.length; i++) {
			compositeExpressions.put(firsts[i].getName(), (CompositeExpression) firsts[i]);
		}

		return compositeExpressions;
	}

	@Override
	public Map<String, CompositeExpression> getCompositeExpressions() {

		return this.compositeExpressions;
	}

}