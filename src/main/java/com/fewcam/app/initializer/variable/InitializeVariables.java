package com.fewcam.app.initializer.variable;

import java.util.HashMap;
import java.util.Map;

import com.fewcam.app.model.LinguisticVariableData;

import efis.EfisFactory;
import efis.Just;
import efis.LinguisticHedge;
import efis.LinguisticTerm;
import efis.LinguisticTermName;
import efis.LinguisticVariable;
import efis.Maximum;
import efis.MembershipFunction;
import efis.Minimum;
import efis.Slightly;
import efis.Triangular;
import efis.Very;

public class InitializeVariables {

	private final Minimum minAggregator;
	private final Maximum maxAggregator;
	private final EfisFactory efisFactory;
	private final Map<String, MembershipFunction> membershipFunctions;
	private final Map<String, LinguisticHedge> linguisticHedges;
	private final Map<String, LinguisticTerm> linguisticTerms;

	public InitializeVariables(EfisFactory efisFactory) {
		this.efisFactory = efisFactory;

		minAggregator = efisFactory.createMinimum();
		minAggregator.setName("and");
		maxAggregator = efisFactory.createMaximum();
		maxAggregator.setName("or");

		Map<String, MembershipFunction> membershipFunctions = new HashMap<>();
		Triangular low = efisFactory.createTriangular();
		low.setA(0.0);
		low.setB(0.0);
		low.setC(50.0);

		Triangular medium = efisFactory.createTriangular();
		medium.setA(0.0);
		medium.setB(50.0);
		medium.setC(100.0);

		Triangular high = efisFactory.createTriangular();
		high.setA(50.0);
		high.setB(100.0);
		high.setC(100.0);

		membershipFunctions.put("low", low);
		membershipFunctions.put("medium", medium);
		membershipFunctions.put("high", high);

		this.membershipFunctions = membershipFunctions;

		Very very = efisFactory.createVery();
		Slightly slightly = efisFactory.createSlightly();
		Just just = efisFactory.createJust();

		Map<String, LinguisticHedge> linguisticHedges = new HashMap<>();
		linguisticHedges.put("very", very);
		linguisticHedges.put("slightly", slightly);
		linguisticHedges.put("just", just);

		this.linguisticHedges = linguisticHedges;

		LinguisticTerm lowTerm = efisFactory.createLinguisticTerm();
		lowTerm.setName(LinguisticTermName.LOW);
		lowTerm.setMembershipfunction(low);

		LinguisticTerm medTerm = efisFactory.createLinguisticTerm();
		medTerm.setName(LinguisticTermName.MEDIUM);
		medTerm.setMembershipfunction(medium);

		LinguisticTerm highTerm = efisFactory.createLinguisticTerm();
		highTerm.setName(LinguisticTermName.HIGH);
		highTerm.setMembershipfunction(high);

		Map<String, LinguisticTerm> linguisticTerms = new HashMap<>();
		linguisticTerms.put("low", lowTerm);
		linguisticTerms.put("medium", medTerm);
		linguisticTerms.put("high", highTerm);

		this.linguisticTerms = linguisticTerms;
	}

	public Map<String, LinguisticVariable> initializeVariables(LinguisticVariableData[] variables) {
		Map<String, LinguisticVariable> linguisticVariables = new HashMap<>();
		for (LinguisticVariableData variable : variables) {
			LinguisticVariable linguisticVariable = efisFactory.createLinguisticVariable();
			linguisticVariable.setName(variable.getName());
			linguisticVariable.setMinValue(variable.getMin());
			linguisticVariable.setMaxValue(variable.getMax());
			linguisticVariable.setCrispValue(variable.getValue());
			linguisticVariables.put(variable.getName(), linguisticVariable);
		}
		return linguisticVariables;
	}

	public LinguisticVariable initializeOutputVariable(LinguisticVariableData outputVariableData) {
		LinguisticVariable outputVariable = efisFactory.createLinguisticVariable();

		outputVariable.setName(outputVariableData.getName());
		outputVariable.setMinValue(outputVariableData.getMin());
		outputVariable.setMaxValue(outputVariableData.getMax());
		outputVariable.setCrispValue(outputVariableData.getValue());

		return outputVariable;
	}

	public Map<String, MembershipFunction> getMembershipFunctions() {
		return membershipFunctions;
	}

	public Map<String, LinguisticHedge> getLinguisticHedges() {
		return linguisticHedges;
	}

	public Map<String, LinguisticTerm> getLinguisticTerms() {
		return linguisticTerms;
	}

	public Minimum getMinAggregator() {
		return minAggregator;
	}

	public Maximum getMaxAggregator() {
		return maxAggregator;
	}

}
