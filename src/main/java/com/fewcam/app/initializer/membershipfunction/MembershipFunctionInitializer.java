package com.fewcam.app.initializer.membershipfunction;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import ecorefewcam.MembershipFunction;

@ApplicationScoped
public interface MembershipFunctionInitializer {

    public Map<String, MembershipFunction> getMembershipFunctions();
    
}
