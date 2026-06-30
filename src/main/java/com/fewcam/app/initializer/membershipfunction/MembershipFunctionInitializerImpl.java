package com.fewcam.app.initializer.membershipfunction;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.fewcam.app.initializer.membershipfunction.factory.MembershipFunctionFactory;

import ecorefewcam.MembershipFunction;

@ApplicationScoped
@Named("MembershipFunctionInitializerImpl")
public class MembershipFunctionInitializerImpl implements MembershipFunctionInitializer {
    

    private final MembershipFunctionFactory factory;

    @Inject
    public MembershipFunctionInitializerImpl(@Named("MembershipFunctionFactoryImpl") MembershipFunctionFactory factory) {
        this.factory = factory;
    }



    @Override
    public Map<String, MembershipFunction> getMembershipFunctions() {
        Map<String, MembershipFunction> membershipFunctions = new HashMap<>();

        MembershipFunction triangularLow = factory.createTriangularMembershipFunction("low", 0, 0, 0.4f);
        MembershipFunction triangularMedium = factory.createTriangularMembershipFunction("medium", 0.3f, 0.5f, 0.7f);
        MembershipFunction triangularHigh = factory.createTriangularMembershipFunction("high", 0.6f, 1.0f, 2.0f);

        membershipFunctions.put("low", triangularLow);
        membershipFunctions.put("medium", triangularMedium);
        membershipFunctions.put("high", triangularHigh);

        return membershipFunctions;
    }



}
