package org.test.gserver.internal;

/**
 * Internal data structure to keep node main properties, it is used
 * for more convenience serialization and deserialization.
 */
class NodeHead {

    private String id;

    private String type;

    public NodeHead() {
    }

    public NodeHead(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return (id + type).hashCode();
    }
}
