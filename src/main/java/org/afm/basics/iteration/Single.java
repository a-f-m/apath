package org.afm.basics.iteration;

import java.util.Iterator;

/**
 * Single element iterator.
 * 
 * @author afm
 *
 * @param <S> ground type of the iterator
 */
public class Single<S> implements Iterator<S> {

	S s;
	boolean consumed;
	
	public Single(S s) {
		this.s = s;
	}

	@Override
	public boolean hasNext() {

		return !consumed;
	}

	@Override
	public S next() {
		
		consumed = true;
		return s;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
