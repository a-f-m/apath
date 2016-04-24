package org.afm.basics.iteration;

import java.util.Iterator;

/**
 * Iterator mapping.
 * 
 * @author afm
 *
 * @param <S> source ground type of the iterator
 * @param <T> target ground type of the iterator
 */
public class Mapper<S, T> extends IterationProcessing<S, T> {

	public Mapper(Iterator<S> source) {
		super(source);
	}

	protected T map(S x) {
		throw new RuntimeException("to be overwritten");
	}

	@Override
	public T next() {

		if (hasNext()) {
			return map(nextOfSource());
		} else {
			throw new RuntimeException();
		}
	}
}
