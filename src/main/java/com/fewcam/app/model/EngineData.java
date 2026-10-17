package com.fewcam.app.model;

public class EngineData {

	private LinguisticVariableData[] linguisticVariableData;

	private LinguisticVariableData outputVariable;

	private FuzzyInferenceRuleData[] ruleData;

	public EngineData() {

	}

	public LinguisticVariableData[] getLinguisticVariableData() {
		return linguisticVariableData;
	}

	public void setLinguisticVariableData(LinguisticVariableData[] linguisticVariableData) {
		this.linguisticVariableData = linguisticVariableData;
	}

	public LinguisticVariableData getOutputVariable() {
		return outputVariable;
	}

	public void setOutputVariable(LinguisticVariableData outputVariable) {
		this.outputVariable = outputVariable;
	}

	public FuzzyInferenceRuleData[] getRuleData() {
		return ruleData;
	}

	public void setRuleData(FuzzyInferenceRuleData[] ruleData) {
		this.ruleData = ruleData;
	}

}
