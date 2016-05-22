package org.afm.apath.core;

import java.util.Arrays;
import java.util.List;

/**
 * A path is a sequence of steps.
 * 
 * @author afm
 *
 */
public class Path {

	private List<Step> steps;

	public Path(Step... steps) {
		
		this(Arrays.asList(steps));
	}
	
	public Path(List<Step> steps) {
		
		this.steps = steps;
	}
	
	public boolean hasStep(int i) {
		return i > 0 && i < steps.size();
	}

	public Step getStep(int i) {
		return steps.get(i);
	}
	
	public Step stepAfter(int i) {
		
		return hasStep(i + 1) ? steps.get(i + 1) : null; 
	}

	public Step stepBefore(int i) {
		
		return hasStep(i - 1) ? steps.get(i - 1) : null; 
	}
	
	@Override
	public String toString() {
		return steps.toString();
	}
}