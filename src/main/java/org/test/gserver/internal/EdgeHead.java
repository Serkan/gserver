package org.test.gserver.internal;

import org.test.gserver.NodeKey;

import java.util.Map;

/**
 * Internal data structure to keep node main properties, it is used
 * for more convenience serialization and deserialization.
 */
class EdgeHead {

	private NodeKey key;

	private Map<String, String> attr;

	public EdgeHead() {
	}

	public EdgeHead(NodeKey key, Map<String, String> attr) {
		this.key = key;
		this.attr = attr;
	}

	public Map<String, String> getAttr() {
		return attr;
	}

	public void setAttr(Map<String, String> attr) {
		this.attr = attr;
	}

	public NodeKey getKey() {
		return key;
	}

	public void setKey(NodeKey key) {
		this.key = key;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return o.hashCode() == this.hashCode();
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}
}
