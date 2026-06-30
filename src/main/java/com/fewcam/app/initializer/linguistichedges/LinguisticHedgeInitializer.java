package com.fewcam.app.initializer.linguistichedges;


import javax.enterprise.context.ApplicationScoped;

import ecorefewcam.LinguisticHedge;

@ApplicationScoped
public interface LinguisticHedgeInitializer {

    public LinguisticHedge[] intializeLinguisticHedges (); 

}
