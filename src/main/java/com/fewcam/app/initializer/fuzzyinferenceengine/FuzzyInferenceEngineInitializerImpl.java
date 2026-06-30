package com.fewcam.app.initializer.fuzzyinferenceengine;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

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

import com.fewcam.app.initializer.compositeexpression.CompositeExpressionInitializer;
import com.fewcam.app.initializer.fuzzyinferencerule.FuzzyInferenceRuleInitializer;
import com.fewcam.app.initializer.linguisticvariableinstance.VariableInstanceInitializer;
import com.fewcam.app.initializer.simpleexpression.SimpleExpressionInitializer;

import ecorefewcam.CompositeExpression;
import ecorefewcam.DefuzzyficationAlgorithm;
import ecorefewcam.EcorefewcamFactory;
import ecorefewcam.FuzzyInferenceEngine;
import ecorefewcam.FuzzyInferenceRule;
import ecorefewcam.InferedMembershipFunction;
import ecorefewcam.SimpleExpression;
import ecorefewcam.VariableInstance;

@ApplicationScoped
@Named("FuzzyInferenceEngineInitializerImpl")
public class FuzzyInferenceEngineInitializerImpl implements FuzzyInferenceEngineInitializer {

	private final EcorefewcamFactory factory;
	private final VariableInstanceInitializer variableInstanceInitializer;
	private final SimpleExpressionInitializer simpleExpressionInitializer;
	private final CompositeExpressionInitializer compositeExpressionInitializer;
	private final FuzzyInferenceRuleInitializer fuzzyInfereRuleInitializer;

	@Inject
	public FuzzyInferenceEngineInitializerImpl(EcorefewcamFactory factory,
			@Named("FuzzyInferenceRuleInitializerImpl") FuzzyInferenceRuleInitializer fuzzyInfereRuleInitializer,
			@Named("VariableInstanceInitializerImpl") VariableInstanceInitializer variableInstanceInitializer,
			@Named("SimpleExpressionIntializerImpl") SimpleExpressionInitializer simpleExpressionInitializer,
			@Named("CompositeExpressionIntializerImpl") CompositeExpressionInitializer compositeExpressionInitializer

	) {

		this.factory = factory;
		this.fuzzyInfereRuleInitializer = fuzzyInfereRuleInitializer;
		this.variableInstanceInitializer = variableInstanceInitializer;
		this.simpleExpressionInitializer = simpleExpressionInitializer;
		this.compositeExpressionInitializer = compositeExpressionInitializer;

	}

	@Override
	public FuzzyInferenceEngine initializeFuzzyInferenceEngine(String[] inputVariableNames, String outputVariableName) {
		FuzzyInferenceEngine engine = factory.createFuzzyInferenceEngine();

		VariableInstance[][] variableInstances = this.variableInstanceInitializer
				.initializeVariableInstances(inputVariableNames);

		VariableInstance[] outputVariableInstances = this.variableInstanceInitializer
				.initializeOutputVariableInstances(outputVariableName);

		int variableCount = inputVariableNames.length;
		SimpleExpression[][] simpleExpressions = simpleExpressionInitializer
				.initializeSimpleExpressions(variableInstances, variableCount);

		Map<String, CompositeExpression> compositeExpressions = this.compositeExpressionInitializer
				.initializeCompositeExpressions(simpleExpressions);

		CompositeExpression[] expressionsArray = this.convertCompositeExpressionMapToArray(compositeExpressions);
		Map<String, VariableInstance> variableInstanceMap = this.createVariableInstanceMap(outputVariableInstances);

		Map<String, FuzzyInferenceRule> fuzzyInferenceRules = this.fuzzyInfereRuleInitializer
				.initializeFuzzyInferenceRules(expressionsArray, variableInstanceMap);
		engine.getFuzzyInferenceRules().addAll(fuzzyInferenceRules.values());

		DefuzzyficationAlgorithm defuzzyficationAlgorithm = factory.createCentroidAlgorithm();
		engine.setDefuzzificationAlgorithm(defuzzyficationAlgorithm);

		InferedMembershipFunction outputFunction = factory.createInferedMembershipFunction();

		outputFunction.setFuzzyInferenceEngine(engine);
		engine.setInferedMembershipFunction(outputFunction);

		EssentialOCLStandaloneSetup.doSetup();

		// 2. Install the OCL Standard Library
		OCLstdlib.install();

		// 3. Initialize the OCL Delegate Domain
		// Using null targets the global EPackage.Registry
		OCLDelegateDomain.initialize(null);

		final String pivotURI = "http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot";

		org.eclipse.ocl.pivot.model.OCLstdlib.install();

		EValidator.ValidationDelegate.Registry.INSTANCE.put(pivotURI, new OCLValidationDelegateFactory.Global());

		EOperation.Internal.InvocationDelegate.Factory.Registry.INSTANCE.put(pivotURI,
				new OCLInvocationDelegateFactory.Global());

		EStructuralFeature.Internal.SettingDelegate.Factory.Registry.INSTANCE.put(pivotURI,
				new OCLSettingDelegateFactory.Global());

		// org.eclipse.ocl.pivot.internal.delegate.OCLDelegateDomain.initialize(null);
		// org.eclipse.ocl.pivot.model.OCLstdlib.install();
		validateEngineGraph(engine);

		return engine;
	}

