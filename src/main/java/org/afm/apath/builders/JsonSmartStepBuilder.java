package org.afm.apath.builders;

import java.util.Collections;
import java.util.Iterator;

import org.afm.apath.core.Path;
import org.afm.apath.core.Step;
import org.afm.apath.core.Step.AllChildren;
import org.afm.apath.core.Step.ChildByIndex;
import org.afm.apath.core.Step.ChildrenByName;
import org.afm.apath.core.Step.Descendants;
import org.afm.apath.core.StepBuilder;
import org.afm.basics.iteration.Mapper;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class JsonSmartStepBuilder extends StepBuilder {
	
	boolean skipArraysAtNameSelection;
	
	public JsonSmartStepBuilder() {
	}
	
	public JsonSmartStepBuilder(boolean skipArraysAtNameSelection) {
		this.skipArraysAtNameSelection = skipArraysAtNameSelection;
	}

	@Override
	public ChildrenByName childrenByName(String name) {
		
		return new ChildrenByName(name) {
			
			@Override
			public Object applyTo(Object node, Path enclosingPath, int currStepNo) {
				
				if (node instanceof JSONObject) {
					
					Object o = getIt(node, name);
					if (o != null) {
						return o;
					}
				} else if (skipArraysAtNameSelection && node instanceof JSONArray) {
					
					Step s = enclosingPath.stepBefore(currStepNo);
					if (s != null && !(s instanceof Descendants)) {
						
						return skipArray(node, enclosingPath, currStepNo);
					} else {
						return null;
					}
				}
				return null;
			}

			private Iterator<Object> skipArray(Object node, Path enclosingPath, int currStepNo) {
				
				Iterator<Object> it = ((JSONArray) node).iterator();
				
				Mapper<Object, Object> m = new Mapper<Object, Object>(it) {
					protected Object map(Object x) {
						return childrenByName(name).applyTo(x, enclosingPath, currStepNo);
					}
				};
				return m.iterator();
			}
		};
	}

	private Object getIt(Object node, String name) {
		return ((JSONObject) node).get(name);
	}

	@Override
	public ChildByIndex childByIndex(int i) {

		return new ChildByIndex(i) {
			
			@Override
			public Object applyTo(Object node, Path enclosingPath, int currStepNo) {

				if (node instanceof JSONArray && inRange((JSONArray) node, i)) {
					return ((JSONArray) node).get(i);
				}
				return null;
			}
		};
	}
	
	@Override
	public AllChildren allChildren() {

		return new AllChildren() {
			
			@Override
			public Iterator<?> applyTo(Object node, Path enclosingPath, int currStepNo) {

				if (node instanceof JSONObject) {
					return ((JSONObject) node).values().iterator();
					
				} else if (node instanceof JSONArray) {					
					return ((JSONArray) node).iterator();
				}
				return Collections.emptyIterator();
			}
		};
	}
	
	private boolean inRange(JSONArray ja, int i) {
		return i >= 0 && i <= ja.size() - 1;
	}



}
