package org.afm.basics.iteration;

import java.util.Iterator;

/**
 * Iterator reducing.
 * 
 * @author afm
 *
 * @param <S> ground type of the iterator
 */
public class Reducer<S> {
	
	S previous;

	public Reducer(Iterator<S> source) {
		this(source, null);
	}
	
	public Reducer(Iterator<S> source, S initial) {
		previous = initial;
		while (source.hasNext()) {
			previous = reduce(previous, source.next()); 
		}
	}
	
	protected S reduce(S previous, S x) {
		throw new RuntimeException("to be overwritten");
	}
	
	public S value() {
		return previous;
	}

}
