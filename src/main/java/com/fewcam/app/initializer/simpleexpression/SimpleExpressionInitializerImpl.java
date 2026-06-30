package com.fewcam.app.initializer.simpleexpression;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import ecorefewcam.EcorefewcamFactory;
import ecorefewcam.SimpleExpression;
import ecorefewcam.VariableInstance;

@ApplicationScoped
@Named("SimpleExpressionIntializerImpl")
public class SimpleExpressionInitializerImpl implements SimpleExpressionInitializer {

	private final EcorefewcamFactory factory;
	private final String[] linguisticTerms;

	@Inject
	public SimpleExpressionInitializerImpl(EcorefewcamFactory factory,
			@Named("linguisticTerms") String[] linguisticTerms

	) {
		this.factory = factory;
		this.linguisticTerms = linguisticTerms;
	}

	@Override
	public SimpleExpression[][] initializeSimpleExpressions(VariableInstance[][] variableInstances, int variableCount) {

		String[] terms = this.linguisticTerms;

		SimpleExpression[][] simpleExpressions = new SimpleExpression[terms.length][variableCount];

		for (int i = 0; i < terms.length; i++) {

			for (int j = 0; j < variableCount; j++) {
				VariableInstance variableInstance = variableInstances[i][j];
				SimpleExpression simpleExpression = factory.createSimpleExpression();
				simpleExpression.setVariableInstance(variableInstance);
				simpleExpressions[i][j] = simpleExpression;
			}
		}

		return simpleExpressions;
	}

}
