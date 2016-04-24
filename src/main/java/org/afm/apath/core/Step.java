package org.afm.apath.core;

import java.util.function.Predicate;

/**
 * A step is an element of a path. <br>
 * We make use of nesting interfaces and classes for compactness.
 * Whenever constructors or attributes are required an abstract
 * class is chosen.
 * 
 * @author afm
 *
 */
public interface Step {

	/**
	 * Applies this step to a node.
	 * 
	 * @param node node of the provided structure (e.g., DOM node, JSON object etc.)
	 * @param enclosingPath path enclosing this step, set by the processor
	 * @param currStepNo step no of this step within {@code enclosingPath}
	 * @return a single selected node of the provided
	 *         structure (Object) or an iteration over nodes (Iterator &lt;Object&gt;)
	 */
	public abstract Object applyTo(Object node, Path enclosingPath, int currStepNo);

	public abstract class ChildrenByName implements Step {

		public interface Namespace {
			// for future namespace use
		}

		protected Namespace namespace; // to serve XML too, deferred
		protected String name;

		protected ChildrenByName(String name) {
			this.name = name;
		}

		public ChildrenByName setNamespace(Namespace namespace) {
			this.namespace = namespace;
			return this;
		}
	}

	public abstract class ChildByIndex implements Step {

		protected int i;

		protected ChildByIndex(int i) {
			this.i = i;
		}
	}

	public interface AllChildren extends Step {
	}

	public interface Descendants extends Step {
	}

	/**
	 * String representation of a node.
	 */
	public interface StringValue extends Step {
	}
	
	public abstract class PredicateEval implements Step {

		protected Predicate<Object> p;

		public PredicateEval(Predicate<Object> p) {
			this.p = p;
		}
	}

}