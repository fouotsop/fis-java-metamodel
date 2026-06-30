package com.fewcam.app.configuration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import ecorefewcam.EcorefewcamFactory;
import ecorefewcam.EcorefewcamPackage;

@ApplicationScoped
public class FewcamConfig {

	private final String linguisticTerms = "low,high,medium";

	private final String thresholds = "0.0,0.5,1.0";

	private final String outputLinguisticTerms = "very_low,low,fairly_low,medium,fairly_high,high,very_high";

	private final String outputMembershipRanges = "0.1,0.3,0.4,0.6,0.7,0.9,1.0";

	@Produces
	@ApplicationScoped
	public EcorefewcamFactory ecoreFewcamFactory() {

		EcorefewcamPackage ecoreFewcamPackage = EcorefewcamPackage.eINSTANCE;

		EcorefewcamFactory ecorefewcamFactory = ecoreFewcamPackage.getEcorefewcamFactory();

		return ecorefewcamFactory;
	}

	@Produces
	@Named("linguisticTerms")
	public String[] linguisticTerms() {

		String[] linguisticTerms = this.linguisticTerms.split(",");

		return linguisticTerms;
	}

	@Produces
	@Named("thresholds")
	public double[] thresholds() {
		String[] thresholdsStr = this.thresholds.split(",");
		double[] thresholds = new double[thresholdsStr.length];
		for (int i = 0; i < thresholdsStr.length; i++) {
			thresholds[i] = Double.parseDouble(thresholdsStr[i]);
		}
		return thresholds;
	}

	@Produces
	@Named("outputMembershipRange")
	public double[] outputMembershipRange() {
		String[] outputMembershipRangeStr = this.outputMembershipRanges.split(",");
		double[] outputMembershipRange = new double[outputMembershipRangeStr.length];
		for (int i = 0; i < outputMembershipRangeStr.length; i++) {
			outputMembershipRange[i] = Double.parseDouble(outputMembershipRangeStr[i]);
		}
		return outputMembershipRange;
	}

	@Produces
	@Named("outputLinguisticTerms")
	public String[] outputLinguisticTerms() {
		return this.outputLinguisticTerms.split(",");
	}

}
