package com.fewcam.app.initializer.predicate;

import java.util.HashMap;
import java.util.Map;

import efis.EfisFactory;
import efis.LinguisticHedge;
import efis.LinguisticTerm;
import efis.LinguisticVariable;
import efis.MembershipFunction;
import efis.Predicate;
import efis.Triangular;

public class PredicateInitializer {

	private final EfisFactory efisFactory;

	public PredicateInitializer(EfisFactory efisFactory) {
		this.efisFactory = efisFactory;
	}

	public Map<String, Predicate> buildPredicates(Map<String, LinguisticVariable> linguisticVariables,
			Map<String, LinguisticTerm> linguisticTerms, Map<String, LinguisticHedge> linguisticHedges) {

		final Map<String, Predicate> predicates = new HashMap<>();
		LinguisticHedge hedge = linguisticHedges.get("just");
		linguisticTerms.forEach((termName, term) -> {

			linguisticVariables.forEach((varName, variable) -> {
				Predicate predicate = efisFactory.createPredicate();

				predicate.setLinguistichedge(hedge);
				predicate.setLinguisticterm(term);
				predicate.setLinguisticvariable(variable);

				MembershipFunction function = term.getMembershipfunction();
				double degree = function.membershipDegree(variable.getCrispValue());

				hedge.setMembershipDegree(degree);

				Triangular tri = (Triangular) function;
				System.out.println(
						term.getName() + " " + variable.getName() + ": " + variable.getCrispValue()
								+ "  degree: " + function.membershipDegree(variable.getCrispValue()) + "   a:"
								+ tri.getA() + " b: " + tri.getB()
								+ " c:"
								+ tri.getC());

				System.out.println(predicate.evaluate());

				predicates.put(termName + "_" + varName, predicate);

			});

		});

		return predicates;
	}

	public Map<String, Predicate> buildOutputPredicates(Map<String, LinguisticTerm> linguisticTerms,
			Map<String, LinguisticHedge> linguisticHedges, LinguisticVariable outputVariable) {

		Map<String, Predicate> predicates = new HashMap<>();

		linguisticTerms.forEach((termName, term) -> {
			LinguisticHedge hedge = linguisticHedges.get("just");
			Predicate predicate = efisFactory.createPredicate();
			predicate.setLinguistichedge(hedge);
			predicate.setLinguisticterm(term);
			predicate.setLinguisticvariable(outputVariable);
			predicates.put(termName + "_" + outputVariable.getName(), predicate);
		});

		return predicates;
	}
}
