package org.afm.apath.builders;

import java.util.Iterator;

import org.afm.apath.core.Path;
import org.afm.apath.core.Step.AllChildren;
import org.afm.apath.core.Step.ChildByIndex;
import org.afm.apath.core.Step.ChildrenByName;
import org.afm.apath.core.Step.ChildrenByName.Namespace;
import org.afm.apath.core.Step.StringValue;
import org.afm.apath.core.StepBuilder;
import org.afm.basics.iteration.Filter;
import org.afm.basics.iteration.Single;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JdkDomStepBuilder extends StepBuilder {
	
	public class JDomNsWrapper implements Namespace {
		// deferred
	}
	
	class NodeListIter implements Iterator<Object> {
		
		NodeList nodeList;
		
		public NodeListIter(NodeList nodeList) {
			this.nodeList = nodeList;
			length = nodeList.getLength();
		}
		
		int length;
		int current;

		@Override
		public boolean hasNext() {
			return current < length;
		}

		@Override
		public Object next() {
			return nodeList.item(current++);
		}
	}
	
	class TagFilter extends Filter<Object> {
		
		String tag;

		public TagFilter(Iterator<Object> source, String tag) {
			super(source);
			this.tag = tag;
		}
		
		@Override
		protected boolean accept(Object x) {

			if (x instanceof Element) {
				if (tag == null) {
					return true;
				} else if (((Element) x).getTagName().equals(tag)) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	@Override
	public ChildrenByName childrenByName(String name) {

		return new ChildrenByName(name) {
			
			@Override
			public Iterator<?> applyTo(Object node, Path enclosingPath, int currStepNo) {

				if (node instanceof Element) {
					return new TagFilter(new NodeListIter(((Element) node).getChildNodes()), name);
				} else if (node instanceof Document) {
					Element e = ((Document) node).getDocumentElement();
					if (e.getTagName().equals(name)) {
						return new Single<Object>(e);
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
					NodeList nodeList =
						((Element) node).getChildNodes();
					if (inRange(nodeList, i)) {
						return nodeList.item(i); 
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
					return new TagFilter(new NodeListIter(((Element) node).getChildNodes()), null);
				} else if (node instanceof Document) {
					return new Single<Object>(((Document) node).getDocumentElement());
				}
				return null;
			}
		};
	}
	
	@Override
	public StringValue stringValue() {

		return new StringValue() {
			
			@Override
			public Object applyTo(Object node, Path enclosingPath, int currStepNo) {
				if (node instanceof Node) {
					return ((Node) node).getTextContent();
				} else  {
					return null;
				}
			}
		};
	}
	
	private boolean inRange(NodeList l, int i) {
		return i >= 0 && i <= l.getLength() - 1;
	}

}
