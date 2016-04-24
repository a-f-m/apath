package deferred;

import java.util.Iterator;
import java.util.List;

import org.afm.apath.core.Path;
import org.afm.apath.core.Step.AllChildren;
import org.afm.apath.core.Step.ChildByIndex;
import org.afm.apath.core.Step.ChildrenByName;
import org.afm.apath.core.Step.ChildrenByName.Namespace;
import org.afm.apath.core.StepBuilder;
import org.afm.basics.iteration.Single;
import org.jdom2.Document;
import org.jdom2.Element;

public class JDomStepBuilder extends StepBuilder {
	
	public class JDomNsWrapper implements Namespace {
		// deferred
	}
		
	@Override
	public ChildrenByName childrenByName(String name) {

		return new ChildrenByName(name) {
			
			// 
			@Override
			public Iterator<?> applyTo(Object node, Path enclosingPath, int currStepNo) {
				
				if (node instanceof Element) {
					return
						namespace != null
							? (Iterator<?>) ((Element) node).getChildren(name, null).iterator()
							: (Iterator<?>) ((Element) node).getChildren(name).iterator();
				} else if (node instanceof Document) {
					Element r = ((Document) node).getRootElement();
					if (r.getName().equals(name)) {
						return new Single<Object>(r);
					}
				}
				return null;
			}
		};
	}
	
	@Override
	public ChildByIndex childByIndex(int i) {

		return new ChildByIndex(i) {
			
			@Override
			public Object applyTo(Object node, Path enclosingPath, int currStepNo) {
				
				if (node instanceof Element) {
					List<Element> children = ((Element) node).getChildren();
					if (inRange(children, i)) {
						return children.get(i); 
					}
				}
				// TODO Document
				return null;
			}
		};
	}
	
	@Override
	public AllChildren allChildren() {

		return new AllChildren() {
			
			@Override
			public Iterator<?> applyTo(Object node, Path enclosingPath, int currStepNo) {
				
				if (node instanceof Element) {
					List<Element> children = ((Element) node).getChildren();
					Iterator<?> ch = (Iterator<?>) children.iterator();
					return ch; 
				} else if (node instanceof Document) {
					return new Single<Object>(((Document) node).getRootElement());
				}
				return null;
			}
		};
	}
	
	private boolean inRange(List<Element> l, int i) {
		return i >= 0 && i <= l.size() - 1;
	}

}
