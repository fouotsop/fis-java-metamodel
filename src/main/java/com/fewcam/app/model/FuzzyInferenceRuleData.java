package com.fewcam.app.model;

public class FuzzyInferenceRuleData {

	private String e1;
	private String e2;
	private Integer e1Size;
	private Integer e2Size;
	private String output;

	public FuzzyInferenceRuleData() {
	}

	public FuzzyInferenceRuleData(String e1, String e2, Integer e1Size, Integer e2Size, String output) {
		this.e1 = e1;
		this.e2 = e2;
		this.e1Size = e1Size;
		this.e2Size = e2Size;
		this.output = output;
	}

	public String getE1() {
		return e1;
	}

	public void setE1(String e1) {
		this.e1 = e1;
	}

	public String getE2() {
		return e2;
	}

	public void setE2(String e2) {
		this.e2 = e2;
	}

	public Integer getE1Size() {
		return e1Size;
	}

	public void setE1Size(Integer e1Size) {
		this.e1Size = e1Size;
	}

	public Integer getE2Size() {
		return e2Size;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setE2Size(Integer e2Size) {
		this.e2Size = e2Size;
	}

}
