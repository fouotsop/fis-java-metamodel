package com.fewcam.app.routes;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.ocl.pivot.internal.delegate.OCLDelegateDomain;
import org.eclipse.ocl.pivot.internal.delegate.OCLInvocationDelegateFactory;
import org.eclipse.ocl.pivot.internal.delegate.OCLSettingDelegateFactory;
import org.eclipse.ocl.pivot.internal.delegate.OCLValidationDelegateFactory;
import org.eclipse.ocl.pivot.model.OCLstdlib;
import org.eclipse.ocl.xtext.essentialocl.EssentialOCLStandaloneSetup;

import com.fewcam.app.initializer.compositepredicate.CompositePredicateInitializer;
import com.fewcam.app.initializer.defuzzificationalgorithm.DefuzzificationAlgorithmInitializer;
import com.fewcam.app.initializer.fuzzyinferenceengine.FuzzyInferenceEngineInitializer;
import com.fewcam.app.initializer.fuzzyinferencerule.FuzzyInferenceRuleInitializer;
import com.fewcam.app.initializer.predicate.PredicateInitializer;
import com.fewcam.app.initializer.variable.InitializeVariables;
import com.fewcam.app.model.EngineData;

import efis.CompositePredicate;
import efis.DefuzzificationAlgorithm;
import efis.EfisFactory;
import efis.EfisPackage;
import efis.FuzzyInferenceEngine;
import efis.FuzzyInferenceRule;
import efis.FuzzyInferenceSystemElement;
import efis.LinguisticVariable;
import efis.Minimum;
import efis.Predicate;

@Path("/evaluate")
public class EvaluationRoute {

	// @Inject
	// @Named("FuzzyInferenceEngineInitializerImpl")
	// private FuzzyInferenceEngineInitializer initialzer;

	public EvaluationRoute() {
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response evaluate(EngineData data) {

//		String[] variables = { "security", "transparency", "reputation" };
//		FuzzyInferenceEngine engine = this.initialzer.initializeFuzzyInferenceEngine(variables, "credibility");
//		double score = engine.defuzzify();
//		return "Credibility value is : " + String.valueOf(score);

		EfisPackage efisPackage = EfisPackage.eINSTANCE;

		OCLstdlib.install();

		OCLDelegateDomain.initialize(null);

		final String pivotURI = "http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot";

		org.eclipse.ocl.pivot.model.OCLstdlib.install();

		EValidator.ValidationDelegate.Registry.INSTANCE.put(pivotURI, new OCLValidationDelegateFactory.Global());

		EOperation.Internal.InvocationDelegate.Factory.Registry.INSTANCE.put(pivotURI,
				new OCLInvocationDelegateFactory.Global());

		EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE.put(pivotURI,
				new OCLSettingDelegateFactory.Global());

		EssentialOCLStandaloneSetup.doSetup();
		org.eclipse.ocl.pivot.internal.delegate.OCLDelegateDomain.initialize(null);
		org.eclipse.ocl.pivot.model.OCLstdlib.install();

		EfisFactory efisFactory = efisPackage.getEfisFactory();

		InitializeVariables variableInitializer = new InitializeVariables(efisFactory);

		Map<String, LinguisticVariable> linguisticVariables = variableInitializer
				.initializeVariables(data.getLinguisticVariableData());

		PredicateInitializer predicateInitializer = new PredicateInitializer(efisFactory);

		Map<String, Predicate> predicates = predicateInitializer.buildPredicates(linguisticVariables,
				variableInitializer.getLinguisticTerms(),
				variableInitializer.getLinguisticHedges());

		Minimum minimum = variableInitializer.getMinAggregator();
		CompositePredicateInitializer cpInitializer = new CompositePredicateInitializer(efisFactory, minimum);
		Map<String, CompositePredicate> compositePredicates = cpInitializer.initializeCompositePredicates(predicates);

		LinguisticVariable outputVariable = variableInitializer.initializeOutputVariable(data.getOutputVariable());
		Map<String, Predicate> outputPredicates = predicateInitializer.buildOutputPredicates(
				variableInitializer.getLinguisticTerms(),
				variableInitializer.getLinguisticHedges(), outputVariable);

		FuzzyInferenceRuleInitializer firInitializer = new FuzzyInferenceRuleInitializer(efisFactory, minimum);

		Map<String, FuzzyInferenceRule> rules = firInitializer.initializeFuzzyInferenceRules(predicates,
				compositePredicates, outputPredicates, data.getRuleData());

		FuzzyInferenceEngineInitializer fieInitializer = new FuzzyInferenceEngineInitializer(efisFactory);

		FuzzyInferenceEngine engine = fieInitializer.initializeFuzzyInferenceEngine(rules);

		validateEngineGraph(engine);

		engine.infer();

		DefuzzificationAlgorithmInitializer algorithmInitializer = new DefuzzificationAlgorithmInitializer(
				efisFactory);

		DefuzzificationAlgorithm algorithm = algorithmInitializer.initializeAlgorithm(engine);

		double score = algorithm.defuzzify();

		Map<String, Object> result = new HashMap<>();

		result.put("variableName", outputVariable.getName());
		result.put("score", score);

		return Response.status(200)
				.entity(result)
				.build();
	}

	protected void validateEngineGraph(FuzzyInferenceSystemElement element) {
		// 1. Use the EMF Diagnostician to perform a full validation.
		// This automatically respects the OCL delegates you registered.
		Diagnostic diagnostic = Diagnostician.INSTANCE.validate(element);

		// 2. Print results regardless of severity to see all statuses
		printDiagnostic(diagnostic, "");

		// 3. Throw an exception only if there are actual errors
		if (diagnostic.getSeverity() == Diagnostic.ERROR) {
			throw new IllegalStateException("Model validation failed with OCL errors.");
		}
	}

	private void printDiagnostic(Diagnostic diagnostic, String indent) {
		// Determine the status label
		String status = "OK";
		if (diagnostic.getSeverity() == Diagnostic.ERROR)
			status = "ERROR";

		else if (diagnostic.getSeverity() == Diagnostic.WARNING)
			status = "WARNING";
		else if (diagnostic.getSeverity() == Diagnostic.INFO)
			status = "INFO";

		// Print the message and the source (which often indicates the OCL constraint
		// name)
		System.out.println(indent + "[" + status + "] " + diagnostic.getMessage());

		// Recurse through children for detailed nested diagnostics
		for (Diagnostic child : diagnostic.getChildren()) {
			printDiagnostic(child, indent + "  ");
		}
	}

}