	/*
	 * protected void validateEngineGraph(FuzzyInferenceEngine engine) {
	 * BasicDiagnostic diagnostics = new BasicDiagnostic(); Set<EObject> visited =
	 * new HashSet<>(); ArrayDeque<EObject> queue = new ArrayDeque<>();
	 * 
	 * queue.add(engine);
	 * 
	 * while (!queue.isEmpty()) { EObject current = queue.removeFirst(); if
	 * (!visited.add(current)) { continue; }
	 * 
	 * for (EObject child : current.eContents()) { if (child != null &&
	 * !visited.contains(child)) { queue.addLast(child); } }
	 * 
	 * for (EObject referenced : current.eCrossReferences()) { if (referenced !=
	 * null && !visited.contains(referenced)) { queue.addLast(referenced); } } }
	 * 
	 * if (diagnostics.getSeverity() >= Diagnostic.ERROR) { throw new
	 * IllegalStateException(formatDiagnostics(diagnostics)); }
	 * System.out.println(diagnostics.getMessage());
	 * System.out.println(diagnostics.toString()); }
	 * 
	 * protected String formatDiagnostics(Diagnostic diagnostic) { StringBuilder
	 * builder = new StringBuilder(); appendDiagnostic(diagnostic, builder); return
	 * builder.toString(); }
	 * 
	 * protected void appendDiagnostic(Diagnostic diagnostic, StringBuilder builder)
	 * { if (diagnostic.getSeverity() >= Diagnostic.ERROR && diagnostic.getMessage()
	 * != null) {
	 * builder.append(diagnostic.getMessage()).append(System.lineSeparator()); }
	 * 
	 * for (Diagnostic child : diagnostic.getChildren()) { appendDiagnostic(child,
	 * builder); } }
	 * 
	 */

	protected void validateEngineGraph(FuzzyInferenceEngine engine) {
		// 1. Use the EMF Diagnostician to perform a full validation.
		// This automatically respects the OCL delegates you registered.
		Diagnostic diagnostic = Diagnostician.INSTANCE.validate(engine);

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

	protected Map<String, VariableInstance> createVariableInstanceMap(VariableInstance[] variableInstances) {
		Map<String, VariableInstance> variableInstanceMap = new java.util.HashMap<>();
		for (VariableInstance instance : variableInstances) {
			variableInstanceMap.put(instance.getName(), instance);
		}
		return variableInstanceMap;
	}

	protected CompositeExpression[] convertCompositeExpressionMapToArray(
			Map<String, CompositeExpression> compositeExpressionMap) {
		return compositeExpressionMap.values().toArray(new CompositeExpression[0]);
	}

}
