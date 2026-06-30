package com.fewcam.app.initializer.linguisticterm;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.fewcam.app.initializer.membershipfunction.MembershipFunctionInitializer;

import ecorefewcam.EcorefewcamFactory;
import ecorefewcam.LinguisticTerm;
import ecorefewcam.MembershipFunction;

@ApplicationScoped
@Named("LinguisticTermInitializerImpl")
public class LinguisticTermInitializerImpl implements LinguisticTermInitializer {

    private final MembershipFunctionInitializer membershipFunctionInitializer;
    private final EcorefewcamFactory factory;
    private final String[] linguisticTermNames;

    @Inject
    public LinguisticTermInitializerImpl(@Named("MembershipFunctionInitializerImpl") MembershipFunctionInitializer membershipFunctionInitializer, EcorefewcamFactory factory, @Named("linguisticTerms") String[] linguisticTerms) {
        this.membershipFunctionInitializer = membershipFunctionInitializer;
        this.factory = factory;
        this.linguisticTermNames = linguisticTerms;
    }

    @Override
    public Map<String, LinguisticTerm> getLinguisticTerms() {

        Map<String, MembershipFunction> membershipFunctions = membershipFunctionInitializer.getMembershipFunctions();

        final Map<String, LinguisticTerm> linguisticTerms = new java.util.HashMap<>();
        for (String termName: linguisticTermNames) {
            LinguisticTerm term = factory.createLinguisticTerm();
            term.setName(termName);
            term.setMembershipFunction(membershipFunctions.get(termName));
            linguisticTerms.put(termName, term);
        }
        return linguisticTerms;

    }

    @Override
    public String[] getLinguisticTermNames() {
        return this.linguisticTermNames;
    }


}
