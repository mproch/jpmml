/*
 * Copyright (c) 2013 University of Tartu
 */
package org.jpmml.knime;

import org.jpmml.evaluator.*;

import org.junit.*;

import static org.junit.Assert.*;

public class ClassificationTest {

	@Test
	public void evaluateDecisionTree() throws Exception {
		Batch batch = new KnimeBatch("DecisionTree", "Iris");

		assertTrue(BatchUtil.evaluate(batch));
	}

	@Test
	public void evaluateNeuralNetwork() throws Exception {
		Batch batch = new KnimeBatch("NeuralNetwork", "Iris");

		assertTrue(BatchUtil.evaluate(batch));
	}
}