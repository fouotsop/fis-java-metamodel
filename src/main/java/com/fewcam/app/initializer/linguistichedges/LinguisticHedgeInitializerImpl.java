package com.fewcam.app.initializer.linguistichedges;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import ecorefewcam.EcorefewcamFactory;
import ecorefewcam.LinguisticHedge;


@ApplicationScoped
@Named("LinguisticHedgeInitializerImpl")
public class LinguisticHedgeInitializerImpl implements LinguisticHedgeInitializer {

    private final EcorefewcamFactory factory;

    @Inject
    public LinguisticHedgeInitializerImpl(EcorefewcamFactory factory) {
        this.factory = factory;
    }

    @Override
    public LinguisticHedge[] intializeLinguisticHedges() {

        LinguisticHedge[] linguisticHedges = new LinguisticHedge[2];
        linguisticHedges[0] = factory.createFairly();
        linguisticHedges[1] = factory.createVery();

        return linguisticHedges;

    }
}
