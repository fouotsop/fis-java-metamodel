package com.fewcam.app.initializer.linguisticterm;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import ecorefewcam.LinguisticTerm;

@ApplicationScoped
public interface LinguisticTermInitializer {
    
    public Map<String, LinguisticTerm> getLinguisticTerms();

    public String[] getLinguisticTermNames();
}
