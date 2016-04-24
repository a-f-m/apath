package org.afm.apath.core;

import java.util.List;

/**
 * Abstract builder for paths.
 * 
 * @author afm
 *
 */
public abstract class PathBuilder {

	protected StepBuilder stepBuilder;

	public PathBuilder(StepBuilder stepBuilder) {
		this.stepBuilder = stepBuilder;
	}
	
	// overriding methods could process the steps further, e.g. algebraic transformations.
	public Path buildPath(List<Step> steps) {
		return new Path(steps);
	}

	public StepBuilder getStepBuilder() {
		return stepBuilder;
	}
}