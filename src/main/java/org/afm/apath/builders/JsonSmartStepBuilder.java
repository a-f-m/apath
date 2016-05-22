package org.afm.apath.builders;

import java.util.Collections;
import java.util.Iterator;

import org.afm.apath.core.Path;
import org.afm.apath.core.Step;
import org.afm.apath.core.Step.AllChildren;
import org.afm.apath.core.Step.ChildByIndex;
import org.afm.apath.core.Step.ChildrenByName;
import org.afm.apath.core.StepBuilder;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class JsonSmartStepBuilder extends StepBuilder {
	
	/**
	 * Arrays will be skipped except before childByIndex-steps
	 */
	private boolean skipArrays;
	
	public JsonSmartStepBuilder() {
	}
	
	public JsonSmartStepBuilder(boolean skipArrays) {
		this.skipArrays = skipArrays;
	}
	
	class JsAllChildren extends AllChildren {

		public JsAllChildren() {
			super();
		}

		@Override
		public Iterator<Object> applyTo(Object node, Path enclosingPath, int currStepNo) {

			if (node instanceof JSONObject) {
				return ((JSONObject) node).values().iterator();
				
			} else if (node instanceof JSONArray) {
				return ((JSONArray) node).iterator();
			}
			return Collections.emptyIterator();
		}
	}
	
	class JsChildrenByName extends ChildrenByName {

		public JsChildrenByName(String name) {
			super(name);
		}
		
		@Override
		public Object applyTo(Object node, Path enclosingPath, int currStepNo) {
			
//			System.out.println("apply " + this + " to " + node);
			
			if (node instanceof JSONObject) {
				
				Object jo = ((JSONObject) node).get(name);
				if (jo != null) {
					return skipArrays && jo instanceof JSONArray ? skipArray(jo, enclosingPath, currStepNo) : jo;
				}
			}
			return null;
		}
	}

	@Override
	public ChildrenByName childrenByName(String name) {
		
		return new JsChildrenByName(name);
	}

	@Override
	public ChildByIndex childByIndex(int i) {

		return new ChildByIndex(i) {
			
			@Override
			public Object applyTo(Object node, Path enclosingPath, int currStepNo) {

//				System.out.println("apply " + this + " to " + node);

				if (node instanceof JSONArray && inRange((JSONArray) node, i)) {
					Object jo = ((JSONArray) node).get(i);
					return skipArrays && jo instanceof JSONArray ? skipArray(jo, enclosingPath, currStepNo) : jo;
				}
				return null;
			}
		};
	}
	
	@Override
	public AllChildren allChildren() {

		return new JsAllChildren();
	}
	
	private Object skipArray(Object node, Path enclosingPath, int currStepNo) {
		
		Step stepAfter = enclosingPath.stepAfter(currStepNo);
		boolean indexSelection = stepAfter != null && stepAfter instanceof ChildByIndex ;
		
		if (!indexSelection) {
			return new JsAllChildren().applyTo(node, enclosingPath, currStepNo);
		}
		return node;
	}
	
	private boolean inRange(JSONArray ja, int i) {
		return i >= 0 && i <= ja.size() - 1;
	}

	public void setSkipArrays(boolean skipArrays) {
		this.skipArrays = skipArrays;
	}



}
