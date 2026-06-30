package com.fewcam.app.initializer.membershipfunction.factory;


import javax.enterprise.context.ApplicationScoped;

import ecorefewcam.EcorefewcamFactory;
import ecorefewcam.Trapezoidal;
import ecorefewcam.Triangular;

@ApplicationScoped
public interface MembershipFunctionFactory {

    Triangular createTriangularMembershipFunction(String name, float a, float b, float c);

    Trapezoidal createTrapezoidalMembershipFunction(String name, float a, float b, float c, float d);

    EcorefewcamFactory getFactory();
    
}
