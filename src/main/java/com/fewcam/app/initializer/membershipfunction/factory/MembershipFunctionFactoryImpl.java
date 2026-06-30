package com.fewcam.app.initializer.membershipfunction.factory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import ecorefewcam.EcorefewcamFactory;
import ecorefewcam.Trapezoidal;
import ecorefewcam.Triangular;

@ApplicationScoped
@Named("MembershipFunctionFactoryImpl")
public class MembershipFunctionFactoryImpl implements MembershipFunctionFactory {

	private final EcorefewcamFactory factory;

	@Inject
	public MembershipFunctionFactoryImpl(EcorefewcamFactory factory) {
		this.factory = factory;
	}

	@Override
	public Triangular createTriangularMembershipFunction(String name, float a, float b, float c) {
		Triangular membershipFunction = factory.createTriangular();

		membershipFunction.setA(a);
		membershipFunction.setB(b);
		membershipFunction.setC(c);

		return membershipFunction;
	}

	@Override
	public Trapezoidal createTrapezoidalMembershipFunction(String name, float a, float b, float c, float d) {
		Trapezoidal membershipFunction = factory.createTrapezoidal();

		membershipFunction.setA(a);
		membershipFunction.setB(b);
		membershipFunction.setC(c);
		membershipFunction.setD(d);

		return membershipFunction;
	}

	@Override
	public EcorefewcamFactory getFactory() {
		return factory;
	}

}
