package com.fewcam.app.initializer.compositepredicate;

import java.util.HashMap;
import java.util.Map;

import efis.CompositePredicate;
import efis.EfisFactory;
import efis.FuzzyAggregator;
import efis.Predicate;

public class CompositePredicateInitializer {

	private final EfisFactory efisFactory;
	private final FuzzyAggregator aggregator;

	public CompositePredicateInitializer(EfisFactory efisFactory, FuzzyAggregator aggregator) {
		this.efisFactory = efisFactory;
		this.aggregator = aggregator;

	}

	public Map<String, CompositePredicate> initializeCompositePredicates(Map<String, Predicate> predicates) {

		Map<String, CompositePredicate> compositePredicates = new HashMap<>();
		predicates.forEach((n1, e1) -> {
			predicates.forEach((n2, e2) -> {
				if (!n1.equals(n2)) {

					CompositePredicate cp = efisFactory.createCompositePredicate();
					cp.setFuzzyaggregator(aggregator);
					cp.getAbstractpredicates().add(e1);
					cp.getAbstractpredicates().add(e2);

					final String key = n1 + "_" + aggregator.getName() + "_" + n2;
					compositePredicates.put(key, cp);
				}

			});

		});

		return compositePredicates;
	}
}
