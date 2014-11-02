package org.test.gserver.visualization.cyto;

/**
 * Created by serkan on 01.11.2014.
 */
class Node {

    private Data data;

    public Node() {
    }

    public Node(String id, String name, String icon) {
        this.data = new Data(id, name, icon);
    }

    public Data getData() {
        return data;
    }

    private class Data {

        private String id;

        private String name;

        private String icon;

        private Data(String id, String name, String icon) {
            this.id = id;
            this.name = name;
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }
}
