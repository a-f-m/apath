package org.afm.apath.core;

import java.util.Iterator;
import java.util.function.Predicate;

import org.afm.apath.core.Step.AllChildren;
import org.afm.apath.core.Step.ChildByIndex;
import org.afm.apath.core.Step.ChildrenByName;
import org.afm.apath.core.Step.Descendants;
import org.afm.apath.core.Step.PredicateEval;
import org.afm.apath.core.Step.StringValue;
import org.afm.basics.iteration.Composer;
import org.afm.basics.iteration.Mapper;
import org.afm.basics.iteration.Single;

/**
 * Abstract builder for steps. Although it is also possible to skip/abdicate a
 * builder and create steps on the fly (
 * "new ChildrenByName(name)"), we recommend using the builder as a constraining
 * artifact. It also provides some default implementation, e.g. 'descendants', 'predicateEval' or 'stringValue'.
 * For every data structure to wrap this StepBuilder has to be extended (e.g., see 'JsonOrgStepBuilder') 
 * 
 * @author afm
 *
 */
public abstract class StepBuilder {

	abstract public ChildrenByName childrenByName(String name);

	abstract public ChildByIndex childByIndex(int i);

	abstract public AllChildren allChildren();

	public PredicateEval predicateEval(Predicate<Object> p) {

		return new PredicateEval(p) {

			@Override
			public Object applyTo(Object node, Path enclosingPath, int currStepNo) {

				if (p.test(node)) {
					return node;
				}
				return null;
			}
		};
	}

	// default implementation; more sophisticated implementations can be placed at the custom builders. 
	public Descendants descendants() {

		return new Descendants() {

			@Override
			public Iterator<Object> applyTo(Object node, Path enclosingPath, int currStepNo) {

				return descendantsHierarchic(node, enclosingPath, currStepNo);
			}
		};
	}

	private Iterator<Object> descendantsHierarchic(Object node, Path enclosingPath, int currStepNo) {

		Object children = allChildren().applyTo(node, enclosingPath, currStepNo);
		if (!(children instanceof Iterator<?>)) {
			throw new RuntimeException("iterator<?> expected");
		}
		@SuppressWarnings("unchecked")
		Iterator<Object> childrenIter = (Iterator<Object>) children;
		// if (ch == null) { deferred TODO
		// throw new RuntimeException("");
		// }
		return new Composer<Object>(new Single<Object>(node), new Mapper<Object, Object>(childrenIter) {
			protected Object map(Object x) {
				return descendantsHierarchic(x, enclosingPath, currStepNo);
			};
		});
	}

	public StringValue stringValue() {
		
		return new StringValue() {
			
			@Override
			public Object applyTo(Object node, Path enclosingPath, int currStepNo) {
				return node.toString();
			}
		};
	}
}