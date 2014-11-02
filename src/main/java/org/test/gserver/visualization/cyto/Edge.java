package org.test.gserver.visualization.cyto;

/**
 * Created by serkan on 01.11.2014.
 */
class Edge {

    private Data data;

    public Edge() {
    }

    public Edge(String source, String target) {
        this.data = new Data(source, target);
    }

    public Data getData() {
        return data;
    }

    private class Data {

        private String source;

        private String target;

        private Data() {
        }

        private Data(String source, String target) {
            this.source = source;
            this.target = target;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

    }


}
