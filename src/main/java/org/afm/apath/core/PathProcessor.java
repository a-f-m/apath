package org.afm.apath.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Processor for paths.
 *
 * It would be possible to avoid splitting into multi-results (Iterator &lt;?&gt;) and
 * single-results by using only multi-results (and returning
 * single-element-iterators). We use splitting to gain better performance
 * avoiding most of the single-element-iterators.
 * 
 * 
 * @author afm
 *
 */
public class PathProcessor {
	
	public PathProcessor() {
	}

	/**
	 * Selects all nodes according to a path.
	 * @param root root for selection
	 * @param path path
	 * @return selected nodes
	 */
	public List<Object> selectAll(Object root, Path path) {

		List<Object> results = new ArrayList<>();
		selectAll(root, results, path, 0);
		return results;

	}

	private void selectAll(Object node, List<Object> results, Path path, int stepNo) {

		// decomposition:
		if (node instanceof Iterator<?>) {
			Iterator<?> iter = (Iterator<?>) node;
			while (iter.hasNext()) {
				selectAll(iter.next(), results, path, stepNo);
			}
			return;
		}
		////

		Step step = path.getStep(stepNo);
		boolean furtherSteps = path.hasStep(stepNo + 1);

		Object stepResults = step.applyTo(node, path, stepNo);
		
		if (stepResults != null) {
			if (furtherSteps) {

				selectAll(stepResults, results, path, stepNo + 1);
			} else {
				gatherResults(stepResults, results);
			}
		}
	}

	private void gatherResults(Object stepResults, List<Object> results) {
		
		if (stepResults instanceof Iterator<?>) {

			Iterator<?> it = (Iterator<?>) stepResults;
			while (it.hasNext()) {
				results.add(it.next());
			}
		} else {
			results.add(stepResults);
		}
	}

}
