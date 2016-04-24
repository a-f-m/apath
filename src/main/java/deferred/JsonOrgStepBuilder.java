package deferred;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.afm.apath.core.Path;
import org.afm.apath.core.Step;
import org.afm.apath.core.Step.AllChildren;
import org.afm.apath.core.Step.ChildByIndex;
import org.afm.apath.core.Step.ChildrenByName;
import org.afm.apath.core.Step.Descendants;
import org.afm.apath.core.StepBuilder;
import org.afm.basics.iteration.Mapper;
import org.json.JSONArray;
import org.json.JSONObject;


public class JsonOrgStepBuilder extends StepBuilder {
	
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
				} else if (node instanceof JSONArray) {
					
					return skipArray(name, node, enclosingPath, currStepNo);
				}
				return null;
			}
		};
	}

	private Object getIt(Object node, String name) {
		return ((JSONObject) node).opt(name);
	}

	private Object skipArray(String name, Object node, Path enclosingPath, int currStepNo) {
		
		Step s = enclosingPath.stepBefore(currStepNo);
		if (s != null && !(s instanceof Descendants)) {
			
			Iterator<Object> x = ((JSONArray) node).iterator();
			
			Mapper<Object, Object> y = new Mapper<Object, Object>(x) {
				protected Object map(Object x) {
					return childrenByName(name).applyTo(x, enclosingPath, currStepNo);
				}
			};
			return y.iterator();
		} else {
			return null;
		}
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

					Field mapField;
					try {
						mapField = JSONObject.class.getDeclaredField("map");
						mapField.setAccessible(true);

						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) mapField.get(node);

						return map.values().iterator();

					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
						| IllegalAccessException e) {
						throw new RuntimeException(e);
					}

				} else if (node instanceof JSONArray) {
					return ((JSONArray) node).iterator();
				}
				return Collections.emptyIterator();
			}
		};
	}
	
	private boolean inRange(JSONArray ja, int i) {
		return i >= 0 && i <= ja.length() - 1;
	}



}
