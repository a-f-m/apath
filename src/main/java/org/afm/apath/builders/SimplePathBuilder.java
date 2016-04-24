package org.afm.apath.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.afm.apath.core.Path;
import org.afm.apath.core.PathBuilder;
import org.afm.apath.core.Step;
import org.afm.apath.core.StepBuilder;

/**
 * Lightweight (simple) path builder that eschews classic parser generators. 
 * 
 * An example for a more complex path builder could be one that constructs a path by parsing a
 * JSONPath expression (<a href="http://goessner.net/articles/JsonPath">defined
 * by Stefan Goessner</a>.) or an XPath expression.
 * 
 * @author afm
 *
 */
public class SimplePathBuilder extends PathBuilder {
	
	public SimplePathBuilder(StepBuilder stepBuilder) {
		super(stepBuilder);
	}
	
	public Path buildPathFromTerms(Object... stepTerms) {
		// 'Object...' used to allow for simple hybrid paths with raw (unboxed) integers, strings etc. 

		return buildPathFromTerms(Arrays.asList(stepTerms));
	}

	public Path buildPathFromTerms(List<Object> stepTerms) {
		
		List<Step> steps = new ArrayList<>();
		
		// of course a boring cascade - more complex syntaxes will follow via parser generators ;-)
		for (Object stepTerm : stepTerms) {
			
			if (stepTerm instanceof String) {
				
				String s = (String) stepTerm;
				
				if (s.equals("*")) {
					
					steps.add(stepBuilder.allChildren());
					
				} else if (s.equals("..")) {
					
					steps.add(stepBuilder.descendants());
					
				} else if (s.equals("string()")) {
					
					steps.add(stepBuilder.stringValue());
					
				} else {
					steps.add(stepBuilder.childrenByName((String) stepTerm));
				}
			} else if (stepTerm instanceof Integer) {
				
				steps.add(stepBuilder.childByIndex((Integer) stepTerm));
				
			} else if (stepTerm instanceof Predicate) {
				
				@SuppressWarnings("unchecked")
				Predicate<Object> pt = (Predicate<Object>) stepTerm;
				steps.add(stepBuilder.predicateEval(pt));
			} else {
				throw new RuntimeException("bad step " + stepTerm);
			}
		}
		return buildPath(steps);
	}

}
