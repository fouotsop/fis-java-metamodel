package com.fewcam.app.model;

public class LinguisticVariableData {

	private String name;

	private Double min;

	private Double max;

	private Double value;

	public LinguisticVariableData() {

	}

	public LinguisticVariableData(String name, Double min, Double max, Double value) {
		this.name = name;
		this.min = min;
		this.max = max;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

}
