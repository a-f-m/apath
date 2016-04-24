package org.afm.basics.iteration;


import java.util.Iterator;

/**
 * Iterator filtering.
 * 
 * @author afm
 *
 * @param <S> ground type of the iterator
 */
public class Filter<S> extends IterationProcessing<S, S> {

	public Filter(Iterator<S> source) {
		super(source);
	}

	protected boolean accept(S x) {
		throw new RuntimeException("to be overwritten");
	}

	@Override
	public S next() {

		return forward();

	}

	@Override
	public boolean hasNext() {

		// to ensure usual idempotence of hasNext()
		if (current != null) {
			return true;
		}

		while (super.hasNext()) {
			if (accept(current = nextOfSource())) {
				return true;
			}
		}
		return false;
	}
}
