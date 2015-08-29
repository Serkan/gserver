package org.test.gserver;

import java.io.Serializable;

/**
 *
 * @author serkan
 * @param <X>
 * @param <Y>
 */
public class Pair<X, Y> implements Serializable {

	private X first;

	private Y second;

	public Pair(X first, Y second) {
		this.first = first;
		this.second = second;
	}

	public X getFirst() {
		return first;
	}

	public Y getSecond() {
		return second;
	}
}
